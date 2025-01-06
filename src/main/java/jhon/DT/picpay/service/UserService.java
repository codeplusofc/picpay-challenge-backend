package jhon.DT.picpay.service;

import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void saveUser(User user){
        this.userRepository.save(user);
    }

    public User findUserById(User user){
        return this.userRepository.findUserById(user.getId()).orElseThrow(() -> new RuntimeException("User not f"));
    }

}
