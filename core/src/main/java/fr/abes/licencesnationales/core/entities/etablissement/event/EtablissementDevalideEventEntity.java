package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("devalide")
@Getter
@Setter
public class EtablissementDevalideEventEntity extends EtablissementEventEntity {
    @Deprecated
    public EtablissementDevalideEventEntity() {
        super();
    }

    public EtablissementDevalideEventEntity(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
