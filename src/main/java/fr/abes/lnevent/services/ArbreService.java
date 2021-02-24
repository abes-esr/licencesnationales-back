package fr.abes.lnevent.services;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.repository.IpRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArbreService {
    private EventRepository eventRepository;
    private IpRepository ipRepository;

    public ArbreService(EventRepository eventRepository, IpRepository ipRepository) {
        this.eventRepository = eventRepository;
        this.ipRepository = ipRepository;
    }

    public String genereArbre() {
        StringBuilder builder = new StringBuilder();
        for (EventEntity eventEntity :
                eventRepository.findAll()) {
            switch (eventEntity.event) {
                case "cree":
                    builder.append("Nouveau : ").append(eventEntity.nomEtab).append("\n");
                    List<IpEntity> ips = ipRepository.findAllBySiren(eventEntity.siren);
                    for (IpEntity ipEntity :
                            ips) {
                        builder.append(ipEntity.ip).append("\n");
                    }
                    break;
                case "supprime":
                    builder.append("Supprimer : ").append(eventEntity.nomEtab).append("\n");
                    break;
                case "divise":
                    builder.append("Divise : ").append(eventEntity.ancienNomEtab).append("\n");
                    for (EtablissementDTO etab :
                            eventEntity.etablisementsDivise) {
                        builder.append(etab.getNom()).append("\n");
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
