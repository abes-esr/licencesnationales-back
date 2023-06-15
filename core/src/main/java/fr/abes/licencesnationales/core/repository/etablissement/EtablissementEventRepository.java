package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EtablissementEventRepository extends JpaRepository<EtablissementEventEntity, Integer> {
    List<EtablissementEventEntity> getAllByDateCreationEventBetweenOrderByDateCreationEvent(Date dateDebut, Date dateFin);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementCreeEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementEventEntity> getDateCreationEtab(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementModifieEventEntity and e.siren = :siren")
    List<EtablissementEventEntity> getLastModicationEtab(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementSupprimeEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementEventEntity> getDateSuppressionEtab(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementFusionneEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementEventEntity> getDateFusion(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementDiviseEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementEventEntity> getDateScission(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementFusionneEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementFusionneEventEntity> getEventFusion(@Param("siren") String siren);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementDiviseEventEntity and e.siren = :siren ORDER BY e.dateCreationEvent DESC")
    List<EtablissementDiviseEventEntity> getEventScission(@Param(("siren")) String siren);

    @Query("select e from EtablissementEventEntity e where e.siren = :siren")
    List<EtablissementEventEntity> findBySiren(String siren);

    @Query("select e from EtablissementEventEntity e where e.dateCreationEvent BETWEEN :dateDebut AND :dateFin")
    List<EtablissementEventEntity> findBetweenDates(Date dateDebut, Date dateFin);

    @Query("select e from EtablissementEventEntity e where TYPE(e)=EtablissementSupprimeEventEntity")
    List<EtablissementEventEntity> findDeleted();

    List<EtablissementEventEntity> findAllByTypeEtablissementIn(List<TypeEtablissementEntity> ids);


}
