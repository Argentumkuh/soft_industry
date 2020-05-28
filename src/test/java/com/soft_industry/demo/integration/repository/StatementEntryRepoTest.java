package com.soft_industry.demo.integration.repository;

import com.soft_industry.demo.DemoApplication;
import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.model.OperationType;
import com.soft_industry.demo.model.StatementEntry;
import com.soft_industry.demo.repository.ClientRepo;
import com.soft_industry.demo.repository.StatementEntryRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@SpringJUnitConfig(DemoApplication.class)
public class StatementEntryRepoTest {

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private StatementEntryRepo statementEntryRepo;

    @Test
    @Transactional
    public void findByClient_Email_OrderByDate() {
        Client client = new Client("test_integration@mail.com", "test_test");
        clientRepo.save(client);
        StatementEntry entry1 = new StatementEntry(client, OperationType.WITHDRAW, 100L);
        StatementEntry entry2 = new StatementEntry(client, OperationType.DEPOSIT, 50L);
        StatementEntry entry3 = new StatementEntry(client, OperationType.WITHDRAW, 200L);
        statementEntryRepo.saveAll(Arrays.asList(entry1, entry2, entry3));
        List<StatementEntry> checkedEntries = statementEntryRepo.findByClient_Email_OrderByDate(client.getEmail());
        Assertions.assertEquals(entry1.getOperationType(), checkedEntries.get(0).getOperationType());
        Assertions.assertEquals(entry1.getValue(), checkedEntries.get(0).getValue());
        Assertions.assertEquals(entry2.getOperationType(), checkedEntries.get(1).getOperationType());
        Assertions.assertEquals(entry2.getValue(), checkedEntries.get(1).getValue());
        Assertions.assertEquals(entry3.getOperationType(), checkedEntries.get(2).getOperationType());
        Assertions.assertEquals(entry3.getValue(), checkedEntries.get(2).getValue());
    }
}
