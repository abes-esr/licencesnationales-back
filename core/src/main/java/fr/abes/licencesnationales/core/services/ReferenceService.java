package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.exception.UnknownStatutException;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if (entity.isEmpty()) {
            throw new UnknownTypeEtablissementException(id.toString());
        }
        return entity.get();
    }

    public List<TypeEtablissementEntity> findTypeEtabByIds(List<Integer> ids) {
        return typeEtabRepository.findAllByIdIn(ids);
    }

    public List<String> findTypeEtabToStringByIds(List<Integer> ids) {
        List<TypeEtablissementEntity> l = typeEtabRepository.findAllByIdIn(ids);
        List<String> list = new ArrayList<>();
        for (TypeEtablissementEntity t : l) {
            list.add(t.getLibelle());
        }
        return list;
    }

    public TypeEtablissementEntity findTypeEtabByLibelle(String libelle) throws UnknownTypeEtablissementException {
        Optional<TypeEtablissementEntity> entity = typeEtabRepository.findFirstByLibelle(libelle);
        if (entity.isEmpty()) {
            throw new UnknownTypeEtablissementException(libelle);
        }
        return entity.get();
    }

    public List<TypeEtablissementEntity> findAllTypeEtab() {
        return typeEtabRepository.findAll();
    }

    public StatutEntity findStatutById(Integer id) throws UnknownStatutException {
        Optional<StatutEntity> entity = statutRepository.findById(id);
        if (entity.isEmpty()) {
            throw new UnknownStatutException(Constant.STATUT_INCONNU);
        }
        return entity.get();
    }

    public StatutEntity findStatutByLibelle(String libelle) throws UnknownStatutException {
        Optional<StatutEntity> entity = statutRepository.findFirstByLibelleStatut(libelle);
        if (entity.isEmpty()) {
            throw new UnknownStatutException(Constant.STATUT_INCONNU);
        }
        return entity.get();
    }

    public List<StatutEntity> findAllStatuts() {
        return statutRepository.findAll();
    }

}
