package fr.abes.licencesnationales.core.constant;


import java.io.Serializable;

/**
 * //TODO: Pourquoi il y a du JWT_TOKEN ici dans le core ?
 */
public class Constant implements Serializable {
    private static final long serialVersionUID = 1L;

       /**Generic messages*/
    public static final String BLOCKED = "blocked";


    /**Authentication failed*/
    public static final String UTILISATEUR_ABSENT_BASE = "Cet utilisateur n'existe pas dans la base de données.";
    public static final String ACCES_INTERDIT = "Accès interdit.";
    public static final String ACCES_REFUSE = "Accès refusé.";
    public static final String IP_BLOCKED = "Votre adresse IP est bloquée.";
    public static final String WRONG_LOGIN_AND_OR_PASS = "Mauvais login et / ou mot de passe.";


    /**Application log messages */
    public static final String ERROR_RESPONDING_WITH_UNAUTHORIZED_ERROR = "Erreur : répond avec une erreur d'autorisation : message - {0}";
    public static final String ERROR_ACCESS_DATABASE = "Erreur acces a la base";
    public static final String ERROR_GENERIC_TECHNICAL_PROBLEMS = "Nous rencontrons actuellement des problèmes techniques.";
    public static final String ERROR_ACCOUNT_BLOCKED = "Le compte est bloqué. Veuillez patienter 15 minutes.";
    public static final String ERROR_ACCESS_RESSOURCE_NOT_ALLOWED = "Vous ne pouvez pas accéder à cette ressource.";
    public static final String ENTER_DOFILTERINTERNAL = "ENTREE DANS doFilterInternal.............................";
    public static final String ERROR_AUTHENTICATION_IN_SECURITY_CONTEXT = "Could not set user authentication in fr.abes.licencesnationales.web.security context";
    public static final String ERROR_BLOCKED_IP = "dans isblocked IP, attemptsCache.get(key) = ";
    public static final String NUMBER_IP_TENTATIVES = "NB de tentatives pour ip =";
    public static final String ENTER_LOGIN_FAILED = "entree dans loginFailed...";
    public static final String ENTER_LOGIN_SUCCEED = "entree dans loginSucceeded pour IP = ";
    public static final String JWT_CLAIMS_STRING_EMPTY = "JWT claims string is empty : {}";
    public static final String JWT_TOKEN_UNSUPPORTED = "Unsupported JWT token : {}";
    public static final String JWT_TOKEN_EXPIRED = "Expired JWT token";
    public static final String JWT_TOKEN_INVALID = "Invalid JWT token : {}";
    public static final String JWT_SIGNATURE_INVALID = "Invalid JWT signature : {}";

    public static final String MESSAGE_REGEXP_PASSWORD = "Votre mot de passe doit contenir au minimum 8 caractères dont une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial parmis @ $ ! % * ? &";


    /** Statuts établissement */
    public static final String STATUT_ETAB_SANSIP = "Sans IP";
    public static final String STATUT_ETAB_EXAMINERIP = "Examiner IP";
    public static final String STATUT_ETAB_ATTENTEATTESTATION = "Attestation à envoyer";
    public static final String STATUT_ETAB_IPOK = "IP Ok";

    /** Statuts IP */
    public static final int STATUT_IP_NOUVELLE = 1;
    public static final int STATUT_IP_ATTESTATION = 2;
    public static final int STATUT_IP_VALIDEE = 3;


    /**Email service */
    public static final String ERROR_CONVERSION_MAIL_TO_JSON = "Erreur lors de la conversion Mail-Json";
    public static final String ERROR_SENDING_MAIL_END_OF_TREATMENT="Erreur lors de l'envoi du mail'";

    /** Gestion des erreurs */
    public static final String MALFORMED_JSON = "Paramètre incorrect";
    public static final String ERROR_SAISIE = "Erreur dans la saisie : ";
    public static final String ERROR_RECAPTCHA = "Erreur Recaptcha : " ;
    public static final String ERROR_BDD = "Erreur de mise à jour de la base de données : ";
    public static final String ERROR_STATUT_IP = "Erreur statut Ip : ";
    public static final String ERROR_TYPEETAB_INCONNU = "Type d'établissement inconnu : ";
    public static final String ERROR_ETAB_INCONNU = "Etablissement inconnu : ";
    public static final String ERROR_IP_INCONNUE = "IP inconnue : ";
    public static final String ERROR_IP_ACTION_INCONNUE = "Action inconnue sur les IPs : ";
    public static final String ERROR_EDITEUR_INCONNU = "Editeur inconnu : ";
    public static final String ERROR_DOUBLON_MAIL = "L'adresse mail saisie est déjà utilisée";
    public static final String ERROR_SPRING_BATCH = "Ne peut pas initialiser spring batch : ";
    public static final String ERROR_RECUP_DERNIERE_DATE_MODIF = "Erreur lors de la recupération de la dernière date de modification : " ;
    public static final String ERROR_CSV_WRITING = "Csv writing error: " ;
    public static final String ERROR_CREDENTIALS = "Erreur d'authentification : " ;
    public static final String ERROR_TOKEN = "Erreur de token d'authentification : ";
    public static final String ERROR_IPV4_INVALIDE = "IP v4 invalide : ";
    public static final String ERROR_IPV6_INVALIDE = "IP v6 invalide : ";

    /**  Messages Format Attribut  **/
    public static final String SIREN_DOIT_CONTENIR_9_CHIFFRES = "Le SIREN doit contenir 9 chiffres";

    /**  Messages champs manquant IP  **/
    public static final String ERROR_IP_IP_OBLIGATOIRE = "Le champ 'ip' est obligatoire";
    public static final String ERROR_IP_TYPE_IP_OBLIGATOIRE = "Le champ 'typeIp' est obligatoire";
    public static final String ERROR_IP_STATUT_OBLIGATOIRE = "Le champ 'statut' est obligatoire";

    /**  Messages champs manquant Etablissement  **/
    public static final String ERROR_ETAB_NOM_OBLIGATOIRE = "Le champ 'nom' est obligatoire";
    public static final String ERROR_ETAB_SIREN_OBLIGATOIRE = "Le champ 'siren' est obligatoire";
    public static final String ERROR_ETAB_TYPE_ETAB_OBLIGATOIRE = "Le champ 'typeEtablissement' est obligatoire";
    public static final String ERROR_ETAB_CONTACT_OBLIGATOIRE = "Le champ 'contact' est obligatoire";
    public static final String ERROR_ETAB_MDP_OBLIGATOIRE = "Le champ 'motDePasse' du contact est obligatoire";
    public static final String ERROR_ETAB_NOM_CONTACT_OBLIGATOIRE = "Le champ 'nom' du contact est obligatoire";
    public static final String ERROR_ETAB_PRENOM_CONTACT_OBLIGATOIRE = "Le champ 'prenom' du contact est obligatoire";
    public static final String ERROR_ETAB_TELEPHONE_CONTACT_OBLIGATOIRE = "Le champ 'telephone' du contact est obligatoire";
    public static final String ERROR_ETAB_MAIL_CONTACT_OBLIGATOIRE = "Le champ 'mail' du contact est obligatoire";
    public static final String ERROR_ETAB_ADRESSE_CONTACT_OBLIGATOIRE = "Le champ 'adresse' du contact est obligatoire";
    public static final String ERROR_ETAB_CODEPOSTAL_CONTACT_OBLIGATOIRE = "Le champ 'codePostal' du contact est obligatoire";
    public static final String ERROR_ETAB_VILLE_CONTACT_OBLIGATOIRE = "Le champ 'ville' du contact est obligatoire";
    public static final String ERROR_ETAB_2_ETAB_OBLIGATOIRE = "La fusion doit porter sur au moins 2 établissements";

    /**  Messages champs manquant Editeur  **/
    public static final String ERROR_EDITEUR_ID_OBLIGATOIRE = "Le champ 'id' est obligatoire";
    public static final String ERROR_EDITEUR_NOM_OBLIGATOIRE = "Le champ 'nom' de l'éditeur est obligatoire";
    public static final String ERROR_EDITEUR_ADRESSE_OBLIGATOIRE = "Le champ 'adresse' de l'éditeur est obligatoire";
    public static final String ERROR_EDITEUR_CONTACT_OBLIGATOIRE = "Au moins un 'contact commercial' ou un 'contact technique' est obligatoire";

    /**  Messages champs manquant Authentification  **/
    public static final String ERROR_AUTHENTIFICATION_TOKEN_OBLIGATOIRE = "Le champ 'token' est obligatoire";
    public static final String ERROR_AUTHENTIFICATION_NOUVEAU_MDP_OBLIGATOIRE = "Le champ 'nouveauMotDePasse' est obligatoire";
    public static final String ERROR_AUTHENTIFICATION_ANCIEN_MDP_OBLIGATOIRE = "Le champ 'ancienMotDePasse' est obligatoire";
    public static final String ERROR_AUTHENTIFICATION_TOKEN_PAS_VALIDE = "Le token n'est pas valide";
    public static final String ERROR_AUTHENTIFICATION_NOUVEAU_MDP_DIFFERENT_DE_ANCIEN = "Votre nouveau mot de passe doit être différent de l'ancien";
    public static final String ERROR_AUTHENTIFICATION_ANCIEN_MDP_DIFFERENT_DE_ACTUEL = "L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel.";

    /** Messages de retour controller **/
    public static final String MESSAGE_AJOUTIP_OK = "IP ajoutée avec succès";
    public static final String MESSAGE_MODIFIP_OK = "IP modifiée avec succès";
    public static final String MESSAGE_SUPPIP_OK = "IP supprimée avec succès";
    public static final String MESSAGE_CREATIONETAB_OK = "Etablissement ajouté avec succès";
    public static final String MESSAGE_MODIFETAB_OK = "Etablissement modifié avec succès";
    public static final String MESSAGE_FUSIONETAB_OK = "Etablissements fusionnés avec succès";
    public static final String MESSAGE_SCISSIONETAB_OK = "Etablissement scissionné avec succès";
    public static final String MESSAGE_VALIDATIONETAB_OK = "Etablissement validé avec succès";
    public static final String MESSAGE_SUPPETAB_OK = "Etablissement supprimé avec succès";
    public static final String MESSAGE_CREATIONEDITEUR_OK = "Editeur ajouté avec succès";
    public static final String MESSAGE_MODIFEDITEUR_OK = "Editeur modifié avec succès";
    public static final String MESSAGE_SUPPEDITEUR_OK = "Editeur supprimé avec succès";
    public static final String MESSAGE_MDP_OUBLIE = "Un mail avec un lien de réinitialisation vous a été envoyé";
    public static final String MESSAGE_RESET_MDP = "Votre mot de passe a bien été réinitialisé";
    public static final String EXCEPTION_CAPTCHA_OBLIGATOIRE = "Le champs 'recaptcha' est obligatoire";
    public static final String MESSAGE_MDP_MODIFIER = "Votre mot de passe a bien été modifié";

    /** Illegal argument message entité **/
    public static final String CONTACT_OBLIGATOIRE = "Le contact est obligatoire";
    public static final String CONTACT_COM_OU_TECH_OBLIGATOIRE = "Le contact doit forcément être commercial ou technique";
    public static final String IP_NOTNULL = "Ip ne peut pas être nulle";
    public static final String IP_UNABLE_TO_DECODE = "Unable to decode IP";
    public static final String MDP_NOTNULL = "Le mot de passe ne doit pas être nulle ou vide";
    public static final String ETAB_NOTNULL = "L'établissement ne peut pas être nul";
    public static final String SIREN_DOUBLON = "Le siren saisi est déjà utilisé";
    public static final String SIREN_NE_CORRESPOND_PAS = "Le siren demandé ne correspond pas au siren de l'utilisateur connecté";
    public static final String STATUT_INCONNU = "Statut inconnu";
    public static final String METHODE_AUTHENTIFICATION_PAS_SUPPORTEE = "La méthode d'authentification n'est pas supportée";
    public static final String CHAMPS_SIREN_OU_EMAIL_OBLIGATOIRE = "Au moins un des champs 'siren' ou 'email' est obligatoire";
    public static final String OPERATION_QUE_PAR_ADMIN = "L'opération ne peut être effectuée que par un administrateur";
    public static final String DEJA_VALIDE = "L'établissement ne doit pas déjà être validé";
    public static final String ERROR_MODIFIER_MAUVAIS_ETAB = "Impossible de modifier un autre établissement que celui de l'utilisateur";
    public static final String ERROR_TOKEN_INVALID = "Token invalide ou absent";
    public static final String ERROR_SIREN_INTROUVABLE = "Siren absent de la base";

    /** STRING avec variable **/
    public static final String ERROR_ETAB_DOUBLON = "L'établissement %s existe déjà";
    public static final String ERROR_IP_DOUBLON = "L'IP %s est déjà utilisée";
    public static final String ERROR_MAIL_DOUBLON = "L'adresse mail %s renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.";
    public static final String ERROR_IP_EXISTE_PAS = "L'IP %s n'existe pas";
    public static final String ERROR_ETAB_EXISTE_PAS = "L'établissement %s n'existe pas";
    public static final String ERROR_IP_MAUVAIS = "L'IP %s n'est pas rattachée à un établissement";
    public static final String ERROR_IP_RESERVEES = "%s est inclus dans les IP réservées %s";
    public static final String ERROR_UTILISATEUR_NOT_FOUND_MAIL = "L'utilisateur avec l'email '%s' n'existe pas";
    public static final String ERROR_UTILISATEUR_NOT_FOUND_SIREN = "L'utilisateur avec le SIREN '%s' n'existe pas";


}

