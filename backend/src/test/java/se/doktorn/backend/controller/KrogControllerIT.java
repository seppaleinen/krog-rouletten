package se.doktorn.backend.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import se.doktorn.backend.KrogRoulettenApplication;
import se.doktorn.backend.repository.entity.Krog;
import se.doktorn.backend.repository.KrogRepository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KrogRoulettenApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "classpath:application-test.properties")
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
    public void canSave_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().get(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canSave() {
        Krog krog = Krog.builder()
                .namn("NAMN")
                .adress("ADRESS")
                .iframe_lank("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>")
                .build();

        given().contentType(ContentType.JSON).body(krog).when().post(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.CREATED.value());

        List<Krog> krogList = repository.findAll();

        assertNotNull(krogList);
        assertEquals(1, krogList.size());
        assertEquals(krog.getNamn(), krogList.get(0).getNamn());
        assertNotNull(krogList.get(0).getLocation());
    }

    @Test
    public void canUpdate_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().get(KrogController.UPDATE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.UPDATE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.UPDATE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.UPDATE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canUpdate() {
        Krog krog = Krog.builder()
                .namn("NAMN")
                .adress("ADRESS")
                .iframe_lank("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>")
                .build();

        given().contentType(ContentType.JSON).body(krog).when().post(KrogController.UPDATE_URL).
                then().statusCode(HttpStatus.OK.value());

        List<Krog> krogList = repository.findAll();

        assertNotNull(krogList);
        assertEquals(1, krogList.size());
        assertEquals(krog.getNamn(), krogList.get(0).getNamn());
        assertNotNull(krogList.get(0).getLocation());
    }

    @Test
    public void canDelete_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().get(KrogController.DELETE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.DELETE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.DELETE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().post(KrogController.DELETE_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canDelete() {
        Krog krog = Krog.builder()
                .id("ID")
                .namn("NAMN")
                .adress("ADRESS")
                .iframe_lank("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>")
                .build();

        repository.save(krog);

        given().contentType(ContentType.JSON).body(krog).when().delete(KrogController.DELETE_URL).
                then().statusCode(HttpStatus.OK.value());

        List<Krog> krogList = repository.findAll();

        assertNotNull(krogList);
        assertTrue(krogList.isEmpty());
    }

    @Test
    public void canFindAllUnapproved_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.FIND_ALL_UNAPPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.FIND_ALL_UNAPPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.FIND_ALL_UNAPPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().post(KrogController.FIND_ALL_UNAPPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canFindAllUnapproved() {
        Krog approved = Krog.builder()
                .id("APPROVED")
                .namn("NAMN")
                .adress("ADRESS")
                .approved(true)
                .iframe_lank("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>")
                .build();

        Krog unapproved = Krog.builder()
                .id("UNAPPROVED")
                .namn("NAMN")
                .adress("ADRESS")
                .approved(false)
                .iframe_lank("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>")
                .build();
        repository.save(Arrays.asList(approved, unapproved));

        Response result = given().contentType(ContentType.JSON).when().get(KrogController.FIND_ALL_UNAPPROVED_URL);

        assertNotNull(result);
        Krog[] list = result.getBody().as(Krog[].class);
        assertEquals(1, list.length);
        assertFalse(list[0].isApproved());
    }

    @Test
    public void canFind_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.FIND_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.FIND_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.FIND_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().post(KrogController.FIND_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canFindOne() {
        Krog krog = Krog.builder().
                id("ID").
                namn("NAMN").
                build();
        repository.save(krog);

        Krog result = given().param("id", krog.getId()).
                when().get(KrogController.FIND_URL).
                getBody().as(Krog.class);

        assertNotNull(result);
        assertEquals(krog.getNamn(), result.getNamn());
    }

    @Test
    public void canFindAllApproved_NotAcceptableRequests() {
        given().contentType(ContentType.JSON).body(new Krog()).when().delete(KrogController.FIND_ALL_APPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().put(KrogController.FIND_ALL_APPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().patch(KrogController.FIND_ALL_APPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        given().contentType(ContentType.JSON).body(new Krog()).when().post(KrogController.FIND_ALL_APPROVED_URL).
                then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    public void canFindAllApproved() {
        Krog krog1 = Krog.builder().namn("NAMN1").approved(true).build();
        Krog krog2 = Krog.builder().namn("NAMN2").approved(false).build();

        repository.save(Arrays.asList(krog1, krog2));

        Krog[] krogList = when().get(KrogController.FIND_ALL_APPROVED_URL).getBody().as(Krog[].class);
        assertNotNull(krogList);
        assertEquals(1, krogList.length);

        assertEquals(krog1.getNamn(), krogList[0].getNamn());
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
