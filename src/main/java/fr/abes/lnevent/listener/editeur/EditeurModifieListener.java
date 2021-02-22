package fr.abes.lnevent.listener.editeur;

import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.entities.EditeurEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EditeurModifieListener implements ApplicationListener<EditeurModifieEvent> {

    private final EditeurRepository editeurRepository;

    public EditeurModifieListener(EditeurRepository editeurRepository) {
        this.editeurRepository = editeurRepository;
    }

    @Override
    public void onApplicationEvent(EditeurModifieEvent editeurModifieEvent) {
        editeurRepository.save(new EditeurEntity(editeurModifieEvent.getId(),
                editeurModifieEvent.getNom(),
                editeurModifieEvent.getAdresse(),
                editeurModifieEvent.getMailPourBatch(),
                editeurModifieEvent.getMailPourInformation()));
    }
}
