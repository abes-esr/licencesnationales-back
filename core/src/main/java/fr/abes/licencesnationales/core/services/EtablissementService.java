package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class EtablissementService {
    @Autowired
    private EtablissementRepository dao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public EtablissementEntity getFirstBySiren(String siren) {
        return dao.getFirstBySiren(siren).orElseThrow(UnknownEtablissementException::new);
    }

    public void save(EtablissementEntity entity) {
        dao.save(entity);
    }

    public void saveAll(List<EtablissementEntity> entities) { dao.saveAll(entities); }

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

    public void changePasswordFromSiren(String siren, String password) throws RestClientException {
        String mdphash = passwordEncoder.encode(password);
        EtablissementEntity e = getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        c.setMotDePasse(mdphash);
        contactService.save(c);
        emailService.constructValidationNewPassEmail(c.getMail());
    }
}
