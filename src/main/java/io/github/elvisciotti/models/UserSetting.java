package io.github.elvisciotti.models;

import javax.persistence.*;

@Entity
@Table(name = "user_setting")
public class UserSetting {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "setting_key")
    String key;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    String value;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public UserSetting() {
    }

    public UserSetting(User user, String key, String value) {
        this.user = user;
        this.key = key;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
