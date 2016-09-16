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
import se.doktorn.backend.controller.csv.CsvManager;
import se.doktorn.backend.controller.domain.Search;
import se.doktorn.backend.controller.repository.KrogRepository;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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


}
