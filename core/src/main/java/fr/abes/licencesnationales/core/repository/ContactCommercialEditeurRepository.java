package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.ContactCommercialEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactCommercialEditeurRepository extends JpaRepository<ContactCommercialEditeurEntity, Long> {

    @Query(nativeQuery = true, value = "select case when exists(select * from ContactCommercialEditeur "
            + "where mailContactCommercial = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);

}
