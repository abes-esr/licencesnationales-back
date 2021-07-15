package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactTechniqueEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactTechniqueEditeurRepository extends JpaRepository<ContactTechniqueEditeurEntity, Long> {

}
