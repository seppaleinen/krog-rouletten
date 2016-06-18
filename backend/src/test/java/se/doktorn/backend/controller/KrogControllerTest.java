package se.doktorn.backend.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import se.doktorn.backend.controller.domain.Search;
import se.doktorn.backend.controller.repository.KrogRepository;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

public class KrogControllerTest {
    @InjectMocks
    private KrogController krogController;
    @Mock
    private KrogRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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


}
