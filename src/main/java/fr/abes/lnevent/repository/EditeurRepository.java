package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EditeurEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EditeurRepository extends JpaRepository<EditeurEntity, Long> {

    void deleteById(Long id);

    EditeurEntity getFirstByNomEditeur(String nom);

    EditeurEntity getFirstById(String id);

    EditeurEntity findEditeurEntityByContactCommercialEditeurEntitiesContains(String mail);

    EditeurEntity findEditeurEntityByContactTechniqueEditeurEntitiesContains(String mail);

    @Query(nativeQuery = true, value = "select case when exists(select * from EditeurEntity "
            + "where contactCommercialEditeurEntities.mailContactCommercial = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);
}
