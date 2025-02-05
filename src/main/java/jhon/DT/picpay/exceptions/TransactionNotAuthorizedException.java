package jhon.DT.picpay.exceptions;

public class TransactionNotAuthorizedException extends RuntimeException{

    public TransactionNotAuthorizedException(String message){
        super(message);
    }

}
