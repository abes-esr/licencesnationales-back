package fr.abes.lnevent.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.lnevent.constant.Constant;
import fr.abes.lnevent.dto.mail.MailDto;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@Slf4j
public class EmailService {


    @Autowired
    private MessageSource messages;

    @Value("${mail.ws.url}")
    protected String url;

    public void constructCreationCompteEmailAdmin(Locale locale, String emailUser, String siren, String nomEtab){
        String message = messages.getMessage("message.CreationCompteAdmin", null, locale);
        message +=nomEtab + " avec le siren " + siren;
        String jsonRequestConstruct = mailToJSON(emailUser,  messages.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(Locale locale, String emailUser){
        String message = messages.getMessage("message.CreationCompteUser", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, messages.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmail(String contextPath, Locale locale, String token, String emailUser, String nomEtab) {
        final String url = contextPath + "/reinitialisationPass?token=" + token; //test
        String objetMsg = messages.getMessage("message.resetTokenEmailObjet",null, locale);
        String message = messages.getMessage("message.resetTokenEmailDebut",null, locale);
        message +=nomEtab + " ";
        message += messages.getMessage("message.resetTokenEmailMilieu", null, locale);
        message += " \r\n" + url;
        message += " \r\n" + messages.getMessage("message.resetTokenEmailFin", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, objetMsg, message);
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(Locale locale, String emailUser){
        final String message = messages.getMessage("message.validationNewPass", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Nouveau mot de passe enregistré", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesModifieEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser){
        String message = messages.getMessage("message.modificationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la modification de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Modification Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesCreeEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser){
        String message = messages.getMessage("message.creationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la création de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Creation Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMail(Locale locale, String motifSuppression, String nomEtab, String emailUser){
        String message = "Bonjour,\n" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr vient d'être supprimé.\n" +
                "Raison de la suppression : \n" + motifSuppression + "\n" + "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n" +
                "Bien cordialement,\n" +
                "L’équipe Licences nationales\n" +
                "https://acces.licencesnationales.fr/";
        String jsonRequestConstruct = mailToJSON(emailUser, "Suppression de votre compte Licences Nationales", message);
        sendMail(jsonRequestConstruct);
    }

    public String getAppUrl(HttpServletRequest request) { //https vers les serveurs
        log.info("getAppUrl = https://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath());
        return "https://" + request.getServerName();//test
        //return "http://" + request.getServerName() + ":8080";//dev
    }


    public void sendMail(String requestJson) {
        RestTemplate restTemplate = new RestTemplate(); //appel ws qui envoie le mail
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestJson, headers);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        try {
            restTemplate.postForObject(url + "htmlMail/", entity, String.class); //appel du ws avec
        } catch (Exception e) {
            log.error(Constant.ERROR_SENDING_MAIL_END_OF_TREATMENT + e.toString());
        }
    }


    protected String mailToJSON(String to, String subject, String text) {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        MailDto mail = new MailDto();
        mail.setTo(to.split(";"));
        mail.setCc(new String[]{});
        mail.setCci(new String[]{});
        mail.setSubject(subject);
        mail.setText(text);
        try {
            json = mapper.writeValueAsString(mail);
        } catch (JsonProcessingException e) {
            log.error(Constant.ERROR_CONVERSION_MAIL_TO_JSON + e.toString());
        }
        return json;
    }

}
