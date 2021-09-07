package fr.abes.licencesnationales.listener.editeur;


import fr.abes.licencesnationales.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.services.EditeurService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EditeurSupprimeListener implements ApplicationListener<EditeurSupprimeEvent> {

    private final EditeurService editeurService;

    public EditeurSupprimeListener(EditeurService editeurService) {
        this.editeurService = editeurService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EditeurSupprimeEvent editeurSupprimeEvent) {
        editeurService.deleteById(editeurSupprimeEvent.getId());
    }
}
