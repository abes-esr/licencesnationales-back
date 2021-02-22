package fr.abes.lnevent.services;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.event.etablissement.EtablissementDiviseEvent;
import fr.abes.lnevent.event.etablissement.EtablissementFusionneEvent;
import fr.abes.lnevent.event.etablissement.EtablissementSupprimeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ResetService {
    private EtablissementRepository etablissementRepository;
    private EventRepository eventRepository;
    private ApplicationEventPublisher applicationEventPublisher;

    public ResetService(EtablissementRepository etablissementRepository, EventRepository eventRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.etablissementRepository = etablissementRepository;
        this.eventRepository = eventRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String resetEtablissement() {
        etablissementRepository.deleteAll();
        for (EventEntity eventEntity :
                eventRepository.findAll()) {
            switch (eventEntity.event) {
                case "cree":
                    EtablissementCreeEvent etablissementCreeEvent =
                            new EtablissementCreeEvent(this,
                                    eventEntity.nomEtab,
                                    eventEntity.adresse,
                                    eventEntity.siren,
                                    eventEntity.typeEtablissement,
                                    eventEntity.motDePasse,
                                    eventEntity.idAbes,
                                    eventEntity.mailContact,
                                    eventEntity.nomContact,
                                    eventEntity.prenomContact,
                                    eventEntity.telephoneContact,
                                    eventEntity.adresseContact);
                    applicationEventPublisher.publishEvent(etablissementCreeEvent);
                    break;
                case "supprime":
                    EtablissementSupprimeEvent etablissementSupprimeEvent =
                            new EtablissementSupprimeEvent(this, eventEntity.nomEtab);
                    applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
                    break;
                case "divise":
                    EtablissementDiviseEvent etablissementDiviseEvent =
                            new EtablissementDiviseEvent(
                                    this,
                                    eventEntity.ancienNomEtab,
                                    (ArrayList<EtablissementDTO>) eventEntity.etablisementsDivise);
                    applicationEventPublisher.publishEvent(etablissementDiviseEvent);
                    break;
                case "fusionne":
                    EtablissementFusionneEvent etablissementFusionneEvent =
                            new EtablissementFusionneEvent(this,
                                    eventEntity.etablissementDTOFusion,
                                    (ArrayList<String>) eventEntity.etablissementsFusionne);
                    applicationEventPublisher.publishEvent(etablissementFusionneEvent);
                    break;
            }
        }
        return "done";
    }
}
