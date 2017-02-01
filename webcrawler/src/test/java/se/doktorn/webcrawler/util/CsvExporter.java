package se.doktorn.webcrawler.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import se.doktorn.webcrawler.Bar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CsvExporter {
    private static final String HEADER = "id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank,approved";
    private static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.withRecordSeparator("\n");

    public static void exportListToCSV(final List<Bar> barList, final File outputFile) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;

        try {
            fileWriter = new FileWriter(outputFile);
            csvFilePrinter = new CSVPrinter(fileWriter, CSV_FILE_FORMAT);
            csvFilePrinter.printRecord(Arrays.asList(HEADER.split(",")));

            for(Bar bar: barList) {
                csvFilePrinter.printRecord(
                        null, //id
                        bar.getName(), //Name
                        bar.getAdress(), //Adress
                        null, //oppetTider
                        null, //barTyp
                        null, //Stadsdel
                        null, //Beskrivning
                        bar.getBetyg(), //Betyg
                        null, //Hemsida
                        null, //Intrade
                        null, //Iframe
                        "True" //Approved
                );
            }
            System.out.println(String.format("Created %s file!", outputFile.getAbsolutePath()));
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter!!!");
            e.printStackTrace();
        } finally {
            try {
                if(fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
                if(csvFilePrinter != null) {
                    csvFilePrinter.close();
                }
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }

    }
}
