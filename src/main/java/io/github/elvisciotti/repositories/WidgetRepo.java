package io.github.elvisciotti.repositories;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.Widget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// https://www.baeldung.com/spring-response-entity
public interface WidgetRepo extends JpaRepository<Widget, Integer> {
    List<Widget> findByUser(User u);

    List<Widget> findByUserAndConnector(User u, String connector);

    Widget getOne(Integer u);
}