package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.NotificationAdminDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class EtablissementService {
    @Autowired
    private EtablissementRepository etablissementDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private ContactRepository contactEtablissementDao;

    @Autowired
    private StatutRepository statutRepository;

    @Setter
    private Calendar oneYearAgo;

    public EtablissementService() {
        this.oneYearAgo = Calendar.getInstance();
        oneYearAgo.add(Calendar.YEAR, -1);
    }

    public EtablissementEntity getFirstBySiren(String siren) {
        return etablissementDao.getFirstBySiren(siren).orElseThrow(() -> new UnknownEtablissementException("Siren : " + siren));
    }

    /**
     * Enregistre ou mets à jour un établissement
     *
     * @param entity
     * @throws SirenExistException
     * @throws MailDoublonException
     */
    public void save(EtablissementEntity entity) throws SirenExistException, MailDoublonException {
        if (entity.getId() == null) {
            // Création d'un nouvel établisssement
            entity.getContact().setRole("etab");
            entity.setValide(false);
        }
        etablissementDao.save(entity);
    }

    public void saveAll(Set<EtablissementEntity> entities) throws MailDoublonException, SirenExistException {

        Iterator<EtablissementEntity> iter = entities.iterator();
        while (iter.hasNext()) {
            this.save(iter.next());
        }

    }

    public void deleteBySiren(String siren) {
        etablissementDao.deleteBySiren(siren);
    }

    public boolean existeSiren(String siren) {
        return etablissementDao.existsBySiren(siren);
    }

    public boolean existeMail(String email) {
        return contactEtablissementDao.findByMail(email).isPresent();
    }

    public List<EtablissementEntity> findAll() {
        List<EtablissementEntity> list = etablissementDao.findAll();
        list.forEach(etab -> {
            etab.setIdAbes(GenererIdAbes.genererIdAbes(etab.getIdAbes()));
        });
        return list;
    }

    public EtablissementEntity getUserByMail(String mail) {
        return etablissementDao.getEtablissementEntityByContact_MailContains(mail).orElseThrow(() -> new UnknownEtablissementException("mail : " + mail));
    }

    public List<EtablissementEntity> getEtabASupprimer() {
        List<EtablissementEntity> emptyEtab = etablissementDao.getEtablissementEntityByIps_Empty();
        List<EtablissementEntity> listeOut = new ArrayList<>();
        for (EtablissementEntity etab : emptyEtab) {
            Date dateSuppressionDerniereIp = eventService.getLastDateSuppressionIpEtab(etab);
            if (dateSuppressionDerniereIp != null) {
                //si on a la date de dernière suppression d'une IP de l'etab, on regarde si elle est plus vieille d'un an
                if (dateSuppressionDerniereIp.before(oneYearAgo.getTime())) {
                    listeOut.add(etab);
                }
            } else {
                //on récupère la date de création de l'établissement et on regarde s'il est plus vieux d'un an
                try {
                    Date dateCreationEtab = eventService.getDateCreationEtab(etab);
                    if (dateCreationEtab.before(oneYearAgo.getTime())) {
                        listeOut.add(etab);
                    }
                } catch (UnknownEtablissementException ex) {
                    log.warn("L'établissement " + etab.getSiren() + " ne dispose pas d'évènement de création");
                }
            }
        }
        return listeOut;
    }

    public List<EtablissementEntity> getEtabsAvecUneIpSupprimeeDepuisDernierEnvoiEditeur(List<EtablissementEntity> listeEtab, Date date) {
        List<EtablissementEntity> listeOut = new ArrayList<>();
        listeEtab.stream().forEach(e -> {
            Date dateSupp = eventService.getLastDateSuppressionIpEtab(e);
            if (dateSupp != null && dateSupp.after(date)) {
                listeOut.add(e);
            }
        });
        return listeOut;
    }

    public List<NotificationAdminDto> getEtabNonValides(List<EtablissementEntity> listEtab) {
        List<NotificationAdminDto> dtos = new ArrayList<>();
        //ajout établissement non validés
        listEtab.stream().filter(e -> !e.isValide()).forEach(e -> {
            NotificationAdminDto dto = new NotificationAdminDto(e.getDateCreation(), "Nouvel établissement", e.getNom());
            dtos.add(dto);
        });
        return dtos;
    }

    public List<NotificationAdminDto> getEtabIpEnValidation(List<EtablissementEntity> listEtab) {
        List<NotificationAdminDto> dtos = new ArrayList<>();
        //ajout établissements avec IP en validation
        listEtab.stream().forEach(e -> {
            Optional<IpEntity> ip = e.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_NOUVELLE)).findFirst();
            if (ip.isPresent()) {
                IpEntity i = ip.get();
                Date dateIp = (i.getDateModification() != null) ? i.getDateModification() : i.getDateCreation();
                NotificationAdminDto dto = new NotificationAdminDto(dateIp, "Nouvelle IP", e.getNom());
                dtos.add(dto);
            }
        });
        return dtos;
        }

        public List<NotificationAdminDto> getEtabIpSupprimee (List <EtablissementEntity> listEtab) {
            List<NotificationAdminDto> dtos = new ArrayList<>();
            //ajout etab ayant supprimé toutes leurs IPs depuis le dernier envoi du batch éditeur
            Date dateDernierEnvoiEditeur = new Date();
            List<EtablissementEntity> listEtabSupprime = getEtabsAvecUneIpSupprimeeDepuisDernierEnvoiEditeur(listEtab, dateDernierEnvoiEditeur);
            listEtab.stream().filter(e -> e.getIps().size() == 0).filter(listEtabSupprime::contains).forEach(e -> {
                NotificationAdminDto dto = new NotificationAdminDto(e.getDateCreation(), e.getNom(), "Suppression IP depuis dernier envoi");
                dtos.add(dto);
            });
            return dtos;
        }
    }
