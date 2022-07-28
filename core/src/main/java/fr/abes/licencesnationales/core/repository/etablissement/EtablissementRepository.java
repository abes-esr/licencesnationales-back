package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface EtablissementRepository extends JpaRepository<EtablissementEntity, Integer> {

    void deleteBySiren(String siren);

    Optional<EtablissementEntity> getFirstBySiren(String siren);

    boolean existsBySiren(@Param("siren") String siren);

    Optional<EtablissementEntity> getEtablissementEntityByContact_MailContains(@Param("x") String email);

    List<EtablissementEntity> getEtablissementEntityByIps_Empty();

    List<EtablissementEntity> findAllBySirenIn(List<String> ids);

    @EntityGraph(attributePaths = {"ips"})
    Set<EtablissementEntity> findAllByValideAndTypeEtablissementIn(boolean valide, List<TypeEtablissementEntity> ids);

}
