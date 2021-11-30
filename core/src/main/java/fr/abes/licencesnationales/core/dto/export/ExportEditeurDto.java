package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExportEditeurDto {
    private String id;
    private String nom;
    private String adresse;
    private List<ContactEditeurDto> contact = new ArrayList<>();

    public void addContact(ContactEditeurDto contact){
        this.contact.add(contact);
    }
}
