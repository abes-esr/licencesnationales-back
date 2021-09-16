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

    private final UtilsMapper utilsMapper;

    public EditeurModifieListener(EditeurRepository editeurRepository, UtilsMapper utilsMapper) {
        this.editeurRepository = editeurRepository;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EditeurModifieEventEntity editeurModifieEvent) {
        EditeurEntity editeurEntity = utilsMapper.map(editeurModifieEvent, EditeurEntity.class);
        editeurRepository.save(editeurEntity);
    }
}
