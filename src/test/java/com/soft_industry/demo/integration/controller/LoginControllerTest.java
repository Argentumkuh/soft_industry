package com.soft_industry.demo.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soft_industry.demo.controller.LoginController;
import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.repository.ClientRepo;
import com.soft_industry.demo.repository.StatementEntryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientRepo clientRepo;

    @MockBean
    private StatementEntryRepo statementEntryRepo;

    @BeforeEach
    void init() {
        Mockito.reset(clientRepo);
        Mockito.when(clientRepo.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(new Client("test_integration@mail.com", "test_test")));
    }

    @Test
    void login() throws Exception {
        Client client = new Client("fake", "fake");
        mockMvc.perform(post("http://localhost:8080/login")
                .content(objectMapper.writeValueAsString(client))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        client = new Client("test_integration@mail.com", "fake");
        mockMvc.perform(post("http://localhost:8080/login")
                .content(objectMapper.writeValueAsString(client))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        client = new Client("test_integration@mail.com", "test_test");
        mockMvc.perform(post("http://localhost:8080/login")
                .content(objectMapper.writeValueAsString(client))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void logout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(get("http://localhost:8080/logout"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("http://localhost:8080/logout").session(session)
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isOk());
    }
}
