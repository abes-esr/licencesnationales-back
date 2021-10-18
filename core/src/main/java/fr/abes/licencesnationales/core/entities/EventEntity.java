package fr.abes.licencesnationales.core.entities;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.persistence.*;
import java.util.Date;


@MappedSuperclass
@Getter
public abstract class EventEntity extends ApplicationEvent {

    @Column(name = "DATE_CREATION_EVENT")
    protected Date dateCreationEvent;

    @Deprecated
    public EventEntity() {
        super(new Object());
        this.dateCreationEvent = new Date();
    }

    public EventEntity(Object source) {
        super(source);
        dateCreationEvent = new Date();
    }

    public EventEntity(Object source, Date created) {
        super(source);
        this.dateCreationEvent = created;
    }

    public void setSource(Object source) {
        this.source = source;
    }

}
