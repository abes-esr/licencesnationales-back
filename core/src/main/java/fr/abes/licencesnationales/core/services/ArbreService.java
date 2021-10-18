package fr.abes.licencesnationales.core.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ArbreService {
    private final EtablissementEventRepository eventRepository;
    private final IpRepository ipRepository;
    private final ObjectMapper mapper;

    public ArbreService(EtablissementEventRepository eventRepository, IpRepository ipRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.ipRepository = ipRepository;
        this.mapper = objectMapper;
    }

    public String genereArbre() throws ParseException, IOException {
        /** TODO : a revoir */
        /*var builder = new StringBuilder();
        var simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
        for (EtablissementEventEntity eventEntity :
                eventRepository.getAllByDateCreationEventBetweenOrderByDateCreationEvent(simpleDateFormat.parse("15-04-2021"),
                        new Date())) {
            switch (eventEntity.getEvent()) {
                case "cree":
                    builder.append("Nouvel établissement : ").append(eventEntity.getNomEtab()).append("\n");
                    Set<IpEntity> ips = ipRepository.findAllBySiren(eventEntity.getSiren());
                    for (IpEntity ipEntity :
                            ips) {
                        builder.append("Nouvelle ip créé : ");
                        builder.append(ipEntity.getIp()).append("\n");
                    }
                    break;
                case "supprime":
                    builder.append("Supprimer : ").append(eventEntity.getNomEtab()).append("\n");
                    break;
                case "divise":
                    builder.append("Divise : ").append(eventEntity.getAncienNomEtab()).append("\n");
                    for (EtablissementEntity etab :
                            Arrays.asList(mapper.readValue(eventEntity.getEtablisementsDivises(), EtablissementEntity[].class))) {
                        builder.append(etab.getSiren()).append("\n");
                    }
                    break;
                case "fusionne":
                    builder.append("Fusion : ").append(eventEntity.getNomEtab()).append("\n");
                    for (EtablissementEntity etab :
                            Arrays.asList(mapper.readValue(eventEntity.getEtablissementsFusionne(), EtablissementEntity[].class))) {
                        builder.append(etab).append("\n");
                    }
                    break;
            }
        }
        return builder.toString();*/
        return "";
    }
}
