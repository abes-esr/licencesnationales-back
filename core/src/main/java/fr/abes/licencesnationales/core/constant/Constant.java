package fr.abes.licencesnationales.core.constant;


import java.io.Serializable;


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
    public static final String JWT_TOKEN_EXPIRED = "Expired JWT token : {}";
    public static final String JWT_TOKEN_INVALID = "Invalid JWT token : {}";
    public static final String JWT_SIGNATURE_INVALID = "Invalid JWT signature : {}";

    /** Statuts établissement */
    public static final int STATUT_ETAB_NOUVEAU = 1;
    public static final int STATUT_ETAB_AUCUNEIP = 2;
    public static final int STATUT_ETAB_ENVALIDATION = 3;
    public static final int STATUT_ETAB_ATTESTATION = 4;
    public static final int STATUT_ETAB_VALIDE = 5;

    /** Statuts IP */
    public static final int STATUT_IP_NOUVELLE = 6;
    public static final int STATUT_IP_ATTESTATION = 7;
    public static final int STATUT_IP_VALIDEE = 8;
    public static final int STATUT_IP_AUCUNEIP = 9;


    /**Email service */
    public static final String ERROR_CONVERSION_MAIL_TO_JSON = "Erreur lors de la conversion Mail-Json";
    public static final String ERROR_SENDING_MAIL_END_OF_TREATMENT="Erreur lors de l'envoi du mail'";
}

