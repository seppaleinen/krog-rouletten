package se.doktorn.backend.controller.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.geo.Point;
import se.doktorn.backend.controller.repository.entity.Krog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class CsvManagerTest {
    @InjectMocks
    private CsvManager csvManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    @DisplayName("Grouped tests for parseIframeLink method")
    public class parseIframeLink {
        @Test
        @DisplayName("Parse iframeLink without Iframe")
        public void testParseIframeLankWithoutIframe() {
            String iframeLank = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773";
            String result = csvManager.parseIframeLink(iframeLank);

            assertEquals(iframeLank, result);
        }

        @Test
        @DisplayName("Parse iframeLink with Iframe")
        public void testParseIframeLankWithIframe() {
            String iframeLank = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";
            String expectedLank = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2037.2153472197385!2d18.06997371607094!3d59.295960581647186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77898affb1ab%3A0xe6fc2f09c0721c6!2sKonstgjutarv%C3%A4gen+57%2C+121+44+Johanneshov!5e0!3m2!1ssv!2sse!4v1464789144183";

            String result = csvManager.parseIframeLink(iframeLank);

            assertEquals(expectedLank, result);
        }
    }

    @Nested
    @DisplayName("Grouped tests for getPointFromIframeLink method")
    public class getPointFromIframeLink {
        @Test
        @DisplayName("Parse point from iframe")
        public void test_getPointFromIframeLink() {
            String link = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101877!2d18.072471616071663!3d59.31445928165361!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1465135903719\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";

            Point result = csvManager.getPointFromIframeLink(link);

            assertNotNull(result);
            assertEquals(Double.valueOf("59.31445928165361"), Double.valueOf(result.getY()));
            assertEquals(Double.valueOf("18.072471616071663"), Double.valueOf(result.getX()));
        }

        @Test
        @DisplayName("Expect null if iframelink is null")
        public void test_getPointFromIframeLink_null() {
            Point result = csvManager.getPointFromIframeLink(null);

            assertNull(result);
        }

        @Test
        @DisplayName("Parse non number point from iframe")
        public void test_getPointFromIframeLink_nonNumber() {
            String link = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101877!2dasd!3ddsa!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1465135903719\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";

            try {
                Point result = csvManager.getPointFromIframeLink(link);

                assertNull(result);
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }

        }

        @Test
        @DisplayName("iframe must contain point")
        public void test_getPointFromIframeLink_NoGeoLocation() {
            String link = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101877!2d!3d!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1465135903719\" width=\"600\" height=\"450\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";

            try {
                Point result = csvManager.getPointFromIframeLink(link);

                assertNull(result);
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }

        }
    }


    @Nested
    @DisplayName("Grouped tests for getPointFromIframeLink method")
    public class parseKrog {

        @Test
        @DisplayName("Parse list of inputs")
        public void testParseKrog() {
            List<String> inputList = new ArrayList<>();
            inputList.add("id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank,false");
            inputList.add("id,Namn,Adress,,,Stadsdel,Beskrivning,Betyg,,Inträde,,false");
            inputList.add("id,adasd,klj,,,lkj,lkjlk,j,,kj,,false");
            inputList.add("id,new,adress,,,asd,ads,adssd,,asd,,true");
            inputList.add("id,asd,lkj,,,lkj,lkj,lkj,,lkj,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773,false");
            inputList.add("id,DAVID,asd,,,asd,asd,asd,,ads,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773,true");

            for(String input: inputList) {
                try {
                    Krog result = csvManager.parseKrog(input);

                    assertAll("krog",
                            () -> assertNotNull(result),
                            () -> assertNotNull(StringUtils.isNotBlank(result.getId())),
                            () -> assertNotNull(StringUtils.isNotBlank(result.getNamn())),
                            () -> assertNotNull(StringUtils.isNotBlank(result.getAdress()))
                    );
                } catch (Exception e) {
                    fail("Should not fail with: " + e.getMessage());
                }
            }
        }

        @Test
        @DisplayName("Parse list from file")
        public void testParseKrogFromFile() {
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

        @Test
        @DisplayName("Test too many fields")
        public void testTooManyFields() {
            String input = "id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank,false,korv";

            try {
                csvManager.parseKrog(input);
                fail("Should fail on validation");
            } catch (Exception e) {
                assertEquals("Not valid csv-file", e.getMessage());
            }
        }

        @Test
        @DisplayName("Test too few fields")
        public void testTooFewFields() {
            String input = "id,namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank";

            try {
                csvManager.parseKrog(input);
                fail("Should fail on validation");
            } catch (Exception e) {
                assertEquals("Not valid csv-file", e.getMessage());
            }
        }
    }
}
