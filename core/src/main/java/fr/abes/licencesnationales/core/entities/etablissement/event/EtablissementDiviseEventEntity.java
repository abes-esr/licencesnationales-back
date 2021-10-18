package fr.abes.licencesnationales.core.entities.etablissement.event;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("divise")
@Getter @Setter
public class EtablissementDiviseEventEntity  extends EtablissementEventEntity {

    @Column(name = "ANCIEN_SIREN")
    private String ancienSiren;

    private transient Set<EtablissementEntity> etablissementDivises = new HashSet<>();

    //ne pas oublier d'initialiser cet attribut au moment de la sauvegarde ou de la récupération de l'entité
    // à partir ou vers le set ci-dessus
    @Lob
    @Column(name = "ETABLISSEMENTS_DIVISE")
    private String etablisementsDivisesInBdd;


    @Deprecated
    public EtablissementDiviseEventEntity() {
        super();
    }

    public EtablissementDiviseEventEntity(Object source, String ancienSiren) {
        super(source);
        this.ancienSiren = ancienSiren;
    }

    @Override
    public String toString() {
        return "EditeurDiviseEventEntity {" + "id=" + id + ", événement=divise, nom de l'établissement=" + nomEtab + " }";
    }
}
