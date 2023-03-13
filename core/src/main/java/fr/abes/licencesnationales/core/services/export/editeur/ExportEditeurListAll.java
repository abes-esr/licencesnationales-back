package fr.abes.licencesnationales.core.services.export.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ExportEditeurService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripAccents;

@Service
public class ExportEditeurListAll extends ExportEditeurService<ExportEtablissementEditeurDto> {
    @Autowired
    private UtilsMapper mapper;
    @Autowired
    private  EtablissementService etablissementService;

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
        List<String> output = writeCommonLine(item.getIdEtablissement(), stripAccents(item.getNomEtablissement().toUpperCase()), stripAccents(item.getTypeEtablissement().toUpperCase()), stripAccents(item.getAdresse().toUpperCase()), item.getBoitePostale(), item.getCodePostal(), item.getCedex(), stripAccents(item.getVille().toUpperCase()), stripAccents(item.getNomContact().toUpperCase()), item.getMailContact(), item.getTelephoneContact());
        for (String ip : item.getListeAcces()) {
            output.add(ip);
        }
        printer.printRecord(output);
    }

    @Override
    protected List<ExportEtablissementEditeurDto> getItems(List<ExportEtablissementEditeurDto> etabs) {
        return mapper.mapList(etabs, ExportEtablissementEditeurDto.class);
    }
}
