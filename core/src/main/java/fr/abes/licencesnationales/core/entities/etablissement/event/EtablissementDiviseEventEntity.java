package fr.abes.licencesnationales.core.entities.etablissement.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@DiscriminatorValue("divise")
@NoArgsConstructor
@Getter @Setter
public class EtablissementDiviseEventEntity  extends EtablissementEventEntity {

    @Column(name = "ANCIEN_SIREN")
    private String ancienSiren;
    @Column(name = "ANCIEN_NOM_ETAB")
    private String ancienNomEtab;

    @Lob
    @Column(name = "ETABLISEMENTS_DIVISE")
    private String etablisementsDivises;

    public EtablissementDiviseEventEntity(Object source, String ancienSiren) {
        super(source);
        this.ancienSiren = ancienSiren;
    }

    @Override
    public String toString() {
        return "EditeurDiviseEventEntity {" + "id=" + id + ", événement=divise, nom de l'établissement=" + nomEtab + " }";
    }
}
