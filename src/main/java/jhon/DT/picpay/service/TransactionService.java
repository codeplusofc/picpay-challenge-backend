package jhon.DT.picpay.service;

import jhon.DT.picpay.dtos.TransactionDTO;
import jhon.DT.picpay.model.transaction.Transaction;
import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final RestTemplate restTemplate;

    public TransactionService(TransactionRepository transactionRepository, UserService userService, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this. restTemplate = restTemplate;
    }

    public void createTransaction(TransactionDTO transactionDTO) throws Exception {

        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = this.authorizeTransaction(sender, transactionDTO.value());
        if (!isAuthorized){
            throw new Exception("the transaction was not authorized");
        }

        Transaction newTransaction = new Transaction();
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
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK){

            if (authorizationResponse.getBody() != null) {
                String status = (String) authorizationResponse.getBody().get("status");
                return "success".equalsIgnoreCase(status);
            } else {
                return false;
            }

        }else{
            return false;
        }
    }


}


