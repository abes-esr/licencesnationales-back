package fr.abes.licencesnationales.web.converter.etablissement;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementDiviseWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementModifieWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class EtablissementWebDtoConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilsMapper utilsMapper;

    @Autowired
    private TypeEtablissementRepository repository;

    @Bean
    public void converterEtablissementCreeWebDto() {
        Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity> myConverter = new Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity>() {

            public EtablissementCreeEventEntity convert(MappingContext<EtablissementCreeWebDto, EtablissementCreeEventEntity> context) {

                try {
                    EtablissementCreeWebDto source = context.getSource();

                    EtablissementCreeEventEntity event = new EtablissementCreeEventEntity(this);

                    // Nom
                    if (source.getName() == null) {
                        throw new IllegalArgumentException("Le champs 'nom' est obligatoire");
                    }
                    event.setNomEtab(source.getName());

                    // Siren
                    if (source.getSiren() == null) {
                        throw new IllegalArgumentException("Le champs 'siren' est obligatoire");
                    }
                    event.setSiren(source.getSiren());

                    // Type d'établissement
                    if (source.getTypeEtablissement() == null) {
                        throw new IllegalArgumentException("Le champs 'typeEtablissement' est obligatoire");
                    }
                    event.setTypeEtablissement(source.getTypeEtablissement());

                    // Pour le contact
                    if (source.getContact() == null) {
                        throw new IllegalArgumentException("Le champs 'contact' est obligatoire");
                    }

                    // Contact - nom
                    if (source.getContact().getNom() == null) {
                        throw new IllegalArgumentException("Le champs 'nom' du contact est obligatoire");
                    }
                    event.setNomContact(source.getContact().getNom());

                    // Contact - prénom
                    if (source.getContact().getPrenom() == null) {
                        throw new IllegalArgumentException("Le champs 'prenom' du contact est obligatoire");
                    }
                    event.setPrenomContact(source.getContact().getPrenom());

                    // Contact - téléphone
                    if (source.getContact().getTelephone() == null) {
                        throw new IllegalArgumentException("Le champs 'telephone' du contact est obligatoire");
                    }
                    event.setTelephoneContact(source.getContact().getTelephone());

                    // Contact - mail
                    if (source.getContact().getMail() == null) {
                        throw new IllegalArgumentException("Le champs 'mail' du contact est obligatoire");
                    }
                    event.setMailContact(source.getContact().getMail());

                    // Contact - mot de passe
                    if (source.getContact().getMotDePasse() == null) {
                        throw new IllegalArgumentException("Le champs 'motDePasse' du contact est obligatoire");
                    }
                    event.setMotDePasse(source.getContact().getMotDePasse());

                    // Contact - adresse
                    if (source.getContact().getAdresse() == null) {
                        throw new IllegalArgumentException("Le champs 'adresse' du contact est obligatoire");
                    }
                    event.setAdresseContact(source.getContact().getAdresse());

                    // Contact - boîte postale
                    if (source.getContact().getBoitePostale() == null) {
                        throw new IllegalArgumentException("Le champs 'boitePostale' du contact est obligatoire");
                    }
                    event.setBoitePostaleContact(source.getContact().getBoitePostale());

                    // Contact - code postal
                    if (source.getContact().getCodePostal() == null) {
                        throw new IllegalArgumentException("Le champs 'codePostal' du contact est obligatoire");
                    }
                    event.setCodePostalContact(source.getContact().getCodePostal());

                    // Contact - cedex
                    if (source.getContact().getCedex() == null) {
                        throw new IllegalArgumentException("Le champs 'cedex' du contact est obligatoire");
                    }
                    event.setCedexContact(source.getContact().getCedex());

                    // Contact - ville
                    if (source.getContact().getVille() == null) {
                        throw new IllegalArgumentException("Le champs 'ville' du contact est obligatoire");
                    }
                    event.setVilleContact(source.getContact().getVille());

                    return event;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementModifieWebDto() {
        Converter<EtablissementModifieWebDto, EtablissementModifieEventEntity> myConverter = new Converter<EtablissementModifieWebDto, EtablissementModifieEventEntity>() {

            public EtablissementModifieEventEntity convert(MappingContext<EtablissementModifieWebDto, EtablissementModifieEventEntity> context) {

                try {
                    EtablissementModifieWebDto source = context.getSource();

                    if (source.getId() == null) {
                        throw new IllegalArgumentException("Le champs 'id' est obligatoire");
                    }

                    EtablissementModifieEventEntity event = new EtablissementModifieEventEntity(this,source.getId());

                    // Nom
                    event.setNomEtab(source.getName());

                    // Siren
                    event.setSiren(source.getSiren());

                    // Type d'établissement
                    event.setTypeEtablissement(source.getTypeEtablissement());

                    // Pour le contact

                    // Contact - nom
                    event.setNomContact(source.getContact().getNom());

                    // Contact - prénom
                    event.setPrenomContact(source.getContact().getPrenom());

                    // Contact - téléphone
                    event.setTelephoneContact(source.getContact().getTelephone());

                    // Contact - mail
                    event.setMailContact(source.getContact().getMail());

                    // Contact - mot de passe
                    event.setMotDePasse(source.getContact().getMotDePasse());

                    // Contact - adresse
                    event.setAdresseContact(source.getContact().getAdresse());

                    // Contact - boîte postale
                    event.setBoitePostaleContact(source.getContact().getBoitePostale());

                    // Contact - code postal
                    event.setCodePostalContact(source.getContact().getCodePostal());

                    // Contact - cedex
                    event.setCedexContact(source.getContact().getCedex());

                    // Contact - ville
                    event.setVilleContact(source.getContact().getVille());

                    return event;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementDiviseWebDto() {
        Converter<EtablissementDiviseWebDto, EtablissementDiviseEventEntity> myConverter = new Converter<EtablissementDiviseWebDto, EtablissementDiviseEventEntity>() {
            @SneakyThrows
            public EtablissementDiviseEventEntity convert(MappingContext<EtablissementDiviseWebDto, EtablissementDiviseEventEntity> context) {
                EtablissementDiviseWebDto source = context.getSource();

                EtablissementDiviseEventEntity etablissementDiviseEvent = new EtablissementDiviseEventEntity(this, source.getAncienSiren());
                List<EtablissementEntity> listeEtab = new ArrayList<>();
                source.getEtablissementDtos().stream().forEach(e -> {
                    EtablissementEntity etablissement = new EtablissementEntity();
                    etablissement.setName(e.getName());
                    etablissement.setSiren(e.getSiren());
                    Optional<TypeEtablissementEntity> type = repository.findFirstByLibelle(e.getTypeEtablissement());
                    if (!type.isPresent()) {
                        throw new Errors().errorMapping(source, context.getDestinationType(), new UnknownTypeEtablissementException("Type d'établissement inconnu")).toMappingException();
                    }
                    etablissement.setTypeEtablissement(type.get());
                    etablissement.setIdAbes(e.getIdAbes());
                    ContactEntity contact = new ContactEntity();
                    contact.setNom(e.getContact().getNom());
                    contact.setPrenom(e.getContact().getPrenom());
                    contact.setAdresse(e.getContact().getAdresse());
                    contact.setBoitePostale(e.getContact().getBoitePostale());
                    contact.setCodePostal(e.getContact().getCodePostal());
                    contact.setVille(e.getContact().getVille());
                    contact.setCedex(e.getContact().getCedex());
                    contact.setTelephone(e.getContact().getTelephone());
                    contact.setMail(e.getContact().getMail());
                    etablissement.setContact(contact);
                    listeEtab.add(etablissement);
                });
                etablissementDiviseEvent.setEtablisementsDivises(objectMapper.writeValueAsString(listeEtab));
                return etablissementDiviseEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

}
