package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("modifie")
@Getter @Setter
public class EtablissementModifieEventEntity extends EtablissementEventEntity {

    @Column(name = "ANCIEN_SIREN")
    private String ancienSiren;

    @Deprecated
    public EtablissementModifieEventEntity() {
        super();
    }

    public EtablissementModifieEventEntity(Object source, String siren) {
        this(source, siren, null);
    }

    public EtablissementModifieEventEntity(Object source, String siren, String ancienSiren) {
        super(source);
        this.siren = siren;
        this.ancienSiren = ancienSiren;
    }

    @Override
    public String toString() {
        return "EtablissementModifieEventEntity {" + "id=" + id + ", événement=modifie, siren de l'établissement="
                + siren + " }";
    }
}
