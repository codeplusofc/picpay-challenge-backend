package jhon.DT.picpay.service;

import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.model.user.UserType;
import jhon.DT.picpay.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User saveUser(User user){
        return this.userRepository.save(user);
    }

    public User findUserById(UUID id) throws Exception{
        return this.userRepository.findUserById(id).orElseThrow(() -> new RuntimeException("User not f"));
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception{

        if (sender.getUserType() == UserType.MERCHANT){
            throw new Exception("This type of user cannot perform transfers");
        }
        if (sender.getBalance().compareTo(amount) <0){
            throw new Exception("Insufficient balance to perform this transaction");

        }
    }

}
