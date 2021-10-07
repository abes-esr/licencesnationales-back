package fr.abes.licencesnationales.core.entities.etablissement.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Set;

@Entity
@DiscriminatorValue("fusionne")
public class EtablissementFusionneEventEntity  extends EtablissementEventEntity {
    @Getter
    private transient Set<String> sirenAnciensEtablissements;

    @Lob
    @Column(name = "ANCIENS_ETABLISSEMENTS")
    private String anciensEtablissementsInBdd;

    private transient ObjectMapper mapper = new ObjectMapper();


    @Deprecated
    public EtablissementFusionneEventEntity() {
        super();
    }


    public EtablissementFusionneEventEntity(Object source, String siren, Set<String> sirenAnciensEtablissements) throws JsonProcessingException {
        super(source);
        this.siren = siren;
        setSirenAnciensEtablissements(sirenAnciensEtablissements);
    }

    public void setSirenAnciensEtablissements(Set<String> sirenAnciensEtablissements) throws JsonProcessingException {
        this.sirenAnciensEtablissements = sirenAnciensEtablissements;
        this.anciensEtablissementsInBdd = mapper.writeValueAsString(this.sirenAnciensEtablissements);
    }

    public void addSirenAnciensEtablissements(String siren) throws JsonProcessingException {
        this.sirenAnciensEtablissements.add(siren);
        this.anciensEtablissementsInBdd = mapper.writeValueAsString(this.sirenAnciensEtablissements);
    }

    @Override
    public String toString() {
        return "EditeurFusionneEventEntity {" + "id=" + id + ", événement=fusionne, nom de l'établissement=" + nomEtab + " }";
    }
}
