package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.dto.export.ExportEditeurDto;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportEditeur extends ExportService<ExportEditeurDto, Integer> {
    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {

    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEditeurDto item) throws IOException {

    }

    @Override
    protected List<ExportEditeurDto> getItems(List<Integer> ids) {
        return null;
    }
}
