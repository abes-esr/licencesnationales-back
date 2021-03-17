package fr.abes.lnevent.recaptcha;

import java.util.Map;

public class ReCaptchaErrorCodes {


    public static final Map<String,String> RECAPTCHA_ERROR_CODES = Map.of(
            "missing-input-secret","Le paramètre secret est manquant.",
            "invalid-input-secret","Le paramètre secret est invalide ou mal formé.",
            "missing-input-response","Le paramètre de réponse est manquant.",
            "invalid-input-response","Le paramètre de réponse est invalide ou mal formé.",
            "bad-request","La requête est invalide ou mal formulée.",
            "timeout-or-duplicate","La réponse n'est plus valide: soit est trop ancienne, soit a déjà été utilisée."
    );



}
