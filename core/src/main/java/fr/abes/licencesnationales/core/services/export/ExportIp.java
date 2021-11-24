package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.dto.export.ExportIpDto;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportIp extends ExportService<ExportIpDto, Integer> {
    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {

    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportIpDto item) throws IOException {

    }

    @Override
    protected List<ExportIpDto> getItems(List<Integer> ids) {
        return null;
    }
}
