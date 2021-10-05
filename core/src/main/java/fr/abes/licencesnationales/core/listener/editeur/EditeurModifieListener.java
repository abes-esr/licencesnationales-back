package fr.abes.licencesnationales.core.listener.editeur;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EditeurModifieListener implements ApplicationListener<EditeurModifieEventEntity> {

    private final EditeurRepository editeurRepository;

    public EditeurModifieListener(EditeurRepository editeurRepository, UtilsMapper utilsMapper) {
        this.editeurRepository = editeurRepository;
    }

    @Override
    public void onApplicationEvent(EditeurModifieEventEntity event) {
        EditeurEntity editeur = new EditeurEntity(event.getId(), event.getNom(), event.getIdentifiant(), event.getAdresse(), event.getTypesEtabs());
        editeur.ajouterContactsTechniques(event.getContactsTechniques());
        editeur.ajouterContactsCommerciaux(event.getContactsCommerciaux());
        editeurRepository.save(editeur);
    }
}
