package fr.abes.licencesnationales.core.entities.editeur.event;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;

public class EditeurSupprimeEventEntity extends EditeurEventEntity {


    public EditeurSupprimeEventEntity(Object source, Integer id) throws JsonProcessingException {
        super(source);
        this.event = "editeurSupprime";
        this.dateCreationEvent = new Date();
        this.id = id;
    }
}
