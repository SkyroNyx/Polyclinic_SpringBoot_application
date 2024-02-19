package ru.babaev.SpringBootApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.babaev.SpringBootApp.Models.AttachedDocument;

@Repository
public interface AttachedDocumentRepository extends JpaRepository<AttachedDocument, Integer> {

    AttachedDocument getAttachedDocumentById(int id);
}
