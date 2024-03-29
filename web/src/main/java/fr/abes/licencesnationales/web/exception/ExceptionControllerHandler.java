package fr.abes.licencesnationales.web.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Gestionnaire des exceptions de l'API.
 * Cette classe récupère toutes les exceptions et renvoi un message d'erreur
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionControllerHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiReturnError apiReturnError) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(apiReturnError, headers, apiReturnError.getStatus());
    }

    /**
     * Erreur de lecture / décodage des paramètres d'une requête HTTP
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String error = Constant.MALFORMED_JSON;

        if (ex.getCause() instanceof MismatchedInputException) {
            String targetType = ((MismatchedInputException) ex.getCause()).getTargetType().getSimpleName();

            if (((MismatchedInputException) ex.getCause()).getPath().size() == 0) {
                return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
            }
            List<JsonMappingException.Reference> errors = ((MismatchedInputException) ex.getCause()).getPath();
            String property = errors.get(errors.size() - 1).getFieldName();

            log.error(ex.getLocalizedMessage());
            return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new MismatchedJsonTypeException(property + " need to be type of '" + targetType + "'")));
        }

        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }


    /**
     * Vérifier les méthodes correspondent avec les URI dans le controller
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Method is not supported for this request";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.METHOD_NOT_ALLOWED, error, ex));
    }

    /**
     * Vérifier la validité (@Valid) des paramètres de la requête
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder msg = new StringBuilder(Constant.ERROR_SAISIE);
        for (int i=0;i<fieldErrors.size();i++) {
            if(i==(fieldErrors.size()-1)){
                msg.append(fieldErrors.get(i).getDefaultMessage());
            } else{
                msg.append(fieldErrors.get(i).getDefaultMessage() + ", ");
            }
        }
        log.error(msg.toString());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, msg.toString(),ex));
    }

    /**
     * Page 404
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Page not found";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.NOT_FOUND, error, ex));
    }

    /**
     * Erreur de paramètre
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Missing request parameter";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /** Erreur dans l'envoi d'un mail */
    @ExceptionHandler(SendMailException.class)
    protected ResponseEntity<Object> handleSendMailException(SendMailException ex) {
        String error = "Erreur dans l'envoi du mail " + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Si la transformation DTO a échoué
     *
     * @param ex MappingException
     * @return
     */
    @ExceptionHandler(MappingException.class)
    protected ResponseEntity<Object> handleMappingException(MappingException ex) {
        String message = Constant.ERROR_SAISIE + ((MappingException) ex.getCause()).getErrorMessages().stream().findFirst().get().getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, message, ex));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleValidationException(ConstraintViolationException ex){
        String error = "Erreur de validation des données" + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException ex) {
        String message = Constant.ERROR_TOKEN+ ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, message, ex));
    }

    /**
     * Gestion des erreurs d'authentification
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({AuthenticationException.class, AccesInterditException.class, SirenIntrouvableException.class})
    protected ResponseEntity<Object> handleAuthentificationException(Exception ex) {
        String error = Constant.ERROR_CREDENTIALS + ex.getMessage();

        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownEditeurException.class)
    protected ResponseEntity<Object> handleUnknownEditeurException(UnknownEditeurException ex) {
        String error = Constant.ERROR_EDITEUR_INCONNU + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownIpException.class)
    protected ResponseEntity<Object> handleUnknownIpException(UnknownIpException ex) {
        String error = Constant.ERROR_IP_INCONNUE + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownEtablissementException.class)
    protected ResponseEntity<Object> handleUnknownEtablissementException(UnknownEtablissementException ex) {
        String error = Constant.ERROR_ETAB_INCONNU;
        ex.printStackTrace();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownTypeEtablissementException.class)
    protected ResponseEntity<Object> handleUnknowTypeEtablissementException(UnknownTypeEtablissementException ex) {
        String error = Constant.ERROR_TYPEETAB_INCONNU + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(BadStatutException.class)
    protected ResponseEntity<Object> handleBadStatutEcception(BadStatutException ex) {
        String error = Constant.ERROR_STATUT_IP + ex.getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<Object> handleTransactionException(TransactionSystemException ex) {
        Optional<Throwable> rootCause = Stream.iterate(ex, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();
        String error = Constant.ERROR_BDD + rootCause.get();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Erreur dans la validation du captcha / Etablissement déjà existant
     * / mail déjà existant / récupération dernière date de modification / IP
     * / Envoi de mail
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({CaptchaException.class, SirenExistException.class, MailDoublonException.class, DateException.class, IpException.class, PasswordMismatchException.class, JsonIncorrectException.class})
    protected ResponseEntity<Object> handleCaptchaException(Exception ex) {
        Optional<Throwable> rootCause = Stream.iterate(ex, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();
        String message = Constant.ERROR_SAISIE + rootCause.get().getMessage();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, message, ex));
    }


}