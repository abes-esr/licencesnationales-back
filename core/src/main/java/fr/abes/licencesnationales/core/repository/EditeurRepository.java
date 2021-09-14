package fr.abes.licencesnationales.core.repository;

import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EditeurRepository extends JpaRepository<EditeurEntity, Long> {

    void deleteById(Long id);

    EditeurEntity getFirstByNomEditeur(String nom);

    EditeurEntity getFirstByIdEditeur(Long id);

}
