package ru.babaev.SpringBootApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.Sick;
import java.util.Optional;

@Repository
public interface SickRepository extends JpaRepository<Sick,Integer> {

    Optional<Sick> getSickByEmail(String email);

    Optional<Sick> getSickByPhoneNumber(String phoneNumber);

}
