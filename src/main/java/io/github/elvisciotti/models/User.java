package io.github.elvisciotti.models;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.elvisciotti.models.views.UserViews;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "user")
public class User {

    @Column(name = "email", length = 64, nullable = false, unique = true)
    String email;

    @Column(name = "password_hash", length = 128, nullable = false)
    String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<UserSetting> settings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Widget> widgets = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Rule> rules = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public User() {
    }

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @JsonView(UserViews.IdAndEmail.class)
    public Integer getId() {
        return id;
    }

    @JsonView(UserViews.IdAndEmail.class)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<UserSetting> getSettings() {
        return settings;
    }

    @JsonView(UserViews.Settings.class)
    public Map<String, String> getSettingsMap() {
        HashMap<String, String> ret = new HashMap<>();
        for (UserSetting setting : getSettings()) {
            ret.put(setting.getKey(), setting.getValue());
        }
        return ret;
    }


}
