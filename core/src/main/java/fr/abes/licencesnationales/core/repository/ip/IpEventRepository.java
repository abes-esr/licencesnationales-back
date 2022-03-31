package fr.abes.licencesnationales.core.repository.ip;

import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IpEventRepository extends JpaRepository<IpEventEntity, Long> {

    @Query("select i from IpEventEntity i where TYPE(i)=IpSupprimeeEventEntity and i.siren=:siren")
    List<IpEventEntity> getIpSupprimeBySiren(@Param("siren") String siren);

    @Query("select i from IpEventEntity i where TYPE(i)=IpValideeEventEntity and i.ip = :ip")
    Optional<IpEventEntity> getDateValidation(String ip);

    @Query("select i from IpEventEntity i where TYPE(i)=IpSupprimeeEventEntity and i.ip = :ip")
    Optional<IpEventEntity> getDateSuppression(String ip);

    @Query("select e from IpEventEntity e where e.siren = :siren")
    List<IpEventEntity> findBySiren(String siren);

    @Query("select e from IpEventEntity e where e.dateCreationEvent BETWEEN :dateDebut AND :dateFin")
    List<IpEventEntity> findBetweenDates(Date dateDebut, Date dateFin);
}
