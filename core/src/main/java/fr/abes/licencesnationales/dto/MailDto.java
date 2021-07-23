package fr.abes.licencesnationales.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MailDto {
    private String[] to;
    private String[] cc;
    private String[] cci;
    private String subject;
    private String text;
}
