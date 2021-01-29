package fr.abes.lnevent.listener.editeur;

import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.repository.entities.EditeurRow;
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
        editeurRepository.save(new EditeurRow(editeurModifieEvent.getId(),
                editeurModifieEvent.getNom(),
                editeurModifieEvent.getAdresse(),
                editeurModifieEvent.getMailPourBatch(),
                editeurModifieEvent.getMailPourInformation()));
    }
}
