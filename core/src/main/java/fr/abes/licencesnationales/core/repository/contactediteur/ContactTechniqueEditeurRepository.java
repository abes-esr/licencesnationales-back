package fr.abes.licencesnationales.core.repository.contactediteur;

import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactTechniqueEditeurRepository extends JpaRepository<ContactTechniqueEditeurEntity, Integer> {
    Optional<ContactTechniqueEditeurEntity> findByMailContact(String mail);
}
