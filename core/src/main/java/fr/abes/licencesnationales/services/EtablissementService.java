package fr.abes.licencesnationales.services;

import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.repository.EtablissementRepository;
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

    public void changePasswordFromSiren(String siren, String password, PasswordEncoder passwordEncoder) throws RestClientException {
        String mdphash = passwordEncoder.encode(password);
        EtablissementEntity e = getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        c.setMotDePasse(mdphash);
        contactService.save(c);
        emailService.constructValidationNewPassEmail(c.getMail());
    }
}
