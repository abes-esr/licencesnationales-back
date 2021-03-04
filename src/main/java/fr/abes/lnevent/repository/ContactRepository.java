package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {
    void deleteBySiren(String siren);
    ContactEntity findBySiren(String siren);


    @Query(nativeQuery = true, value = "select case when exists(select * from Contact "
            + "where siren = :siren) then 'true' else 'false' end from dual")
    Boolean existeSiren(@Param("siren") String siren);

    @Query("select e.name from EtablissementEntity e where e.siren like :x")
    public String getNomEtabBySiren(@Param("x") String siren);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ContactEntity c WHERE c.siren = :siren")
    boolean sirenExist(@Param("siren") String siren);

}
