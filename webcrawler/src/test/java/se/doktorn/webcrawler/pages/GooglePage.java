package se.doktorn.webcrawler.pages;

import org.openqa.selenium.WebDriver;
import se.doktorn.webcrawler.Bar;

import java.util.List;

public class GooglePage {
    private WebDriver driver;

    public GooglePage(WebDriver driver) {
        this.driver = driver;
    }

    public List<Bar> refineBarList(List<Bar> barList) {
        return barList;
    }
}
