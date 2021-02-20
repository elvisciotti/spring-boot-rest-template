package io.github.elvisciotti.controllers;

import io.github.elvisciotti.models.Rule;
import io.github.elvisciotti.models.User;
import io.github.elvisciotti.repositories.RuleRepo;
import io.github.elvisciotti.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RuleController {

    @Autowired
    private UserService userService;

    @Autowired
    private RuleRepo ruleRepo;


    @GetMapping("/rules")
    public List<Rule> list(
            Principal principal
    ) {
        User user = userService.findByPrincipal(principal);

        return ruleRepo.findByUser(user);
    }

    @GetMapping("/rules/{id}")
    public Rule getOne(
            @PathVariable Integer id,
            Principal principal
    ) {
        return getByIdAndCheckOwnership(id, principal);
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    public Rule add(
            @RequestBody final HashMap<String, String> body,
            Principal principal
    ) {
        User user = userService.findByPrincipal(principal);

        Rule rule = new Rule(
                user,
                body.get("name"),
                body.get("trigger"),
                body.get("content"),
                body.get("output")
        );

        ruleRepo.saveAndFlush(rule);

        return rule;
    }

    @PatchMapping("/rules/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Rule patch(
            @RequestBody Map<String, Object> fields,
            @PathVariable Integer id,
            Principal principal
    ) {
        Rule rule = getByIdAndCheckOwnership(id, principal);

        if (fields.get("content") != null) {
            rule.setContent((String) fields.get("content"));
        }
        if (fields.get("name") != null) {
            rule.setName((String) fields.get("name"));
        }
        if (fields.get("output") != null) {
            rule.setOutput((String) fields.get("output"));
        }
        if (fields.get("trigger") != null) {
            rule.setTrigger((String) fields.get("trigger"));
        }

        ruleRepo.save(rule);

        return rule;
    }


    @DeleteMapping("/rules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Integer id,
            Principal principal
    ) {
        Rule rule = getByIdAndCheckOwnership(id, principal);
        ruleRepo.delete(rule);
    }

    private Rule getByIdAndCheckOwnership(Integer id, Principal principal) {
        User loggedUser = userService.findByPrincipal(principal);
        Rule rule = ruleRepo.getOne(id);
        if (!rule.getUser().equals(loggedUser)) {
            throw new RuntimeException("Not belonging to user");
        }
        return rule;
    }

}
