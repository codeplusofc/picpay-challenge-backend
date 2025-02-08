package jhon.DT.picpay.controller;

import jhon.DT.picpay.model.user.User;
import jhon.DT.picpay.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user){
        var newUser = userService.saveUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers(){
        var users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
