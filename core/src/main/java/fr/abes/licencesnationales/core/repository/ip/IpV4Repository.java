package fr.abes.licencesnationales.core.repository.ip;


import fr.abes.licencesnationales.core.entities.ip.IpV4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpV4Repository extends JpaRepository<IpV4, Integer> {

}
