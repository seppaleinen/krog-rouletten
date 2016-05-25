package se.doktorn.backend.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.controller.repository.entity.Krog;
import se.doktorn.backend.controller.repository.KrogRepository;

import javax.validation.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Log
public class KrogController {
    public static final String SAVE_URL = "/save";
    public static final String FIND_URL = "/find";
    public static final String FIND_ALL_URL = "/find/all";
    public static final String FIND_RANDOM_URL = "/find/random";
    public static final String SAVE_CSV_URL = "/save/csv";
    public static final String EXPORT_CSV_URL = "/export/csv";
    @Autowired
    private KrogRepository krogRepository;
    @Autowired
    private CsvManager csvManager;

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

    @RequestMapping(value = FIND_RANDOM_URL, method = RequestMethod.GET)
    public Krog findRandom() {
        log.log(Level.INFO, "Finding random");
        long count = krogRepository.count();

        if(count > 0) {
            Long random = ThreadLocalRandom.current().nextLong(0, count + 1);

            random = random == 0 ? 0L : random - 1;

            return krogRepository.findAll().get((random.intValue()));
        } else {
            return null;
        }
    }

    @RequestMapping(value = EXPORT_CSV_URL, method = RequestMethod.GET)
    public List<Krog> exportCsv() {
        return krogRepository.findAll();
    }

    @RequestMapping(value = SAVE_CSV_URL, method = RequestMethod.POST)
    public ResponseEntity saveCsv(@RequestParam("file") MultipartFile file) throws Exception {
        List<Krog> krogList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

            String firstLineString = br.lines().limit(1).collect(Collectors.toList()).get(0);
            Krog firstLineKrog = csvManager.parseKrog(firstLineString);
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
                krogList.add(csvManager.parseKrog(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        krogRepository.save(krogList);

        return new ResponseEntity(HttpStatus.OK);
    }

    void assertion(boolean bool) throws Exception {
        if(!bool) {
            throw new Exception("Not valid csv-file");
        }
    }

}
