package fr.abes.lnevent.repository;

import fr.abes.lnevent.dto.User;
import fr.abes.lnevent.entities.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ContactEntity, String> {

    User findByUserName(String userName);
}
