package fr.abes.licencesnationales.core.repository.ip;


import fr.abes.licencesnationales.core.entities.ip.IpV6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpV6Repository extends JpaRepository<IpV6, Integer> {

}
