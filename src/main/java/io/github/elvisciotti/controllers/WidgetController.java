package io.github.elvisciotti.controllers;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.Widget;
import io.github.elvisciotti.repositories.WidgetRepo;
import io.github.elvisciotti.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class WidgetController {

    @Autowired
    private UserService userService;

    @Autowired
    private WidgetRepo widgetRepo;

    //TODO

    @PostMapping("/widgets")
    @ResponseStatus(HttpStatus.CREATED)
    public Widget add(
            @RequestBody Widget widget,
            Principal principal
    ) {
        User user = userService.findByPrincipal(principal);

        widget.setUser(user);
        widgetRepo.saveAndFlush(widget);

        return widget;
    }

    @PatchMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Widget patch(
            @RequestBody Map<String, Object> fields,
            @PathVariable Integer id,
            Principal principal
    ) {
        Widget widget = getByIdAndCheckOwnership(id, principal);

        if (fields.containsKey("connector")) {
            widget.setConnector((String) fields.get("connector"));
        }
        if (fields.containsKey("name")) {
            widget.setName((String) fields.get("name"));
        }
        if (fields.containsKey("widgetId")) {
            widget.setWidgetId((String) fields.get("widgetId"));
        }
        if (fields.containsKey("template")) {
            widget.setTemplate((String) fields.get("template"));
        }
        if (fields.containsKey("contentBefore")) {
            widget.setContentBefore((String) fields.get("contentBefore"));
        }
        if (fields.containsKey("contentAfter")) {
            widget.setContentAfter((String) fields.get("contentAfter"));
        }

        if (fields.get("options") != null) {
            LinkedHashMap<String, String> options = (LinkedHashMap<String, String>) fields.get("options");
            options.forEach((k, v) -> {
                widget.getOptions().put(k, v);
            });
        }

        widgetRepo.save(widget);

        return widget;
    }


    @GetMapping("/widgets")
    public List<Widget> list(
            Principal principal,
            @RequestParam(required = false) String connector
    ) {
        User user = userService.findByPrincipal(principal);
        if (connector != null) {
            return widgetRepo.findByUserAndConnector(user, connector);
        }
        return widgetRepo.findByUser(user);
    }

    @GetMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Widget getOne(
            @PathVariable Integer id,
            Principal principal
    ) {
        return getByIdAndCheckOwnership(id, principal);
    }


    @DeleteMapping("/widgets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(
            @PathVariable Integer id,
            Principal principal
    ) {
        Widget widget = getByIdAndCheckOwnership(id, principal);

        widgetRepo.delete(widget);
    }


    private Widget getByIdAndCheckOwnership(Integer id, Principal principal) {
        User loggedUser = userService.findByPrincipal(principal);
        Widget widget = widgetRepo.getOne(id);
        if (!widget.getUser().equals(loggedUser)) {
            throw new RuntimeException("Not belonging to user");
        }
        return widget;
    }
}
