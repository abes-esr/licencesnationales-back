package fr.abes.licencesnationales.web.dto.editeur;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditeurSearchWebDto {
    private Integer id;
    private String nom;
    private String idEditeur;
    private String adresse;
    private List<ContactEditeurWebDto> contactsTechniques = new ArrayList<>();
    private List<ContactEditeurWebDto> contactsCommerciaux = new ArrayList<>();

    public void ajouterContactCommercial(ContactEditeurWebDto contact) {
        this.contactsCommerciaux.add(contact);
    }

    public void ajouterContactTechnique(ContactEditeurWebDto contact) {
        this.contactsTechniques.add(contact);
    }
}
