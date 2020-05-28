package com.soft_industry.demo.controller;

import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.repository.ClientRepo;
import com.soft_industry.demo.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class LoginController {

    @NonNull
    private ClientRepo clientRepo;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Client client, HttpSession session) {
        return clientRepo.findByEmail(client.getEmail()).map(savedClient -> {
            if (savedClient.getPassword().equals(client.getPassword())) {
                session.setAttribute("client", client.getEmail());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                session.invalidate();
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }).orElseGet(() -> {
            session.invalidate();
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        });
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpSession session, HttpServletRequest request) {
        if (Util.checkSession(request)) {
            session.invalidate();
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
