package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.PasswordEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.event.Event;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.*;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

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
    private PasswordEventRepository motDePasseDao;

    public void save(Event event) {

        if (event instanceof EtablissementCreeEvent) {
            etablissementDao.save(new EtablissementEventEntity((EtablissementCreeEvent)event));
        } else  if (event instanceof EtablissementModifieEvent) {
            etablissementDao.save(new EtablissementEventEntity((EtablissementModifieEvent)event));
        }
    }
}
