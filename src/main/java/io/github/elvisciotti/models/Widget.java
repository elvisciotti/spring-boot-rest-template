package io.github.elvisciotti.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(
        name = "widget",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"widgetId"})}
)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
})
public class Widget {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;

    String connector;

    // TODO rename to: description
    String name;

    @Column(nullable = false)
    String widgetId;

    String template;

    @Column(length = 64000)
    String preview;

    @Deprecated
    String contentBefore;

    @Deprecated
    String contentAfter;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private final HashMap<String, String> options = new HashMap<String, String>();

    public Widget() {
    }

    public Widget(User user, String connector, String name, String widgetId, String template) {
        this.user = user;
        this.connector = connector;
        this.name = name;
        this.widgetId = widgetId;
        this.template = template;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public String getContentBefore() {
        return contentBefore;
    }

    public void setContentBefore(String contentBefore) {
        this.contentBefore = contentBefore;
    }

    public String getContentAfter() {
        return contentAfter;
    }

    public void setContentAfter(String contentAfter) {
        this.contentAfter = contentAfter;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Map<String, String> getOptions() {
        return options;
    }
}
