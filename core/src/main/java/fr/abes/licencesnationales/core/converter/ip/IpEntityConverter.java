package fr.abes.licencesnationales.core.converter.ip;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.exception.IpException;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Cette classe recense tous les convertisseurs d'objet événement sur les IP  vers les objets d'entité
 */
@Component
public class IpEntityConverter {

    @Autowired
    private UtilsMapper utilsMapper;

    /**
     * Bean de conversion d'un événement d'ajout d'adresse IP
     */
    @Bean
    public void converterIpAjouteeEvent() {
        Converter<IpAjouteeEvent, IpEntity> myConverter = new Converter<IpAjouteeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpAjouteeEvent, IpEntity> context) {
                try {
                    IpAjouteeEvent ipAjouteeEvent = context.getSource();
                    IpEntity entity;
                    if (ipAjouteeEvent.getTypeIp() == IpType.IPV4) {
                        entity = new IpV4(ipAjouteeEvent.getIp(), ipAjouteeEvent.getCommentaires());
                    } else if (ipAjouteeEvent.getTypeIp() == IpType.IPV6) {
                        entity = new IpV6(ipAjouteeEvent.getIp(), ipAjouteeEvent.getCommentaires());
                    } else {
                        throw new UnsupportedOperationException("Le type IP n'est pas supporté : " + ipAjouteeEvent.getTypeIp());
                    }

                    return entity;
                } catch (IpException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de modification d'adresse IP
     */
    @Bean
    public void converterIpModifieeEvent() {
        Converter<IpModifieeEvent, IpEntity> myConverter = new Converter<IpModifieeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpModifieeEvent, IpEntity> context) {
                try {
                    IpModifieeEvent ipModifieeEvent = context.getSource();
                    IpEntity entity;
                    if (ipModifieeEvent.getTypeIp() == IpType.IPV4) {
                        entity = new IpV4(ipModifieeEvent.getId(), ipModifieeEvent.getIp(), ipModifieeEvent.getCommentaires());
                    } else if (ipModifieeEvent.getTypeIp() == IpType.IPV6) {
                        entity = new IpV6(ipModifieeEvent.getId(), ipModifieeEvent.getIp(), ipModifieeEvent.getCommentaires());
                    } else {
                        throw new UnsupportedOperationException("Le type IP n'est pas supporté : " + ipModifieeEvent.getTypeIp());
                    }

                    return entity;
                } catch (IpException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }
}
