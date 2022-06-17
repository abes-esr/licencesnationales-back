package fr.abes.licencesnationales.core.services.export.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurFusionDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ExportEditeurService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportEditeurNewAccess extends ExportEditeurService<ExportEtablissementEditeurDto> {
    @Autowired
    private UtilsMapper mapper;
    @Autowired
    private EtablissementService etablissementService;
    @Autowired
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;
    @Autowired
    private EventService eventService;

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
        printer.printRecord((Object[]) new String[] {
                "ID Etablissement",
                "Nom Etablissement",
                "Type d'Etablissement",
                "Adresse",
                "Boite Postale",
                "Code Postal",
                "Cedex",
                "Ville",
                "Contact Nom",
                "Contact Email",
                "Contact Tel",
                "Liste Acces"
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementEditeurDto item) throws IOException {
        List<String> output = writeCommonLine(item.getIdEtablissement(), item.getNomEtablissement(), item.getTypeEtablissement(), item.getAdresse(), item.getBoitePostale(), item.getCodePostal(), item.getCedex(), item.getVille(), item.getNomContact(), item.getMailContact(), item.getTelephoneContact());
        for (String ip : item.getListeAcces()) {
            output.add(ip);
        }
        printer.printRecord(output);
    }

    @Override
    protected List<ExportEtablissementEditeurDto> getItems(List<EtablissementEntity> etabs) {
        Date dateDernierEnvoi = dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc().orElse(new DateEnvoiEditeurEntity()).getDateEnvoi();
        etabs.stream().forEach(e -> {
            //on récupère les IP validées depuis la dernière exécution
            e.setIps(e.getIps().stream().filter(i -> {
                Date dateValidation = eventService.getDateValidationIp(i);
                if (dateValidation == null || dateValidation.before(dateDernierEnvoi))
                    return false;
                else
                    return true;
            }).collect(Collectors.toSet()));
        });
        //on ne retourne que les etablissement disposant d'au moins une ip validée depuis la dernière exécution
        return mapper.mapList(etabs.stream().filter(e -> e.getIps().size() != 0).collect(Collectors.toList()), ExportEtablissementEditeurDto.class);
    }
}
