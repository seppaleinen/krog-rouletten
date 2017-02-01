package se.doktorn.webcrawler;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import se.doktorn.webcrawler.pages.GooglePage;
import se.doktorn.webcrawler.pages.PopularaBarerPage;
import se.doktorn.webcrawler.util.CsvExporter;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SeleniumTest {
    private WebDriver driver;
    private static final String URL = "https://popularabarer.se/Stockholm/?filter=&distance=9999";

    @Before
    public void setup() {
        String binary = System.getProperty("phantomjs.binary");
        assertNotNull("phantomjs.binary property must not be null", binary);
        assertTrue("Binary file must exist: " + binary, new File(binary).exists());

        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
                "--web-security=no",
                "--ignore-ssl-errors=yes",
                "--debug=no"
        });

        driver = new PhantomJSDriver(capabilities);
    }

    @Test
    public void test() {
        driver.get(URL);

        PopularaBarerPage popularaBarerPage = new PopularaBarerPage(driver);
        GooglePage googlePage = new GooglePage(driver);

        List<Bar> barList = popularaBarerPage.getBasicInfo();
        List<Bar> refinedBarList = googlePage.refineBarList(barList);

        String path = ClassLoader.getSystemResource(".").getPath();
        CsvExporter.exportListToPath(
                CsvExporter.transformToCSVFormat(refinedBarList), new File(path, "bar.csv")
        );
    }

}
