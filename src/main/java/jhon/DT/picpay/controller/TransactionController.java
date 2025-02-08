package jhon.DT.picpay.controller;


import jakarta.validation.Valid;
import jhon.DT.picpay.dtos.TransactionDTO;
import jhon.DT.picpay.exceptions.TransactionNotAuthorizedException;
import jhon.DT.picpay.exceptions.UserNotFoundException;
import jhon.DT.picpay.model.transaction.Transaction;
import jhon.DT.picpay.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            var newTransaction = transactionService.createTransaction(transactionDTO);
            return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TransactionNotAuthorizedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().contains("Notification service is down")) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Transaction completed, but notification failed.");
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor: " + e.getMessage());
        }
    }





}
