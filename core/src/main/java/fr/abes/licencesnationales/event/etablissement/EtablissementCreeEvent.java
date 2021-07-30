package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.event.Event;
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
