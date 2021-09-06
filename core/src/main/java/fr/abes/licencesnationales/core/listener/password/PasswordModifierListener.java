package fr.abes.licencesnationales.core.listener.password;


import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.event.password.UpdatePasswordEvent;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PasswordModifierListener implements ApplicationListener<UpdatePasswordEvent> {


    private final PasswordEncoder passwordEncoder;

    private final EtablissementRepository etablissementRepository;

    public PasswordModifierListener(PasswordEncoder passwordEncoder, EtablissementRepository etablissementRepository) {
        this.passwordEncoder = passwordEncoder;
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(UpdatePasswordEvent updatePasswordEvent) {
        Optional<EtablissementEntity> etablissementEntity = etablissementRepository.getFirstBySiren(updatePasswordEvent.getSiren());
        if (etablissementEntity.isPresent()) {
            EtablissementEntity entity = etablissementEntity.get();
            entity.getContact().setMotDePasse(updatePasswordEvent.getNewpasswordHash());
            etablissementRepository.save(entity);
        }
    }
}
