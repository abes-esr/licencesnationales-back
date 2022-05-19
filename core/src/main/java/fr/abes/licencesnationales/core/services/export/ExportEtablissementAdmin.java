package fr.abes.licencesnationales.core.services.export;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementAdminDto;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.services.ExportService;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripAccents;

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
                "Identifiant Abes",
                "Siren",
                "Nom de l'etablissement",
                "Type de l'etablissement",
                "Adresse de l'etablissement",
                "Ville",
                "Telephone contact",
                "Nom et prenom contact",
                "Adresse mail contact",
                "IP validees"
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementAdminDto item) throws IOException {
        List<String> output = new ArrayList<>();
        output.add(GenererIdAbes.genererIdAbes(item.getIdAbes()));
        output.add(item.getSiren());
        output.add(stripAccents(item.getNom()));
        output.add(stripAccents(item.getTypeEtablissement()));
        output.add(stripAccents(item.getAdresse()));
        output.add(stripAccents(item.getVille()));
        output.add(item.getTelephone());
        output.add(stripAccents(item.getNomPrenomContact()));
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
