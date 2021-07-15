package fr.abes.lnevent.dto.editeur;


import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;


@Getter
public class ContactCommercialEditeurDTO {


    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le nom fourni n'est pas valide")
    public String nomContactCommercial;


    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    public String prenomContactCommercial;


    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "L'adresse mail fournie n'est pas valide")
    public String mailContactCommercial;



}
