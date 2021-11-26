package fr.abes.licencesnationales.core.repository.ip;

import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpEventRepository extends JpaRepository<IpEventEntity, Long> {

    @Query("select i from IpEventEntity i where TYPE(i)=IpSupprimeeEventEntity and i.siren=:siren")
    List<IpEventEntity> getIpSupprimeBySiren(@Param("siren") String siren);
}
