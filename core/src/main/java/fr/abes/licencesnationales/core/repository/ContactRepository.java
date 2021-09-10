package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {
    Optional<ContactEntity> findContactEntityByMail(String mail);

}
