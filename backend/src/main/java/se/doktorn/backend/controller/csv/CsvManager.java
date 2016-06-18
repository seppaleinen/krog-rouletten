package se.doktorn.backend.controller.csv;

import lombok.extern.java.Log;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        log.info("Parsing line: " + string);
        String[] split = string.split(",", -1);

        if(split.length != 12) {
            throw new Exception("Not valid csv-file");
        }

        String id = split[0].replaceAll("\"", "");
        id = id.isEmpty() ? null : id;
        String namn = split[1].replaceAll("\"", "");
        String adress = split[2].replaceAll("\"", "");
        String oppetTider = split[3].replaceAll("\"", "");
        String barTyp = split[4].replaceAll("\"", "");
        String stadsdel = split[5].replaceAll("\"", "");
        String beskrivning = split[6].replaceAll("\"", "");
        String betyg = split[7].replaceAll("\"", "");
        String hemsideLank = split[8].replaceAll("\"", "");
        String intrade = split[9].replaceAll("\"", "");
        String iframeLank = parseIframeLink(split[10]).replaceAll("\"", "");
        String approvedString = parseIframeLink(split[11]).replaceAll("\"", "");
        boolean approved = "True".equals(approvedString);
        Point location = getPointFromIframeLink(iframeLank);

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
                location(location).
                approved(approved).
                build();
    }

    public String parseIframeLink(String iframeLank) {
        final String IFRAME_START = "iframe src=\"";
        final String IFRAME_END   = "\" width=";

        if(iframeLank != null) {
            if (iframeLank.contains(IFRAME_START)) {
                iframeLank = iframeLank.split(IFRAME_START)[1];
                iframeLank = iframeLank.split(IFRAME_END)[0];
            }
        }

        return iframeLank;
    }

    public Point getPointFromIframeLink(String iframeLink) {
        //Ignore all until !2d and get longitude between !2d to !3d and latitude between !3d to !2m
        if(iframeLink != null) {
            final String regex = ".*!2d(.*)!3d(.*)!2m.*";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(iframeLink);

            if (matcher.matches()) {
                final String latitude = matcher.group(2);
                final String longitude = matcher.group(1);

                return new Point(Double.valueOf(longitude), Double.valueOf(latitude));
            }
        }

        return null;
    }
}
