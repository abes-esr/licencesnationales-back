package fr.abes.licencesnationales.core.entities.etablissement;

import fr.abes.licencesnationales.core.event.password.UpdatePasswordEvent;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PasswordEvent")
@NoArgsConstructor
public class PasswordEventEntity implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "passwordevent_Sequence")
    @SequenceGenerator(name = "passwordevent_Sequence", sequenceName = "PASSWORDEVENT_SEQ", allocationSize = 1)
    public Long id;

    String siren;
    String motDePasse;
    @Column(name = "DATE_CREATION_EVENT")
    public Date dateCreationEvent;

    @Column(name = "EVENT")
    public String event;

    /**
     * CTOR à partir d'un événement de modification de mot de passe
     * @param updatePasswordEvent Evénement de modification de mot de passe
     */
    public PasswordEventEntity(UpdatePasswordEvent updatePasswordEvent) {
        this.event = "motDePasseMisAJour";
        this.dateCreationEvent = updatePasswordEvent.created;
        this.siren = updatePasswordEvent.getSiren();
        this.motDePasse = updatePasswordEvent.getNewpasswordHash();
    }
}
