package fr.abes.licencesnationales.core.repository.ip;

import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpEventRepository extends JpaRepository<IpEventEntity, Long> {
}
