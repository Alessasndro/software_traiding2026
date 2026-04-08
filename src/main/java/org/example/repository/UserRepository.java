package org.example.repository;

public interface UserRepository {
    boolean login(String email, String pwd);
    boolean signIn(String nome, String cognome, String email, String pwd);
}