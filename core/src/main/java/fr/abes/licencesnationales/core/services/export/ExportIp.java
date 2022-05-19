package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportIpDto;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripAccents;

@Service
public class ExportIp extends ExportService<ExportIpDto, String> {
    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private IpRepository dao;

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
        printer.printRecord((Object[]) new String[] {
                "Date de saisie",
                "Type d'IP",
                "Valeur",
                "Date de modification du statut",
                "Statut",
                "Commentaires"
        });

    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportIpDto item) throws IOException {
        List<String> output = new ArrayList<>();
        output.add(item.getDateCreation());
        output.add(item.getType());
        output.add(item.getIp());
        output.add(item.getDateModificationStatut());
        output.add(stripAccents(item.getStatut()));
        output.add(stripAccents(item.getCommentaire()));
        printer.printRecord(output);
    }

    @Override
    protected List<ExportIpDto> getItems(List<String> sirens) {
        return mapper.mapList(dao.findAllBySiren(sirens.get(0)), ExportIpDto.class);
    }
}
