package fr.abes.licencesnationales.core.listener.editeur;

import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EditeurCreeListener implements ApplicationListener<EditeurCreeEvent> {
    private final EditeurRepository editeurRepository;

    private final UtilsMapper utilsMapper;

    public EditeurCreeListener(EditeurRepository editeurRepository, UtilsMapper utilsMapper) {
        this.editeurRepository = editeurRepository;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EditeurCreeEvent editeurCreeEvent) {
        EditeurEntity editeurEntity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);
        editeurRepository.save(editeurEntity);
    }

}
