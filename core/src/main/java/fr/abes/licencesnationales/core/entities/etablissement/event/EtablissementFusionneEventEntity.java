package fr.abes.licencesnationales.core.entities.etablissement.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;

@Entity
@DiscriminatorValue("fusionne")
@NoArgsConstructor
@Getter
@Setter
public class EtablissementFusionneEventEntity  extends EtablissementEventEntity {
    @Autowired
    private ObjectMapper mapper;

    //TODO: Définir la différence avec etablissementsFusionne
    @Lob
    @Column(name = "ETABLISSEMENTDTOFUSION")
    private String etablissementsFusionnes;

    //TODO: Définir la différence avec etablissementsFusionnes
    @Lob
    @Column(name = "ETABLISSEMENTS_FUSIONNE")
    private String etablissementsFusionne;

    public EtablissementFusionneEventEntity(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return "EditeurFusionneEventEntity {" + "id=" + id + ", événement=fusionne, nom de l'établissement=" + nomEtab + " }";
    }
}
