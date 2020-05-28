package com.soft_industry.demo.repository;

import com.soft_industry.demo.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepo extends CrudRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
}
