package fr.abes.licencesnationales.repository;

import fr.abes.licencesnationales.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {

    @Query(nativeQuery = true, value = "select case when exists(select * from Contact "
            + "where mail = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);

    ContactEntity findContactEntityByMail(String mail);

}
