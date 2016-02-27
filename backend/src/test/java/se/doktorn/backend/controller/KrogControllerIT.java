package se.doktorn.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import se.doktorn.backend.KrogRoulettenApplication;
import se.doktorn.backend.controller.repository.entity.Krog;
import se.doktorn.backend.controller.repository.KrogRepository;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        Krog krog = Krog.builder().namn("NAMN").build();

        given().contentType(ContentType.JSON).body(krog).when().post(KrogController.SAVE_URL).
                then().statusCode(HttpStatus.CREATED.value());

        List<Krog> krogList = repository.findAll();

        assertNotNull(krogList);
        assertEquals(1, krogList.size());
        assertEquals(krog.getNamn(), krogList.get(0).getNamn());
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
