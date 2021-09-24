package fr.abes.licencesnationales.core.entities.etablissement.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("modifie")
public class EtablissementModifieEventEntity  extends EtablissementEventEntity {

    @Deprecated
    public EtablissementModifieEventEntity() {
        super();
    }

    public EtablissementModifieEventEntity(Object source, String siren) {
        super(source);
        this.siren = siren;
    }

    @Override
    public String toString() {
        return "EditeurModifieEventEntity {" + "id=" + id + ", événement=modifie, siren de l'établissement=" + siren + " }";
    }
}
