package fr.abes.licencesnationales.core.entities;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Getter
public abstract class EventEntity {

    @Column(name = "DATE_CREATION_EVENT")
    protected Date dateCreationEvent;

    @Column(name = "EVENT")
    protected String event;
}
