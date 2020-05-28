package com.soft_industry.demo.integration.repository;

import com.soft_industry.demo.DemoApplication;
import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.repository.ClientRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;

@SpringJUnitConfig(DemoApplication.class)
public class ClientRepoTest {

    @Autowired
    private ClientRepo clientRepo;

    @Test
    @Transactional
    public void findByEmail() {
        Client client = new Client("test_integration@mail.com", "test_test");
        clientRepo.save(client);
        Client checkedClient = clientRepo.findByEmail("test_integration@mail.com").orElseThrow();
        Assertions.assertEquals(client.getEmail(), checkedClient.getEmail());
        Assertions.assertEquals(client.getPassword(), checkedClient.getPassword());
    }
}
