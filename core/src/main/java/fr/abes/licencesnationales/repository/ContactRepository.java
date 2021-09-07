package fr.abes.licencesnationales.repository;

import fr.abes.licencesnationales.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {
    Optional<ContactEntity> findContactEntityByMail(String mail);

}
