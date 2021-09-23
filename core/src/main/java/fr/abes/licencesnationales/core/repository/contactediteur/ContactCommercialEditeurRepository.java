package fr.abes.licencesnationales.core.repository.contactediteur;

import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactCommercialEditeurRepository extends JpaRepository<ContactCommercialEditeurEntity, Integer> {
    Optional<ContactCommercialEditeurEntity> findByMailContact(String mail);

}
