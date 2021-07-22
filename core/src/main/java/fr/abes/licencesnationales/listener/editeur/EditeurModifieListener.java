package fr.abes.licencesnationales.listener.editeur;


import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.repository.EditeurRepository;
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
        editeurRepository.save(new EditeurEntity(
                editeurModifieEvent.getId(),
                editeurModifieEvent.getNomEditeur(),
                editeurModifieEvent.getIdentifiantEditeur(),
                editeurModifieEvent.getGroupesEtabRelies(),
                editeurModifieEvent.getAdresseEditeur(),
                editeurModifieEvent.getContactCommercialEditeurDTOS(),
                editeurModifieEvent.getContactTechniqueEditeurDTOS()));
    }
}
