package org.example.service;

import org.example.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String email, String pwd) {
        // Qui in futuro potresti aggiungere logiche come il controllo di ban o l'hashing della password
        return userRepository.login(email, pwd);
    }

    public boolean signIn(String nome, String cognome, String email, String pwd) {
        // Qui in futuro potresti validare che la mail contenga la chiocciola
        return userRepository.signIn(nome, cognome, email, pwd);
    }
}