package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;




@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, Long> , JpaSpecificationExecutor<EtablissementEntity> {

    void deleteBySiren(String siren);

    EtablissementEntity getFirstBySiren(String siren);

    @Query(nativeQuery = true, value = "select case when exists(select * from Etablissement "
            + "where siren = :siren) then 'true' else 'false' end from dual")
    Boolean existeSiren(@Param("siren") String siren);

    @Query("select e from EtablissementEntity e where e.contact.mail like :x")
    EtablissementEntity getUserByMail(@Param("x") String email);

    @Query(nativeQuery = true, value = "select case when exists(select * from Etablissement "
            + "where contact.mail = :mail) then 'true' else 'false' end from dual")
    Boolean existeMail(@Param("mail") String mail);

    EtablissementEntity findEtablissementEntityByIpsContains(IpEntity ip);


}
