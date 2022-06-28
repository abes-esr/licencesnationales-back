package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementUserDto;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripAccents;

@Service
public class ExportEtablissementUser extends ExportService<ExportEtablissementUserDto, String> {
    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private EtablissementRepository dao;

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
       printer.printRecord((Object[]) new String[]{
               "Identifiant Abes",
               "Siren",
               "Nom de l'etablissement",
               "Type de l'etablissement",
               "Adresse de l'etablissement",
               "Telephone contact",
               "Nom et prenom contact",
               "Adresse mail contact",
               "IP validees"
       });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementUserDto item) throws IOException {
        List<String> output = new ArrayList<>();
        if (item.getIps().size() == 0) {
            writeEtabInfo(item, output);
            printer.printRecord(output);
        }
        else {
            for (String ip : item.getIps()) {
                writeEtabInfo(item, output);
                output.add(ip);
                printer.printRecord(output);
                output.clear();
            }
        }
    }

    private void writeEtabInfo(ExportEtablissementUserDto item, List<String> output) {
        output.add(GenererIdAbes.genererIdAbes(item.getIdAbes()));
        output.add(item.getSiren());
        output.add(stripAccents(item.getNom()));
        output.add(stripAccents(item.getTypeEtablissement()));
        output.add(stripAccents(item.getAdresse()));
        output.add(item.getTelephone());
        output.add(stripAccents(item.getNomPrenomContact()));
        output.add(item.getMailContact());
    }

    @Override
    protected List<ExportEtablissementUserDto> getItems(List<String> ids) {
        return mapper.mapList(dao.findAllBySirenIn(ids), ExportEtablissementUserDto.class);
    }
}
