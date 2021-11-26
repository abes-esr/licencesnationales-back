package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.editeur.EditeurEventRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {
    @Autowired
    private EtablissementEventRepository etablissementDao;

    @Autowired
    private EditeurEventRepository editeurDao;

    @Autowired
    private IpEventRepository ipDao;

    @Autowired
    private ObjectMapper mapper;

    public void save(EventEntity event) throws JsonProcessingException {
        if (event instanceof EtablissementEventEntity) {
            if (event instanceof EtablissementDiviseEventEntity) {
                ((EtablissementDiviseEventEntity) event).setEtablisementsDivisesInBdd(mapper.writeValueAsString(((EtablissementDiviseEventEntity) event).getEtablissementDivises()));
            }
            if (event instanceof EtablissementFusionneEventEntity) {
                ((EtablissementFusionneEventEntity) event).setAnciensEtablissementsInBdd(mapper.writeValueAsString(((EtablissementFusionneEventEntity) event).getSirenAnciensEtablissements()));
            }
            etablissementDao.save((EtablissementEventEntity)event);
        } else  if (event instanceof EditeurEventEntity) {
            editeurDao.save((EditeurEventEntity)event);
        } else if (event instanceof IpEventEntity) {
            ipDao.save((IpEventEntity) event);
        }
    }

    public Date getLastDateSuppressionIpEtab(EtablissementEntity etab) {
        List<IpEventEntity> listeIpSupprimees = ipDao.getIpSupprimeBySiren(etab.getSiren());
        if (listeIpSupprimees.size() > 0) {
            return listeIpSupprimees.stream().sorted((o1, o2) -> o2.getDateCreationEvent().compareTo(o1.getDateCreationEvent())).findFirst().get().getDateCreationEvent();
        }
        return null;
    }

    public Date getDateCreationEtab(EtablissementEntity etab) {
        Optional<EtablissementEventEntity> etablissement = etablissementDao.getDateCreationEtab(etab.getSiren());
        if (!etablissement.isPresent()){
            throw new UnknownEtablissementException("Etablissement inconnu");
        }
        return etablissement.get().getDateCreationEvent();
    }
}
