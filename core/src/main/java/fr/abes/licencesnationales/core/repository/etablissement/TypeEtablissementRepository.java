package fr.abes.licencesnationales.core.repository.etablissement;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeEtablissementRepository extends JpaRepository<TypeEtablissementEntity, Integer> {
    Optional<TypeEtablissementEntity> findFirstByLibelle(String libelle);
}
