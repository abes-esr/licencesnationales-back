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
    protected String mailServerURL;

    @Value("${site.url}")
    protected String frontBaseURL;

    public void constructCreationCompteEmailAdmin(Locale locale, String emailUser, String siren, String nomEtab) throws RestClientException {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.CreationCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser,  messageSource.getMessage("message.NouveauCompteCree",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(Locale locale,  String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.CreationCompteUser", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmail(Locale locale, String token, String emailUser, String nomEtab) throws RestClientException {
        final String url = this.frontBaseURL + "/reinitialisationPass?token=" + token;
        String objetMsg = messageSource.getMessage("message.resetTokenEmailObjet",null, locale);
        String message = messageSource.getMessage("message.resetTokenEmailDebut",null, locale);
        message +=nomEtab + " ";
        message += messageSource.getMessage("message.resetTokenEmailMilieu", null, locale);
        message += " <br>" + url;
        message += " <br>" + messageSource.getMessage("message.resetTokenEmailFin", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.resetTokenEmailObjet",null, locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(Locale locale, String emailUser) throws RestClientException {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.validationNewPass", null, locale));
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Nouveau mot de passe enregistré", message.toString());
        sendMail(jsonRequestConstruct);
    }


    public void constructSuppressionCompteMailUser(Locale locale, String motifSuppression, String nomEtab, String emailUser) throws RestClientException {
        StringBuilder message = new StringBuilder();
        message.append("Bonjour,\n");
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être supprimé.\n");
        message.append("Raison de la suppression : \n");
        message.append(motifSuppression);
        message.append("\n");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n");
        message.append("Bien cordialement,\n");
        message.append("L’équipe Licences nationales\n");
        message.append("https://acces.licencesnationales.fr/");
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.CompteSupprimeUser",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailAdmin(Locale locale, String motifSuppression, String emailUser, String nomEtab, String siren) {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.suppressionCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        message.append(" pour le motif : ");
        message.append(motifSuppression);
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.CompteSupprimeAdmin",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailAdmin(Locale locale, String emailUser, String nomEtab, String siren) {
        StringBuilder message = new StringBuilder();
        message.append(messageSource.getMessage("message.validationCompteAdmin", null, locale));
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.CompteValideAdmin",null,locale), message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailUser(Locale locale, String nomEtab, String emailUser) {
        StringBuilder message = new StringBuilder();
        message.append("Bonjour,\n");
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être validé par l'administrateur.\n");
        message.append("\n");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n");
        message.append("Bien cordialement,\n");
        message.append("L’équipe Licences nationales\n");
        message.append("https://acces.licencesnationales.fr/");
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.CompteValideUser",null,locale), message.toString());
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
