package io.github.elvisciotti.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.elvisciotti.auth.SecurityConstants;
import io.github.elvisciotti.repositories.TestFixtures;
import io.github.elvisciotti.repositories.UserRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractTest {

    protected static String token;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected TestFixtures fixtures;

    @Autowired
    protected UserRepo userRepo;

    public String authAndGetToken(MockMvc mvc) throws Exception {
        return authAndGetToken(mvc, TestFixtures.USER_1_EMAIL);
    }

    public String authAndGetToken(MockMvc mvc, String email) throws Exception {
        String body = new JSONObject()
                .put("email", email)
                .put("passwordRaw", TestFixtures.USER_PASSWORD_RAW)
                .toString();

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/login");
        post
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MockHttpServletResponse response = mvc.perform(post).andReturn().getResponse();
        String contentAsString = response.getContentAsString();
        if (response.getStatus() != 200) {
            throw new RuntimeException("cannot auth: " + contentAsString);
        }
        HashMap responseJson = new ObjectMapper()
                .readValue(
                        contentAsString,
                        HashMap.class
                );

        return responseJson.get(SecurityConstants.RESPONSE_FIELD_JWT).toString();
    }

    MockHttpServletRequestBuilder GET(String uri, String token) {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get(uri);
        if (token != null) {
            mockHttpServletRequestBuilder.header(SecurityConstants.HEADER_NAME, "Bearer " + token);
        }

        return mockHttpServletRequestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                ;
    }

    MockHttpServletRequestBuilder DELETE(String uri, String token) {
        assertThat(token).isNotEmpty();

        return MockMvcRequestBuilders.delete(uri)
                .header(SecurityConstants.HEADER_NAME, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                ;
    }

    MockHttpServletRequestBuilder POST(String uri, Object body, String token) {
        assertThat(token).isNotEmpty();

        return MockMvcRequestBuilders.post(uri)
                .header("Authorization", "Bearer " + token)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                ;
    }

    MockHttpServletRequestBuilder PATCH(String uri, Object body, String token) {
        assertThat(token).isNotEmpty();

        return MockMvcRequestBuilders.patch(uri)
                .header(SecurityConstants.HEADER_NAME, "Bearer " + token)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                ;
    }


}