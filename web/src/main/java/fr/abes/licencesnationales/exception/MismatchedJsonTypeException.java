package fr.abes.licencesnationales.exception;

/**
 * Si le critère de tri n'existe pas ou s'il n'est pas autorisé
 */
public class MismatchedJsonTypeException extends Exception {

    public MismatchedJsonTypeException(final String msg) {
        super(msg);
    }

}

