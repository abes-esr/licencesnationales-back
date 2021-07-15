package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactCommercialEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactCommercialEditeurRepository extends JpaRepository<ContactCommercialEditeurEntity, Long> {

}
