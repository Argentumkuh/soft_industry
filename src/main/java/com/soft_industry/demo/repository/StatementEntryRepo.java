package com.soft_industry.demo.repository;

import com.soft_industry.demo.model.StatementEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatementEntryRepo extends CrudRepository<StatementEntry, Long> {
    List<StatementEntry> findByClient_Email_OrderByDate(String email);
}
