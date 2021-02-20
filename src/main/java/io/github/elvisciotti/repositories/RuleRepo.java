package io.github.elvisciotti.repositories;

import io.github.elvisciotti.models.Rule;
import io.github.elvisciotti.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepo extends JpaRepository<Rule, Integer> {
    List<Rule> findByUser(User u);

    Rule getOne(Integer integer);
}