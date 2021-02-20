package io.github.elvisciotti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.elvisciotti.models.Rule;
import io.github.elvisciotti.repositories.RuleRepo;
import io.github.elvisciotti.repositories.TestFixtures;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RuleControllerTest extends AbstractTest {

    public static final String BASE_URI = "/rules/";

    boolean done;

    @BeforeEach
    void setUp() throws Exception {
        if (done) {
            return;
        }
        userRepo.deleteAll();
        fixtures.userGetOrCreate(TestFixtures.USER_1_EMAIL);
        token = authAndGetToken(mvc, TestFixtures.USER_1_EMAIL);
        done = true;
    }

    @Autowired
    EntityManager em;

    @Autowired
    protected RuleRepo ruleRepo;

    @Test
    public void add() throws Exception {
        MockHttpServletRequestBuilder request = POST(
                "/rules",
                new JSONObject()
                        .put("name", "rule1")
                        .put("trigger", "daily")
                        .put("content", "content")
                        .put("output", "elvis@gmail")
                        .toString(),
                token
        );

        String response = mvc.perform(request)

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        assertThat(new ObjectMapper().readValue(response, HashMap.class).get("id")).isNotNull();
    }

    @Test
    public void getOne() throws Exception {
        Rule rule = fixtures.createAndFlushRuleEntity("name");

        mvc.perform(GET("/rules/" + rule.getId(), token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rule.getId()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.trigger").value("trigger"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.output").value("output"))
        ;
    }

    @Test
    @Transactional
    public void patch() throws Exception {
        Rule rule = fixtures.createAndFlushRuleEntity("name");

        JSONObject bodyPatched = new JSONObject()
                .put("content", "contentChanged");

        mvc.perform(PATCH("/rules/" + rule.getId(), bodyPatched, token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("contentChanged"))
        ;

        assertThat(ruleRepo.getOne(rule.getId()).getContent()).isEqualTo("contentChanged");
    }

    @Test
    public void list() throws Exception {
        Rule rule = fixtures.createAndFlushRuleEntity("name");

        mvc.perform(GET("/rules", token))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rule.getId()))
                .andExpect(jsonPath("$[0].name").value("name"))
        ;
    }

    @Test
    public void delete() throws Exception {
        Rule rule1 = fixtures.createAndFlushRuleEntity("name1");
        assertThat(ruleRepo.existsById(rule1.getId())).isTrue();

        Rule rule2 = fixtures.createAndFlushRuleEntity("name2");
        assertThat(ruleRepo.existsById(rule2.getId())).isTrue();

        mvc.perform(DELETE(BASE_URI + rule1.getId(), token))
                .andExpect(status().isNoContent())
        ;

        assertThat(ruleRepo.existsById(rule1.getId())).isFalse();
        assertThat(ruleRepo.existsById(rule2.getId())).isTrue();
    }
}
