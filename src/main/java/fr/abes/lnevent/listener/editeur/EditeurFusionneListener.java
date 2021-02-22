package fr.abes.lnevent.listener.editeur;

import fr.abes.lnevent.dto.editeur.EditeurDTO;
import fr.abes.lnevent.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.entities.EditeurEntity;
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
                editeurDTO.getMailPourInformation()));

        for (Long idEditeur :
                editeurFusionneEvent.getIdEditeurFusionnes()) {
            editeurRepository.deleteById(idEditeur);
        }
    }
}
