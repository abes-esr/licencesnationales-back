package fr.abes.licencesnationales.core.listener.editeur;


import fr.abes.licencesnationales.core.entities.editeur.event.EditeurSupprimeEventEntity;
import fr.abes.licencesnationales.core.services.EditeurService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EditeurSupprimeListener implements ApplicationListener<EditeurSupprimeEventEntity> {

    private final EditeurService editeurService;

    public EditeurSupprimeListener(EditeurService editeurService) {
        this.editeurService = editeurService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EditeurSupprimeEventEntity editeurSupprimeEvent) {
        editeurService.deleteById(editeurSupprimeEvent.getId());
    }
}
