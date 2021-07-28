package fr.abes.licencesnationales.services;

import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    @Autowired
    private ContactRepository dao;

    public boolean existeMail(String mail) {
        return dao.findContactEntityByMail(mail).isPresent();
    }

    public void save(ContactEntity contactEntity) {
        dao.save(contactEntity);
    }
}
