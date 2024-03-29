package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ContactEditeurDto;
import fr.abes.licencesnationales.core.dto.export.ExportEditeurDto;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripAccents;

@Service
public class ExportEditeur extends ExportService<ExportEditeurDto, Integer> {

    private final UtilsMapper mapper;
    private final EditeurRepository dao;

    public ExportEditeur(UtilsMapper mapper, EditeurRepository repository) {
        this.mapper = mapper;
        this.dao = repository;
    }

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
        printer.printRecord((Object[]) new String[]{
                "Identifiant editeur",
                "Nom de l'editeur",
                "Adresse de l'editeur",
                "Nom(s) et Prenom(s) des contacts",
                "Adresse(s) mail(s) des contacts",
                "Type de contact",
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEditeurDto item) throws IOException {
        List<String> output = new ArrayList<>();
        if (item.getContact().size() == 0) {
            writeEditeurInfo(item, output);
            printer.printRecord(output);
        }
        else {
            for (ContactEditeurDto contact : item.getContact()) {
                writeEditeurInfo(item, output);
                output.add(stripAccents(contact.getNomPrenom()));
                output.add(contact.getMail());
                output.add(contact.getType());
                printer.printRecord(output);
                output.clear();
            }
        }
    }

    private void writeEditeurInfo(ExportEditeurDto item, List<String> output) {
        output.add(item.getId());
        output.add(stripAccents(item.getNom()));
        output.add(stripAccents(item.getAdresse()));
    }

    @Override
    protected List<ExportEditeurDto> getItems(List<Integer> ids) {
        return mapper.mapList(dao.getAllByIdIn(ids),ExportEditeurDto.class);
    }
}
