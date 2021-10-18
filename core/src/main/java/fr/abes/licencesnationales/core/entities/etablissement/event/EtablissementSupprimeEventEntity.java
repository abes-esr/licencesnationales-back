package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("supprime")
@NoArgsConstructor
public class EtablissementSupprimeEventEntity extends EtablissementEventEntity {
    public EtablissementSupprimeEventEntity(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
