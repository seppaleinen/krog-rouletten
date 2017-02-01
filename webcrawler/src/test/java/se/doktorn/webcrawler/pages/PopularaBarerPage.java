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
    private static final String ADDRESS_REPLACER = "//li[@id='%s']/div/div/span/*";

    public PopularaBarerPage(WebDriver driver) {
        this.driver = driver;
        this.barList = new ArrayList<>();
    }

    public List<Bar> getBasicInfo() {
        System.out.println("RESULT: " + getIfExists(driver, NEXT_BUTTON).getAttribute("href"));

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

                Bar bar = Bar.builder().
                        name(name == null ? null : name.getText()).
                        adress(address == null ? null : address.getText()).
                        build();

                System.out.println("BAR: " + bar);

                barList.add(bar);
            }
        }
    }

}
