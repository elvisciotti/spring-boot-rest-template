package io.github.elvisciotti.controllers;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.repositories.UserRepo;
import io.github.elvisciotti.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserSettingsController {

    @Autowired
    private UserRepo userRepoJpa;

    @Autowired
    private UserService userService;

    @GetMapping("/settings")
    public Map<String, String> list(
            Principal principal
    ) {
        User user = userService.findByPrincipal(principal);

        return user.getSettingsMap();
    }

    @PatchMapping("/settings")
    public Map<String, String> patch(
            @RequestBody final HashMap<String, String> newSettingsMap,
            Principal principal
    ) {
        User user = userService.findByPrincipal(principal);
        userService.updateUserSettings(user, newSettingsMap);

        return user.getSettingsMap();
    }

}
