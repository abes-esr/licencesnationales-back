package fr.abes.licencesnationales.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class NotificationAdminDto {
    private Date dateEvent;
    private String typeNotif;
    private String nomEtab;
}
