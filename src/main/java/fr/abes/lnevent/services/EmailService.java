package fr.abes.lnevent.services;

import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
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


    /*public void sendSimpleMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@licencesnationales.fr");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }*/

    /*private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }*/

    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, UserDetailsImpl user) {
        final String url = contextPath + "/reinitialisationPass?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reinitialisation mot de passe", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, UserDetailsImpl user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(noreply);
        return email;
    }

    public String getAppUrl(HttpServletRequest request) {
        log.info("getAppUrl = http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        return "http://" + request.getServerName() + ":8080"  + request.getContextPath();
    }
}
