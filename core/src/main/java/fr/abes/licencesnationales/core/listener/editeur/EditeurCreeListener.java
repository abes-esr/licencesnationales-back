package fr.abes.licencesnationales.core.listener.editeur;

import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.repository.ContactCommercialEditeurRepository;
import fr.abes.licencesnationales.core.repository.ContactTechniqueEditeurRepository;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class EditeurCreeListener implements ApplicationListener<EditeurCreeEvent> {
    private final EditeurRepository editeurRepository;

    @Autowired
    ContactCommercialEditeurRepository ccRepository;

    @Autowired
    ContactTechniqueEditeurRepository ctRepository;


    private final UtilsMapper utilsMapper;

    public EditeurCreeListener(EditeurRepository editeurRepository, UtilsMapper utilsMapper) {
        this.editeurRepository = editeurRepository;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EditeurCreeEvent editeurCreeEvent) {

        log.debug("editeurCreeEvent.getCC" + editeurCreeEvent.getListeContactCommercialEditeur());
        log.debug("editeurCreeEvent.getCT" + editeurCreeEvent.getListeContactTechniqueEditeur());
        log.debug("editeurCreeEvent.getEditeur().getDateCreation()" + editeurCreeEvent.getDateCreation());

        EditeurEntity editeurEntity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);


        Set<ContactCommercialEditeurEntity> CC = editeurEntity.getContactCommercialEditeurEntities();
        Set<ContactTechniqueEditeurEntity> CT = editeurEntity.getContactTechniqueEditeurEntities();

        for (ContactCommercialEditeurEntity C:CC) {
            log.debug(" editeurEntity.getContactCommercialEditeurEntities() =  " + C.getMailContactCommercial() + C.getNomContactCommercial() + C.getPrenomContactCommercial());

        }
        for (ContactTechniqueEditeurEntity T:CT) {
            log.debug(" editeurEntity.getContactTechniqueEditeurEntities() =  " + T.getMailContactTechnique() + T.getNomContactTechnique() + T.getPrenomContactTechnique());

        }

        editeurRepository.save(editeurEntity);

    }

}
