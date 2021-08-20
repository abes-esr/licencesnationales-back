package fr.abes.licencesnationales.core.listener.editeur;

import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
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
        log.info("editeurCreeEvent.getCC" + editeurCreeEvent.getEditeur().getListeContactCommercialEditeurDto());
        log.info("editeurCreeEvent.getCT" + editeurCreeEvent.getEditeur().getListeContactTechniqueEditeurDto());
        log.info("editeurCreeEvent.getEditeur().getDateCreation()" + editeurCreeEvent.getEditeur().getDateCreation());
        editeurEntity.setDateCreation(editeurCreeEvent.getEditeur().getDateCreation());

        Set<ContactCommercialEditeurDto> cc = editeurCreeEvent.getEditeur().getListeContactCommercialEditeurDto();
        Set<ContactTechniqueEditeurDto> ct = editeurCreeEvent.getEditeur().getListeContactTechniqueEditeurDto();

        Set<ContactCommercialEditeurEntity> CC = new HashSet<>();
        Set<ContactTechniqueEditeurEntity> CT = new HashSet<>();
        for (ContactCommercialEditeurDto c:cc) {
            ContactCommercialEditeurEntity cce = new ContactCommercialEditeurEntity();
            cce.setNomContactCommercial(c.nomContactCommercial);
            cce.setPrenomContactCommercial(c.prenomContactCommercial);
            cce.setMailContactCommercial(c.mailContactCommercial);
            cce.setIdEditeur(editeurEntity);
            CC.add(cce);
        }
        for (ContactTechniqueEditeurDto t:ct) {
            ContactTechniqueEditeurEntity cte = new ContactTechniqueEditeurEntity();
            cte.setNomContactTechnique(t.nomContactTechnique);
            cte.setPrenomContactTechnique(t.prenomContactTechnique);
            cte.setMailContactTechnique(t.mailContactTechnique);
            cte.setIdEditeur(editeurEntity);
            CT.add(cte);
        }
        for (ContactCommercialEditeurEntity C:CC)
            log.info(" ListeContactCommercialEditeurDto après remplissage =  " + C.getMailContactCommercial() + C.getNomContactCommercial() + C.getPrenomContactCommercial());
        for (ContactTechniqueEditeurEntity T:CT)
            log.info(" ListeContactCommercialEditeurDto après remplissage =  " + T.getMailContactTechnique() + T.getNomContactTechnique() + T.getPrenomContactTechnique());

        editeurEntity.setContactCommercialEditeurEntities(CC);
        editeurEntity.setContactTechniqueEditeurEntities(CT);

        editeurRepository.save(editeurEntity);

    }

}
