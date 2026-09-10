package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.editeur.EditeurEventRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {
    @Autowired
    private EtablissementEventRepository etablissementDao;

    @Autowired
    private EditeurEventRepository editeurDao;

    @Autowired
    private IpEventRepository ipDao;

    @Autowired
    private ObjectMapper mapper;

    public void save(EventEntity event) throws JsonProcessingException {
        if (event instanceof EtablissementEventEntity) {
            if (event instanceof EtablissementDiviseEventEntity) {
                ((EtablissementDiviseEventEntity) event).setEtablisementsDivisesInBdd(mapper.writeValueAsString(((EtablissementDiviseEventEntity) event).getEtablissementDivises()));
            }
            if (event instanceof EtablissementFusionneEventEntity) {
                ((EtablissementFusionneEventEntity) event).setAnciensEtablissementsInBdd(mapper.writeValueAsString(((EtablissementFusionneEventEntity) event).getSirenAnciensEtablissements()));
            }
            etablissementDao.save((EtablissementEventEntity) event);
        } else if (event instanceof EditeurEventEntity) {
            editeurDao.save((EditeurEventEntity) event);
        } else if (event instanceof IpEventEntity) {
            ipDao.save((IpEventEntity) event);
        }
    }

    /**
     * Récupère la date de dernière suppression des IP d'un établissement
     * @param etab l'établissement
     * @return la date de dernière suppression d'une IP de l'établissement, null si aucune IP supprimée
     */
    public Date getLastDateSuppressionIpEtab(EtablissementEntity etab) {
        List<IpEventEntity> listeIpSupprimees = ipDao.getIpSupprimeBySiren(etab.getSiren());
        if (listeIpSupprimees.size() > 0) {
            return listeIpSupprimees.stream().sorted((o1, o2) -> o2.getDateCreationEvent().compareTo(o1.getDateCreationEvent())).findFirst().get().getDateCreationEvent();
        }
        return null;
    }

    /**
     * Récupère la date de création d'un établissement sous le SIREN spécifié.
     * Cette méthode se base sur le premier événement chronologique de l'historique
     * où ce numéro SIREN est mentionné.
     *
     * @param siren Le numéro SIREN de l'établissement
     * @return La date du premier événement où ce SIREN apparaît
     * @throws UnknownEtablissementException si aucun événement n'est trouvé pour cet établissement
     */
    public Date getDateCreationEtab(String siren) throws UnknownEtablissementException {
        List<EtablissementEventEntity> etablissement = getHistoEtab(siren);
        if (etablissement.isEmpty()) {
            throw new UnknownEtablissementException(String.format(Constant.ERROR_ETAB_EXISTE_PAS, siren));
        }
        return etablissement.stream()
                .filter(e -> siren.equals(e.getSiren()))
                .findFirst()
                .map(EventEntity::getDateCreationEvent)
                .orElse(etablissement.get(0).getDateCreationEvent());
    }

    /**
     * Récupère la date de dernière modification d'un établissement
     *
     * @param etab
     * @return
     */
    public Date getLastDateModificationEtab(String siren) {
        List<EtablissementEventEntity> etablissementsModifies = etablissementDao.getLastModicationEtab(siren);
        if (etablissementsModifies.size() > 0) {
            return etablissementsModifies.stream().sorted((e1, e2) -> e2.getDateCreationEvent().compareTo(e1.getDateCreationEvent())).findFirst().get().getDateCreationEvent();
        }
        return null;
    }

    public Date getDateSuppressionEtab(String siren) {
        List<EtablissementEventEntity> etablissement = etablissementDao.getDateSuppressionEtab(siren);
        if (etablissement.isEmpty())
            return null;
        return etablissement.get(0).getDateCreationEvent();
    }

    public Date getDateValidationIp(String ip) {
        List<IpEventEntity> ipEventEntity = ipDao.getDateValidation(ip);
        if (ipEventEntity.isEmpty())
            return null;
        return ipEventEntity.get(0).getDateCreationEvent();
    }

    public Date getDateSuppressionIp(String ip) {
        List<IpEventEntity> ipEventEntity = ipDao.getDateSuppression(ip);
        if (ipEventEntity.isEmpty())
            return null;
        return ipEventEntity.get(0).getDateCreationEvent();
    }

    public Date getDateFusionEtab(String siren) {
        List<EtablissementEventEntity> etablissementEventEntity = etablissementDao.getDateFusion(siren);
        if (etablissementEventEntity.isEmpty())
            return null;
        return etablissementEventEntity.get(0).getDateCreationEvent();
    }

    public Date getDateScissionEtab(String siren) {
        List<EtablissementEventEntity> etablissementEventEntity = etablissementDao.getDateScission(siren);
        if (etablissementEventEntity.isEmpty())
            return null;
        return etablissementEventEntity.get(0).getDateCreationEvent();
    }

    public EtablissementFusionneEventEntity getEtabFusionEvent(String siren) {
        List<EtablissementFusionneEventEntity> etab = etablissementDao.getEventFusion(siren);
        if (etab.isEmpty())
            throw new UnknownEtablissementException("Etablissement inconnu");
        return etab.get(0);
    }

    public EtablissementDiviseEventEntity getEtabScissionEvent(String siren) {
        List<EtablissementDiviseEventEntity> etab = etablissementDao.getEventScission(siren);
        if (etab.isEmpty())
            throw new UnknownEtablissementException("Etablissement inconnu");
        return etab.get(0);
    }

    /**
     * Récupère l'historique de tous les événements pour les établissements ayant des événements
     * sur la période donnée, depuis leur création.
     *
     * @param dateDebut Date de début
     * @param dateFin   Date de fin
     * @return Liste ordonnée de tous les événements
     */
    public List<EtablissementEventEntity> getHistoAllEtab(Date dateDebut, Date dateFin) {
        List<EtablissementEventEntity> events = etablissementDao.findBetweenDates(dateDebut, dateFin);
        List<EtablissementEventEntity> allEvents = new ArrayList<>();

        for (EtablissementEventEntity event : events) {
            allEvents.addAll(getHistoEtab(event.getSiren()));
        }

        List<EtablissementEventEntity> uniqueEvents = allEvents.stream().distinct().collect(Collectors.toList());
        uniqueEvents.sort((e1, e2) -> e1.getDateCreationEvent().compareTo(e2.getDateCreationEvent()));
        return uniqueEvents;
    }

    /**
     * Récupère l'historique des événements d'un établissement de manière récursive.
     * Remonte la chaîne des anciens SIRENs et descend vers les nouveaux SIRENs.
     *
     * @param siren Le SIREN actuel de l'établissement
     * @return La liste ordonnée de tous les événements de l'historique
     */
    public List<EtablissementEventEntity> getHistoEtab(String siren) {
        return getHistoEtab(siren, new HashSet<>());
    }

    /**
     * Méthode récursive pour récupérer l'historique d'un SIREN (anciens et nouveaux).
     * 
     * @param siren Le SIREN courant
     * @param visited L'ensemble des SIRENs déjà visités pour éviter les boucles
     * @return La liste des événements liés
     */
    private List<EtablissementEventEntity> getHistoEtab(String siren, Set<String> visited) {
        if (siren == null || siren.isEmpty() || !visited.add(siren)) {
            return new ArrayList<>();
        }

        List<EtablissementEventEntity> events = new ArrayList<>(etablissementDao.findBySiren(siren));

        // Récupération récursive des anciens événements
        for (EtablissementEventEntity event : new ArrayList<>(events)) {
            if (event instanceof EtablissementModifieEventEntity) {
                String ancienSiren = ((EtablissementModifieEventEntity) event).getAncienSiren();
                if (ancienSiren != null && !ancienSiren.isEmpty()) {
                    events.addAll(getHistoEtab(ancienSiren, visited));
                    break;
                }
            }
        }

        // Récupération récursive des nouveaux événements
        List<EtablissementEventEntity> nouveaux = etablissementDao.findByAncienSiren(siren);
        for (EtablissementEventEntity event : nouveaux) {
            String nouveauSiren = event.getSiren();
            if (nouveauSiren != null && !nouveauSiren.isEmpty()) {
                events.addAll(getHistoEtab(nouveauSiren, visited));
                break;
            }
        }

        List<EtablissementEventEntity> uniqueEvents = events.stream().distinct().collect(Collectors.toList());
        uniqueEvents.sort((e1, e2) -> e1.getDateCreationEvent().compareTo(e2.getDateCreationEvent()));
        return uniqueEvents;
    }

    public List<EtablissementEventEntity> getEtabsSupprimes() {
        return etablissementDao.findDeleted();
    }

    List<EtablissementEventEntity> findAllByTypeEtablissementIn(List<TypeEtablissementEntity> ids) {
        return etablissementDao.findAllByTypeEtablissementIn(ids);
    }

    public List<IpEventEntity> getHistoAllIp(Date dateDebut, Date dateFin) {
        return ipDao.findBetweenDates(dateDebut, dateFin);
    }

    public List<IpEventEntity> getHistoIp(String siren) {
        return ipDao.findBySiren(siren);
    }

    public List<IpEventEntity> getIpSupprimeesBySiren(String siren) {
        return ipDao.getIpSupprimeBySiren(siren);
    }

}
