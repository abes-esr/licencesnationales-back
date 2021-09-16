package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    @Autowired
    private ContactRepository dao;

    public boolean existeMail(String mail) {
        return dao.findByMail(mail).isPresent();
    }

    public void save(ContactEntity contactEntity) {
        dao.save(contactEntity);
    }
}
