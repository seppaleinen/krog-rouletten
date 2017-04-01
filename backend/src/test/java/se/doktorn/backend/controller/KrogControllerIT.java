package se.doktorn.backend.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import se.doktorn.backend.KrogRoulettenApplication;
import se.doktorn.backend.repository.entity.Krog;
import se.doktorn.backend.repository.KrogRepository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = KrogRoulettenApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "classpath:application-test.properties"
)
public class KrogControllerIT {
    @Autowired
    private KrogRepository repository;
    @LocalServerPort
    private int randomServerPort;

    @Before
    public void setup() {
        repository.deleteAll();
        RestAssured.port = randomServerPort;
    }

    @Test
    public void canExportCsv_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.EXPORT_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.EXPORT_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.EXPORT_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().post(KrogController.EXPORT_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canExportCsv() {
        Krog krog1 = Krog.builder().namn("NAMN1").approved(true).build();
        Krog krog2 = Krog.builder().namn("NAMN2").approved(false).build();

        repository.save(Arrays.asList(krog1, krog2));

        Krog[] krogList = when().get(KrogController.EXPORT_CSV_URL).getBody().as(Krog[].class);
        assertNotNull(krogList);
        assertEquals(2, krogList.length);

        assertEquals(krog1.getNamn(), krogList[0].getNamn());
        assertEquals(krog2.getNamn(), krogList[1].getNamn());
    }

    @Test
    public void canSaveCsv_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.SAVE_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.SAVE_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.SAVE_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().get(KrogController.SAVE_CSV_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canSaveCsv() {
        String path = KrogControllerIT.class.getClassLoader().getResource("imports/export.csv").getPath();
        File file = new File(path);

        given().multiPart(file).
                when().post(KrogController.SAVE_CSV_URL).
                then().statusCode(HttpStatus.OK.value());

        List<Krog> krogList = repository.findAll();
        assertNotNull(krogList);
        assertEquals(36, krogList.size());
    }
}
