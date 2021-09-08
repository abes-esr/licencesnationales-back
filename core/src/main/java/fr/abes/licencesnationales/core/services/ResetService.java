package fr.abes.licencesnationales.core.services;


import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementDiviseEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementSupprimeEvent;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.EventRepository;
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

    public void resetEtablissement() {
        etablissementRepository.deleteAll();
        for (EventEntity eventEntity :
                eventRepository.findAll()) {
            switch (eventEntity.event) {
                case "cree":
                    EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this,
                            eventEntity.nomEtab,
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
                            eventEntity.roleContact,
                            eventEntity.villeContact);

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
                                    (ArrayList<EtablissementDto>) eventEntity.etablisementsDivises);
                    applicationEventPublisher.publishEvent(etablissementDiviseEvent);
                    break;
                case "fusionne":
                    EtablissementFusionneEvent etablissementFusionneEvent =
                            new EtablissementFusionneEvent(this,
                                    eventEntity.getNomEtab(),
                                    eventEntity.getSiren(),
                                    eventEntity.getTypeEtablissement(),
                                    eventEntity.getIdAbes(),
                                    eventEntity.getNomContact(),
                                    eventEntity.getPrenomContact(),
                                    eventEntity.getAdresseContact(),
                                    eventEntity.getBoitePostaleContact(),
                                    eventEntity.getCodePostalContact(),
                                    eventEntity.getVilleContact(),
                                    eventEntity.getCedexContact(),
                                    eventEntity.getTelephoneContact(),
                                    eventEntity.getMailContact(),
                                    eventEntity.getMotDePasse(),
                                    eventEntity.getRoleContact(),
                                    (ArrayList<String>) eventEntity.etablissementsFusionne);
                    applicationEventPublisher.publishEvent(etablissementFusionneEvent);
                    break;
            }
        }
    }
}
