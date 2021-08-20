package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.ContactTechniqueEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactTechniqueEditeurRepository extends JpaRepository<ContactTechniqueEditeurEntity, Long> {

    @Query(nativeQuery = true, value = "select case when exists(select * from ContactTechniqueEditeur "
            + "where mailContactTechnique = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);

}
