package fr.abes.licencesnationales.core.entities.etablissement.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("divise")
public class EtablissementDiviseEventEntity  extends EtablissementEventEntity {

    @Getter @Setter
    @Column(name = "ANCIEN_SIREN")
    private String ancienSiren;

    @Getter
    private transient Set<EtablissementEntity> etablissementDivises;

    //on empêche l'accès à cet attribut qui sera automatiquement mis à jour lors d'une mise à jour du set en transcient
    @Lob
    @Column(name = "ETABLISSEMENTS_DIVISE")
    private String etablisementsDivisesInBdd;

    private transient ObjectMapper mapper = new ObjectMapper();

    @Deprecated
    public EtablissementDiviseEventEntity() {
        super();
    }

    public EtablissementDiviseEventEntity(Object source, String ancienSiren) {
        super(source);
        this.ancienSiren = ancienSiren;
    }

    /**
     * set à la fois le set d'établissements divisés et la chaine correspondant à son équivalent en bdd
     * @param entities
     * @throws JsonProcessingException
     */
    public void setEtablissementDivises(Set<EtablissementEntity> entities) throws JsonProcessingException {
        this.etablissementDivises = entities;
        this.etablisementsDivisesInBdd = mapper.writeValueAsString(this.etablissementDivises);
    }

    @Override
    public String toString() {
        return "EditeurDiviseEventEntity {" + "id=" + id + ", événement=divise, nom de l'établissement=" + nomEtab + " }";
    }
}
