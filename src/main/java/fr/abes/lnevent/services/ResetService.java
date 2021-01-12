package fr.abes.lnevent.services;

import fr.abes.lnevent.entities.EventRow;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.event.EtablissementDiviseEvent;
import fr.abes.lnevent.event.EtablissementFusionneEvent;
import fr.abes.lnevent.event.EtablissementSupprimeEvent;
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
        for (EventRow eventRow :
                eventRepository.findAll()) {
            switch (eventRow.event) {
                case "cree":
      /*              EtablissementCreeEvent etablissementCreeEvent =
                            new EtablissementCreeEvent(this, eventCollection.nomEtab);
                    applicationEventPublisher.publishEvent(etablissementCreeEvent);*/
                    break;
                case "supprime":
                    EtablissementSupprimeEvent etablissementSupprimeEvent =
                            new EtablissementSupprimeEvent(this, eventRow.nomEtab);
                    applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
                    break;
                case "divise":
                    EtablissementDiviseEvent etablissementDiviseEvent =
                            new EtablissementDiviseEvent(
                                    this,
                                    eventRow.ancienNomEtab,
                                    (ArrayList<String>) eventRow.etablisementsDivise);
                    applicationEventPublisher.publishEvent(etablissementDiviseEvent);
                    break;
                case "fusionne":
                    EtablissementFusionneEvent etablissementFusionneEvent =
                            new EtablissementFusionneEvent(this,
                                    eventRow.nomEtab,
                                    (ArrayList<String>) eventRow.etablissementsFusionne);
                    applicationEventPublisher.publishEvent(etablissementFusionneEvent);
                    break;
            }
        }
        return "done";
    }
}
