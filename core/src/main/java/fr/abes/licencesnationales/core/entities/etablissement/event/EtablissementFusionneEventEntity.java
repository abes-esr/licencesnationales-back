package fr.abes.licencesnationales.core.entities.etablissement.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;

@Entity
@DiscriminatorValue("fusionne")
@Getter
@Setter
public class EtablissementFusionneEventEntity  extends EtablissementEventEntity {
    private transient List<String> sirenAnciensEtablissements;

    @Lob
    @Column(name = "ANCIENS_ETABLISSEMENTS")
    private String anciensEtablissementsInBdd = null;

    @Deprecated
    public EtablissementFusionneEventEntity() {
        super();
    }

    public EtablissementFusionneEventEntity(Object source, String siren) {
        super(source);
        this.siren = siren;
    }

    public EtablissementFusionneEventEntity(Object source, String siren, List<String> sirenAnciensEtablissements) {
        super(source);
        this.siren = siren;
        this.sirenAnciensEtablissements = sirenAnciensEtablissements;
    }

    @Override
    public String toString() {
        return "EditeurFusionneEventEntity {" + "id=" + id + ", événement=fusionne, nom de l'établissement=" + nomEtab + " }";
    }
}
