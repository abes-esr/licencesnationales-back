package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.UnknownEditeurException;
import fr.abes.licencesnationales.core.repository.contactediteur.ContactEditeurRepository;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class EditeurService {
    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private ContactEditeurRepository contactEditeurRepository;


    public void save(@Valid EditeurEntity editeur) throws MailDoublonException {
        checkDoublonMail(editeur);
        editeurRepository.save(editeur);
    }

    public EditeurEntity getFirstEditeurById(Integer id) {
        return editeurRepository.getFirstById(id).orElseThrow(() -> new UnknownEditeurException("id : " + id));
    }

    public List<EditeurEntity> findAllEditeur() {
        return editeurRepository.findAll();
    }


    public void deleteById(Integer id) {
        editeurRepository.deleteById(id);
    }

    public void checkDoublonMail(EditeurEntity editeur) throws MailDoublonException {
        for (ContactEditeurEntity ct : editeur.getContactsTechniques()) {
            if (contactEditeurRepository.findByMail(ct.getMail()).isPresent()) {
                throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
            }
        }
        for (ContactEditeurEntity ct : editeur.getContactsCommerciaux()) {
            if (contactEditeurRepository.findByMail(ct.getMail()).isPresent()) {
                throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
            }
        }
    }
}
