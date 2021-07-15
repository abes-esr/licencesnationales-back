package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EditeurEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditeurRepository extends JpaRepository<EditeurEntity, Long> {

    void deleteById(Long id);

    EditeurEntity getFirstByNomEditeur(String nom);

    EditeurEntity getFirstById(String id);

    boolean findEditeurEntityByContactCommercialEditeurEntitiesContains(String mail);

    boolean findEditeurEntityByContactTechniqueEditeurEntitiesContains(String mail);
}
