package fr.abes.licencesnationales.core.repository.ip;


import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface IpRepository extends JpaRepository<IpEntity, Integer> {

    @Query("select etab.ips from EtablissementEntity etab where etab.siren = :siren")
    Set<IpEntity> findAllBySiren(@Param("siren") String siren);

    void deleteById(Integer id);

    IpEntity getFirstById(Integer id);

    @Query("select i.ip from IpEntity i")
    List<String> findAllIp();

}