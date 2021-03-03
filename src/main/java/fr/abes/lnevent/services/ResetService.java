package fr.abes.lnevent.services;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.*;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.event.etablissement.EtablissementDiviseEvent;
import fr.abes.lnevent.event.etablissement.EtablissementFusionneEvent;
import fr.abes.lnevent.event.etablissement.EtablissementSupprimeEvent;
import fr.abes.lnevent.repository.IpRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class ResetService {
    private EtablissementRepository etablissementRepository;
    private EventRepository eventRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private EditeurRepository editeurRepository;
    private IpRepository ipRepository;

    public ResetService(EtablissementRepository etablissementRepository, EventRepository eventRepository, ApplicationEventPublisher applicationEventPublisher, EditeurRepository editeurRepository, IpRepository ipRepository) {
        this.etablissementRepository = etablissementRepository;
        this.eventRepository = eventRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.editeurRepository = editeurRepository;
        this.ipRepository = ipRepository;
    }

    public String resetEtablissement() {
        etablissementRepository.deleteAll();
        for (EventEntity eventEntity :
                eventRepository.findAll()) {
            switch (eventEntity.event) {
                case "cree":
                    EtablissementCreeEvent etablissementCreeEvent =
                            new EtablissementCreeEvent(this,
                                    new EtablissementDTO(eventEntity.nomEtab,
                                            eventEntity.siren,
                                            eventEntity.typeEtablissement,
                                            eventEntity.idAbes,
                                            eventEntity.mailContact,
                                            eventEntity.motDePasse,
                                            eventEntity.nomContact,
                                            eventEntity.prenomContact,
                                            eventEntity.telephoneContact,
                                            eventEntity.adresseContact,
                                            eventEntity.boitePostaleContact,
                                            eventEntity.codePostalContact,
                                            eventEntity.cedexContact,
                                            eventEntity.villeContact));
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





































