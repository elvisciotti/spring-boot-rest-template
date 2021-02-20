package io.github.elvisciotti.repositories;

import io.github.elvisciotti.models.User;
import io.github.elvisciotti.models.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepo extends JpaRepository<UserSetting, Integer> {
    UserSetting findOneByUserAndKey(User user, String key);

    Integer deleteByUser(User user);
}