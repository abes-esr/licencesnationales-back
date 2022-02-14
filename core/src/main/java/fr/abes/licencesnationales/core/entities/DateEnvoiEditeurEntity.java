package fr.abes.licencesnationales.core.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "dateEnvoiEditeur")
@NoArgsConstructor
@Getter @Setter
public class DateEnvoiEditeurEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dateEnvoi_Sequence")
    @SequenceGenerator(name = "dateEnvoi_Sequence", sequenceName = "DATEENVOI_SEQ", allocationSize = 1)
    public Integer id;

    @Column(name = "dateEnvoi")
    public Date dateEnvoi = Calendar.getInstance().getTime();

    public DateEnvoiEditeurEntity(Date time) {
        this.dateEnvoi = time;
    }
}
