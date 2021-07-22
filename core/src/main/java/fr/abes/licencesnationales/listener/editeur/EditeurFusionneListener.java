/*
package fr.abes.lnevent.fr.abes.licencesnationales.listener.editeur;

import fr.abes.lnevent.fr.abes.licencesnationales.dto.editeur.EditeurDTO;
import fr.abes.lnevent.fr.abes.licencesnationales.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.fr.abes.licencesnationales.repository.EditeurRepository;
import fr.abes.lnevent.fr.abes.licencesnationales.entities.EditeurEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EditeurFusionneListener implements ApplicationListener<EditeurFusionneEvent> {

    private final EditeurRepository editeurRepository;

    public EditeurFusionneListener(EditeurRepository editeurRepository) {
        this.editeurRepository = editeurRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EditeurFusionneEvent editeurFusionneEvent) {

        EditeurDTO editeurDTO = editeurFusionneEvent.getEditeurDTO();

        editeurRepository.save(new EditeurEntity(null,
                editeurDTO.getNom(),
                editeurDTO.getAdresse(),
                editeurDTO.getMailPourBatch(),
                editeurDTO.getMailPourInformation(),
                null));

        for (Long idEditeur :
                editeurFusionneEvent.getIdEditeurFusionnes()) {
            editeurRepository.deleteById(idEditeur);
        }
    }
}
*/
