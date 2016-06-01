package se.doktorn.backend.controller.csv;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import se.doktorn.backend.controller.repository.entity.Krog;


@Component
@Log
public class CsvManager {
    private static final String NAMN = "namn";
    private static final String ADRESS = "adress";
    private static final String OPPET_TIDER = "oppet_tider";
    private static final String BAR_TYP = "bar_typ";
    private static final String STADSDEL = "stadsdel";
    private static final String BESKRIVNING = "beskrivning";
    private static final String BETYG = "betyg";
    private static final String HEMSIDE_LANK = "hemside_lank";
    private static final String INTRADE = "intrade";
    private static final String IFRAME_LANK = "iframe_lank";

    public Krog parseKrog(String string) throws Exception {
        log.info("LINE: " + string);
        String[] split = string.split(",", -1);

        if(split.length != 11) {
            throw new Exception("Not valid csv-file");
        }

        String id = split[0].replaceAll("\"", "");
        String namn = split[1].replaceAll("\"", "");
        String adress = split[2].replaceAll("\"", "");
        String oppetTider = split[3].replaceAll("\"", "");
        String barTyp = split[4].replaceAll("\"", "");
        String stadsdel = split[5].replaceAll("\"", "");
        String beskrivning = split[6].replaceAll("\"", "");
        String betyg = split[7].replaceAll("\"", "");
        String hemsideLank = split[8].replaceAll("\"", "");
        String intrade = split[9].replaceAll("\"", "");
        String iframeLank = split[10].replaceAll("\"", "");

        return Krog.builder().
                id(id).
                namn(namn).
                adress(adress).
                oppet_tider(oppetTider).
                bar_typ(barTyp).
                stadsdel(stadsdel).
                beskrivning(beskrivning).
                betyg(betyg).
                hemside_lank(hemsideLank).
                intrade(intrade).
                iframe_lank(iframeLank).
                build();
    }
}
