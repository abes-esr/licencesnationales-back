package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutRepository extends JpaRepository<StatutEntity, Integer> {
    Optional<StatutEntity> findFirstByLibelleStatut(String libelle);
}
