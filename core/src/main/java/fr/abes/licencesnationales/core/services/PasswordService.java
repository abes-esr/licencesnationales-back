package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * On crypte le mot de passe
     * @param motDePasse
     * @return
     */
    public String getEncodedMotDePasse(String motDePasse) {

        if (motDePasse == null || motDePasse.isEmpty() || motDePasse.isBlank()) {
            throw new IllegalArgumentException(Constant.MDP_NOTNULL);
        }
        return passwordEncoder.encode(motDePasse);
    }

    /**
     * Vérifie si le mot de passe passé en paramètre correspond au mot de passe
     * @param motDePasseClair Mot de passe en clair
     * @param motDePasseCrypte Mot de passe crytpé
     * @return Vrai si le mot de passe en clair correspond au mot de passe crypté, Faux sinon
     */
    public boolean estLeMotDePasse(String motDePasseClair, String motDePasseCrypte) {
        return passwordEncoder.matches(motDePasseClair, motDePasseCrypte);
    }
}
