package se.doktorn.backend.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.domain.Search;
import se.doktorn.backend.repository.entity.Krog;
import se.doktorn.backend.repository.KrogRepository;

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
    static final String SAVE_URL = "/save";
    static final String UPDATE_URL = "/update";
    static final String DELETE_URL = "/delete/krog";
    static final String FIND_URL = "/find";
    static final String FIND_ALL_APPROVED_URL = "/find/all/approved";
    static final String FIND_ALL_UNAPPROVED_URL = "/find/all/unapproved";
    static final String SAVE_CSV_URL = "/save/csv";
    static final String EXPORT_CSV_URL = "/export/csv";
    @Autowired
    private KrogRepository krogRepository;
    @Autowired
    private CsvManager csvManager;

    @RequestMapping(value = SAVE_URL, method = RequestMethod.POST)
    ResponseEntity save(@RequestBody Krog krog) {
        log.log(Level.INFO, "Saving: " + krog);
        krog.setLocation(csvManager.getPointFromIframeLink(krog.getIframe_lank()));
        krog.setIframe_lank(csvManager.parseIframeLink(krog.getIframe_lank()));
        krogRepository.save(krog);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = UPDATE_URL, method = RequestMethod.POST)
    ResponseEntity update(@RequestBody Krog krog) {
        log.log(Level.INFO, "Updating: " + krog);
        krog.setLocation(csvManager.getPointFromIframeLink(krog.getIframe_lank()));
        krog.setIframe_lank(csvManager.parseIframeLink(krog.getIframe_lank()));
        krogRepository.save(krog);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = DELETE_URL, method = RequestMethod.DELETE)
    ResponseEntity delete(@RequestBody Krog krog) {
        log.log(Level.INFO, "Deleting: " + krog.toString());
        krogRepository.delete(krog);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = FIND_URL, method = RequestMethod.GET)
    Krog find(@RequestParam String id) {
        log.log(Level.INFO, "Finding id: " + id);
        return krogRepository.findOne(id);
    }

    @RequestMapping(value = FIND_ALL_APPROVED_URL, method = RequestMethod.GET)
    List<Krog> findAllApproved() {
        log.log(Level.INFO, "Finding all");
        return krogRepository.findByApprovedIsTrueOrderByNamnAsc();
    }

    @RequestMapping(value = FIND_ALL_UNAPPROVED_URL, method = RequestMethod.GET)
    List<Krog> findAllUnapproved() {
        log.log(Level.INFO, "Finding all");
        return krogRepository.findByApprovedIsFalseOrApprovedNullOrderByNamnAsc();
    }

    @RequestMapping(value = EXPORT_CSV_URL, method = RequestMethod.GET)
    List<Krog> exportCsv() {
        return krogRepository.findAll();
    }

    @RequestMapping(value = SAVE_CSV_URL, method = RequestMethod.POST)
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
                //namn;adress;oppet_tider;bar_typ;stadsdel;beskrivning;betyg;hemside_lank;intrade;iframe_lank;
                krogList.add(csvManager.parseKrog(line));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Import failed: " + e.getMessage());
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
