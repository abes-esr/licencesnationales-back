package fr.abes.licencesnationales.core.services.export.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurFusionDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurScissionDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ExportEditeurService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportEditeurSplitInstitutions extends ExportEditeurService<ExportEtablissementEditeurScissionDto> {
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
                "Siren scindé",
                "Liste Acces"
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementEditeurScissionDto item) throws IOException {
        List<String> output = writeCommonLine(item.getIdEtablissement(), item.getNomEtablissement(), item.getTypeEtablissement(), item.getAdresse(), item.getBoitePostale(), item.getCodePostal(), item.getCedex(), item.getVille(), item.getNomContact(), item.getMailContact(), item.getTelephoneContact());
        output.add(item.getSirenScinde());
        for (String ip : item.getListeAcces()) {
            output.add(ip);
        }
        printer.printRecord(output);
    }

    @Override
    protected List<ExportEtablissementEditeurScissionDto> getItems(List<Integer> ids) {
        List<EtablissementEntity> etabs = etablissementService.getAllEtabEditeur(ids);
        Date dateDernierEnvoi = dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc().orElse(new DateEnvoiEditeurEntity()).getDateEnvoi();
        List<ExportEtablissementEditeurScissionDto> dtos = mapper.mapList(etabs.stream().filter(e -> {
            Date dateModificationEtab = eventService.getDateScissionEtab(e);
            if (dateModificationEtab == null) {
                return false;
            } else {
                return dateModificationEtab.after(dateDernierEnvoi);
            }
        }).collect(Collectors.toList()), ExportEtablissementEditeurScissionDto.class);
        dtos.forEach(dto -> dto.setSirenScinde(eventService.getEtabScissionEvent(dto.getSirenEtablissement()).getAncienSiren()));
        return dtos;
    }
}
