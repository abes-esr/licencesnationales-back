package fr.abes.licencesnationales.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe permettant de créer un objet mail qui sera envoyé au ws
 * Contient toutes les informations nécessaires au mail : destinataire(s), copie(s), copie(s) cachée(s), sujet et corps
 */
@Setter
@Getter
public class MailDto {
    private String app;
    private String[] to;
    private String[] cc;
    private String[] cci;
    private String subject;
    private String text;
}
