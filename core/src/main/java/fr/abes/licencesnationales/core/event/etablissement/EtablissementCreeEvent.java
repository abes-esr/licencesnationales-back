package fr.abes.licencesnationales.core.event.etablissement;


import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class EtablissementCreeEvent extends Event {
    private EtablissementCreeDto etablissement;

    public EtablissementCreeEvent(
            Object source,
            EtablissementCreeDto etablissement) {
        super(source);
        this.etablissement = etablissement;
    }


}
