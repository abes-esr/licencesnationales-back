package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.MailDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class EmailService {
    private static final String BONJOUR = "Bonjour,<br><br>";
    private static final String ENDOFLINE = "</td></tr>";

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mail.ws.url}")
    private String mailServerURL;

    @Value("${site.url}")
    private String urlSite;

    private final String urlWordpress = "https://www.licencesnationales.fr/";

    public void constructCreationCompteEmailAdmin(String emailUser, String nomEtab) throws RestClientException {
        String subject = getEnv() + "[Appli LN] Nouveau compte Etablissement créé";
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, "Le compte établissement suivant a été créé par : " + nomEtab + "<br /><a href='" + urlSite + "' target='_blank'>Se rendre sur le tableau de bord.</a><br /><br />");
        sendMail(jsonRequestConstruct);
    }

    public void constructCreationCompteEmailUser(EtablissementCreeEventEntity etab) throws RestClientException {
        String subject = getEnv() + "[Appli LN] Compte Etablissement créé sur le site Licences Nationales";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous venez de créer le compte de l'établissement <b>");
        message.append(etab.getNomEtab());
        message.append("</b> sur l'<a href='");
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
        String subject = getEnv() + "[Appli LN] Réinitialisation de votre mot de passe";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous souhaitez accéder au compte de ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr. ");
        message.append("Pour créer un nouveau mot de passe, cliquez sur le lien ci-dessous :  <br>");
        message.append("<a href='").append(url).append("' target='_blank'>").append(url).append("</a>");
        message.append("<br><br>Le lien sera valable 24 heures.<br>");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionCompteMailUserEtAdmin(String nomEtab, String emailUser, String emailAdmin) throws RestClientException {
        String subject = getEnv() + "[Appli LN] Compte supprimé sur le site Licencesnationales.fr";
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
        String subject = getEnv() + "[Appli LN] Validation du compte Etablissement";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Après vérifications, le compte de l’établissement ");
        message.append(nomEtab);
        message.append(" a été validé par l'Admin");
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructValidationCompteMailUser(String nomEtab, String emailUser) {
        String subject = getEnv() + "[Appli LN] Validation du compte sur le site Licencesnationales.fr";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Après vérifications, le compte de l’établissement ");
        message.append(nomEtab);
        message.append(" créé sur l’application Licencesnationales ");
        message.append("<b>a été validé par l'Abes.</b><br><br>");
        message.append("<b>Si cela n’est pas encore fait, nous vous invitons dès à présent à déclarer des adresses IP publiques </b>");
        message.append("afin que votre établissement puisse bénéficier de l’accès aux ressources acquises sous licences nationales.");
        message.append(aideALaSaisieIp());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructDevalidationCompteMailUser(String nomEtab, String emailUser) {
        String subject = getEnv() + "[Appli LN] Modification du compte sur le site Licencesnationales.fr";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("le compte de l’établissement");
        message.append(nomEtab);
        message.append(" créé sur l’application Licencesnationales");
        message.append("<b> a été repassé en statut \"Nouveau\" dans l’application de gestion des accès aux licences nationales.</b><br><br>");
        message.append("Pour toute question contacter l’Abes via le guichet d’assistance ABESstp. ");
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }


    public void constructRelanceEtabMailUser(String nomEtab, String emailUser) throws RestClientException {
        String subject = getEnv() + "[Appli LN] Relance : aucune IP déclarée sur le site Licencesnationales.fr";
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

    private String getEnv() {
        switch(activeProfile.toUpperCase(Locale.ROOT)) {
            case "DEV":
                return "[DEV]";
            case "TEST":
                return "[TEST]";
            case "PROD":
                return "";
            default:
                return "[LOCAL]";
        }
    }

    public void constructBilanRecapActionIpUser(String emailUser, String nomEtab, Map<String, List<String>> listIps) throws RestClientException {
        String subject = getEnv() + "[Appli LN] Vérification des nouvelles IP déclarées";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Vous avez déclaré une ou plusieurs IP sur le compte de l’établissement ");
        message.append(nomEtab);
        message.append(" dans l’<a href='https://acces.licencesnationales.fr/' target='_blank'>application de gestion des accès aux licences nationales</a> administrée par l’Abes.<br><br>");
        message.append("L’Abes a vérifié l’éligibilité des nouvelles IP déclarées comme suit :");
        message.append("<table style=\"border: solid 1px; border-radius: 5px; border-collapse: collapse; font: 1em 'Open Sans', sans-serif;\">\n");
        message.append("<tr style=\"height: 3em; background-color: #F7F9FA\">" +
                "            <th style=\"border: 1px solid; width: 20%\">Action de l'Abes</th>" +
                "<th style=\"border: 1px solid; width: 20%\">IP concernée</th>" +
                "<th style=\"border: 1px solid;\">Détails</th></tr>");

        if (!listIps.get("suppression").isEmpty()) {
            message.append("<tr style=\"border-bottom: 1px solid;\">\n" +
                    "            <td style=\"border: 1px solid; font-weight: bold;\">IP supprimée(s)</td>" +
                    "            <td style=\"border: 1px solid;\" colspan=\"2\">" +
                    "<table style=\"width: 100%; border-collapse: collapse;\">");

            List<String> listeSuppr = listIps.get("suppression");
            Iterator<String> iterator = listeSuppr.iterator();
            while (iterator.hasNext()) {
                String ip = iterator.next();
                String[] commentaire = ip.split("[|]");
                if (iterator.hasNext()) {
                    message.append("<tr style=\"border-bottom: 1px solid;\"><td style=\"border-right: solid 1px; width: 25%; vertical-align: top;\"><b>" + commentaire[0] + "</b>");
                    message.append("</td><td style=\"font-size: 0.8em;\"><p style=\"text-decoration: underline;\">Raison de la suppresion :<br /></p>");
                    if (commentaire.length > 1) {
                        if (commentaire[1] == null || commentaire[1].equals("null")) {
                            message.append("Non précisée");
                        } else {
                            message.append(commentaire[1]);
                        }
                    } else {
                        message.append("Non précisée");
                    }
                    message.append("</td></tr>");
                } else {
                    message.append("<tr><td style=\"border-right: solid 1px; width: 25%; vertical-align: top;\"><b>" + commentaire[0] + "</b>");
                    message.append("</td><td style=\"font-size: 0.8em;\"><p style=\"text-decoration: underline;\">Raison de la suppresion :<br /></p>");
                    if (commentaire.length > 1) {
                        if (commentaire[1] == null || commentaire[1].equals("null")) {
                            message.append("Non précisée");
                        } else {
                            message.append(commentaire[1]);
                        }
                    } else {
                        message.append("Non précisée");
                    }
                    message.append("</td></tr>");
                }
            }
            message.append("</table>");
            message.append(ENDOFLINE);
        }

        if (!listIps.get("validation").isEmpty()) {
            message.append("<tr style=\"border-bottom: 1px solid;\"><td style=\"border: 1px solid; font-weight: bold;\">IP validée(s)</td>");
            message.append("<td style=\"border: 1px solid; vertical-align: top;\">");
            message.append("<table style=\"width: 100%; border-collapse: collapse;\">");

            List<String> listeValid = listIps.get("validation");
            Iterator<String> iterator = listeValid.iterator();
            while (iterator.hasNext()) {
                String ip = iterator.next();
                if (iterator.hasNext()) {
                    message.append("<tr style=\"border-bottom: 1px solid;\"><td style=\"vertical-align: top;\"><b>");
                    message.append(ip);
                    message.append("</b></td></tr>");
                } else {
                    message.append("<tr><td style=\"vertical-align: top;\"><b>");
                    message.append(ip);
                    message.append("</b></td></tr>");
                }
            }
            message.append("</table>" +
                    "</td>" +
                    "<td style=\"border: 1px solid; font-size: 0.8em;\">Les IP validées sont transmises une fois par  mois aux éditeurs et à l’Inist qui disposent d’un délai de trois semaines maximum après réception pour ouvrir les accès. <br /></td>" +
                    "</tr>");
        }


        if (!listIps.get("rejet").isEmpty()) {
            message.append("<tr style=\"border-bottom: 1px solid;\">");
            message.append("<td style=\"border: 1px solid; font-weight: bold;\">IP en attente d'attestation</td>");
            message.append("<td style=\"border: 1px solid; vertical-align: top;\"> <table style=\"width: 100%; border-collapse: collapse;\">");

            List<String> listeRejet = listIps.get("rejet");
            Iterator<String> iterator = listeRejet.iterator();
            while (iterator.hasNext()) {
                String ip = iterator.next();
                if (iterator.hasNext()) {
                    message.append("<tr style=\"border-bottom: 1px solid;\"><td style=\"vertical-align: top;\"><b>");
                    message.append(ip);
                    message.append("</b></td></tr>");
                } else {
                    message.append("<tr><td style=\"vertical-align: top;\"><b>");
                    message.append(ip);
                    message.append("</b></td></tr>");
                }
            }
            message.append("</table></td>");
            message.append("<td style=\"border: 1px solid; font-size: 0.8em;\">Lorsque nos vérifications ne permettent pas de rattacher une IP à votre établissement, l’IP ne peut pas être validée et  transmise aux éditeurs et à l'Inist pour ouverture des accès. <br><br><i>Les IP déclarées doivent impérativement être rattachées au seul établissement bénéficiaire des licences nationales et ne peuvent pas être localisées à l'étranger sauf pour les établissements bénéficiaires dont le siège se situe à l’étranger ou dans le cas d’un reverse proxy géré par un prestataire depuis l’étranger.</i><br><br><b>Nous vous invitons donc à nous fournir pour la ou les IP concernées un justificatif</b> de la part de votre service informatique ou de votre fournisseur Internet / fournisseur de services d’accès distant, qui atteste que la ou les IP en attente appartiennent bien à votre institution. <b><a href='https://www.licencesnationales.fr/modele_attestation_ip_licences_nationales/' target='_blank'>Télécharger un modèle d’attestation</a></b><br><br>Ce document doit être envoyé à l’adresse : <b><a href='mailto:ln-admin@abes.fr'>ln-admin@abes.fr</a></b></td>" +
                    "        </tr>");

        }

        message.append("</tbody></table>");

        message.append(aideALaSaisieIp());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(emailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppresionIpMail(List<String> ipsSupprimees, List<String> ipsAttestation, String to, String cc) {
        String subject;
        if (!ipsSupprimees.isEmpty() && !ipsAttestation.isEmpty())
            subject = getEnv() + "[Appli LN] Relance automatique : IP supprimées et en attente d’attestation sur le site Licencesnationales.fr";
        else if (ipsAttestation.isEmpty() && !ipsSupprimees.isEmpty())
            subject = getEnv() + "[Appli LN] Relance automatique : IP supprimées sur le site Licencesnationales.fr";
        else
            subject = getEnv() + "[Appli LN] Relance automatique : IP en attente d’attestation sur le site Licencesnationales.fr";


        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("<table>");
        if (!ipsSupprimees.isEmpty()) {
            message.append("<tr><td style='border: solid'><b>Bilan IP supprimé(es)</b><br><br>Les IP en attente d’attestation depuis plus d’un an sont supprimées automatiquement.</td><td style='border: solid'>");
            ipsSupprimees.forEach(ip -> {
                message.append("<b>");
                message.append(ip);
                message.append("</b><br>");
            });
            message.append(ENDOFLINE);
        }
        if (!ipsAttestation.isEmpty()) {
            message.append("<tr><td style='border: solid'><b>RAPPEL : IP en attente d’attestation</b><br><br>Lorsque nos vérifications ne permettent pas de rattacher une IP à votre établissement, l’IP ne peut pas être validée et  transmise aux éditeurs et à l'Inist pour ouverture des accès. <br><br><i>Les IP déclarées doivent impérativement être rattachées au seul établissement bénéficiaire des licences nationales et ne peuvent pas être localisées à l'étranger sauf pour les établissements bénéficiaires dont le siège se situe à l’étranger ou dans le cas d’un reverse proxy géré par un prestataire depuis l’étranger.</i><br><br>Nous vous invitons donc à nous fournir pour la ou les IP concernées un justificatif de la part de votre service informatique ou de votre fournisseur Internet / fournisseur de services d’accès distant, qui atteste que la ou les IP en attente appartiennent bien à votre institution. <a href='https://www.licencesnationales.fr/modele_attestation_ip_licences_nationales/' target='_blank'>Télécharger un modèle d’attestation</a><br><br>Ce document doit être envoyé à l’adresse : ln-admin@abes.fr<br><br>Pour en savoir plus sur la vérification des IP, vous pouvez consulter <a href='https://documentation.abes.fr/aidelicencesnationales/index.html#StatutsEtProcessusDeValidationDesIP' target='_blank'>cette page</a>.</td><td style='border: solid'>");
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
        String subject = getEnv() + "[Appli LN] Changement de mail de contact";
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
        String subject = getEnv() + "[Appli LN] Suppression du compte Etablissement";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Le compte que vous avez créé pour ");
        message.append(nomEtab);
        message.append(" sur le site Licencesnationales.fr vient d'être supprimé automatiquement de l’application Licencesnationales car, malgré nos  relances, aucune IP n’a été déclarée depuis plus d’un an.<br><br>");
        message.append(lienAssistance());
        message.append(signature());
        String jsonRequestConstruct = mailToJSON(mailUser, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructSuppressionMailAdmin(String mailAdmin, List<EtablissementEntity> listeEtab) {
        String subject = getEnv() + "[Appli LN] Bilan mensuel des comptes Etab supprimés car sans IP depuis au moins un an";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Les comptes établissements ci-dessous, ont été supprimés automatiquement de l’application licences nationales car ils sont restés sans aucune IP déclarée depuis plus d’un an :<br>");
        message.append("<ul>");
        listeEtab.forEach(etab -> {
            message.append("<li>");
            message.append(etab.getNom());
            message.append("</li>");
        });
        message.append("</ul>");
        String jsonRequestConstruct = mailToJSON(mailAdmin, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructEnvoiFichierEditeursConfirmationAdmin(String mailAdmin, Date dateEnvoi) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String subject = getEnv() + "[Appli LN] Confirmation envoi fichiers éditeurs";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Le traitement d'envoi des fichiers aux éditeurs s'est déroulé avec succès. <br>");
        message.append("la date d'envoi a été mise à jour au ");
        message.append(format.format(dateEnvoi));
        String jsonRequestConstruct = mailToJSON(mailAdmin, null, subject, message.toString());
        sendMail(jsonRequestConstruct);
    }

    public void constructEnvoiFichierEditeurs(String mailEditeur, String mailAdmin, Map<String, ByteArrayInputStream> listeFichier) throws IOException {
        String subject = getEnv() + "Licences nationales France – Mise à jour des adresses IP des bénéficiaires / French national licences - beneficiaries' IP update";
        StringBuilder message = new StringBuilder(BONJOUR);
        message.append("Veuillez trouver en pièce jointe les adresses IP ainsi que les informations afférentes des bénéficiaires de la licence nationale.<br><br>");
        message.append("Dans l'objectif de faciliter l'exploitation de ces données, notre équipe a remis en place le service d'envoi des fichiers incrémentaux d'ajouts, modifications et suppressions de comptes et d'IP.<br><br>");
        message.append("Cet envoi comporte donc :<br><br><ul>");
        message.append("<li>Un fichier listant les IP à supprimer (\"ListeDeletedAcces\")</li>");
        message.append("<li>Un fichier listant les nouvelles IP à ajouter (\"ListeNewAccess\"). Dans un souci d’homogénéité, les plages IP v4 déclarées par les établissements sont sous forme XXX.XXX.XXX-XXX.XXX-XXX (où XXX est un chiffre de 1 à 255) et ne contiennent aucun caractère spécial. Par exemple : la plage 100.100.100.* est ainsi transcrite dans le fichier : 100.100.100-100.0-255 ; et 100.100.100.100-180 de cette manière : 100.100.100-100.100-180.</li>");
        message.append("<li>Un fichier listant les comptes établissements à supprimer (\"ListeDeletedInstitutions\")</li>");
        message.append("<li>Un fichier listant les nouveaux comptes établissements à créer (\"ListeNewInstitutions\")</li>");
        message.append("<li>Un fichier listant les comptes établissements pour lesquels des modifications ont été apportées (nom de l’établissement, adresse, nom ou adresse mail du contact…) (\"ListeModifiedInstitutions\")</li>");
        message.append("<li>Un fichier listant les comptes établissements fusionnés (Merged institutions)</li>");
        message.append("<li>Un fichier listant les comptes établissements scindés (Split institutions)</li>");
        message.append("<li>Un fichier récapitulatif avec l'ensemble des comptes validés et leurs IP associées (\"ListeALL\").</li></ul><br><br>");
        message.append("A noter que si l’un de ces fichiers ne figure pas dans la liste des pièces jointes, cela signifie qu’aucune donnée n’a été modifiée.<br><br>");
        message.append("Si vous observez des erreurs ou la moindre incohérence, ou si vous rencontrez des difficultés pour ouvrir ces fichiers, merci de nous en informer dans les meilleurs délais en écrivant à l’adresse <a mailto:ln-admin@abes.fr>ln-admin@abes.fr</a>. Nous vous conseillons d'enregistrer la pièce jointe avant de l'ouvrir dans un tableur pour modifier éventuellement les paramètres - pour rappel, les fichiers sont encodés en UTF-8.<br><br>");
        message.append("Nous vous rappelons que vous disposez d'un délai contractuel de trois (3) semaines pour ouvrir les accès aux contenus sur votre plate-forme.<br><br>");
        message.append(signature());
        message.append("<br><br><hr><br><br>");
        message.append("Dear all,<br><br>");
        message.append("Please find attached the IP addresses for licensees of the French national license for your product.<br><br>");
        message.append("In order to facilitate the data exploitation, our team has set up the service of sending files for additions, modifications and deletions of accounts and IP.<br><br>");
        message.append("You will find :<br><br><ul>");
        message.append("<li>A file listing the IPs to be deleted (\"ListeDeletedAcces\")</li>");
        message.append("<li>A file listing the new IPs to add (\"ListeNewAccess\"). In order to homogenize IP formats, IP v4 ranges reported by institutions are in the form XXX.XXX.XXX-XXX.XXX-XXX (where XXX is a number from 1 to 255) and don’t contain any special symbol. For example: the range 100.100.100. * is also transcribed in the file: 100.100.100-100.0-255 ; and 100.100.100.100-180 in this way: 100.100.100-100.100-180.</li>");
        message.append("<li>A file listing the institutions accounts to be deleted (\"ListeDeletedInstitutions\")</li>");
        message.append("<li>A file listing the new institutions accounts to be created (\"ListeNewInstitutions\")</li>");
        message.append("<li>A file listing the institutions accounts for which changes have been made (institution’s name , address, contact name or contact email address...) (\"ListeModifiedInstitutions\")</li>");
        message.append("<li>A file listing the institutions accounts to be merged into new institutions (Merged institutions)</li>");
        message.append("<li>A file listing the institutions accounts to be split into new institutions (Split institutions)</li>");
        message.append("<li>A global file with all validated accounts and their associated IPs (\"ListeALL\")</li></ul><br><br>");
        message.append("Please note that if one of these files does not appear in the attachment list, it means that no data has been modified.<br><br>");
        message.append("If you observe any errors or if you have any difficulties with opening these files, please inform us as soon as possible: <a mailto:ln-admin@abes.fr>ln-admin@abes.fr</a>. We would like to remind you that the best way to proceed is usually to download the files before opening it, and that it has been encoded in UTF-8.<br><br>");
        message.append("As specified in the license agreement, the accesses have to be set up within three (3) weeks.<br><br>");
        message.append("Best regards,<br><br>");
        message.append("French national licences team");

        String jsonRequestConstruct = mailToJSON(mailEditeur, mailAdmin, subject, message.toString());
        sendMailWithAttachments(jsonRequestConstruct, listeFichier);

    }

    public void sendMail(String requestJson) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.postForObject(mailServerURL + "/htmlMail/", entity, String.class);
    }


    public void sendMailWithAttachments(String requestJson, Map<String,ByteArrayInputStream> listeFichiers) throws IOException {
        HttpPost uploadFile = new HttpPost(mailServerURL + "/htmlMailAttachment/");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("mail", requestJson, ContentType.APPLICATION_JSON);

        /*Ajout des fichiers au mail d'envoi*/
        for(Map.Entry nomFichier : listeFichiers.entrySet()){
            byte[] readBuffer = ((ByteArrayInputStream) nomFichier.getValue()).readAllBytes();
            // On ne rajoute pas de piece jointe vide
            if (readBuffer.length > 0) {
                builder.addBinaryBody(
                        "attachment",
                        readBuffer,
                        ContentType.APPLICATION_OCTET_STREAM,
                        (String) nomFichier.getKey()
                );
            }
        }

        org.apache.http.HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        /*Envoi du mail*/
        CloseableHttpClient httpClient = HttpClients.createDefault();
        httpClient.execute(uploadFile);
    }

    protected String mailToJSON(String to, String cc, String subject, String text) {
        if (!getEnv().equals("")) {
            to = "ln-admin@abes.fr";
        }
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        MailDto mail = new MailDto();
        mail.setApp("ln");
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
        signature.append("<br>Bien cordialement,<br><br>");
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
        message.append("<ul><li><b>Pour vous aider dans la déclaration des adresses IP,</b> vous pouvez consulter ");
        message.append("<a href='http://documentation.abes.fr/aidelicencesnationales/index.html' target='_blank'>Le Guide à destination des établissements bénéficiaires</a>");
        message.append(" rubrique ");
        message.append("<a href='http://documentation.abes.fr/aidelicencesnationales/index.html#DeclarerAdressesIP' target='_blank'>Déclarer les IP publiques.</a></li>");
        message.append("<li>Le site <a href='");
        message.append(urlWordpress);
        message.append("' target='_blank'>");
        message.append("Licences Nationales");
        message.append("</a> centralise les informations sur les licences nationales et vous permettra notamment d’explorer <a href='https://www.licencesnationales.fr/les-corpus-acquis/' target='_blank'>les corpus acquis.</a></li>");
        message.append("<li>");
        message.append(lienAssistance());
        message.append("</li></ul>");
        return message.toString();
    }


}
