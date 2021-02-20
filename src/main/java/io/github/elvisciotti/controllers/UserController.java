package io.github.elvisciotti.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.elvisciotti.auth.PasswordEncoder;
import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.views.UserViews;
import io.github.elvisciotti.repositories.UserRepo;
import io.github.elvisciotti.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    PasswordEncoder passEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/users/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(UserViews.IdAndEmail.class)
    public User add(
            @RequestBody final HashMap<String, String> body
    ) {
        User user = userService.createUser(
                body.get("email"),
                body.get("passwordRaw")
        );

        return user;
    }
}
