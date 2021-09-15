package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.etablissement.PasswordEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordEventRepository extends JpaRepository<PasswordEventEntity, Integer> {
}
