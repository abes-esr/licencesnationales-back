/*
package fr.abes.lnevent.fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fr.abes.lnevent.fr.abes.licencesnationales.web.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.lnevent.fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.lnevent.fr.abes.licencesnationales.core.repository.EventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeuplementService {

    private EtablissementRepository etablissementRepository;
    private EventRepository eventRepository;
    private ApplicationEventPublisher applicationEventPublisher;

    public PeuplementService(EtablissementRepository etablissementRepository, EventRepository eventRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.etablissementRepository = etablissementRepository;
        this.eventRepository = eventRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public <T> List<T> loadObjectList(Class<T> type, String fileName) {
        try {
            CsvSchema bootstrapSchema = CsvSchema.emptySchema()
                    .withHeader()
                    .withColumnSeparator(';')
                    .withNullValue("");
            CsvMapper mapper = new CsvMapper();
            File file = new ClassPathResource(fileName).getFile();
            MappingIterator<T> readValues =
                    mapper.readerWithSchemaFor(type).with(bootstrapSchema).readValues(file);
            return readValues.readAll();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String ajoutEtablissementfromCSV() {


        List<EtablissementCSV> etablissementCSVS = loadObjectList(EtablissementCSV.class, "ComptesEtablissementsLNSiren8.csv");

        List<EtablissementCSV> etablissementFiltered = etablissementCSVS.stream().filter(
                etablissementCSV -> !etablissementCSV.IDEtablissement.contains("=>")
                        && !etablissementCSV.SIRENEtalissement.contains("=>")
                        && !etablissementCSV.NomEtablissement.contains("=>")
                        && !etablissementCSV.TypeEtablissement.contains("=>")
                        && !etablissementCSV.Adresse.contains("=>")
                        && !etablissementCSV.Adresse2.contains("=>")
                        && !etablissementCSV.BoitePostale.contains("=>")
                        && !etablissementCSV.CodePostal.contains("=>")
                        && !etablissementCSV.Cedex.contains("=>")
                        && !etablissementCSV.Ville.contains("=>")
                        && !etablissementCSV.ContactNom.contains("=>")
                        && !etablissementCSV.ContactEmail.contains("=>")
                        && !etablissementCSV.ContactTel.contains("=>")
                        && !etablissementCSV.ListeAcces.contains("=>")).collect(Collectors.toList());

        for (EtablissementCSV etablissementCSV :
                etablissementFiltered) {
            EtablissementCreeEvent etablissementCreeEvent =
                    new EtablissementCreeEvent(this,
                            new EtablissementDTO(
                                    etablissementCSV.NomEtablissement,
                                    etablissementCSV.SIRENEtalissement,
                                    etablissementCSV.TypeEtablissement,
                                    etablissementCSV.IDEtablissement,
                                    etablissementCSV.ContactEmail,
                                    "",
                                    etablissementCSV.ContactNom,
                                    "",
                                    etablissementCSV.ContactTel,
                                    etablissementCSV.Adresse,
                                    etablissementCSV.BoitePostale,
                                    etablissementCSV.CodePostal,
                                    etablissementCSV.Cedex,
                                    etablissementCSV.Ville,)
                            );
*/
/*            applicationEventPublisher.publishEvent(etablissementCreeEvent);
            eventRepository.save(new EventEntity(etablissementCreeEvent));*//*

        }

        return "done";
    }
}
*/
