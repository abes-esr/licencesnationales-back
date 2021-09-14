package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, Long> , JpaSpecificationExecutor<EtablissementEntity> {

    void deleteBySiren(String siren);

    Optional<EtablissementEntity> getFirstBySiren(String siren);

    @Query(nativeQuery = true, value = "select case when exists(select * from Etablissement "
            + "where siren = :siren) then 'true' else 'false' end from dual")
    Boolean existeSiren(@Param("siren") String siren);

    @Query("select e from EtablissementEntity e where e.contact.mail like :x")
    Optional<EtablissementEntity> getUserByMail(@Param("x") String email);

    @Query(nativeQuery = true, value = "select case when exists(select * from Etablissement "
            + "where contact.mail = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);

    EtablissementEntity findEtablissementEntityByIpsContains(IpEntity ip);


}
