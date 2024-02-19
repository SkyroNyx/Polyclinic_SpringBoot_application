package ru.babaev.SpringBootApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.Doctor;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
}
