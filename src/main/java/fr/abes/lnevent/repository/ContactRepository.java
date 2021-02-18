package fr.abes.lnevent.repository;

import fr.abes.lnevent.entities.ContactRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactRow, String> {
    void deleteBySiren(String siren);
}
