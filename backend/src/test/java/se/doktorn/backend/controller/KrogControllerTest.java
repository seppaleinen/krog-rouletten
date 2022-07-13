package se.doktorn.backend.controller;

import org.apache.http.impl.io.EmptyInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.repository.KrogRepository;
import se.doktorn.backend.repository.entity.Krog;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KrogControllerTest {
    @InjectMocks
    private KrogController krogController;
    @Mock
    private KrogRepository repository;
    @Mock
    private CsvManager csvManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    @DisplayName("Grouped tests for exportCsv method")
    public class exportCsv {
        @Test
        public void test_exportCsv() {
            Krog krog = Krog.builder().
                    id("ID").
                    build();

            when(repository.findAll()).thenReturn(Collections.singletonList(krog));

            List<Krog> result = krogController.exportCsv();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(krog.getId(), result.get(0).getId());

            verifyNoInteractions(csvManager);
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Grouped tests for saveCsv method")
    public class saveCSV {
        @Test
        @DisplayName("Too many headers")
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
        @DisplayName("Test with wrong headers")
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
        @DisplayName("Correct headers should save to repository")
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
                verify(repository, times(1)).saveAll(anyList());
            } catch (FileNotFoundException e) {
                fail("Import file must exist");
            } catch (IOException e) {
                fail("Mock multipart failed: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("Shouldn't fail: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Throw IOException")
        public void test_SaveCSV_ThrowException() {
            String path = KrogControllerTest.class.getClassLoader().getResource("imports/export.csv").getPath();

            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                MultipartFile multipartFile = new MockMultipartFile("file", "NameOfTheFile.csv", "multipart/form-data", fileInputStream);

                doThrow(new IOException()).when(csvManager).parseKrog(anyString());

                ResponseEntity result = krogController.saveCsv(multipartFile);

                assertNotNull(result);
                assertEquals(HttpStatus.OK, result.getStatusCode());
                verify(csvManager, times(1)).parseKrog(anyString());
                verify(repository, times(1)).saveAll(anyList());
            } catch (IOException e) {
                fail("Should not fail on multipartFile: " + e.getMessage());
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Throw Exception when empty inputstream")
        public void test_SaveCSV_EmptyInputStream() {
            try {
                MultipartFile multipartFile = new MockMultipartFile("file", "NameOfTheFile.csv", "multipart/form-data", EmptyInputStream.INSTANCE);

                ResponseEntity result = krogController.saveCsv(multipartFile);
                fail("Should throw exception");
            } catch (IOException e) {
                fail("Should not fail on multipartFile: " + e.getMessage());
            } catch (Exception e) {
                assertTrue(e instanceof IndexOutOfBoundsException);
                assertEquals("Index 0 out of bounds for length 0", e.getMessage());
            }
        }


        @Test
        @DisplayName("╯°□°）╯")
        public void test() {
            assertTrue(true);
        }
    }

}
