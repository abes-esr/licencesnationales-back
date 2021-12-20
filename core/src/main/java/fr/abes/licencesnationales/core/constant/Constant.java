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
    public static final String ERROR_SAISIE = "Erreur dans la saisie : ";
    public static final String ERROR_BDD = "Erreur de mise à jour de la base de données : ";
    public static final String ERROR_ETAB = "Erreur dans le statut de l'établissement : ";
    public static final String ERROR_TYPEETAB_INCONNU = "Type d'établissement inconnu : ";
    public static final String ERROR_ETAB_INCONNU = "Etablissement inconnu : ";
    public static final String ERROR_IP_INCONNUE = "IP inconnue : ";
    public static final String ERROR_IP_ACTION_INCONNUE = "Action inconnue sur les IPs : ";
    public static final String ERROR_EDITEUR_INCONNU = "Editeur inconnu : ";
    public static final String ERROR_DOUBLON_MAIL = "L'adresse mail saisie est déjà utilisée";

    /** Messages de retour controller **/
    public static final String MESSAGE_AJOUTIP_OK = "IP ajoutée avec succès";
    public static final String MESSAGE_MODIFIP_OK = "IP modifiée avec succès";
    public static final String MESSAGE_SUPPIP_OK = "IP supprimée avec succès";
    public static final String MESSAGE_CREATIONCOMPTE_OK = "Etablissement ajouté avec succès";
    public static final String MESSAGE_MODIFETAB_OK = "Etablissement modifié avec succès";
    public static final String MESSAGE_FUSIONETAB_OK = "Etablissements fusionnés avec succès";
    public static final String MESSAGE_SCISSIONETAB_OK = "Etablissement scissionné avec succès";
    public static final String MESSAGE_VALIDATIONETAB_OK = "Etablissement validé avec succès";





}

