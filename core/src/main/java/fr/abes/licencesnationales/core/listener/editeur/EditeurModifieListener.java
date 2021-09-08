package fr.abes.licencesnationales.core.listener.editeur;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EditeurModifieListener implements ApplicationListener<EditeurModifieEvent> {

    private final EditeurRepository editeurRepository;

    private final UtilsMapper utilsMapper;

    public EditeurModifieListener(EditeurRepository editeurRepository, UtilsMapper utilsMapper) {
        this.editeurRepository = editeurRepository;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EditeurModifieEvent editeurModifieEvent) {
        EditeurEntity editeurEntity = utilsMapper.map(editeurModifieEvent, EditeurEntity.class);
        editeurRepository.save(editeurEntity);
    }
}
