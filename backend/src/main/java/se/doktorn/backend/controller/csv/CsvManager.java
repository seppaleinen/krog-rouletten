package se.doktorn.backend.controller.csv;

import lombok.extern.java.Log;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import se.doktorn.backend.repository.entity.Krog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Log
public class CsvManager {
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
        final String iframeStart = "iframe src=\"";
        final String iframeEnd   = "\" width=";

        if(iframeLank != null && iframeLank.contains(iframeStart)) {
            iframeLank = iframeLank.split(iframeStart)[1];
            iframeLank = iframeLank.split(iframeEnd)[0];
        }

        return iframeLank;
    }

    public Point getPointFromIframeLink(String iframeLink) {
        //Ignore all until !2d and get longitude between !2d to !3d and latitude between !3d to !2m
        if(iframeLink != null) {
            final String latLngRegex = ".*!2d([0-9\\.]+)!3d([0-9\\.]+)!2m.*";
            final Pattern latLngPattern = Pattern.compile(latLngRegex);
            final Matcher latLngMatcher = latLngPattern.matcher(iframeLink);

            if (latLngMatcher.matches()) {
                final String longitude = latLngMatcher.group(1);
                final String latitude = latLngMatcher.group(2);

                return new Point(Double.valueOf(longitude), Double.valueOf(latitude));
            }
        }

        return null;
    }
}
