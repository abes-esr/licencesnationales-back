package fr.abes.licencesnationales.web.converter.etablissement;

import com.google.common.collect.Sets;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.dto.etablissement.creation.ContactCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.fusion.EtablissementFusionneWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieAdminWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieUserWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.scission.EtablissementDiviseWebDto;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class EtablissementWebDtoConverter {
    @Autowired
    private UtilsMapper utilsMapper;

    private DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    @Bean
    public void converterEtablissementCreeWebDto() {
        Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity> myConverter = new Converter<EtablissementCreeWebDto, EtablissementCreeEventEntity>() {

            public EtablissementCreeEventEntity convert(MappingContext<EtablissementCreeWebDto, EtablissementCreeEventEntity> context) {

                try {
                    EtablissementCreeWebDto source = context.getSource();

                    EtablissementCreeEventEntity event = new EtablissementCreeEventEntity(this);

                    // Nom
                    if (source.getName() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_NOM_OBLIGATOIRE);
                    }
                    event.setNomEtab(source.getName());

                    // Siren
                    if (source.getSiren() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_SIREN_OBLIGATOIRE);
                    }
                    event.setSiren(source.getSiren());

                    // Type d'établissement
                    if (source.getTypeEtablissement() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_TYPE_ETAB_OBLIGATOIRE);
                    }
                    event.setTypeEtablissement(source.getTypeEtablissement());

                    // Pour le contact
                    if (source.getContact() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_CONTACT_OBLIGATOIRE);
                    }

                    event.setValide(false);

                    setContact(source.getContact(), event);

                    // Contact - mot de passe
                    if (source.getContact().getMotDePasse() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_MDP_OBLIGATOIRE);
                    }
                    event.setMotDePasse(source.getContact().getMotDePasse());
                    return event;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementModifieAdminWebDto() {
        Converter<EtablissementModifieAdminWebDto, EtablissementModifieEventEntity> myConverter = new Converter<EtablissementModifieAdminWebDto, EtablissementModifieEventEntity>() {

            public EtablissementModifieEventEntity convert(MappingContext<EtablissementModifieAdminWebDto, EtablissementModifieEventEntity> context) {

                try {
                    EtablissementModifieAdminWebDto source = context.getSource();
                    EtablissementModifieEventEntity event = new EtablissementModifieEventEntity(this, source.getSiren());

                    // Nom
                    if (source.getNom() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_NOM_OBLIGATOIRE);
                    }
                    event.setNomEtab(source.getNom());

                    // Siren
                    if (source.getSiren() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_SIREN_OBLIGATOIRE);
                    }
                    event.setSiren(source.getSiren());

                    // Type d'établissement
                    if (source.getTypeEtablissement() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_TYPE_ETAB_OBLIGATOIRE);
                    }
                    event.setTypeEtablissement(source.getTypeEtablissement());

                    // Pour le contact
                    setContact(source.getContact(), event);

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
        Converter<EtablissementModifieUserWebDto, EtablissementModifieEventEntity> myConverter = new Converter<EtablissementModifieUserWebDto, EtablissementModifieEventEntity>() {

            public EtablissementModifieEventEntity convert(MappingContext<EtablissementModifieUserWebDto, EtablissementModifieEventEntity> context) {

                try {
                    EtablissementModifieUserWebDto source = context.getSource();

                    EtablissementModifieEventEntity event = new EtablissementModifieEventEntity(this, source.getSiren());

                    // Pour le contact
                    if (source.getContact() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_CONTACT_OBLIGATOIRE);
                    }

                    setContact(source.getContact(), event);

                    return event;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    private void setContact(ContactWebDto source, EtablissementEventEntity event) {

        // Contact - nom
        if (source.getNom() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_NOM_CONTACT_OBLIGATOIRE);
        }
        event.setNomContact(source.getNom());

        // Contact - prénom
        if (source.getPrenom() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_PRENOM_CONTACT_OBLIGATOIRE);
        }
        event.setPrenomContact(source.getPrenom());

        // Contact - téléphone
        if (source.getTelephone() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_TELEPHONE_CONTACT_OBLIGATOIRE);
        }
        event.setTelephoneContact(source.getTelephone());

        // Contact - mail
        if (source.getMail() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_MAIL_CONTACT_OBLIGATOIRE);
        }
        event.setMailContact(source.getMail());

        // Contact - adresse
        if (source.getAdresse() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_ADRESSE_CONTACT_OBLIGATOIRE);
        }
        event.setAdresseContact(source.getAdresse());

        // Contact Boite Postale
        event.setBoitePostaleContact(source.getBoitePostale());

        // Contact - code postal
        if (source.getCodePostal() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_CODEPOSTAL_CONTACT_OBLIGATOIRE);
        }
        event.setCodePostalContact(source.getCodePostal());

        // Contact - cedex
        event.setCedexContact(source.getCedex());

        // Contact - ville
        if (source.getVille() == null) {
            throw new IllegalArgumentException(Constant.ERROR_ETAB_VILLE_CONTACT_OBLIGATOIRE);
        }
        event.setVilleContact(source.getVille());
    }

    @Bean
    public void converterEtablissementFusionneWebDto() {
        Converter<EtablissementFusionneWebDto, EtablissementFusionneEventEntity> myConverter = new Converter<EtablissementFusionneWebDto, EtablissementFusionneEventEntity>() {
            @SneakyThrows
            public EtablissementFusionneEventEntity convert(MappingContext<EtablissementFusionneWebDto, EtablissementFusionneEventEntity> context) {
                try {
                    EtablissementFusionneWebDto source = context.getSource();
                    if (source.getSirenFusionnes().size() < 2) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_2_ETAB_OBLIGATOIRE);
                    }
                    if (source.getNouveauEtab().getSiren() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_SIREN_OBLIGATOIRE);
                    }
                    EtablissementFusionneEventEntity event = new EtablissementFusionneEventEntity(this, source.getNouveauEtab().getSiren(), Sets.newHashSet(source.getSirenFusionnes()));
                    if (source.getNouveauEtab().getNom() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_NOM_OBLIGATOIRE);
                    }
                    event.setNomEtab(source.getNouveauEtab().getNom());
                    event.setTypeEtablissement(source.getNouveauEtab().getTypeEtablissement());

                    // Pour le contact
                    if (source.getNouveauEtab().getContact() == null) {
                        throw new IllegalArgumentException(Constant.ERROR_ETAB_CONTACT_OBLIGATOIRE);
                    }

                    setContact(source.getNouveauEtab().getContact(), event);
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

                if (source.getSirenScinde() == null) {
                    throw new IllegalArgumentException(Constant.ERROR_ETAB_SIREN_OBLIGATOIRE);
                }
                EtablissementDiviseEventEntity etablissementDiviseEvent = new EtablissementDiviseEventEntity(this, source.getSirenScinde());
                List<EtablissementEntity> listeEtab = new ArrayList<>();
                source.getNouveauxEtabs().stream().forEach(e -> {
                    ContactCreeWebDto contactDto = e.getContact();
                    ContactEntity contact = new ContactEntity(contactDto.getNom(), contactDto.getPrenom(), contactDto.getAdresse(), contactDto.getBoitePostale(), contactDto.getCodePostal(), contactDto.getVille(), contactDto.getCedex(), contactDto.getTelephone(), contactDto.getMail(), contactDto.getMotDePasse());
                    EtablissementEntity etablissement = new EtablissementEntity(e.getNom(), e.getSiren(), contact);
                    listeEtab.add(etablissement);
                });
                etablissementDiviseEvent.setEtablissementDivises(Sets.newHashSet(listeEtab));
                return etablissementDiviseEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementUserWebDto() {
        Converter<EtablissementEntity, EtablissementUserWebDto> myConverter = new Converter<EtablissementEntity, EtablissementUserWebDto>() {
            @SneakyThrows
            public EtablissementUserWebDto convert(MappingContext<EtablissementEntity, EtablissementUserWebDto> context) {
                EtablissementEntity source = context.getSource();

                EtablissementUserWebDto dto = new EtablissementUserWebDto();
                dto.setId(source.getId());
                dto.setName(source.getNom());
                dto.setTypeEtablissement(source.getTypeEtablissement().getLibelle());
                dto.setSiren(source.getSiren());
                dto.setIdAbes(source.getIdAbes());
                dto.setStatut(source.isValide()?"Validé":"Nouveau");
                dto.setStatutIps(source.getStatut());
                dto.setDateCreation(format.format(source.getDateCreation()));
                ContactWebDto contact = getContactWebDto(source);
                dto.setContact(contact);
                source.getIps().forEach(ip -> {
                    dto.ajouterIp(utilsMapper.map(ip, IpWebDto.class));
                });
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementAdminWebDto() {
        Converter<EtablissementEntity, EtablissementAdminWebDto> myConverter = new Converter<EtablissementEntity, EtablissementAdminWebDto>() {
            @SneakyThrows
            public EtablissementAdminWebDto convert(MappingContext<EtablissementEntity, EtablissementAdminWebDto> context) {
                EtablissementEntity source = context.getSource();

                EtablissementAdminWebDto dto = new EtablissementAdminWebDto();
                dto.setId(source.getId());
                dto.setName(source.getNom());
                dto.setTypeEtablissement(source.getTypeEtablissement().getLibelle());
                dto.setSiren(source.getSiren());
                dto.setIdAbes(source.getIdAbes());
                dto.setStatut(source.isValide()?"Validé":"Nouveau");
                dto.setStatutIps(source.getStatut());
                dto.setDateCreation(format.format(source.getDateCreation()));
                if (source.getIps().size() != 0) {
                    Optional<IpEntity> dernierDateModif = source.getIps().stream().filter(i -> i.getDateModification() != null).sorted(Comparator.comparing(IpEntity::getDateModification).reversed()).findFirst();
                    if (dernierDateModif.isPresent())
                        dto.setDateModificationDerniereIp(format.format(dernierDateModif.get().getDateModification()));
                }
                ContactWebDto contact = getContactWebDto(source);

                dto.setContact(contact);
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    private ContactWebDto getContactWebDto(EtablissementEntity source) {
        ContactWebDto contact = new ContactWebDto();
        contact.setNom(source.getContact().getNom());
        contact.setPrenom(source.getContact().getPrenom());
        contact.setAdresse(source.getContact().getAdresse());
        contact.setBoitePostale(source.getContact().getBoitePostale());
        contact.setCedex(source.getContact().getCedex());
        contact.setCodePostal(source.getContact().getCodePostal());
        contact.setVille(source.getContact().getVille());
        contact.setTelephone(source.getContact().getTelephone());
        contact.setMail(source.getContact().getMail());
        contact.setRole("etab");
        return contact;
    }

    @Bean
    public void converterEtablissementNotificationsDto() {
        Converter<EtablissementEntity, NotificationsDto> myConverter = new Converter<EtablissementEntity, NotificationsDto>() {
            @SneakyThrows
            public NotificationsDto convert(MappingContext<EtablissementEntity, NotificationsDto> context) {
                EtablissementEntity source = context.getSource();

                NotificationsDto dto = new NotificationsDto();
                //cas ou aucune IP déclarée
                if (Constant.STATUT_ETAB_SANSIP.equals(source.getStatut())) {
                    Map<String, String> notif = new HashMap<>();
                    notif.put("message", "Aucune IP déclarée");
                    notif.put("description", "les accès aux corpus acquis ne sont pas ouverts.");
                    dto.ajouterNotification(notif);
                    return dto;
                }
                //cas ou toutes les IP déclarées sont en état nouvelle IP
                if (source.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_NOUVELLE)).collect(Collectors.toList()).size() == source.getIps().size()) {
                    Map<String, String> notif = new HashMap<>();
                    notif.put("message", "Toutes les adresses IP déclarées sont en attente d'examen par l'Abes");
                    notif.put("description", "les accès aux corpus acquis ne sont pas ouverts.");
                    dto.ajouterNotification(notif);
                    return dto;
                }
                //cas nouvelle IP
                source.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_NOUVELLE)).forEach(i -> {
                    Map<String, String> notif = new HashMap<>();
                    StringBuilder value =  new StringBuilder("en attente d'examen par l'Abes : ");
                    value.append(i.getIp());
                    value.append(". L'accès correspondant n'est pas ouvert");
                    notif.put("message", "Nouvelle adresse IP");
                    notif.put("description", value.toString());
                    dto.ajouterNotification(notif);
                });
                //cas aucune IP validée
                if (source.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_ATTESTATION)).collect(Collectors.toList()).size() == source.getIps().size()) {
                    Map<String, String> notif = new HashMap<>();
                    notif.put("message", "Aucune IP validée");
                    notif.put("description", "les accès aux corpus acquis ne sont pas ouverts.");
                    dto.ajouterNotification(notif);
                    return dto;
                }
                //cas IP attestation demandée
                source.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_ATTESTATION)).forEach(i -> {
                    Map<String, String> notif = new HashMap<>();
                    StringBuilder key = new StringBuilder("IP en attente de validation : ");
                    key.append(i.getIp());
                    key.append(". Envoyer l'attestation à ln-admin@abes.fr.");
                    notif.put("message", key.toString());
                    notif.put("description", "Tant qu'une IP n'est pas validée l'accès correspondant n'est pas ouvert. Pour en savoir plus cliquer ici");
                    dto.ajouterNotification(notif);
                });
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementSearchWebDto() {
        Converter<EtablissementEntity, EtablissementSearchWebDto> myConverter = new Converter<EtablissementEntity, EtablissementSearchWebDto>() {
            @SneakyThrows
            public EtablissementSearchWebDto convert(MappingContext<EtablissementEntity, EtablissementSearchWebDto> context) {
                EtablissementEntity source = context.getSource();

                EtablissementSearchWebDto dto = new EtablissementSearchWebDto();
                dto.setId(source.getId());
                dto.setNomEtab(source.getNom());
                dto.setSiren(source.getSiren());
                dto.setIdAbes(source.getIdAbes());
                dto.setNomContact(source.getContact().getNom());
                dto.setPrenomContact(source.getContact().getPrenom());
                dto.setCpContact(source.getContact().getCodePostal());
                dto.setAdresseContact(source.getContact().getAdresse());
                dto.setVilleContact(source.getContact().getVille());
                dto.setMailContact(source.getContact().getMail());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

}
