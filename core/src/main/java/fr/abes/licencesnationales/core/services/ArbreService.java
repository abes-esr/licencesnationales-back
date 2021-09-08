package fr.abes.licencesnationales.core.services;


import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.IpEntity;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@Service
public class ArbreService {
    private EventRepository eventRepository;
    private IpRepository ipRepository;

    public ArbreService(EventRepository eventRepository, IpRepository ipRepository) {
        this.eventRepository = eventRepository;
        this.ipRepository = ipRepository;
    }

    public String genereArbre() throws ParseException {
        var builder = new StringBuilder();
        var simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
        for (EventEntity eventEntity :
                eventRepository.getAllByDateCreationEventBetweenOrderByDateCreationEvent(simpleDateFormat.parse("15-04-2021"),
                        new Date())) {
            switch (eventEntity.event) {
                case "cree":
                    builder.append("Nouvel établissement : ").append(eventEntity.nomEtab).append("\n");
                    Set<IpEntity> ips = ipRepository.findAllBySiren(eventEntity.siren);
                    for (IpEntity ipEntity :
                            ips) {
                        builder.append("Nouvelle ip créé : ");
                        builder.append(ipEntity.getIp()).append("\n");
                    }
                    break;
                case "supprime":
                    builder.append("Supprimer : ").append(eventEntity.nomEtab).append("\n");
                    break;
                case "divise":
                    builder.append("Divise : ").append(eventEntity.ancienNomEtab).append("\n");
                    for (EtablissementDto etab :
                            eventEntity.etablisementsDivises) {
                        builder.append(etab.getSiren()).append("\n");
                    }
                    break;
                case "fusionne":
                    builder.append("Fusion : ").append(eventEntity.nomEtab).append("\n");
                    for (String etab :
                            eventEntity.etablissementsFusionne) {
                        builder.append(etab).append("\n");
                    }
                    break;
            }
        }
        return builder.toString();
    }
}
