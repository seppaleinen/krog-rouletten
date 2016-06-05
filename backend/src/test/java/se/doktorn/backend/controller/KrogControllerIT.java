package se.doktorn.backend.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import se.doktorn.backend.KrogRoulettenApplication;
import se.doktorn.backend.controller.repository.entity.Krog;
import se.doktorn.backend.controller.repository.KrogRepository;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KrogRoulettenApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(locations = "classpath:application-test.properties")
public class KrogControllerIT {
    @Autowired
    private KrogRepository repository;
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setup() {
        repository.deleteAll();
        RestAssured.port = port;
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
    public void canFindAll() {
        Krog krog1 = Krog.builder().namn("NAMN1").build();
        Krog krog2 = Krog.builder().namn("NAMN2").build();

        repository.save(Arrays.asList(krog1, krog2));

        Krog[] krogList = when().get(KrogController.FIND_ALL_URL).getBody().as(Krog[].class);
        assertNotNull(krogList);
        assertEquals(2, krogList.length);

        assertEquals(krog1.getNamn(), krogList[0].getNamn());
        assertEquals(krog2.getNamn(), krogList[1].getNamn());
    }

}
