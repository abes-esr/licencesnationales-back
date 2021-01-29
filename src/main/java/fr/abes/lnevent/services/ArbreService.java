package fr.abes.lnevent.services;

import fr.abes.lnevent.event.editeur.dto.etablissement.Etablissement;
import fr.abes.lnevent.repository.entities.EventRow;
import fr.abes.lnevent.repository.entities.IpRow;
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
        for (EventRow eventRow :
                eventRepository.findAll()) {
            switch (eventRow.event) {
                case "cree":
                    builder.append("Nouveau : ").append(eventRow.nomEtab).append("\n");
                    List<IpRow> ips = ipRepository.findAllBySiren(eventRow.siren);
                    for (IpRow ipRow :
                            ips) {
                        builder.append(ipRow.ip).append("\n");
                    }
                    break;
                case "supprime":
                    builder.append("Supprimer : ").append(eventRow.nomEtab).append("\n");
                    break;
                case "divise":
                    builder.append("Divise : ").append(eventRow.ancienNomEtab).append("\n");
                    for (Etablissement etab :
                            eventRow.etablisementsDivise) {
                        builder.append(etab.getNom()).append("\n");
                    }
                    break;
                case "fusionne":
                    builder.append("Fusion : ").append(eventRow.nomEtab).append("\n");
                    for (String etab :
                            eventRow.etablissementsFusionne) {
                        builder.append(etab).append("\n");
                    }
                    break;
            }
        }
        return builder.toString();
    }
}
