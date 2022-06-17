package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.UnknownEditeurException;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.repository.contactediteur.ContactEditeurRepository;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EditeurService {
    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private ContactEditeurRepository contactEditeurRepository;

    @Autowired
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;


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
            if (contactEditeurRepository.findByMailContains(ct.getMail()).isPresent()) {
                throw new MailDoublonException(Constant.ERROR_DOUBLON_MAIL);
            }
        }
        for (ContactEditeurEntity ct : editeur.getContactsCommerciaux()) {
            if (contactEditeurRepository.findByMailContains(ct.getMail()).isPresent()) {
                throw new MailDoublonException(Constant.ERROR_DOUBLON_MAIL);
            }
        }
    }

    public List<EditeurEntity> search(List<String> criteres) {
        return searchByCriteria(editeurRepository.findAll(), criteres);
    }

    /**
     * Fonction récursive permettant d'alimenter une liste de résultats à partir d'une liste d'éditeur et de critères de recherche
     * @param editeurs liste des éditeurs sur lesquels effectuer la recherche
     * @param criteres liste des critères dépilée à chaque nouveau passage
     */
    private List<EditeurEntity> searchByCriteria(List<EditeurEntity> editeurs, List<String> criteres) {
        List<EditeurEntity> resultats = new ArrayList<>();
        String critLower = criteres.get(0).toLowerCase(Locale.ROOT);
        resultats.addAll(editeurs.stream().filter(editeur -> {
            if (editeur.getNom().toLowerCase().contains(critLower) || editeur.getAdresse().toLowerCase().contains(critLower) || editeur.getIdentifiant().toLowerCase().contains(critLower))
                return true;
            final boolean[] ccFound = {false};
            editeur.getContactsCommerciaux().forEach(cc -> {
                if (cc.getNom().toLowerCase().contains(critLower) || cc.getPrenom().toLowerCase().contains(critLower) || cc.getMail().toLowerCase().contains(critLower))
                    ccFound[0] = true;
            });
            final boolean[] ctFound = {false};
            editeur.getContactsTechniques().forEach(ct -> {
                if (ct.getNom().toLowerCase().contains(critLower) || ct.getPrenom().toLowerCase().contains(critLower) || ct.getMail().toLowerCase().contains(critLower))
                    ctFound[0] = true;
            });
            if (ccFound[0] || ctFound[0]) return true;
            return false;
        }).collect(Collectors.toList()));
        criteres.remove(0);
        //condition de sortie de la fonction récursive
        if (criteres.size() == 0) {
            return resultats;
        }
        return searchByCriteria(editeurs, criteres);
    }


    public List<String> getDateEnvoiEditeurs() {
        ArrayList<String> dates = new ArrayList<>();
        for (DateEnvoiEditeurEntity date : dateEnvoiEditeurRepository.findAllByOrderByDateEnvoiDesc()) {
            dates.add(date.getDateEnvoi().toString());
        }
        return dates;
    }
}
