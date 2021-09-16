package fr.abes.licencesnationales.core.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResetService {
    @Autowired
    private ObjectMapper mapper;
    private EtablissementRepository etablissementRepository;
    private EtablissementEventRepository eventRepository;
    private TypeEtablissementRepository typeRepository;
    private ApplicationEventPublisher applicationEventPublisher;

    public ResetService(EtablissementRepository etablissementRepository, EtablissementEventRepository eventRepository, TypeEtablissementRepository typeRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.etablissementRepository = etablissementRepository;
        this.eventRepository = eventRepository;
        this.typeRepository = typeRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void resetEtablissement() throws IOException {
        /** TODO : resetEtablissement : méthode bien utile pour les besoins fonctionnels ? */
        /*etablissementRepository.deleteAll();
        for (EtablissementEventEntity eventEntity :
                eventRepository.findAll()) {
            switch (eventEntity.getEvent()) {
                case "cree":
                    EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this,
                            eventEntity.getNomEtab(),
                            eventEntity.getSiren(),
                            eventEntity.getTypeEtablissement(),
                            eventEntity.getIdAbes(),
                            eventEntity.getMailContact(),
                            eventEntity.getMotDePasse(),
                            eventEntity.getNomContact(),
                            eventEntity.getPrenomContact(),
                            eventEntity.getTelephoneContact(),
                            eventEntity.getAdresseContact(),
                            eventEntity.getBoitePostaleContact(),
                            eventEntity.getCodePostalContact(),
                            eventEntity.getCedexContact(),
                            eventEntity.getRoleContact(),
                            eventEntity.getVilleContact());

                    applicationEventPublisher.publishEvent(etablissementCreeEvent);
                    break;
                case "supprime":
                    EtablissementSupprimeEvent etablissementSupprimeEvent =
                            new EtablissementSupprimeEvent(this, eventEntity.getNomEtab());
                    applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
                    break;
                case "divise":
                    EtablissementDiviseEvent etablissementDiviseEvent =
                            new EtablissementDiviseEvent(
                                    this,
                                    eventEntity.getAncienNomEtab(),
                                    Arrays.asList(mapper.readValue(eventEntity.getEtablisementsDivises(), EtablissementEntity[].class)));
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
                                    Arrays.asList(mapper.readValue(eventEntity.getEtablissementsFusionne(), String[].class)));
                    applicationEventPublisher.publishEvent(etablissementFusionneEvent);
                    break;
            }
        }*/
    }
}
