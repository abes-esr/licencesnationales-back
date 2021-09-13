package fr.abes.licencesnationales.core.entities.statut;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("ip")
public class StatutIpEntity extends StatutEntity implements Serializable {
}
