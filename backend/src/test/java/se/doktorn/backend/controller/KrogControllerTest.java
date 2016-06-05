package se.doktorn.backend.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Point;
import se.doktorn.backend.controller.repository.KrogRepository;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
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

            when(repository.count()).thenReturn(Long.valueOf(i));
            when(repository.findAll()).thenReturn(krogList);

            try {
                krogController.findRandom();
            } catch(Exception e) {
                fail("Should not fail: " + e.toString());
            }
        }
    }


}
