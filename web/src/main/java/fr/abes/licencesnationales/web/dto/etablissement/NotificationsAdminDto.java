package fr.abes.licencesnationales.web.dto.etablissement;

import fr.abes.licencesnationales.core.dto.NotificationAdminDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotificationsAdminDto {
    List<NotificationAdminDto> notif = new ArrayList<>();

    public void ajouterListNotif(List<NotificationAdminDto> notif) {
        this.notif.addAll(notif);
    }
}
