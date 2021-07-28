package fr.abes.licencesnationales.services;

import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtablissementService {
    @Autowired
    private EtablissementRepository dao;

    public EtablissementEntity getFirstBySiren(String siren) {
        return dao.getFirstBySiren(siren).orElseThrow(() -> new UnknownEtablissementException());
    }

    public void save(EtablissementEntity entity) {
        dao.save(entity);
    }

    public void deleteBySiren(String siren) {
        dao.deleteBySiren(siren);
    }

    public boolean existeSiren(String siren) {
        return dao.existeSiren(siren);
    }

    public List<EtablissementEntity> findAll() {
        return dao.findAll();
    }

    public EtablissementEntity getUserByMail(String mail) {
        return dao.getUserByMail(mail).orElseThrow(UnknownEtablissementException::new);
    }
}
