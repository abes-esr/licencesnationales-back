package fr.abes.lnevent.services;

import fr.abes.lnevent.recaptcha.ReCaptchaResponse;

public interface ReCaptchaService {

    ReCaptchaResponse verify(String response);
}
