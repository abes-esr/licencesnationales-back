package fr.abes.licencesnationales.core.entities.statut;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("etab")
public class StatutEtablissementEntity extends StatutEntity implements Serializable {
    public StatutEtablissementEntity(Integer id, String libelle) {
        super(id, libelle);
    }

    public StatutEtablissementEntity() {
        super();
    }
}
