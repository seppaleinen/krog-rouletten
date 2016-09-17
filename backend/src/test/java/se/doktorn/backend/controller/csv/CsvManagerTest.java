package se.doktorn.backend.controller.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Point;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class CsvManagerTest {
    @InjectMocks
    private CsvManager csvManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParseKrog() {
        validate("id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank,false");
        validate("id,Namn,Adress,,,Stadsdel,Beskrivning,Betyg,,Inträde,,false");
        validate("id,adasd,klj,,,lkj,lkjlk,j,,kj,,false");
        validate("id,new,adress,,,asd,ads,adssd,,asd,,true");
        validate("id,asd,lkj,,,lkj,lkj,lkj,,lkj,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773,false");
        validate("id,DAVID,asd,,,asd,asd,asd,,ads,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773,true");
    }

    @Test
    public void testParseIframeLankWithoutIframe() {
        String iframeLank = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773";
        String result = csvManager.parseIframeLink(iframeLank);

        assertEquals(iframeLank, result);
    }

    @Test
    public void testParseIframeLankWithIframe() {
        String iframeLank = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";
        String expectedLank = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183";

        String result = csvManager.parseIframeLink(iframeLank);

        assertEquals(expectedLank, result);
    }

    @Test
    public void test_getPointFromIframeLink() {
        String link = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101877!2d18.072471616071663!3d59.31445928165361!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1465135903719\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";

        Point result = csvManager.getPointFromIframeLink(link);

        assertNotNull(result);
        assertEquals(Double.valueOf("59.31445928165361"), Double.valueOf(result.getY()));
        assertEquals(Double.valueOf("18.072471616071663"), Double.valueOf(result.getX()));
    }

    @Test
    public void test_getPointFromIframeLink_null() {
        String link = null;

        Point result = csvManager.getPointFromIframeLink(link);

        assertNull(result);
    }

    @Test
    public void test() {
        InputStream resource = CsvManagerTest.class.getClassLoader().getResourceAsStream("KR.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(resource));

        for(String line : br.lines().collect(Collectors.toList())) {
            try {
                Krog result = csvManager.parseKrog(line);
                assertNotNull(result);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }



    @Nested
    @DisplayName("New junit 5 tests")
    public class JunitFiveTests {
        @Test
        @DisplayName("Grouping assertions")
        void groupedAssertions() {
            Krog krog = Krog.builder().
                    id("1").
                    namn("test").
                    approved(true).
                    build();

            assertAll("address",
                    () -> assertEquals("test", krog.getNamn()),
                    () -> assertEquals("1", krog.getId()),
                    () -> assertEquals(true, krog.isApproved())
            );
        }
    }


    private void validate(String string) {
        try {
            csvManager.parseKrog(string);
        } catch (Exception e) {
            fail("Should not fail: " + e.toString());
        }
    }
}
