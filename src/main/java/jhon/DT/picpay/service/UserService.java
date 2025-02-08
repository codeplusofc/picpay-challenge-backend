package jhon.DT.picpay.service;

import jhon.DT.picpay.exceptions.InsufficientFundsException;
import jhon.DT.picpay.exceptions.UserNotFoundException;
import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.model.user.UserType;
import jhon.DT.picpay.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        if (user.getId() == null) { // Apenas se for um novo usuário
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }
            if (userRepository.existsByDocument(user.getDocument())) {
                throw new IllegalArgumentException("Document already registered");
            }
        }
        return this.userRepository.save(user);
    }

    public User findUserById(Long id) throws UserNotFoundException {
        return this.userRepository.findUserById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id));
    }

    public void validateSenderBalance(User sender, BigDecimal amount) throws InsufficientFundsException {
        if (sender.getBalance().compareTo(amount)<0){
            throw new InsufficientFundsException("Insufficient balance to perform this transaction");
        }
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception{
        if (sender.getUserType() == UserType.MERCHANT){
            throw new IllegalArgumentException("This type of user cannot perform transfers");
        }
        validateSenderBalance(sender, amount);
    }

}
