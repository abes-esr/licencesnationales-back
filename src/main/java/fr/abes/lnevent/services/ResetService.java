package fr.abes.lnevent.services;

import fr.abes.lnevent.event.editeur.dto.etablissement.Etablissement;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.repository.entities.EventRow;
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
        for (EventRow eventRow :
                eventRepository.findAll()) {
            switch (eventRow.event) {
                case "cree":
                    EtablissementCreeEvent etablissementCreeEvent =
                            new EtablissementCreeEvent(this,
                                    eventRow.nomEtab,
                                    eventRow.adresse,
                                    eventRow.siren,
                                    eventRow.typeEtablissement,
                                    eventRow.motDePasse,
                                    eventRow.idAbes,
                                    eventRow.mailContact,
                                    eventRow.nomContact,
                                    eventRow.prenomContact,
                                    eventRow.telephoneContact,
                                    eventRow.adresseContact);
                    applicationEventPublisher.publishEvent(etablissementCreeEvent);
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
                                    (ArrayList<Etablissement>) eventRow.etablisementsDivise);
                    applicationEventPublisher.publishEvent(etablissementDiviseEvent);
                    break;
                case "fusionne":
                    EtablissementFusionneEvent etablissementFusionneEvent =
                            new EtablissementFusionneEvent(this,
                                    eventRow.etablissementFusion,
                                    (ArrayList<String>) eventRow.etablissementsFusionne);
                    applicationEventPublisher.publishEvent(etablissementFusionneEvent);
                    break;
            }
        }
        return "done";
    }
}
