package fr.abes.lnevent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;



@ApiModel
@NoArgsConstructor
@Getter @Setter
@Table(name= "UTILISATEUR")
public class User {


    @NotNull(message = "Le siren ne doit pas être null")
    @NotEmpty(message = "Le siren ne doit pas être vide")
    @ApiModelProperty(value = "identifiant siren", name = "userName", dataType = "String", example = "266706027")
    private String username;

    @NotNull(message = "Le mot de passe ne doit pas être null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Le mot de passe ne respecte pas les règles de sécurité")
    @ApiModelProperty(value = "mot de passe de l'utilisateur", name = "passWord", dataType = "String", example = "motDePasseC0!mplex")
    private String passWord;

    private Boolean isAdmin;

    private String nomEtab;

    public User(String siren, String password, Boolean isAdmin, String nomEtab ) {
        this.username = siren;
        this.passWord = password;
        this.isAdmin = isAdmin;
        this.nomEtab = nomEtab;
    }
}
