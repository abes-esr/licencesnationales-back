package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutRepository extends JpaRepository<StatutEntity, Integer> {
}
