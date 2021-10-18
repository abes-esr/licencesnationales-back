package fr.abes.licencesnationales.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO A quoi sert cette classe ?
 */
@Setter
@Getter
public class MailDto {
    private String[] to;
    private String[] cc;
    private String[] cci;
    private String subject;
    private String text;
}
