package fr.abes.licencesnationales.web.converter.etablissement;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementDiviseWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class EtablissementWebDtoConverter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilsMapper utilsMapper;

    @Autowired
    private GenererIdAbes genererIdAbes;

    @Autowired
    private TypeEtablissementRepository repository;

    @Bean
    public void converterEtablissementCreeWebDto() {
        Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity> myConverter = new Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity>() {

            public EtablissementCreeEventEntity convert(MappingContext<EtablissementCreeWebDto, EtablissementCreeEventEntity> context) {
                EtablissementCreeWebDto source = context.getSource();

                EtablissementCreeEventEntity etablissementCreeEvent = new EtablissementCreeEventEntity(this);
                etablissementCreeEvent.setNomEtab(source.getName());
                etablissementCreeEvent.setSiren(source.getSiren());
                etablissementCreeEvent.setTypeEtablissement(source.getTypeEtablissement());
                etablissementCreeEvent.setNomContact(source.getContact().getNom());
                etablissementCreeEvent.setPrenomContact(source.getContact().getPrenom());
                etablissementCreeEvent.setAdresseContact(source.getContact().getAdresse());
                etablissementCreeEvent.setBoitePostaleContact(source.getContact().getBoitePostale());
                etablissementCreeEvent.setCodePostalContact(source.getContact().getCodePostal());
                etablissementCreeEvent.setVilleContact(source.getContact().getVille());
                etablissementCreeEvent.setCedexContact(source.getContact().getCedex());
                etablissementCreeEvent.setTelephoneContact(source.getContact().getTelephone());
                etablissementCreeEvent.setMailContact(source.getContact().getMail());

                return etablissementCreeEvent;
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
