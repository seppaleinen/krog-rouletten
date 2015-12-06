package se.doktorn.backend;

import com.jayway.restassured.RestAssured;
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

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KrogRoulettenApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(locations = "classpath:application-test.properties")
public class ControllerTest {
    @Autowired
    private KrogRouletteRepository repository;
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setup() {
        repository.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void canSave() {
        String  result = given().param("param", "asd").
                when().get("/save").
                getBody().asString();

        assertNotNull(result);
        System.out.println(result);
        KrogRouletteEntity krogRouletteEntity = repository.findOne(result);

        assertNotNull(krogRouletteEntity);
        assertEquals("asd", krogRouletteEntity.getParam());
    }
    
    @Test
    public void canFind() {
        KrogRouletteEntity krogRouletteEntity = new KrogRouletteEntity();
        krogRouletteEntity.setId("ID");
        krogRouletteEntity.setParam("PARAM");
        repository.save(krogRouletteEntity);

        String result = given().param("id", "ID").
                when().get("/find").
                getBody().asString();

        assertNotNull(result);
        assertEquals("PARAM", result);
    }

}
