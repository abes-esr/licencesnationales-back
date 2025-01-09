package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.NotificationAdminDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EtablissementService {
    @Autowired
    private EtablissementRepository etablissementDao;

    @Autowired
    private IpRepository ipDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private ContactRepository contactEtablissementDao;

    @Autowired
    private ReferenceService referenceService;

    @Setter
    private Calendar oneYearAgo;
    
    @Autowired
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;

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
        return list.stream().map(e -> {
            EtablissementEntity etablissement =  new EtablissementEntity(
                    e.getId(),
                    e.getNom(),
                    e.getSiren(),
                    e.getTypeEtablissement(),
                    GenererIdAbes.genererIdAbes(e.getIdAbes()),
                    e.getContact()
            );
                    etablissement.setDateCreation(e.getDateCreation());
                    etablissement.setValide(e.isValide());
                    etablissement.setIps(e.getIps());
                    return etablissement;
        }
        ).collect(Collectors.toList());
    }

    public EtablissementEntity getUserByMail(String mail) {
        return etablissementDao.getEtablissementEntityByContact_MailContains(mail).orElseThrow(() -> new UnknownEtablissementException("mail : " + mail));
    }

    public List<EtablissementEntity> getEtabASupprimer() {
        List<EtablissementEntity> emptyEtab = etablissementDao.getEtablissementEntityByIps_Empty();
        List<EtablissementEntity> listeOut = new ArrayList<>();
        for (EtablissementEntity etab : emptyEtab) {
            if (!etab.getContact().getRole().equals("admin")) {
                Date dateSuppressionDerniereIp = eventService.getLastDateSuppressionIpEtab(etab);
                if (dateSuppressionDerniereIp != null) {
                    //si on a la date de dernière suppression d'une IP de l'etab, on regarde si elle est plus vieille d'un an
                    if (dateSuppressionDerniereIp.before(oneYearAgo.getTime())) {
                        listeOut.add(etab);
                    }
                } else {
                    //on récupère la date de création de l'établissement et on regarde s'il est plus vieux d'un an
                    try {
                        Date dateCreationEtab = eventService.getDateCreationEtab(etab.getSiren());
                        if (dateCreationEtab.before(oneYearAgo.getTime())) {
                            listeOut.add(etab);
                        }
                    } catch (UnknownEtablissementException ex) {
                        log.warn("L'établissement " + etab.getSiren() + " ne dispose pas d'évènement de création");
                    }
                }
            }
        }
        return listeOut;
    }

    /**
     * Permet de récupérer les établissements pour l'export Editeur
     * Condition sur le type d'établissement
     * Ne retourne que les établissements validés et ayant au moins une IP validée, après avoir supprimés les IP non validées
     *
     * @param ids liste des types d'établissements devant être retournés
     * @return liste d'établissements dont le type est un des types passé en paramètre, ayant au moins une IP validée, et purgé de ses IP non validées
     */
    public List<EtablissementEntity> getAllEtabEditeur(List<Integer> ids) {
        List<TypeEtablissementEntity> typesEtab = referenceService.findTypeEtabByIds(ids);
        Set<EtablissementEntity> listeEtab = etablissementDao.findAllByValideAndTypeEtablissementIn(true, typesEtab);
        List<EtablissementEntity> listeEtabFiltres = listeEtab.stream().filter(e -> e.getIps().stream().filter(i -> i.getStatut().getIdStatut().equals(Constant.STATUT_IP_VALIDEE)).collect(Collectors.toList()).size() > 0).collect(Collectors.toList());
        //on supprime les IP non validés des établissements retournés
        listeEtabFiltres.stream().forEach(e -> e.getIps().removeIf(ipEntity -> !ipEntity.getStatut().getIdStatut().equals(Constant.STATUT_IP_VALIDEE)));
        return listeEtabFiltres;
    }

    public List<ExportEtablissementEditeurDto> getDeletedEtabs(List<Integer> ids) {
        List<String> typesEtab = referenceService.findTypeEtabToStringByIds(ids);

        List<EtablissementEventEntity> listeEtabEvent = eventService.getEtabsSupprimes();
        List<ExportEtablissementEditeurDto> listeEtab = new ArrayList<>();

        for (EtablissementEventEntity e: listeEtabEvent) {
            if(e.getTypeEtablissement() != null & typesEtab.contains(e.getTypeEtablissement())){
                ExportEtablissementEditeurDto etab = new ExportEtablissementEditeurDto();
                etab.setSirenEtablissement(e.getSiren());
                etab.setNomEtablissement(e.getNomEtab());
                etab.setIdEtablissement(e.getIdAbes());
                etab.setTypeEtablissement(e.getTypeEtablissement());
                listeEtab.add(etab);
            }
        }
        return listeEtab;
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
            NotificationAdminDto dto = new NotificationAdminDto(e.getSiren(), e.getDateCreation(), "Nouvel établissement", e.getNom());
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
                NotificationAdminDto dto = new NotificationAdminDto(e.getSiren(), dateIp, "Nouvelle IP", e.getNom());
                dtos.add(dto);
            }
        });
        return dtos;
    }

    public List<NotificationAdminDto> getEtabIpSupprimee(List<EtablissementEntity> listEtab) {
        List<NotificationAdminDto> dtos = new ArrayList<>();
        //ajout etab ayant supprimé toutes leurs IPs depuis le dernier envoi du batch éditeur
        Optional<DateEnvoiEditeurEntity> dateEnvoiEditeurEntity = dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc();
        Date dateDernierEnvoiEditeur = new Date();
        if (dateEnvoiEditeurEntity.isPresent()) {
            dateDernierEnvoiEditeur = dateEnvoiEditeurEntity.get().getDateEnvoi();
        }
        List<EtablissementEntity> listEtabSupprime = getEtabsAvecUneIpSupprimeeDepuisDernierEnvoiEditeur(listEtab, dateDernierEnvoiEditeur);
        final Date finalDateDernierEnvoiEditeur = dateDernierEnvoiEditeur;
        listEtab.stream().filter(e -> e.getIps().size() == 0).filter(listEtabSupprime::contains).forEach(e -> {
            NotificationAdminDto dto = new NotificationAdminDto(e.getSiren(), finalDateDernierEnvoiEditeur,  "Suppression IP depuis dernier envoi", e.getNom());
            dtos.add(dto);
        });
        return dtos;
    }

    public List<EtablissementEntity> search(List<String> criteres) {
        return searchByCriteria(etablissementDao.findAll(), criteres);
    }

    /**
     * Fonction récursive permettant d'alimenter une liste de résultats à partir d'une liste d'établissements et de critères de recherche
     * @param etabs liste des établissements sur lesquels effectuer la recherche
     * @param criteres liste des critères dépilée à chaque nouveau passage
     */
    private List<EtablissementEntity> searchByCriteria(List<EtablissementEntity> etabs, List<String> criteres) {
        List<EtablissementEntity> resultats = new ArrayList<>();
        String critLower = criteres.get(0).toLowerCase();
        resultats.addAll(etabs.stream().filter(etab -> etab.getSiren().toLowerCase().contains(critLower) || etab.getNom().toLowerCase().contains(critLower) || etab.getIdAbes().toLowerCase().contains(critLower)
                || etab.getContact().getNom().toLowerCase().contains(critLower) || etab.getContact().getPrenom().toLowerCase().contains(critLower) || etab.getContact().getAdresse().toLowerCase().contains(critLower)
                || etab.getContact().getCodePostal().toLowerCase().contains(critLower) || etab.getContact().getVille().toLowerCase().contains(critLower) || etab.getContact().getMail().toLowerCase().contains(critLower)).collect(Collectors.toList()));
        criteres.remove(0);
        //condition de sortie de la fonction récursive
        if (criteres.size() == 0) {
            return resultats;
        }
        return searchByCriteria(resultats, criteres);
    }
}
