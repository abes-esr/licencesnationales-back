package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class NotificationsDto {
    List<Map<String, String>> notifications = new ArrayList<>();

    public void ajouterNotification(Map<String,String> notification) {
        this.notifications.add(notification);
    }
}
