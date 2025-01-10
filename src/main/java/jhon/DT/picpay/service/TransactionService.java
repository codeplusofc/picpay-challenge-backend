package jhon.DT.picpay.service;

import jhon.DT.picpay.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private UserService userService;

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }


}


