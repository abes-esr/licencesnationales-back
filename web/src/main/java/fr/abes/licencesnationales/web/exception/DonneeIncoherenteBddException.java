package fr.abes.licencesnationales.web.exception;

public class DonneeIncoherenteBddException extends Exception{


    private static final long serialVersionUID = 1L;

    public DonneeIncoherenteBddException(String message){
        super(message);
    }
}
