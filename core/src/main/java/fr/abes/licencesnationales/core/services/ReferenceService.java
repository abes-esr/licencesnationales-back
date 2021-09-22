package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.exception.UnknownStatutException;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ReferenceService {
    private final TypeEtablissementRepository typeEtabRepository;
    private final StatutRepository statutRepository;

    public ReferenceService(TypeEtablissementRepository typeEtabRepository, StatutRepository statutRepository) {
        this.typeEtabRepository = typeEtabRepository;
        this.statutRepository = statutRepository;
    }

    public TypeEtablissementEntity findTypeEtabById(Integer id) throws UnknownTypeEtablissementException {
        Optional<TypeEtablissementEntity> entity = typeEtabRepository.findById(id);
        if (!entity.isPresent()) {
            throw new UnknownTypeEtablissementException("Type d'établissement inconnu");
        }
        return entity.get();
    }

    public TypeEtablissementEntity findTypeEtabByLibelle(String libelle) throws UnknownTypeEtablissementException {
        Optional<TypeEtablissementEntity> entity = typeEtabRepository.findFirstByLibelle(libelle);
        if (!entity.isPresent()) {
            throw new UnknownTypeEtablissementException("Type d'établissement inconnu");
        }
        return entity.get();
    }

    public List<TypeEtablissementEntity> findAllTypeEtab() {
        return typeEtabRepository.findAll();
    }

    public StatutEntity findStatutById(Integer id) throws UnknownStatutException {
        Optional<StatutEntity> entity = statutRepository.findById(id);
        if (!entity.isPresent()) {
            throw new UnknownStatutException("Statut inconnu");
        }
        return entity.get();
    }

    public StatutEntity findStatutByLibelle(String libelle) throws UnknownStatutException {
        Optional<StatutEntity> entity = statutRepository.findFirstByLibelleStatut(libelle);
        if (!entity.isPresent()) {
            throw new UnknownStatutException("Statut inconnu");
        }
        return entity.get();
    }

    public List<StatutEntity> findAllStatuts() {
        return statutRepository.findAll();
    }

}
