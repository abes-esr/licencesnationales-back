package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserDao extends JpaRepository<AppUser, String> {

    AppUser findByUserName(String userName);
}
