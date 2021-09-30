package fr.abes.licencesnationales.core.listener.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class EditeurCreeListener implements ApplicationListener<EditeurCreeEventEntity> {


    @Autowired
    private EditeurService service;

    @Autowired
    private ReferenceService referenceService;

    private final UtilsMapper utilsMapper;

    public EditeurCreeListener(UtilsMapper utilsMapper) {
        this.utilsMapper = utilsMapper;
    }


    @SneakyThrows
    @Override
    public void onApplicationEvent(EditeurCreeEventEntity event) {

        Set<TypeEtablissementEntity> typeEtablissementEntities = new HashSet<>();
        for (String s : event.getTypesEtabs()){
            typeEtablissementEntities.add(referenceService.findTypeEtabByLibelle(s));
        }

        EditeurEntity editeur = new EditeurEntity(event.getNomEditeur(), event.getIdentifiantEditeur(), event.getAdresseEditeur(), new Date(), typeEtablissementEntities);


        for(ContactEditeurDto c : event.getListeContactCommercialEditeur()){
            ContactCommercialEditeurEntity cc = new ContactCommercialEditeurEntity();
            cc.setNomContact(c.getNomContact());
            cc.setPrenomContact(c.getPrenomContact());
            cc.setMailContact(c.getMailContact());
            editeur.ajouterContact(cc);
        }

        for(ContactEditeurDto c : event.getListeContactTechniqueEditeur()){
            ContactTechniqueEditeurEntity ct = new ContactTechniqueEditeurEntity();
            ct.setNomContact(c.getNomContact());
            ct.setPrenomContact(c.getPrenomContact());
            ct.setMailContact(c.getMailContact());
            editeur.ajouterContact(ct);
        }

        service.save(editeur);

    }

}
