package fr.abes.lnevent.services;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
@Slf4j
@ConfigurationProperties(prefix = "mail.set.from")
public class EmailService {


    @Autowired
    private MessageSource messages;
    private String noreply;



    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, String emailUser, String nomEtab) {
        final String url = contextPath + "/reinitialisationPass?token=" + token;
        String objetMsg = messages.getMessage("message.resetTokenEmailObjet",null, locale);
        String message = messages.getMessage("message.resetTokenEmailDebut",null, locale);
        message +=nomEtab + " ";
        message += messages.getMessage("message.resetTokenEmailMilieu", null, locale);
        message += " \r\n" + url;
        message += " \r\n" + messages.getMessage("message.resetTokenEmailFin", null, locale);
        return constructEmail(objetMsg, message, emailUser);
    }

    public SimpleMailMessage constructValidationNewPassEmail(Locale locale, String emailUser){
        final String message = messages.getMessage("message.validationNewPass", null, locale);
        return constructEmail("LN Nouveau mot de passe enregistré", message , emailUser);
    }

    public SimpleMailMessage constructAccesModifieEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser){
        String message = messages.getMessage("message.modificationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la modification de l'accès : " + commentaires;
        return constructEmail("LN Modification Acces", message , emailUser);
    }
    public SimpleMailMessage constructAccesCreeEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser){
        String message = messages.getMessage("message.creationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la création de l'accès : " + commentaires;
        return constructEmail("LN Creation Acces", message , emailUser);
    }

    public SimpleMailMessage constructSuppressionMail(Locale locale, String motifSuppression, String nomEtab, String emailUser){
        String message = "Bonjour,\n" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr vient d'être supprimé.\n" +
                "Raison de la suppression : \n" + motifSuppression + "\n" + "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n" +
                "Bien cordialement,\n" +
                "L’équipe Licences nationales\n" +
                "https://acces.licencesnationales.fr/";
        return constructEmail("Suppression de votre compte Licences Nationales", message , emailUser);
    }

    private SimpleMailMessage constructEmail(String subject, String body, String emailUser) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(emailUser);
        email.setFrom(noreply);
        return email;
    }

    public String getAppUrl(HttpServletRequest request) {
        log.info("getAppUrl = https://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        return "https://" + request.getServerName();
    }
}
