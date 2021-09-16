package fr.abes.licencesnationales.core.entities.etablissement.event;

import fr.abes.licencesnationales.core.entities.EventEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EtablissementEvent")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event", columnDefinition = "varchar(20)", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class EtablissementEventEntity extends EventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etabevent_Sequence")
    @SequenceGenerator(name = "etabevent_Sequence", sequenceName = "ETABEVENT_SEQ", allocationSize = 1)
    protected Integer id;

    @Column(name = "NOM_ETAB")
    protected String nomEtab;

    @Column(name = "SIREN")
    protected String siren;

    @Column(name = "ADRESSE")
    protected String adresse;

    @Column(name = "TYPE_ETABLISSEMENT")
    protected String typeEtablissement;

    @Column(name = "MOT_DE_PASSE")
    protected String motDePasse;

    @Column(name = "ID_ABES")
    protected String idAbes;

    @Column(name = "MAIL_CONTACT")
    protected String mailContact;

    @Column(name = "NOM_CONTACT")
    protected String nomContact;

    @Column(name = "PRENOM_CONTACT")
    protected String prenomContact;

    @Column(name = "TELEPHONE_CONTACT")
    protected String telephoneContact;

    @Column(name = "ADRESSE_CONTACT")
    protected String adresseContact;

    @Column(name = "BOITE_POSTALE_CONTACT")
    protected String boitePostaleContact;

    @Column(name = "CODE_POSTAL_CONTACT")
    protected String codePostalContact;

    @Column(name = "CEDEX_CONTACT")
    protected String cedexContact;

    @Column(name = "VILLE_CONTACT")
    protected String villeContact;

    @Column(name = "ROLE_CONTACT")
    protected String roleContact;


    public EtablissementEventEntity(Object source) {
        super(source);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return id != null && id.equals(((EtablissementEventEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2021;
    }

}
