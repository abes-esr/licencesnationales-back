package fr.abes.licencesnationales.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TypeEtablissement")
public class TypeEtablissementEntity implements Serializable {
    @Id
    private Integer id;
    @Column(name = "Libelle")
    private String libelle;
}
