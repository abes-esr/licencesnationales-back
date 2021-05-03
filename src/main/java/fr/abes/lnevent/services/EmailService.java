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
        return constructEmail("Nouveau mot de passe enregistré", message , emailUser);
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
        log.info("getAppUrl = http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        return "http://" + request.getServerName();
    }
}
