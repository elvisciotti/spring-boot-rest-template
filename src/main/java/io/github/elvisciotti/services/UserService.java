package io.github.elvisciotti.services;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.UserSetting;
import io.github.elvisciotti.repositories.SettingRepo;
import io.github.elvisciotti.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private EntityManager em;

    @Autowired
    private SettingRepo settingsRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void updateUserSettings(User user, Map<String, String> newSettingsMap) {
        ArrayList newSettingsToPersist = new ArrayList<UserSetting>();
        newSettingsMap.forEach((k, v) -> {
            UserSetting us = settingsRepo.findOneByUserAndKey(user, k);
            if (us == null) {
                // add new
                newSettingsToPersist.add(new UserSetting(user, k, v));
            } else if (us.getValue() != v) {
                us.setValue(v);
            }
        });
        settingsRepo.saveAll(newSettingsToPersist);
    }

    public User createUser(String email, String passwordRaw) {
        String passwordEncrypted = passwordEncoder.encode(passwordRaw);

        User u = new User(email, passwordEncrypted);
        userRepo.save(u);
        return u;
    }

    public User findByPrincipal(Principal p) {
        return userRepo.findOneByEmail(p.getName());
    }
}