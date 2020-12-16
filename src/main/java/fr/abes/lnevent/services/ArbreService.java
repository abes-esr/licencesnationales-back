package fr.abes.lnevent.services;

import fr.abes.lnevent.collection.EventCollection;
import fr.abes.lnevent.collection.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class ArbreService {
    private EventRepository eventRepository;

    public ArbreService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public String genereArbre() {
        StringBuilder builder = new StringBuilder();
        for (EventCollection eventCollection :
                eventRepository.findAll()) {
            switch (eventCollection.event) {
                case "cree":
                    builder.append("Nouveau : ").append(eventCollection.nomEtab).append("\n");
                    break;
                case "supprime":
                    builder.append("Supprimer : ").append(eventCollection.nomEtab).append("\n");
                    break;
                case "divise":
                    builder.append("Divise : ").append(eventCollection.ancienNomEtab).append("\n");
                    for (String etab :
                            eventCollection.etablisementsDivise) {
                        builder.append(etab).append("\n");
                    }
                    break;
                case "fusionne":
                    builder.append("Fusion : ").append(eventCollection.nomEtab).append("\n");
                    for (String etab :
                            eventCollection.etablissementsFusionne) {
                        builder.append(etab).append("\n");
                    }
                    break;
            }
        }
        return builder.toString();
    }
}
