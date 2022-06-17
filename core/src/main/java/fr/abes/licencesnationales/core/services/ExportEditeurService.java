package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public abstract class ExportEditeurService<T> {
    protected abstract void writeHeader(CSVPrinter printer) throws IOException;

    protected abstract void writeLine(CSVPrinter printer, T item) throws IOException;

    protected abstract List<T> getItems(List<EtablissementEntity> ids);

    public ByteArrayInputStream generateCsv(List<EtablissementEntity> ids) {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream();
             final CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\r\n"))) {
            List<T> query = this.getItems(ids);
            if (!query.isEmpty()) {
                this.writeHeader(printer);
                ListIterator<T> liste = query.listIterator();
                while (liste.hasNext()) {
                    this.writeLine(printer, liste.next());
                }
            }
            printer.flush();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException("Csv writing error: " + e.getMessage());
        }
    }

    protected List<String> writeCommonLine(String idEtablissement, String nomEtablissement, String typeEtablissement, String adresse, String boitePostale, String codePostal, String cedex, String ville, String nomContact, String mailContact, String telephoneContact) {
        List<String> output = new ArrayList<>();
        output.add(idEtablissement);
        output.add(nomEtablissement);
        output.add(typeEtablissement);
        output.add(adresse);
        output.add(boitePostale);
        output.add(codePostal);
        output.add(cedex);
        output.add(ville);
        output.add(nomContact);
        output.add(mailContact);
        output.add(telephoneContact);
        return output;
    }
}
