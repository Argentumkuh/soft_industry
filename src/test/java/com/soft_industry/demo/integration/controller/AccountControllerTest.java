package com.soft_industry.demo.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soft_industry.demo.controller.AccountController;
import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.model.OperationType;
import com.soft_industry.demo.model.StatementEntry;
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
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientRepo clientRepo;

    @MockBean
    private StatementEntryRepo statementEntryRepo;

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void init() {
        Mockito.reset(clientRepo, statementEntryRepo);
        Client client = new Client("test_integration@mail.com", "test_test");
        Mockito.when(clientRepo.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(client));
    }

    @Test
    void deposit() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Client client = new Client("test_integration@mail.com", "test_test");
        mockMvc.perform(post("http://localhost:8080/deposit")
                .content(String.valueOf(100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("http://localhost:8080/deposit").session(session)
                    .content(String.valueOf(100))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("cookie", "JSESSIONID=" + session.getId()))
                    .andExpect(status().isNotFound());
        mockMvc.perform(post("http://localhost:8080/deposit").session(session)
                        .sessionAttr("client", client.getEmail())
                        .content(String.valueOf(100))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Already added to account amount of : $" + 100));
    }

    @Test
    void withdraw() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Client client = new Client("test_integration@mail.com", "test_test");
        Mockito.when(clientRepo.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(client));
        mockMvc.perform(post("http://localhost:8080/withdraw")
                .content(String.valueOf(100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("http://localhost:8080/withdraw").session(session)
                        .content(String.valueOf(100))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isNotFound());
        mockMvc.perform(post("http://localhost:8080/withdraw").session(session)
                        .sessionAttr("client", client.getEmail())
                        .content(String.valueOf(1000))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isNotAcceptable())
                        .andExpect(content().string("You have not enough money!"));
        client.setAccount(1000L);
        mockMvc.perform(post("http://localhost:8080/withdraw").session(session)
                        .sessionAttr("client", client.getEmail())
                        .content(String.valueOf(500))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Already withdrawn from account amount of : $" + 500));
    }

    @Test
    void statement() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Client client = new Client("test_integration@mail.com", "test_test");
        StatementEntry entry1 = new StatementEntry(client, OperationType.WITHDRAW, 100L);
        StatementEntry entry2 = new StatementEntry(client, OperationType.DEPOSIT, 50L);
        StatementEntry entry3 = new StatementEntry(client, OperationType.WITHDRAW, 200L);
        Mockito.when(statementEntryRepo.findByClient_Email_OrderByDate(Mockito.anyString()))
                .thenReturn(Arrays.asList(entry1, entry2, entry3));
        mockMvc.perform(get("http://localhost:8080/statement")
                .content(String.valueOf(100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("http://localhost:8080/statement").session(session)
                        .sessionAttr("client", client.getEmail())
                        .header("cookie", "JSESSIONID=" + session.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string(objectMapper.writeValueAsString(Arrays.asList(entry1, entry2 ,entry3))));
    }

    @Test
    void balance() throws Exception {
        MockHttpSession session = new MockHttpSession();
        Client client = new Client("test_integration@mail.com", "test_test");
        mockMvc.perform(get("http://localhost:8080/balance")
                .content(String.valueOf(100))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform((get("http://localhost:8080/balance").session(session)
                .header("cookie", "JSESSIONID=" + session.getId())))
                .andExpect(status().isNotFound());
        mockMvc.perform((get("http://localhost:8080/balance").session(session)
                .sessionAttr("client", client.getEmail())
                .header("cookie", "JSESSIONID=" + session.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("Current account balance is : $" + 0));
    }
}
