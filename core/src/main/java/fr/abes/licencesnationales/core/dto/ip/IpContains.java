package fr.abes.licencesnationales.core.dto.ip;


import fr.abes.licencesnationales.core.entities.IpEntity;
import lombok.extern.slf4j.Slf4j;

// Stocke les infos retournées après un test d'existence d'ip dans la base.
// erreurAcces contient le message d'erreur correspondant à l'existence de l'ip (voir cointains)
// contains contient le code correspondant à l'existence de l'ip dans la base (si l'ip est comprise dans une plage, si elle chevauche une plahe, si elle contient une adresse...)
// dbacces contient l'ip déjà présente dans la base
@Slf4j
public class IpContains {
    private IpEntity erreurAcces;
    private IpEntity dbAcces;
    private Integer contains;

    public IpContains(IpEntity erreurAcces, IpEntity dbAcces, Integer contains) {
        this.erreurAcces = erreurAcces;
        this.dbAcces = dbAcces;
        this.contains = contains;
    }

    public IpEntity getErreurAcces() {
        return erreurAcces;
    }

    public void setErreurAcces(IpEntity erreurAcces) {
        this.erreurAcces = erreurAcces;
    }

    public IpEntity getDBAcces() {
        return dbAcces;
    }

    public void setDBAcces(IpEntity acces) {
        this.dbAcces = acces;
    }

    public Integer getContains() {
        return contains;
    }

    public void setContains(Integer contains) {
        this.contains = contains;
    }

}