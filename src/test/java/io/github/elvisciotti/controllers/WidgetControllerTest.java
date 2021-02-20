package io.github.elvisciotti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.elvisciotti.models.Widget;
import io.github.elvisciotti.repositories.WidgetRepo;
import io.github.elvisciotti.repositories.TestFixtures;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WidgetControllerTest extends AbstractTest {

    public static final String BASE_URI = "/widgets";

    boolean setupDone;

    @BeforeEach
    void setUp() throws Exception {
        if (setupDone) {
            return;
        }
        userRepo.deleteAll();
        fixtures.userGetOrCreate(TestFixtures.USER_1_EMAIL);
        token = authAndGetToken(mvc, TestFixtures.USER_1_EMAIL);
        setupDone = true;
    }

    @Autowired
    protected WidgetRepo widgetRepo;

    @Test
    public void add() throws Exception {
        fixtures.userGetOrCreate(TestFixtures.USER_1_EMAIL);
        token = authAndGetToken(mvc, TestFixtures.USER_1_EMAIL);

        JSONObject body = new JSONObject()
                .put("connector", "todoist")
                .put("name", "name")
                .put("widgetId", "widgetId")
                .put("template", "template")
                .put("contentBefore", "contentBefore")
                .put("contentAfter", "contentAfter")
                .put("preview", "preview")
                .put("options", new JSONObject()
                        .put("listId", "listId")
                        .put("filter", "filter")
                );
        String response = mvc.perform(POST(BASE_URI, body, token))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        assertThat(new ObjectMapper().readValue(response, HashMap.class).get("id")).isNotNull();
    }


    @Test
    public void getOne() throws Exception {
        Widget widget = fixtures.createAndFlushWidgetEntity("widget-id");

        mvc.perform(GET("/widgets/" + widget.getId(), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widget.getId()))
                .andExpect(jsonPath("$.connector").value("todoist"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.widgetId").value("widget-id"))
                .andExpect(jsonPath("$.template").value("template"))
                .andExpect(jsonPath("$.preview").value("preview"))
                .andExpect(jsonPath("$.options.listId").value("listId"))
                .andExpect(jsonPath("$.options.filter").value("filter"))
        ;
    }

    @Test
    @Transactional
    public void patch() throws Exception {
        Widget widget = fixtures.createAndFlushWidgetEntity("widget-id");

        JSONObject bodyPatched = new JSONObject()
                .put("template", "templateChanged")
                .put("contentAfter", JSONObject.NULL)
                .put("preview", "previewChanged")
                .put("options", new JSONObject()
                        .put("listId", "listIdChanged")
                );

        mvc.perform(PATCH("/widgets/" + widget.getId(), bodyPatched, token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connector").value("todoist")) // unchanged
                .andExpect(jsonPath("$.template").value("templateChanged")) // changed
                .andExpect(jsonPath("$.preview").value("previewChanged")) // changed
                .andExpect(jsonPath("$.options.filter").value("filter")) // option unchanged
                .andExpect(jsonPath("$.options.listId").value("listIdChanged")) // options changed
        ;
        Assertions.assertThat(widgetRepo.getOne(widget.getId()).getTemplate()).isEqualTo("templateChanged");
    }


    @Test
    public void list() throws Exception {
        Widget widget = fixtures.createAndFlushWidgetEntity("widget-id");

        mvc.perform(GET("/widgets", token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(widget.getId()))
                .andExpect(jsonPath("$[0].options.listId").value("listId"))
        ;
    }

    @Test
    public void listFilters() throws Exception {
        fixtures.createAndFlushWidgetEntity("widget-id");

        mvc.perform(GET("/widgets?connector=" + "todoist", token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].connector").value("todoist"));

        mvc.perform(GET("/widgets?connector=connectorNotExisting", token))

                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void delete() throws Exception {
        Widget widget1 = fixtures.createAndFlushWidgetEntity("widget1-id");
        assertThat(widgetRepo.existsById(widget1.getId())).isTrue();

        Widget widget2 = fixtures.createAndFlushWidgetEntity("widget2-id");
        assertThat(widgetRepo.existsById(widget2.getId())).isTrue();

        mvc.perform(DELETE("/widgets/" + widget1.getId(), token))

                .andExpect(status().isNoContent())
        ;

        assertThat(widgetRepo.existsById(widget1.getId())).isFalse();
        assertThat(widgetRepo.existsById(widget2.getId())).isTrue();
    }
}