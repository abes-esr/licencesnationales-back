package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DateEnvoiEditeurRepository extends JpaRepository<DateEnvoiEditeurEntity, Integer> {
    Optional<DateEnvoiEditeurEntity> findTopByOrderByDateEnvoiDesc();

    List<DateEnvoiEditeurEntity> findAllByOrderByDateEnvoiDesc();
}
