package br.com.socialNetwork.rest.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
