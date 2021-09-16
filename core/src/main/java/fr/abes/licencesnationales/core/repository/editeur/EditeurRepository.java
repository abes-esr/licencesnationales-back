package fr.abes.licencesnationales.core.repository.editeur;

import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EditeurRepository extends JpaRepository<EditeurEntity, Integer> {

    void deleteById(Integer id);

    EditeurEntity getFirstByNomEditeur(String nom);

    EditeurEntity getFirstByIdEditeur(Long id);

}
