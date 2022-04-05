package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementAdminDto;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportEtablissementAdmin extends ExportService<ExportEtablissementAdminDto, String> {
    private final UtilsMapper mapper;
    private final EtablissementRepository dao;

    public ExportEtablissementAdmin(UtilsMapper mapper, EtablissementRepository repository) {
        this.mapper = mapper;
        this.dao = repository;
    }

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
        printer.printRecord((Object[]) new String[]{
                "ID Abes",
                "Siren",
                "Nom de l'établissement",
                "Type de l'établissement",
                "Adresse de l'établissement",
                "Ville",
                "Téléphone contact",
                "Nom et prénom contact",
                "Adresse mail contact",
                "IP validées"
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementAdminDto item) throws IOException {
        List<String> output = new ArrayList<>();
        output.add(item.getIdAbes());
        output.add(item.getSiren());
        output.add(item.getNom());
        output.add(item.getTypeEtablissement());
        output.add(item.getAdresse());
        output.add(item.getVille());
        output.add(item.getTelephone());
        output.add(item.getNomPrenomContact());
        output.add(item.getMailContact());
        for (String ip : item.getIps()) {
            output.add(ip);
        }
        printer.printRecord(output);
    }

    @Override
    protected List<ExportEtablissementAdminDto> getItems(List<String> ids) {
        return mapper.mapList(dao.findAllBySirenIn(ids), ExportEtablissementAdminDto.class);
    }
}
