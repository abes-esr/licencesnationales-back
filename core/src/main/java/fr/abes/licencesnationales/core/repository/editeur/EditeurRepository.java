package fr.abes.licencesnationales.core.repository.editeur;

import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EditeurRepository extends JpaRepository<EditeurEntity, Integer> {

    void deleteById(Integer id);

    Optional<EditeurEntity> getFirstById(Integer id);

}
