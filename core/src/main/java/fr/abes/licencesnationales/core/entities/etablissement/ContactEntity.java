package fr.abes.licencesnationales.core.entities.etablissement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Table(name = "Contact")
@Getter
@Setter
public class ContactEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_Sequence")
    @SequenceGenerator(name = "contact_Sequence", sequenceName = "CONTACT_SEQ", allocationSize = 1)
    private Integer id;

    @NotNull
    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le nom fourni n'est pas valide")
    private String nom;

    @NotNull
    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    private String prenom;

    @NotNull
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "L'adresse mail fournie n'est pas valide")
    private String mail;

    /**
     * Mot de passe crypté
     */
    private String motDePasse;

    @NotNull
    @Pattern(regexp = "^\\d{10}$", message = "Veuillez entrer 10 chiffres sans espace")
    private String telephone;

    @NotNull
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresse;

    private String boitePostale;

    @NotNull
    @Pattern(regexp = "^\\d{5}$", message = "Le code postal fourni n'est pas valide")
    private String codePostal;

    private String cedex;

    @NotNull
    @Pattern(regexp = "^([a-zA-Z\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z\\u0080-\\u024F]*$", message = "La ville fournie n'est pas valide")
    private String ville;

    private String role = "etab";

    @Deprecated
    public ContactEntity() {

    }

    /**
     * CTOR d'un contact sans le rôle est par défaut à 'etab'.
     *
     * @param prenom       Prénom du contact
     * @param mail         Email du contact
     * @param telephone    Numéro de téléphone du contact
     * @param adresse      Adresse postale du contact
     * @param boitePostale Numéro de boîte postale du contact
     * @param codePostal   Code postal du contact
     * @param cedex        Numéro du cedex du contact
     * @param ville        Ville du contact
     * @param motDePasse   Mot de passe du contact
     */
    public ContactEntity(String nom, String prenom, String adresse, String boitePostale, String codePostal, String ville, String cedex, String telephone, String mail, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.setMotDePasse(motDePasse);
        this.mail = mail;
        this.telephone = telephone;
        this.adresse = adresse;
        this.boitePostale = boitePostale;
        this.codePostal = codePostal;
        this.ville = ville;
        this.cedex = cedex;
    }

    public ContactEntity(String nom, String prenom, String adresse, String codePostal, String ville, String telephone, String mail, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }
    /**
     * CTOR d'un contact avec un identifiant et le rôle par défaut à 'etab'.
     *
     * @param id           Identifiant du contact
     * @param prenom       Prénom du contact
     * @param mail         Email du contact
     * @param telephone    Numéro de téléphone du contact
     * @param adresse      Adresse postale du contact
     * @param boitePostale Numéro de boîte postale du contact
     * @param codePostal   Code postal du contact
     * @param cedex        Numéro du cedex du contact
     * @param ville        Ville du contact
     * @param motDePasse   Mot de passe du contact
     */
    public ContactEntity(Integer id, String nom, String prenom, String adresse, String boitePostale, String codePostal, String ville, String cedex, String telephone, String mail, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.motDePasse = motDePasse;
        this.mail = mail;
        this.telephone = telephone;
        this.adresse = adresse;
        this.boitePostale = boitePostale;
        this.codePostal = codePostal;
        this.ville = ville;
        this.cedex = cedex;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return id != null && id.equals(((ContactEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "ContactEntity {" + "id=" + id + ", nom=" + nom + " }";
    }
}
