package io.github.elvisciotti.repositories;

import io.github.elvisciotti.models.Rule;
import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.Widget;
import io.github.elvisciotti.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
public class TestFixtures {

    public static final String USER_1_EMAIL = "user1@email";

    public static final String USER_PASSWORD_RAW = "p";

    @Autowired
    UserRepo userRepo;

    @Autowired
    private SettingRepo settingsRepo;

    @Autowired
    private UserService userService;


    @Autowired
    protected RuleRepo ruleRepo;

    @Autowired
    protected WidgetRepo widgetRepo;

    @Transactional
    public User userGetOrCreate(String email) {
        User byEmail = userRepo.findOneByEmail(email);
        if (byEmail != null) {
            return byEmail;
        }

        return userService.createUser(email, USER_PASSWORD_RAW);
    }

    @Transactional
    public TestFixtures userDeleteCascade(String email) {
        userRepo.deleteByEmail(email);

        return this;
    }

    @Transactional
    public void userSettingsSet(User user, Map<String, String> newSettingsMap) {
        settingsRepo.deleteByUser(user);

        if (newSettingsMap == null) {
            return;
        }

        userService.updateUserSettings(user, newSettingsMap);
    }

    public Rule createAndFlushRuleEntity(String name) {
        Rule rule = new Rule(
                userGetOrCreate(TestFixtures.USER_1_EMAIL),
                name, "trigger", "content", "output"
        );
        ruleRepo.saveAndFlush(rule);

        return rule;
    }

    public Widget createAndFlushWidgetEntity(String widgetId) {
        Widget widget = new Widget(
                userGetOrCreate(TestFixtures.USER_1_EMAIL),
                "todoist",
                "name",
                widgetId,
                "template"
        );
        widget.setPreview("preview");
        widget.getOptions().put("listId", "listId");
        widget.getOptions().put("filter", "filter");
        widgetRepo.saveAndFlush(widget);

        return widget;
    }


}