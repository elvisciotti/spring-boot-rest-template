package io.github.elvisciotti.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "rule")
public class Rule {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @Column(name = "`name`")
    String name;

    @Column(name = "`trigger`")
    String trigger;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(name = "output")
    String output;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Rule() {
    }

    public Rule(User user, String name, String trigger, String content, String output) {
        this.user = user;
        this.name = name;
        this.trigger = trigger;
        this.content = content;
        this.output = output;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
