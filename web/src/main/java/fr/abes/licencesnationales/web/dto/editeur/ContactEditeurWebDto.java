package fr.abes.licencesnationales.web.dto.editeur;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactEditeurWebDto {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("nom")
    public String nomContact;
    @JsonProperty("prenom")
    public String prenomContact;
    @JsonProperty("mail")
    public String mailContact;

    public ContactEditeurWebDto(String nomContact, String prenomContact, String mailContact) {
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
    }
}
