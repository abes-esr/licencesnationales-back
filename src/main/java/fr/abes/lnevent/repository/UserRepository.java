package fr.abes.lnevent.repository;

import fr.abes.lnevent.dto.User;
import fr.abes.lnevent.entities.ContactRow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ContactRow, String> {

    User findByUserName(String userName);
}
