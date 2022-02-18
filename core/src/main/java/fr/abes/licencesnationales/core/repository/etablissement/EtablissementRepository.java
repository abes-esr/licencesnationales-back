package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, Integer> , JpaSpecificationExecutor<EtablissementEntity> {

    void deleteBySiren(String siren);

    Optional<EtablissementEntity> getFirstBySiren(String siren);

    boolean existsBySiren(@Param("siren") String siren);

    Optional<EtablissementEntity> getEtablissementEntityByContact_MailContains(@Param("x") String email);

    List<EtablissementEntity> getEtablissementEntityByIps_Empty();

    List<EtablissementEntity> findAllBySirenIn(List<String> ids);

    List<EtablissementEntity> findAllByValideAndTypeEtablissementIn(boolean valide, List<TypeEtablissementEntity> ids);

}
