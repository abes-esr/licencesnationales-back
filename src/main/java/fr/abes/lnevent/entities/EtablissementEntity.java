package fr.abes.lnevent.entities;


import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Etablissement")
@NoArgsConstructor
public class EtablissementEntity {

    public EtablissementEntity(Long id, String name, String adresse, String siren, String typeEtablissement, String idAbes) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "etablissement_Sequence")
    @SequenceGenerator(name = "etablissement_Sequence", sequenceName = "ETABLISSEMENT_SEQ", allocationSize = 1)
    public Long id;

    public String name;

    private String adresse;

    private String siren;

    private String typeEtablissement;

    private String idAbes;
}
