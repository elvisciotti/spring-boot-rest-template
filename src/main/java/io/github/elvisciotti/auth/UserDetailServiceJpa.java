package io.github.elvisciotti.auth;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailServiceJpa implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User oneByEmail = userRepo.findOneByEmail(s);
        if (oneByEmail == null) {
            throw new UsernameNotFoundException("Cannot find user");
        }

        // more info at https://docs.spring.io/spring-security/site/docs/4.2.12.RELEASE/apidocs/org/springframework/security/core/userdetails/User.html
        return new org.springframework.security.core.userdetails.User(
                oneByEmail.getEmail(),
                oneByEmail.getPasswordHash(),
                Collections.emptyList()
        );
    }
}
