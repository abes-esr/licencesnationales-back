package fr.abes.licencesnationales.web.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import fr.abes.licencesnationales.core.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        return new ResponseEntity<>(apiReturnError, apiReturnError.getStatus());
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

        String error = "Malformed JSON request";

        if (ex.getCause() instanceof MismatchedInputException) {
            String targetType = ((MismatchedInputException) ex.getCause()).getTargetType().getSimpleName();

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
        String error = "The credentials are not valid";
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder msg = new StringBuilder("Incorrect fields : ");
        for (FieldError fieldError : fieldErrors) {
            msg.append(fieldError.getDefaultMessage() + ", ");
        }
        log.error(msg.toString());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new JsonIncorrectException(msg.toString())));
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

    /**
     * Si la transformation DTO a échoué
     *
     * @param ex MappingException
     * @return
     */
    @ExceptionHandler(MappingException.class)
    protected ResponseEntity<Object> handleMappingException(MappingException ex) {
        String error = "Malformed JSON request";
        log.error(ex.getCause().getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex.getCause()));
    }

    /**
     * Gestion des erreurs d'authentification
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({AuthenticationException.class, AccesInterditException.class, SirenIntrouvableException.class})
    protected ResponseEntity<Object> handleAuthentificationException(Exception ex) {
        String error = "Credentials not valid";
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownEtablissementException.class)
    protected ResponseEntity<Object> handleUnknownEtablissementException(UnknownEtablissementException ex) {
        String error = "Etablissement inconnu";
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(UnknownTypeEtablissementException.class)
    protected ResponseEntity<Object> handleUnknowTypeEtablissementException(UnknownTypeEtablissementException ex) {
        String error = "Type d'établissement inconnu";
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<Object> handleTransactionException(TransactionSystemException ex) {
        String error = "Erreur de mise à jour de la base de données";
        Optional<Throwable> rootCause = Stream.iterate(ex, Throwable::getCause)
                .filter(element -> element.getCause() == null)
                .findFirst();
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, rootCause.get()));
    }

    /**
     * Erreur dans la validation du captcha / Etablissement déjà existant
     * / mail déjà existant / récupération dernière date de modification / IP
     * / Envoi de mail
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({CaptchaException.class, SirenExistException.class, MailDoublonException.class, DateException.class, IpException.class, PasswordMismatchException.class, JsonIncorrectException.class, InvalidTokenException.class})
    protected ResponseEntity<Object> handleCaptchaException(Exception ex) {
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
    }


}