package fr.abes.licencesnationales.core.repository.contactediteur;

import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactEditeurRepository extends JpaRepository<ContactEditeurEntity, Integer> {
}
