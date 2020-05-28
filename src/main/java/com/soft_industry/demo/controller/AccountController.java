package com.soft_industry.demo.controller;

import com.soft_industry.demo.model.OperationType;
import com.soft_industry.demo.model.StatementEntry;
import com.soft_industry.demo.repository.ClientRepo;
import com.soft_industry.demo.repository.StatementEntryRepo;
import com.soft_industry.demo.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequiredArgsConstructor
public class AccountController {

    @NonNull
    private ClientRepo clientRepo;

    @NonNull
    private StatementEntryRepo statementEntryRepo;

    @PostMapping("/deposit")
    @Transactional
    public ResponseEntity deposit(@RequestBody long depositAmount, HttpServletRequest request) {
        if (Util.checkSession(request)) {
            return clientRepo.findByEmail(getClientEmailFromSession()).map(client -> {
                client.setAccount(client.getAccount() + depositAmount);
                statementEntryRepo.save(new StatementEntry(client, OperationType.DEPOSIT, depositAmount));
                return new ResponseEntity<>("Already added to account amount of : $" + depositAmount, HttpStatus.OK);
            }).orElseGet(() -> new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/withdraw")
    @Transactional
    public ResponseEntity withdraw(@RequestBody long withdrawAmount, HttpServletRequest request) {
        if (Util.checkSession(request))  {
            return clientRepo.findByEmail(getClientEmailFromSession()).map(client -> {
                long totalAmount = client.getAccount();
                if (withdrawAmount > totalAmount) {
                    return new ResponseEntity<>("You have not enough money!", HttpStatus.NOT_ACCEPTABLE);
                } else {
                    client.setAccount(client.getAccount() - withdrawAmount);
                    statementEntryRepo.save(new StatementEntry(client, OperationType.WITHDRAW, withdrawAmount));
                    return new ResponseEntity<>("Already withdrawn from account amount of : $" + withdrawAmount,
                            HttpStatus.OK);
                }
            }).orElseGet(() -> new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/statement")
    public ResponseEntity statement(HttpServletRequest request) {
        if (Util.checkSession(request)) {
            return new ResponseEntity<>(statementEntryRepo.findByClient_Email_OrderByDate(getClientEmailFromSession()), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity balance(HttpServletRequest request) {
        if (Util.checkSession(request)) {
            return clientRepo.findByEmail(getClientEmailFromSession())
                    .map(client -> new ResponseEntity<>("Current account balance is : $" + client.getAccount(), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }

    private String getClientEmailFromSession() {
        return (String) RequestContextHolder.currentRequestAttributes().getAttribute("client", RequestAttributes.SCOPE_SESSION);
    }
}
