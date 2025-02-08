package jhon.DT.picpay.service;

import jakarta.transaction.Transactional;
import jhon.DT.picpay.dtos.TransactionDTO;
import jhon.DT.picpay.exceptions.TransactionNotAuthorizedException;
import jhon.DT.picpay.model.transaction.Transaction;
import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final WebClient webClient;

    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, WebClient.Builder webClientBuilder, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.webClient = webClientBuilder.baseUrl("https://util.devi.tools/api/v2").build();

        this.notificationService = notificationService;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        if (transactionDTO.value() == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        var sender = userService.findUserById(transactionDTO.senderId());
        var receiver = userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = authorizeTransaction(sender, transactionDTO.value());
        if (!isAuthorized) {
            throw new TransactionNotAuthorizedException("The transaction was not authorized");
        }

        var newTransaction = buildTransaction(sender, receiver, transactionDTO.value());
        updateBalance(sender, receiver, transactionDTO.value());

        transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        try {
            notificationService.sendNotification(sender, "Transaction completed successfully");
            notificationService.sendNotification(receiver, "Transaction received successfully");
        } catch (Exception e) {
            System.err.println("Falha ao enviar notificação: " + e.getMessage());
            // Aqui você pode logar o erro ou até mesmo armazenar para enviar mais tarde
        }

        return newTransaction;
    }


    private Transaction buildTransaction(User sender, User receiver, BigDecimal amount){
        var transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());
        return transaction;
    }
    private void updateBalance(User sender, User receiver, BigDecimal amount){
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        try{
            Map response = webClient.get()
                    .uri("/authorize")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return isTransactionAuthorized(response);
        }catch (Exception e){
            return false;
        }
    }
    public boolean isTransactionAuthorized(Map response){
        return Optional.ofNullable(response)
                .map(res -> res.get("status"))
                .filter(String.class::isInstance)
                .map(status -> "success".equalsIgnoreCase((String)status))
                .orElse(false);
    }



}


