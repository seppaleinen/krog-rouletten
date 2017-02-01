package se.doktorn.webcrawler.util;

import org.apache.commons.io.FileUtils;
import se.doktorn.webcrawler.Bar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CsvExporter {
    private static final String HEADER = "id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank,approved";

    //@TODO Use real csv framework.
    public static List<String> transformToCSVFormat(List<Bar> barList){
        List<String> csvList = new ArrayList<>(Collections.singletonList(HEADER));

        barList.forEach(bar -> csvList.add(
                String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        "", //id
                        bar.getName(), //Name
                        bar.getAdress(), //Adress
                        "", //oppetTider
                        "", //barTyp
                        "", //Stadsdel
                        "", //Beskrivning
                        "", //Betyg
                        "", //Hemsida
                        "", //Intrade
                        "", //Iframe
                        "True" //Approved
                )
        ));

        return csvList;
    }

    public static void exportListToPath(final List<String> barListCSV, final File outputFile) {
        try {
            FileUtils.writeLines(outputFile, barListCSV);
            System.out.println(String.format("Created %s file!", outputFile.getAbsolutePath()));
        } catch (IOException e) {
            System.out.println("Failed to write to path: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
