package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementUserDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportEtablissementUser extends ExportService<ExportEtablissementUserDto, String> {
    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private EtablissementRepository dao;

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
       printer.printRecord((Object[]) new String[] {
               "ID Abes",
               "Siren",
               "Nom de l'établissement",
               "Type de l'établissement",
               "Adresse de l'établissement",
               "Téléphone contact",
               "Nom et prénom contact",
               "Adresse mail contact",
               "IP"
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
            }
        }
    }

    private void writeEtabInfo(ExportEtablissementUserDto item, List<String> output) {
        output.add(item.getIdAbes());
        output.add(item.getSiren());
        output.add(item.getNom());
        output.add(item.getTypeEtablissement());
        output.add(item.getAdresse());
        output.add(item.getTelephone());
        output.add(item.getNomPrenomContact());
        output.add(item.getMailContact());
    }

    @Override
    protected List<ExportEtablissementUserDto> getItems(List<String> ids) {
        return mapper.mapList(dao.findAllBySirenIn(ids), ExportEtablissementUserDto.class);
    }
}
