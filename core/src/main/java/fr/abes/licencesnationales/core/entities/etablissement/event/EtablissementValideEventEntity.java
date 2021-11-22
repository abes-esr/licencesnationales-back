package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("valide")
@Getter @Setter
public class EtablissementValideEventEntity extends EtablissementEventEntity {
    @Deprecated
    public EtablissementValideEventEntity() {
        super();
    }

    public EtablissementValideEventEntity(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
