package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.MailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@Slf4j
public class EmailService {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mail.ws.url}")
    private String mailServerURL;

    @Value("${site.url}")
    private String frontBaseURL;

    @Value("${site.url}")
    private String urlSite;

    public void constructCreationCompteEmailAdmin(Locale locale, String emailUser, String siren, String nomEtab) throws RestClientException {
        String message = messageSource.getMessage("message.CreationCompteAdmin", null, locale);
        message +=nomEtab + " avec le siren " + siren;
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(Locale locale,  String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.CreationCompteUser", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmail(Locale locale, String token, String emailUser, String nomEtab) throws RestClientException {
        final String url = this.frontBaseURL + "/reinitialisationPass?token=" + token;
        String objetMsg = messageSource.getMessage("message.resetTokenEmailObjet",null, locale);
        String message = messageSource.getMessage("message.resetTokenEmailDebut",null, locale);
        message +=nomEtab + " ";
        message += messageSource.getMessage("message.resetTokenEmailMilieu", null, locale);
        message += " \r\n" + url;
        message += " \r\n" + messageSource.getMessage("message.resetTokenEmailFin", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, null, objetMsg, message);
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(Locale locale, String emailUser) throws RestClientException {
        final String message = messageSource.getMessage("message.validationNewPass", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, null,"LN Nouveau mot de passe enregistré", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesModifieEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.modificationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la modification de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, null,"LN Modification Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesCreeEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.creationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la création de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, null,"LN Creation Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMail(String motifSuppression, String nomEtab, String emailUser) throws RestClientException {
        String message = "Bonjour,\n" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr vient d'être supprimé.\n" +
                "Raison de la suppression : \n" + motifSuppression + "\n" + "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n" +
                "Bien cordialement,\n" +
                "L’équipe Licences nationales\n" +
                urlSite;
        String jsonRequestConstruct = mailToJSON(emailUser, null,"Suppression de votre compte Licences Nationales", message);
        sendMail(jsonRequestConstruct);
    }


    public void sendMail(String requestJson) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.postForObject(mailServerURL + "/htmlMail/", entity, String.class);
    }


    protected String mailToJSON(String to, String cc, String subject, String text) {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        MailDto mail = new MailDto();
        mail.setTo(to.split(";"));
        if (cc != null) {
            mail.setCc(cc.split(";"));
        } else {
            mail.setCc(new String[]{});
        }
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

    public void constructSuppresionIpMail(String ip, String nomEtab, String to, String cc) {
        StringBuilder message = new StringBuilder("Bonjour,");
        message.append(System.lineSeparator());
        message.append("en l'absence de document certifiant l'appartenance de l'IP ");
        message.append(ip);
        message.append(", déclarée il y a un an sous le compte '");
        message.append(nomEtab);
        message.append("', à l'établissement, nous procédons à sa suppression automatique.");
        message.append(System.lineSeparator());
        message.append("Pour toute question, contacter l'équipe d'assistance de l'Abes : ");
        message.append("<a href='https://stp.abes.fr/node/3?origine=LicencesNationales'>https://stp.abes.fr/node/3?origine=LicencesNationales</a>");
        message.append(System.lineSeparator());
        message.append("Bien cordialement,");
        message.append(System.lineSeparator());
        message.append("L'équipe Licences Nationales");
        message.append(System.lineSeparator());
        message.append("<a href='");
        message.append(urlSite);
        message.append("'>");
        message.append(urlSite);
        message.append("</a>");
        String jsonRequestConstruct = mailToJSON(to, cc, "Suppression des IP en validation de votre compte Licences nationales", message.toString());
        sendMail(jsonRequestConstruct);
    }
}
