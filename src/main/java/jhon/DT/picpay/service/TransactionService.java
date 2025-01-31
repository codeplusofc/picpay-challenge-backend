package jhon.DT.picpay.service;

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

@Service
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final WebClient webClient;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.webClient = webClientBuilder.baseUrl("https://util.devi.tools/api/v2").build();

    }

    public void createTransaction(TransactionDTO transactionDTO) throws Exception {

        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transactionDTO.value());
        if (!isAuthorized){
            throw new TransactionNotAuthorizedException("the transaction was not authorized");
        }

        var newTransaction = new Transaction();
        newTransaction.setAmount(transactionDTO.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        try {
            Map response = webClient.get()
                    .uri("/authorize")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null){
                String status = (String) response.get("status");
                return "seccess".equalsIgnoreCase(status);
            }else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }



}


