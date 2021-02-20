package io.github.elvisciotti.repositories;

import io.github.elvisciotti.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findOneByEmail(String email);

    void deleteByEmail(String email);
}