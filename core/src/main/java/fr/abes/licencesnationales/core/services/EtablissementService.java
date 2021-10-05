package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class EtablissementService {
    @Autowired
    private EtablissementRepository etablissementDao;

    @Autowired
    private ContactRepository contactEtablissementDao;



    @Autowired
    private StatutRepository statutRepository;

    public EtablissementEntity getFirstBySiren(String siren) {
        return etablissementDao.getFirstBySiren(siren).orElseThrow(() -> new UnknownEtablissementException("Siren : " + siren));
    }

    /**
     * Enregistre ou mets à jour un établissement
     *
     * @param entity
     * @throws SirenExistException
     * @throws MailDoublonException
     */
    public void save(EtablissementEntity entity) throws SirenExistException, MailDoublonException {
        if (entity.getId() == null) {
            // Création d'un nouvel établisssement
            entity.getContact().setRole("etab");
            entity.setStatut((StatutEtablissementEntity) statutRepository.findById(Constant.STATUT_ETAB_NOUVEAU).get());
        }
        etablissementDao.save(entity);
    }

    public void saveAll(Set<EtablissementEntity> entities) throws MailDoublonException, SirenExistException {

        Iterator<EtablissementEntity> iter = entities.iterator();
        while (iter.hasNext()) {
            this.save(iter.next());
        }

    }

    public void deleteBySiren(String siren) {
        etablissementDao.deleteBySiren(siren);
    }

    public boolean existeSiren(String siren) {
        return etablissementDao.existeSiren(siren);
    }

    public boolean existeMail(String email) {
        return contactEtablissementDao.findByMail(email).isPresent();
    }

    public List<EtablissementEntity> findAll() {
        return etablissementDao.findAll();
    }

    public EtablissementEntity getUserByMail(String mail) {
        return etablissementDao.getUserByMail(mail).orElseThrow(() -> new UnknownEtablissementException("mail : " + mail));
    }
}
