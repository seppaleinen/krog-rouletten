package se.doktorn.backend.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.repository.entity.Krog;
import se.doktorn.backend.controller.repository.KrogRepository;

import javax.validation.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Log
public class KrogController {
    public static final String SAVE_URL = "/save";
    public static final String FIND_URL = "/find";
    public static final String FIND_ALL_URL = "/find/all";
    public static final String SAVE_CSV_URL = "/save/csv";
    @Autowired
    private KrogRepository krogRepository;

    @RequestMapping(value = SAVE_URL, method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody Krog krog) {
        log.log(Level.INFO, "Saving: " + krog);
        krogRepository.save(krog);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = FIND_URL, method = RequestMethod.GET)
    public Krog find(@RequestParam String id) {
        log.log(Level.INFO, "Finding id: " + id);
        return krogRepository.findOne(id);
    }

    @RequestMapping(value = FIND_ALL_URL, method = RequestMethod.GET)
    public List<Krog> findAll() {
        log.log(Level.INFO, "Finding all");
        return krogRepository.findAll();
    }

    @RequestMapping(value = SAVE_CSV_URL, method = RequestMethod.POST)
    public ResponseEntity saveCsv(@RequestParam("file") MultipartFile file) throws Exception {
        List<Krog> krogList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

            String firstLineString = br.lines().limit(1).collect(Collectors.toList()).get(0);
            Krog firstLineKrog = parseKrog(firstLineString);
            assertion(firstLineKrog != null);
            assertion("namn".equals(firstLineKrog.getNamn()));
            assertion("adress".equals(firstLineKrog.getAdress()));
            assertion("oppet_tider".equals(firstLineKrog.getOppet_tider()));
            assertion("bar_typ".equals(firstLineKrog.getBar_typ()));
            assertion("stadsdel".equals(firstLineKrog.getStadsdel()));
            assertion("beskrivning".equals(firstLineKrog.getBeskrivning()));
            assertion("betyg".equals(firstLineKrog.getBetyg()));
            assertion("hemside_lank".equals(firstLineKrog.getHemside_lank()));
            assertion("intrade".equals(firstLineKrog.getIntrade()));
            assertion("iframe_lank".equals(firstLineKrog.getIframe_lank()));

            for(String line : br.lines().skip(1).collect(Collectors.toList())) {
                log.log(Level.FINE, "Reading line: " + line);
                //namn;adress;oppet_tider;bar_typ;stadsdel;beskrivning;betyg;hemside_lank;intrade;iframe_lank;
                krogList.add(parseKrog(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        krogRepository.save(krogList);

        return new ResponseEntity(HttpStatus.OK);
    }

    Krog parseKrog(String string) throws Exception {
        String[] split = string.split(";");

        assertion(split.length == 10);

        String namn = split[0].replaceAll("\"", "");
        String adress = split[1].replaceAll("\"", "");
        String oppetTider = split[2].replaceAll("\"", "");
        String barTyp = split[3].replaceAll("\"", "");
        String stadsdel = split[4].replaceAll("\"", "");
        String beskrivning = split[5].replaceAll("\"", "");
        String betyg = split[6].replaceAll("\"", "");
        String hemsideLank = split[7].replaceAll("\"", "");
        String intrade = split[8].replaceAll("\"", "");
        String iframeLank = split[9].replaceAll("\"", "");

        return Krog.builder().
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

    void assertion(boolean bool) throws Exception {
        if(!bool) {
            throw new Exception("Not valid csv-file");
        }
    }

}