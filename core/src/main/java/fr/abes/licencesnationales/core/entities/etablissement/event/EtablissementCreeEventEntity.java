package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("cree")
@Getter
@Setter
public class EtablissementCreeEventEntity  extends EtablissementEventEntity {

    @Deprecated
    public EtablissementCreeEventEntity() {
        super();
    }

    public EtablissementCreeEventEntity(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return "EditeurCreeEventEntity {" + "id=" + id + ", événement=cree, nom de l'établissement=" + nomEtab + " }";
    }
}
