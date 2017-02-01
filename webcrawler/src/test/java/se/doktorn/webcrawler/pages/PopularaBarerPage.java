package se.doktorn.webcrawler.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import se.doktorn.webcrawler.Bar;
import se.doktorn.webcrawler.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class PopularaBarerPage extends Helper {
    private WebDriver driver;
    private List<Bar> barList;

    private static final By NEXT_BUTTON = By.linkText("Visa fler resultat »");

    private static final String VENUE_ID_REPLACER = "venue_%s";
    private static final String NAME_REPLACER = "//li[@id='%s']/div/div/h3/a";
    private static final String BETYG_REPLACER = "//li[@id='%s']/div/div/h3/span/span";
    private static final String ADDRESS_REPLACER = "//li[@id='%s']/div/div/span";
    private static final String ADDRESS_REPLACER2 = "//li[@id='%s']/div/div/span/*";

    public PopularaBarerPage(WebDriver driver) {
        this.driver = driver;
        this.barList = new ArrayList<>();
    }

    public List<Bar> getBasicInfo() {
        System.out.println("NEXTPAGE: " + getIfExists(driver, NEXT_BUTTON).getAttribute("href"));

        loopRows();

        return barList;
    }

    private void loopRows() {
        for(int i = 0; i < 10; i++) {
            WebElement row = getIfExists(driver, By.id(String.format(VENUE_ID_REPLACER, String.valueOf(i))));

            if(row != null) {
                String id = String.format(VENUE_ID_REPLACER, String.valueOf(i));
                WebElement name = getIfExists(driver, By.xpath(String.format(NAME_REPLACER, id)));
                WebElement address = getIfExists(driver, By.xpath(String.format(ADDRESS_REPLACER, id)));
                WebElement betyg = getIfExists(driver, By.xpath(String.format(BETYG_REPLACER, id)));

                Bar bar = Bar.builder().
                        name(name == null ? null : name.getText()).
                        adress(trimAddress(address, id)).
                        betyg(betyg == null ? null : betyg.getText()).
                        build();

                System.out.println("Parsed: " + bar);

                barList.add(bar);
            }
        }
    }

    private String trimAddress(WebElement webElement, String id) {
        if(webElement == null) {
            return null;
        } else {
            List<WebElement> subElements = driver.findElements(By.xpath(String.format(ADDRESS_REPLACER2, id)));

            String text = webElement.getText();
            for(WebElement element: subElements) {
                text = text.replaceAll(element.getText() + "\n*", "");
            }
            return text;
        }
    }

}
