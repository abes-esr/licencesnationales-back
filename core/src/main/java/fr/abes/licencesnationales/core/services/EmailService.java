package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.MailDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementSupprimeEventEntity;
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
    private RestTemplate restTemplate;

    @Value("${mail.ws.url}")
    private String mailServerURL;

    @Value("${site.url}")
    private String urlSite;

    public void constructCreationCompteEmailAdmin(String emailUser, EtablissementCreeEventEntity etab) throws RestClientException {
        String subject = "[Appli LN] Nouveau compte Etablissement créé";
        StringBuilder message = new StringBuilder("Le compte établissement suivant a été créé par : ");
        message.append("Prénom : ");
        message.append(etab.getPrenomContact());
        message.append(" Nom : ");
        message.append(etab.getNomContact());
        message.append(" E-Mail : ");
        message.append(etab.getMailContact());
        message.append("<br><br>");
        message.append("Etablissement : ");
        message.append(etab.getNomEtab());
        message.append("<br>Siren : ");
        message.append(etab.getSiren());
        String jsonRequestConstruct = mailToJSON(emailUser,  null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(EtablissementCreeEventEntity etab) throws RestClientException {
        String subject = "[Appli LN] Compte Etablissement créé";
        StringBuilder message = new StringBuilder("Bonjour,<br><br>");
        message.append("Vous venez de créer le compte établissement ci-dessous sur l'");
        message.append("<a href='");
        message.append(urlSite);
        message.append("' target='_blank'>application de gestion des accès aux licences nationales</a> administrée par l’Abes.<br><br>");
        message.append("Un seul compte par établissement est autorisé. L’Abes va vérifier l’éligibilité de l’établissement.<br><br>");
        message.append("<b>Nous vous invitons dès à présent à déclarer des adresses IP publiques</b> afin que votre établissement puisse bénéficier de l’accès aux ressources acquises sous licences nationales.<br><br>");
        message.append("<ul><li><b>Pour vous aider dans la déclaration des IP</b> vous pouvez consulter <a href='https://documentation.abes.fr' target='_blank'>Le Guide à destination des établissements bénéficiaires</a> rubrique ");
        message.append("<a href='http://documentation.abes.fr/aidelicencesnationales/index.html#DeclarerAdressesIP' target='_blank'>Déclarer les IP publiques</a>.</li>");
        message.append("<li>Le site <a href='");
        message.append(urlSite);
        message.append("' target='_blank'>licencesnationales.fr</a> centralise les informations sur les licences nationales et vous permettra notamment d’explorer <a href='https://www.licencesnationales.fr/les-corpus-acquis/' target='_blank'>les corpus acquis</a>.</li>");
        message.append("<li>Pour toute question contacter l’Abes via le guichet d’assistance <a href='https://stp.abes.fr/node/3?origine=LicencesNationales' target='_blank'>ABESstp</a></li></ul>");
        message.append("<br><br>Bien Cordialement,<br><br>L’équipe Licences nationales");
        message.append("<br><br><b>Infos du compte : </b><br><br>");
        message.append("<i>Etablissement : ");
        message.append(etab.getNomEtab());
        message.append("<br>");
        message.append("Siren : ");
        message.append(etab.getSiren());
        message.append("<br>");
        message.append("<br><br>CONTACT : <br>");
        message.append("Prénom : ");
        message.append(etab.getPrenomContact());
        message.append("<br>");
        message.append("Nom : ");
        message.append(etab.getNomContact());
        message.append("<br>");
        message.append("E-Mail : ");
        message.append(etab.getMailContact());
        message.append("<br>");
        message.append("Téléphone : ");
        message.append(etab.getTelephoneContact());
        message.append("<br>");
        message.append("Adresse : ");
        message.append(etab.getAdresseContact());
        message.append(" ");
        if (etab.getBoitePostaleContact() != null) {
            message.append(etab.getBoitePostaleContact());
            message.append(" ");
        }
        message.append(etab.getCodePostalContact());
        message.append(" ");
        message.append(etab.getVilleContact());
        message.append(" ");
        if (etab.getCedexContact() != null) {
            message.append(etab.getCedexContact());
        }

        String jsonRequestConstruct = mailToJSON(etab.getMailContact(),  null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmail(String token, String emailUser, String nomEtab) throws RestClientException {
        final String url = this.urlSite + "/reinitialisationPass?token=" + token;
        String subject = "";
        StringBuilder message = new StringBuilder();
        message.append(nomEtab);
        message.append(" ");
        message.append(" <br>");
        message.append(url);
        message.append(" <br>");
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationNewPassEmail(String emailUser) throws RestClientException {
        String subject = "";
        StringBuilder message = new StringBuilder();
        String jsonRequestConstruct = mailToJSON(emailUser, null, "LN Nouveau mot de passe enregistré", message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailUserEtAdmin(EtablissementSupprimeEventEntity etab, String emailUser, String emailAdmin) throws RestClientException {
        String subject = "[Appli LN] Suppression du compte Etablissement";
        StringBuilder message = new StringBuilder();
        message.append("Bonjour,<br>");
        message.append("Le compte établissement ci-dessous, a été supprimé de l’application licences nationales par l’Abes pour la raison suivante : ");
        message.append("<br>");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales<br>");
        message.append("Bien cordialement,<br>");
        message.append("L’équipe Licences nationales<br>");
        message.append("<br>");
        message.append("Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales\n");
        message.append("Bien cordialement,<br>");
        message.append("L’équipe Licences nationales<br>");
        message.append("https://acces.licencesnationales.fr/");
        String jsonRequestConstruct = mailToJSON(emailUser, emailAdmin, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailAdmin(String emailUser, String nomEtab, String siren) {
        String subject = "";
        StringBuilder message = new StringBuilder();
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailAdmin(String emailUser, String nomEtab, String siren) {
        String subject = "";
        StringBuilder message = new StringBuilder();
        message.append(nomEtab);
        message.append(" avec le siren ");
        message.append(siren);
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailUser(String nomEtab, String emailUser) {
        String subject = "";
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
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructRelanceEtabMail(String nomEtab, String emailUser, String dateSuppression) throws RestClientException {
        String subject = "Relance pour création d'IP sur votre compte Licences Nationales";
        String message = "Bonjour,<br>" +
                "Le compte que vous avez créé pour " + nomEtab + " sur le site Licencesnationales.fr ne contient pas d'adresse IP déclarée.<br>" +
                "Merci de renseigner au moins une adresse IP avant le " + dateSuppression + ", date à laquelle votre compte sera automatiquement supprimé.<br>" +
                "Pour toute question, contactez l’équipe d’assistance de l’Abes : https://stp.abes.fr/node/3?origine=LicencesNationales<br>" +
                "Bien cordialement,<br>" +
                "L’équipe Licences nationales<br>" +
                urlSite;
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message);
        sendMail(jsonRequestConstruct);
    }

    public void constructBilanRecapActionIp(String emailUser, List<Map<String, String>> listIps) throws RestClientException {
        String subject = "Action sur les IPs de votre établissement";
        StringBuilder message = new StringBuilder("Bonjour,<br>");
        message.append("Les actions suivantes viennent d'être réalisées sur les IPs de votre établissement : ");
        listIps.forEach(ip -> {
            message.append(ip.get("ip"));
            message.append(" : ");
            message.append(ip.get("action"));
            message.append("<br>");
        });
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
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
        String subject = "Suppression des IP en validation de votre compte Licences nationales";
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
        String jsonRequestConstruct = mailToJSON(to, cc, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMail(String motif, String nom, String mail) {
        String subject = "Suppression des IP en validation de votre compte Licences nationales";
        StringBuilder message = new StringBuilder(nom);
        String jsonRequestConstruct = mailToJSON(mail, null, subject, message.toString());
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
