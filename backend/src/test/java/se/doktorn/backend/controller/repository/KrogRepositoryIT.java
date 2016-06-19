package se.doktorn.backend.controller.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.doktorn.backend.KrogRoulettenApplication;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KrogRoulettenApplication.class)
@IntegrationTest("server.port:0")
@TestPropertySource(locations = "classpath:application-test.properties")
public class KrogRepositoryIT {
    @Autowired
    private KrogRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void test_FindOnlyKrog_ByRandom_IfApproved() {
        Point point = new Point(1, 1);
        Krog approved = Krog.builder().
                id("1").
                approved(true).
                location(point).
                build();
        Krog unapproved = Krog.builder().
                id("2").
                approved(false).
                location(point).
                build();

        repository.save(approved);
        repository.save(unapproved);

        List<Krog> result = repository.findByLocationNearAndApprovedIsTrue(point, new Distance(10, Metrics.KILOMETERS));

        assertNotNull("Result should not be null", result);
        assertEquals("There should only be one result", 1, result.size());
        assertEquals("Id should be 1", "1", result.get(0).getId());
    }

    @Test
    public void test_FindOnlyKrog_IfApproved() {
        Point point = new Point(1, 1);
        Krog approved = Krog.builder().
                id("1").
                approved(true).
                location(point).
                build();
        Krog unapproved = Krog.builder().
                id("2").
                approved(false).
                location(point).
                build();

        repository.save(approved);
        repository.save(unapproved);

        List<Krog> result = repository.findByApprovedIsTrueOrderByNamnAsc();

        assertNotNull("Result should not be null", result);
        assertEquals("There should only be one result", 1, result.size());
        assertEquals("Id should be 1", approved.getId(), result.get(0).getId());
    }

    @Test
    public void test_FindOnlyKrog_IfUnapproved() {
        Point point = new Point(1, 1);
        Krog approved = Krog.builder().
                id("1").
                approved(true).
                location(point).
                build();
        Krog unapproved = Krog.builder().
                id("2").
                approved(false).
                location(point).
                build();
        Krog approvedNull = Krog.builder().
                id("3").
                location(point).
                build();

        repository.save(approved);
        repository.save(unapproved);
        repository.save(approvedNull);

        List<Krog> result = repository.findByApprovedIsFalseOrApprovedNullOrderByNamnAsc();

        assertNotNull("Result should not be null", result);
        assertEquals("There should only be one result", 2, result.size());
    }


}