package se.doktorn.backend.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.repository.entity.Krog;
import se.doktorn.backend.repository.KrogRepository;

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
    static final String SAVE_CSV_URL = "/save/csv";
    static final String EXPORT_CSV_URL = "/export/csv";
    @Autowired
    private KrogRepository krogRepository;
    @Autowired
    private CsvManager csvManager;

    @GetMapping(value = EXPORT_CSV_URL)
    List<Krog> exportCsv() {
        return krogRepository.findAll();
    }

    @PostMapping(value = SAVE_CSV_URL)
    ResponseEntity saveCsv(@RequestParam("file") MultipartFile file) throws Exception {
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
                // namn;adress;oppet_tider;bar_typ;stadsdel;beskrivning;betyg;hemside_lank;intrade;iframe_lank
                krogList.add(csvManager.parseKrog(line));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Import failed: " + e.getMessage(), e);
        }

        krogRepository.save(krogList);

        return new ResponseEntity(HttpStatus.OK);
    }

    private void assertion(boolean bool) throws Exception {
        if(!bool) {
            throw new Exception("Not valid csv-file");
        }
    }

}
