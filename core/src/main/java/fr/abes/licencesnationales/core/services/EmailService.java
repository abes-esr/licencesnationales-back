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
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.CreationCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser,  null, messageSource.getMessage("message.NouveauCompteCree",null,locale), message.toString());
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
        StringBuilder message = new StringBuilder(messageSource.getMessage("message.resetTokenEmailDebut",null, locale));
        message.append(nomEtab);
        message.append(" ");
        message.append(messageSource.getMessage("message.resetTokenEmailMilieu", null, locale));
        message.append(" <br>");
        message.append(url);
        message.append(" <br>");
        message.append(messageSource.getMessage("message.resetTokenEmailFin", null, locale));
        String jsonRequestConstruct = mailToJSON(emailUser, null, objetMsg, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(Locale locale, String emailUser) throws RestClientException {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.validationNewPass", null, locale));
        String jsonRequestConstruct = mailToJSON(emailUser, null, "LN Nouveau mot de passe enregistré", message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailUser(Locale locale, String nomEtab, String emailUser) throws RestClientException {
        StringBuilder message = new StringBuilder();
        message.append("Bonjour,<br>");
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être supprimé.<br>");
        message.append("<br>");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales<br>");
        message.append("Bien cordialement,<br>");
        message.append("L’équipe Licences nationales<br>");
        message.append("<br>");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n");
        message.append("Bien cordialement,<br>");
        message.append("L’équipe Licences nationales<br>");
        message.append("https://acces.licencesnationales.fr/");
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.CompteSupprimeUser",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailAdmin(Locale locale, String emailUser, String nomEtab, String siren) {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.suppressionCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.CompteSupprimeAdmin",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailAdmin(Locale locale, String emailUser, String nomEtab, String siren) {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.validationCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.CompteValideAdmin",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailUser(Locale locale, String nomEtab, String emailUser) {
        StringBuilder message = new StringBuilder();
        message.append("Bonjour,<br>");
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être validé par l'administrateur.<br>");
        message.append("<br>");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales<br>");
        message.append("Bien cordialement,<br>");
        message.append("L’équipe Licences nationales<br>");
        message.append("https://acces.licencesnationales.fr/");
        String jsonRequestConstruct = mailToJSON(emailUser, null, messageSource.getMessage("message.CompteValideUser",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructRelanceEtabMail(String nomEtab, String emailUser, String dateSuppression) throws RestClientException {
        String message = "Bonjour,<br>" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr ne contient pas d'adresse IP déclarée.<br>" +
                "Merci de renseigner au moins une adresse IP avant le " + dateSuppression + ", date à laquelle votre compte sera automatiquement supprimé.<br>" +
                "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales<br>" +
                "Bien cordialement,<br>" +
                "L’équipe Licences nationales<br>" +
                urlSite;
        String jsonRequestConstruct = mailToJSON(emailUser, null, "Relance pour création d'IP sur votre compte Licences Nationales", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructBilanRecapActionIp(String emailUser, List<Map<String, String>> listIps) throws RestClientException {
        StringBuilder message = new StringBuilder("Bonjour,<br>");
        message.append("Les actions suivantes viennent d'être réalisées sur les IPs de votre établissement : ");
        listIps.forEach(ip -> {
            message.append(ip.get("ip"));
            message.append(" : ");
            message.append(ip.get("action"));
            message.append("<br>");
        });
        String jsonRequestConstruct = mailToJSON(emailUser, null, "Action sur les IPs de votre établissement", message.toString());
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

    public void constructSuppresionIpMail(List<String> ipsSupprimees, List<String> ipsAttestation, String nomEtab, String to, String cc) {
        StringBuilder message = new StringBuilder("Bonjour,");
        message.append("<br>");
        message.append("en l'absence de document certifiant l'appartenance de l'IP ");
        message.append(", déclarée il y a un an sous le compte '");
        message.append(nomEtab);
        message.append("', à l'établissement, nous procédons à sa suppression automatique.");
        message.append("<br>");
        message.append("Pour toute question, contacter l'équipe d'assistance de l'Abes : ");
        message.append("<a href='https://stp.abes.fr/node/3?origine=LicencesNationales'>https://stp.abes.fr/node/3?origine=LicencesNationales</a>");
        message.append("<br>");
        message.append("Bien cordialement,");
        message.append("<br>");
        message.append("L'équipe Licences Nationales");
        message.append("<br>");
        message.append("<a href='");
        message.append(urlSite);
        message.append("'>");
        message.append(urlSite);
        message.append("</a>");
        String jsonRequestConstruct = mailToJSON(to, cc, "Suppression des IP en validation de votre compte Licences nationales", message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMail(String motif, String nom, String mail) {
        StringBuilder message = new StringBuilder(nom);
        String jsonRequestConstruct = mailToJSON(mail, null, "Suppression des IP en validation de votre compte Licences nationales", message.toString());
        sendMail(jsonRequestConstruct);
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



}
