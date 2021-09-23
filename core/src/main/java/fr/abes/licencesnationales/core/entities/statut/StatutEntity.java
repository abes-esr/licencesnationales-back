package fr.abes.licencesnationales.core.entities.statut;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Statut")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar(5)")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class StatutEntity implements Serializable {
    @Id
    protected int idStatut;

    @Column(name = "libelle")
    protected String libelleStatut;
}