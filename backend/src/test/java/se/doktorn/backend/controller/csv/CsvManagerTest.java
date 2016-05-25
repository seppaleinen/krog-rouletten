package se.doktorn.backend.controller.csv;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.fail;

public class CsvManagerTest {
    @InjectMocks
    private CsvManager csvManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParseKrog() {
        validate("namn,adress,oppet_tider,bar_typ,stadsdel,beskrivning,betyg,hemside_lank,intrade,iframe_lank");
        validate("Namn,Adress,,,Stadsdel,Beskrivning,Betyg,,Inträde,");
        validate("adasd,klj,,,lkj,lkjlk,j,,kj,");
        validate("new,adress,,,asd,ads,adssd,,asd,");
        validate("asd,lkj,,,lkj,lkj,lkj,,lkj,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773");
        validate("DAVID,asd,,,asd,asd,asd,,ads,https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773");
    }

    private void validate(String string) {
        try {
            csvManager.parseKrog(string);
        } catch (Exception e) {
            fail("Should not fail: " + e.toString());
        }
    }
}
