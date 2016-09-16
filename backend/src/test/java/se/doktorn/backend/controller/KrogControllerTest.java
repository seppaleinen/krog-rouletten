package se.doktorn.backend.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.controller.domain.Search;
import se.doktorn.backend.controller.repository.KrogRepository;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class KrogControllerTest {
    @InjectMocks
    private KrogController krogController;
    @Mock
    private KrogRepository repository;
    @Mock
    private CsvManager csvManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_save() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        ResponseEntity result = krogController.save(krog);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        verify(csvManager, times(1)).getPointFromIframeLink(anyString());
        verify(csvManager, times(1)).parseIframeLink(anyString());
        verify(repository, times(1)).save(any(Krog.class));
    }

    @Test
    public void test_update() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        ResponseEntity result = krogController.update(krog);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(csvManager, times(1)).getPointFromIframeLink(anyString());
        verify(csvManager, times(1)).parseIframeLink(anyString());
        verify(repository, times(1)).save(any(Krog.class));
    }

    @Test
    public void test_delete() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        ResponseEntity result = krogController.delete(krog);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verifyZeroInteractions(csvManager);
        verify(repository, times(1)).delete(any(Krog.class));
    }

    @Test
    public void test_find() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        when(repository.findOne(anyString())).thenReturn(krog);

        Krog result = krogController.find("ID");

        assertNotNull(result);
        assertEquals(krog.getId(), result.getId());

        verifyZeroInteractions(csvManager);
        verify(repository, times(1)).findOne("ID");
    }

    @Test
    public void test_findAllApproved() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        when(repository.findByApprovedIsTrueOrderByNamnAsc()).thenReturn(Arrays.asList(krog));

        List<Krog> result = krogController.findAllApproved();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(krog.getId(), result.get(0).getId());

        verifyZeroInteractions(csvManager);
        verify(repository, times(1)).findByApprovedIsTrueOrderByNamnAsc();
    }

    @Test
    public void test_findAllUnapproved() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        when(repository.findByApprovedIsFalseOrApprovedNullOrderByNamnAsc()).thenReturn(Arrays.asList(krog));

        List<Krog> result = krogController.findAllUnapproved();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(krog.getId(), result.get(0).getId());

        verifyZeroInteractions(csvManager);
        verify(repository, times(1)).findByApprovedIsFalseOrApprovedNullOrderByNamnAsc();
    }

    @Test
    public void test_exportCsv() {
        Krog krog = Krog.builder().
                id("ID").
                build();

        when(repository.findAll()).thenReturn(Arrays.asList(krog));

        List<Krog> result = krogController.exportCsv();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(krog.getId(), result.get(0).getId());

        verifyZeroInteractions(csvManager);
        verify(repository, times(1)).findAll();
    }

    @Test
    public void test_FindRandom_ThreadLocalRandom() {
        for(int i = 0; i < 20; i++) {
            List<Krog> krogList = new ArrayList<>();
            for(int x = 0; x < i; x++) {
                krogList.add(new Krog());
            }

            when(repository.findByLocationNearAndApprovedIsTrue(any(Point.class), any(Distance.class))).thenReturn(krogList);

            try {
                Search search = Search.builder()
                        .latitude(123.0)
                        .longitude(123.0)
                        .distance(0.1)
                        .build();
                krogController.findRandom(search);
            } catch(Exception e) {
                fail("Should not fail: " + e.toString());
            }
        }
    }

    @Test
    public void test_SaveCSV_TooManyHeaders() {
        String path = KrogControllerTest.class.getClassLoader().getResource("imports/too_many_headers.csv").getPath();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            MultipartFile multipartFile = new MockMultipartFile("file", "NameOfTheFile.csv", "multipart/form-data", fileInputStream);

            krogController.saveCsv(multipartFile);
            fail("Should fail");
        } catch (FileNotFoundException e) {
            fail("Import file must exist");
        } catch (IOException e) {
            fail("Mock multipart failed: " + e.getMessage());
        } catch (Exception e) {
            assertEquals("Not valid csv-file", e.getMessage());
        }
    }

    @Test
    public void test_SaveCSV_WrongHeaders() {
        String path = KrogControllerTest.class.getClassLoader().getResource("imports/wrong_headers.csv").getPath();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            MultipartFile multipartFile = new MockMultipartFile("file", "NameOfTheFile.csv", "multipart/form-data", fileInputStream);

            krogController.saveCsv(multipartFile);
            fail("Should fail");
        } catch (FileNotFoundException e) {
            fail("Import file must exist");
        } catch (IOException e) {
            fail("Mock multipart failed: " + e.getMessage());
        } catch (Exception e) {
            assertEquals("Not valid csv-file", e.getMessage());
        }
    }

    @Test
    public void test_SaveCSV() {
        String path = KrogControllerTest.class.getClassLoader().getResource("imports/export.csv").getPath();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            MultipartFile multipartFile = new MockMultipartFile("file", "NameOfTheFile.csv", "multipart/form-data", fileInputStream);

            Krog krog = Krog.builder().
                            namn("namn").
                            adress("adress").
                            oppet_tider("oppet_tider").
                            bar_typ("bar_typ").
                            stadsdel("stadsdel").
                            beskrivning("beskrivning").
                            betyg("betyg").
                            hemside_lank("hemside_lank").
                            intrade("intrade").
                            iframe_lank("iframe_lank").
                            build();

            when(csvManager.parseKrog(anyString())).thenReturn(krog);

            ResponseEntity result = krogController.saveCsv(multipartFile);

            assertNotNull(result);
            assertEquals(HttpStatus.OK, result.getStatusCode());

            verify(csvManager, times(37)).parseKrog(anyString());
            verify(repository, times(1)).save(any(List.class));
        } catch (FileNotFoundException e) {
            fail("Import file must exist");
        } catch (IOException e) {
            fail("Mock multipart failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Shouldn't fail: " + e.getMessage());
        }
    }


}
