package fr.abes.lnevent.listener.editeur;

import fr.abes.lnevent.dto.editeur.ContactTechniqueEditeurDTO;
import fr.abes.lnevent.dto.editeur.EditeurDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.*;
import fr.abes.lnevent.event.editeur.EditeurCreeEvent;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.repository.EditeurRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

@Component
public class EditeurCreeListener implements ApplicationListener<EditeurCreeEvent> {

    private final EditeurRepository editeurRepository;

    public EditeurCreeListener(EditeurRepository editeurRepository) {
        this.editeurRepository = editeurRepository;
    }

    @Override
    public void onApplicationEvent(EditeurCreeEvent editeurCreeEvent) {
        EditeurDTO editeurDTO = editeurCreeEvent.getEditeurDTO();
        Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities= null;
        Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities= null;
        EditeurEntity editeurEntity = null;


        if(editeurDTO.getListeContactTechniqueEditeurDTO()!=null && !editeurDTO.getListeContactTechniqueEditeurDTO().isEmpty()) {
            ContactTechniqueEditeurEntity contactTechniqueEditeurEntity = null;

            for (ContactTechniqueEditeurDTO c : editeurDTO.getListeContactTechniqueEditeurDTO()) {
                contactTechniqueEditeurEntity =
                        new ContactTechniqueEditeurEntity(null,
                                c.getNomContactTechnique(),
                                c.getPrenomContactTechnique(),
                                c.getMailContactTechnique());

            }
            contactTechniqueEditeurEntities.add(contactTechniqueEditeurEntity);
        }
        else if(editeurDTO.getListeContactCommercialEditeurDTO()!=null && !editeurDTO.getListeContactCommercialEditeurDTO().isEmpty()) {
            ContactCommercialEditeurEntity contactCommercialEditeurEntity = null;

            for (ContactTechniqueEditeurDTO c : editeurDTO.getListeContactTechniqueEditeurDTO()) {
                contactCommercialEditeurEntity =
                        new ContactCommercialEditeurEntity(null,
                                c.getNomContactTechnique(),
                                c.getPrenomContactTechnique(),
                                c.getMailContactTechnique());

            }
            contactCommercialEditeurEntities.add(contactCommercialEditeurEntity);
        }
        if(contactCommercialEditeurEntities!=null && !contactCommercialEditeurEntities.isEmpty() &&
                contactTechniqueEditeurEntities!=null && !contactTechniqueEditeurEntities.isEmpty()) {
            editeurEntity = new EditeurEntity(null,
                    editeurDTO.getNomEditeur(),
                    editeurDTO.getIdentifiantEditeur(),
                    editeurDTO.getAdresseEditeur(),
                    contactCommercialEditeurEntities,
                    contactTechniqueEditeurEntities);

            editeurRepository.save(editeurEntity);
        }
        else if(contactCommercialEditeurEntities!=null && !contactCommercialEditeurEntities.isEmpty() &&
                contactTechniqueEditeurEntities==null && contactTechniqueEditeurEntities.isEmpty()){

            editeurEntity = new EditeurEntity(null,
                    editeurDTO.getNomEditeur(),
                    editeurDTO.getIdentifiantEditeur(),
                    editeurDTO.getAdresseEditeur(),
                    contactCommercialEditeurEntities);

            editeurRepository.save(editeurEntity);
        }
        /*else if(contactCommercialEditeurEntities==null && contactCommercialEditeurEntities.isEmpty() &&
                contactTechniqueEditeurEntities!=null && !contactTechniqueEditeurEntities.isEmpty()){

            editeurEntity = new EditeurEntity(null,
                    editeurDTO.getNomEditeur(),
                    editeurDTO.getIdentifiantEditeur(),
                    editeurDTO.getAdresseEditeur(),
                    contactTechniqueEditeurEntities);

            editeurRepository.save(editeurEntity);
        }*/


    }

}
