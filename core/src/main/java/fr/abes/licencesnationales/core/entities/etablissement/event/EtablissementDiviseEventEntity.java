package fr.abes.licencesnationales.core.entities.etablissement.event;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;

@Entity
@DiscriminatorValue("divise")
@NoArgsConstructor
@Getter @Setter
public class EtablissementDiviseEventEntity  extends EtablissementEventEntity {

    @Column(name = "ANCIEN_SIREN")
    private String ancienSiren;

    private transient List<EtablissementEntity> etablissementDivises;

    @Lob
    @Column(name = "ETABLISSEMENTS_DIVISE")
    private String etablisementsDivisesInBdd;

    public EtablissementDiviseEventEntity(Object source, String ancienSiren) {
        super(source);
        this.ancienSiren = ancienSiren;
    }

    @Override
    public String toString() {
        return "EditeurDiviseEventEntity {" + "id=" + id + ", événement=divise, nom de l'établissement=" + nomEtab + " }";
    }
}
