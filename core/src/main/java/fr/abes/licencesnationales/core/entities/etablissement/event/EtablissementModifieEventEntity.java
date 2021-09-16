package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("modifie")
@NoArgsConstructor
public class EtablissementModifieEventEntity  extends EtablissementEventEntity {
    public EtablissementModifieEventEntity(Object source, Integer id) {
        super(source);
        this.id = id;
    }

    @Override
    public String toString() {
        return "EditeurModifieEventEntity {" + "id=" + id + ", événement=modifie, nom de l'établissement=" + nomEtab + " }";
    }
}
