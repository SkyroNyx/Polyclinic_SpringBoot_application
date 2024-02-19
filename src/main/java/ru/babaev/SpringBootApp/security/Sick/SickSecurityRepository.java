package ru.babaev.SpringBootApp.security.Sick;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.Sick;

import java.util.Optional;

@Repository
public interface SickSecurityRepository extends JpaRepository<Sick,Integer> {
    public Optional<Sick> getSickByEmailOrPhoneNumber(String email, String phoneNumber);
}