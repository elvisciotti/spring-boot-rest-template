package io.github.elvisciotti.controllers.converters;


import io.github.elvisciotti.models.User;
import io.github.elvisciotti.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Deprecated
public class JwtTokenToUserConverter implements Converter<String, User> {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User convert(String token) {

        Integer userId = Integer.valueOf(token.replace("token", ""));
        return userRepo.getOne(userId);
    }
}
