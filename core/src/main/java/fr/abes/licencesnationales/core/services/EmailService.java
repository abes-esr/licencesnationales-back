package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.MailDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.repository.ContactTechniqueEditeurRepository;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
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
import java.util.Set;

@Component
@Slf4j
public class EmailService {
    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private ContactTechniqueEditeurRepository contactTechniqueEditeurRepository;

    @Autowired
    private MessageSource messageSource;

    @Value("${mail.ws.url}")
    protected String url;

    public boolean checkDoublonMail(Set<ContactCommercialEditeurDto> c, Set<ContactTechniqueEditeurDto> t) {
        log.info("DEBUT checkDoublonMail ");
        boolean existeMailCommercial = false;
        boolean existeMailTechnique = false;
        String mail;
        for (ContactCommercialEditeurDto contact : c){
            mail = contact.getMailContactCommercial();
            log.info("mail = "+ mail);
            existeMailCommercial = editeurRepository.existeMail(mail);
        }
        for (ContactTechniqueEditeurDto contact : t){
            mail = contact.getMailContactTechnique();
            log.info("mail = "+ mail);
            existeMailTechnique = contactTechniqueEditeurRepository.existeMail(mail);
        }
        log.info("existeMailCommercial = "+ existeMailCommercial);
        log.info("existeMailTechnique = "+ existeMailTechnique);
        return existeMailCommercial || existeMailTechnique;
    }

    public void constructCreationCompteEmailAdmin(Locale locale, String emailUser, String siren, String nomEtab) throws RestClientException {
        String message = messageSource.getMessage("message.CreationCompteAdmin", null, locale);
        message +=nomEtab + " avec le siren " + siren;
        String jsonRequestConstruct = mailToJSON(emailUser,  messageSource.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(Locale locale, String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.CreationCompteUser", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, messageSource.getMessage("message.NouveauCompteCree",null,locale), message);
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmail(String contextPath, Locale locale, String token, String emailUser, String nomEtab) throws RestClientException {
        final String url = contextPath + "/reinitialisationPass?token=" + token; //test
        String objetMsg = messageSource.getMessage("message.resetTokenEmailObjet",null, locale);
        String message = messageSource.getMessage("message.resetTokenEmailDebut",null, locale);
        message +=nomEtab + " ";
        message += messageSource.getMessage("message.resetTokenEmailMilieu", null, locale);
        message += " \r\n" + url;
        message += " \r\n" + messageSource.getMessage("message.resetTokenEmailFin", null, locale);
        String jsonRequestConstruct = mailToJSON(emailUser, objetMsg, message);
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(String emailUser) throws RestClientException {
        final String message = messageSource.getMessage("message.validationNewPass", null, Locale.FRANCE);
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Nouveau mot de passe enregistré", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesModifieEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.modificationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la modification de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Modification Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructAccesCreeEmail(Locale locale, String descriptionAcces, String commentaires, String emailUser) throws RestClientException {
        String message = messageSource.getMessage("message.creationAcces", null, locale);
        message += "\r\n" + descriptionAcces;
        message += "\r\n Commentaires liés à la création de l'accès : " + commentaires;
        String jsonRequestConstruct = mailToJSON(emailUser, "LN Creation Acces", message);
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMail(Locale locale, String motifSuppression, String nomEtab, String emailUser) throws RestClientException {
        String message = "Bonjour,\n" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr vient d'être supprimé.\n" +
                "Raison de la suppression : \n" + motifSuppression + "\n" + "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n" +
                "Bien cordialement,\n" +
                "L’équipe Licences nationales\n" +
                "https://acces.licencesnationales.fr/";
        String jsonRequestConstruct = mailToJSON(emailUser, "Suppression de votre compte Licences Nationales", message);
        sendMail(jsonRequestConstruct);
    }


    public void sendMail(String requestJson) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate(); //appel ws qui envoie le mail
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.postForObject(url + "htmlMail/", entity, String.class); //appel du ws avec
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
