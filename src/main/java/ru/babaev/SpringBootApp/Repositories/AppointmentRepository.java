package ru.babaev.SpringBootApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.Appointment;

@Component
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
}
