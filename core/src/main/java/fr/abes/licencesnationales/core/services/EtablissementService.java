package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@Slf4j
public class EtablissementService {
    @Autowired
    private EtablissementRepository etablissementDao;

    @Autowired
    private ContactRepository contactEtablissementDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StatutRepository statutRepository;

    public EtablissementEntity getFirstBySiren(String siren) {
        return etablissementDao.getFirstBySiren(siren).orElseThrow(UnknownEtablissementException::new);
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
            // Initialisation du statut à nouvel établisssement
           entity.setStatut((StatutEtablissementEntity) statutRepository.findById(Constant.STATUT_ETAB_NOUVEAU).get());
        }

        //verifier que le siren n'est pas déjà en base
        boolean existeSiren = existeSiren(entity.getSiren());
        log.debug("existeSiren = "+ existeSiren);
        if (existeSiren) {
            throw new SirenExistException("Cet établissement existe déjà.");
        }

        //verifier que le mail du contact n'est pas déjà en base
        if (existeMail(entity.getContact().getMail())) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }

        etablissementDao.save(entity);
    }

    public void saveAll(List<EtablissementEntity> entities) { etablissementDao.saveAll(entities); }

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
        return etablissementDao.getUserByMail(mail).orElseThrow(UnknownEtablissementException::new);
    }

    public void changePasswordFromSiren(String siren, String password) throws RestClientException {
        String mdphash = passwordEncoder.encode(password);
        EtablissementEntity e = getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        c.setMotDePasse(mdphash);
        etablissementDao.save(e);
    }
}
