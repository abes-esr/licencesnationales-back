package fr.abes.licencesnationales.core.repository.ip;


import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface IpRepository extends JpaRepository<IpEntity, Long> {

    @Query("select etab.ips from EtablissementEntity etab where etab.siren = ?1")
    Set<IpEntity> findAllBySiren(String siren);

    void deleteById(Long id);

    IpEntity getFirstById(Long id);

    @Query("select i.ip from IpEntity i")
    List<String> findAllIp();

}
