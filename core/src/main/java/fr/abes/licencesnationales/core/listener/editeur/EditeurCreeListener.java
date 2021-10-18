package fr.abes.licencesnationales.core.listener.editeur;

import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.services.EditeurService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EditeurCreeListener implements ApplicationListener<EditeurCreeEventEntity> {

    private final EditeurService service;

    public EditeurCreeListener(EditeurService service) {
        this.service = service;
    }


    @SneakyThrows
    @Override
    public void onApplicationEvent(EditeurCreeEventEntity event) {
        EditeurEntity entity = new EditeurEntity(event.getNom(), event.getIdentifiant(), event.getAdresse(), event.getTypesEtabs());

        entity.ajouterContactsCommerciaux(event.getContactsCommerciaux());
        entity.ajouterContactsTechniques(event.getContactsTechniques());

        service.save(entity);

    }

}
