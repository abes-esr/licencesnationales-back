package fr.abes.lnevent.event;

import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.UUID;

public abstract class Event extends ApplicationEvent {
    public UUID id;
    public Date created;

    public Event(Object source, UUID id, Date created) {
        super(source);
        this.id = id;
        this.created = created;
    }

    public Event(Object source) {
        super(source);
        id = UUID.randomUUID();
        created = new Date();
    }
}
