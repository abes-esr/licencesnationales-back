package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.MailDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmailService {
    private static final String BONJOUR = "Bonjour,<br><br>";
    private static final String ENDOFLINE = "</td></tr>";
    @Autowired
    private RestTemplate restTemplate;

    @Value("${mail.ws.url}")
    private String mailServerURL;

    @Value("${site.url}")
    private String urlSite;

    public void constructCreationCompteEmailAdmin(String emailUser, String nomEtab) throws RestClientException {
        String subject = "[Appli LN] Nouveau compte Etablissement créé";
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, "Le compte établissement suivant a été créé par : " + nomEtab);
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(EtablissementCreeEventEntity etab) throws RestClientException {
        String subject = "[Appli LN] Compte Etablissement créé";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous venez de créer le compte établissement ci-dessous sur l'");
        message.append("<a href='");
        message.append(urlSite);
        message.append("' target='_blank'>application de gestion des accès aux licences nationales</a> administrée par l’Abes.<br><br>");
        message.append("Un seul compte par établissement est autorisé. L’Abes va vérifier l’éligibilité de l’établissement.<br><br>");
        message.append("<b>Nous vous invitons dès à présent à déclarer des adresses IP publiques</b> afin que votre établissement puisse bénéficier de l’accès aux ressources acquises sous licences nationales.<br><br>");
        message.append(aideALaSaisieIp());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(etab.getMailContact(), null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructResetTokenEmailUser(String token, String emailUser, String nomEtab) throws RestClientException {
        final String url = this.urlSite + "/reinitialisationPass?token=" + token;
        String subject = "[Appli LN] Réinitialisation de votre mot de passe";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous souhaitez accéder au compte de ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr. ");
        message.append("Pour créer un nouveau mot de passe, cliquez sur le lien ci-dessous :  <br>");
        message.append(url);
        message.append(" Le lien sera valable 24 heures.<br>");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailUserEtAdmin(String nomEtab, String emailUser, String emailAdmin) throws RestClientException {
        String subject = "[Appli LN] Suppression du compte Etablissement";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être supprimé.");
        message.append("<br><br>");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, emailAdmin, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailAdmin(String emailUser, String nomEtab) {
        String subject = "[Appli LN] Validation du compte Etablissement";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Après vérifications, le compte de l’établissement ");
        message.append(nomEtab);
        message.append("a été validé par l'Admin");
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailUser(String nomEtab, String emailUser) {
        String subject = "[Appli LN] Validation du compte sur le site Licencesnationales.fr";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Après vérifications, le compte de l’établissement");
        message.append(nomEtab);
        message.append(" créé sur l’application Licencesnationales");
        message.append("<b>a été validé par l'Abes.</b><br><br>");
        message.append("<b>Si cela n’est pas encore fait, nous vous invitons dès à présent à déclarer des adresses IP publiques </b>");
        message.append("afin que votre établissement puisse bénéficier de l’accès aux ressources acquises sous licences nationales.");
        message.append(aideALaSaisieIp());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructRelanceEtabMailUser(String nomEtab, String emailUser) throws RestClientException {
        String subject = "[Appli LN] Relance : aucune IP déclarée sur le site Licencesnationales.fr";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Aucune IP n’est déclarée sur le compte établissement ");
        message.append(nomEtab);
        message.append("<br><b>Au bout d’un an sans IP, le compte sera supprimé automatiquement de l’<a href='https://acces.licencesnationales.fr/' target='_blank'>application de gestion des accès licences nationales</a>.</b><br><br>");
        message.append("<b>Nous vous invitons dès à présent à déclarer des adresses IP publiques</b> afin que votre établissement puisse bénéficier de l’accès aux ressources acquises sous licences nationales.");
        message.append("en l'absence de document certifiant l'appartenance de l'IP ");
        message.append(aideALaSaisieIp());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructBilanRecapActionIpUser(String emailUser, String nomEtab, Map<String, List<String>> listIps) throws RestClientException {
        String subject = "[Appli LN] Vérification des nouvelles IP déclarées";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous avez déclaré une ou plusieurs IP sur le compte de l’établissement ");
        message.append(nomEtab);
        message.append(" dans l’<a href='https://acces.licencesnationales.fr/' target='_blank'>application de gestion des accès aux licences nationales</a> administrée par l’Abes.<br><br>");
        message.append("L’Abes a vérifié l’éligibilité des nouvelles IP déclarées comme suit :");
        message.append("<table>");
        message.append("<tr><td><b>IP supprimé(es)</b></td><td>");
        if (listIps.get("suppression").isEmpty()) {
            message.append("Aucune IP");
        } else {
            listIps.get("suppression").stream().forEach(ip -> {
                message.append(ip);
                message.append("<br>");
            });
            message.append(ENDOFLINE);
        }
        message.append("<tr><td><b>IP en attente d'attestation</b><br><br>Lorsque nos vérifications ne permettent pas de rattacher une IP à votre établissement, l’IP ne peut pas être validée et  transmise aux éditeurs et à l'Inist pour ouverture des accès. <br><br><i>Les IP déclarées doivent impérativement être rattachées au seul établissement bénéficiaire des licences nationales et ne peuvent pas être localisées à l'étranger sauf pour les établissements bénéficiaires dont le siège se situe à l’étranger ou dans le cas d’un reverse proxy géré par un prestataire depuis l’étranger.</i><br><br>Nous vous invitons donc à nous fournir pour la ou les IP concernées un justificatif de la part de votre service informatique ou de votre fournisseur Internet / fournisseur de services d’accès distant, qui atteste que la ou les IP en attente appartiennent bien à votre institution. Télécharger un modèle d’attestation<br><br>Ce document doit être envoyé à l’adresse : <a href='mailto:ln-admin@abes.fr'>ln-admin@abes.fr</a></td><td>");
        if (listIps.get("rejet").isEmpty()) {
            message.append("Aucune IP");
        } else {
            listIps.get("rejet").stream().forEach(ip -> {
                message.append(ip);
                message.append("<br>");
            });
            message.append(ENDOFLINE);
        }
        message.append("<tr><td><b>Ip Validées<b><br><br><i>Les IP validées sont transmises une fois par  mois aux éditeurs et à l’Inist qui disposent d’un délai de trois semaines maximum après réception pour ouvrir les accès.</i></td><td>");
        if (listIps.get("validation").isEmpty()) {
            message.append("Aucune IP");
        } else {
            listIps.get("validation").stream().forEach(ip -> {
                message.append(ip);
                message.append("<br>");
            });
            message.append(ENDOFLINE);
        }

        message.append("</table><br>");
        message.append(aideALaSaisieIp());
        message.append(signature());
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

    public void constructSuppresionIpMail(List<String> ipsSupprimees, List<String> ipsAttestation, String to, String cc) {
        String subject = "[Appli LN] Relance automatique : IP supprimées et/ou en attente d’attestation sur le site Licencesnationales.fr";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("<table>");
        if (!ipsSupprimees.isEmpty()) {
            message.append("<tr><td><b>Bilan IP supprimé(es)</b><br><br>Les IP en attente d’attestation depuis plus d’un an sont supprimées automatiquement.</td><td>");
            ipsSupprimees.forEach(ip -> {
                message.append("<b>");
                message.append(ip);
                message.append("</b><br>");
            });
            message.append(ENDOFLINE);
        }
        if (!ipsAttestation.isEmpty()) {
            message.append("<tr><td><b>RAPPEL : IP en attente d’attestation</b><br><br>Lorsque nos vérifications ne permettent pas de rattacher une IP à votre établissement, l’IP ne peut pas être validée et  transmise aux éditeurs et à l'Inist pour ouverture des accès. <br><br><i>Les IP déclarées doivent impérativement être rattachées au seul établissement bénéficiaire des licences nationales et ne peuvent pas être localisées à l'étranger sauf pour les établissements bénéficiaires dont le siège se situe à l’étranger ou dans le cas d’un reverse proxy géré par un prestataire depuis l’étranger.</i><br><br>Nous vous invitons donc à nous fournir pour la ou les IP concernées un justificatif de la part de votre service informatique ou de votre fournisseur Internet / fournisseur de services d’accès distant, qui atteste que la ou les IP en attente appartiennent bien à votre institution. Télécharger un modèle d’attestation<br><br>Ce document doit être envoyé à l’adresse : ln-admin@abes.fr<br><br>Pour en savoir plus sur la vérification des IP, vous pouvez consulter <a href='http://documentation.abes.fr/aidelicencesnationales/co/DeclarerAdressesIP.html#h6cleoZtHXSMiDIX5wCuf' target='_blank'>cette page</a>.");
            ipsAttestation.forEach(ip -> {
                message.append("<b>");
                message.append(ip);
                message.append("</b><br>");
            });
            message.append(ENDOFLINE);
        }
        message.append("</table>");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(to, cc, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructModificationMailAdmin(String mailAdmin, String nomEtab, String ancienMail, String nouveauMail) {
        String subject = "[Appli LN] Changement de mail de contact";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Le mail de contact du compte établissement suivant a été modifié : ");
        message.append(nomEtab);
        message.append("<br><br>");
        message.append("Ancienne adresse : ");
        message.append(ancienMail);
        message.append("<br>");
        message.append("Nouvelle adresse : ");
        message.append(nouveauMail);
        String jsonRequestConstruct = mailToJSON(mailAdmin, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMailUser(String nomEtab, String mailUser) {
        String subject = "[Appli LN] Suppression du compte Etablissement";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être supprimé automatiquement de l’application Licencesnationales car, malgré nos  relances, aucune IP n’a été déclarée depuis plus d’un an.");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(mailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMailAdmin(String mailAdmin, List<EtablissementEntity> listeEtab) {
        String subject = "[Appli LN] Bilan mensuel des comptes Etab supprimés car sans IP depuis au moins un an";
        StringBuilder message = new StringBuilder("Bonjour, <br><br>");
        message.append("Les comptes établissements ci-dessous, ont été supprimés automatiquement de l’application licences nationales car ils sont restés sans aucune IP déclarée depuis plus d’un an :");
        listeEtab.forEach(etab -> {
            message.append(etab.getNom());
            message.append("<br>");
        });
        String jsonRequestConstruct = mailToJSON(mailAdmin, null, subject, message.toString());
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

    private String signature() {
        StringBuilder signature = new StringBuilder();
        signature.append("Bien cordialement,<br><br>");
        signature.append("L'équipe Licences nationales<br>");
        signature.append("<a href='");
        signature.append(urlSite);
        signature.append("' target='_blank'>");
        signature.append(urlSite);
        signature.append("</a>");
        return signature.toString();
    }

    private String lienAssistance() {
        StringBuilder lienAssistance = new StringBuilder("Pour toute question contacter l’Abes via le <a href='https://stp.abes.fr/node/3?origine=LicencesNationales' target='_blank'>guichet d’assistance ABESstp.</a><br>");
        return lienAssistance.toString();
    }

    private String aideALaSaisieIp() {
        StringBuilder message = new StringBuilder();
        message.append("<ul><li><b>Pour vous aider dans la déclaration des IP,</b> vous pouvez consulter ");
        message.append("<a href='http://documentation.abes.fr/aidelicencesnationales/index.html' target='_blank'>Le Guide à destination des établissements bénéficiaires</a>");
        message.append(" rubrique ");
        message.append("<a href='http://documentation.abes.fr/aidelicencesnationales/index.html#DeclarerAdressesIP' target='_blank'>Déclarer les IP publiques.</a></li>");
        message.append("<li>Le site <a href='");
        message.append(urlSite);
        message.append("' target='_blank'>");
        message.append(urlSite);
        message.append("</a> centralise les informations sur les licences nationales et vous permettra notamment d’explorer <a href='https://www.licencesnationales.fr/les-corpus-acquis/' target='_blank'>les corpus acquis.</a></li>");
        message.append("<li>");
        message.append(lienAssistance());
        message.append("</li></ul>");
        return message.toString();
    }

}
