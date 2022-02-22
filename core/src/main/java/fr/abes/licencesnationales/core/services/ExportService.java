package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

public abstract class ExportService<T, E> {
    protected abstract void writeHeader(CSVPrinter printer) throws IOException;

    protected abstract void writeLine(CSVPrinter printer, T item) throws IOException;

    protected abstract List<T> getItems(List<E> ids);

    public ByteArrayInputStream generateCsv(List<E> ids) {
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
            throw new RuntimeException(Constant.ERROR_CSV_WRITING + e.getMessage());
        }
    }
}
