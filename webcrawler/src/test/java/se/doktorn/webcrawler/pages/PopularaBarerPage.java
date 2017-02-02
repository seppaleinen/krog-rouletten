package se.doktorn.webcrawler.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import se.doktorn.webcrawler.Bar;
import se.doktorn.webcrawler.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PopularaBarerPage extends Helper {
    private WebDriver driver;
    private List<Bar> barList;

    private static final By NEXT_BUTTON = By.linkText("Visa fler resultat »");
    private static final By NEXT_BUTTON2 = By.linkText("Nästa »");

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
        WebElement nastaButton = getIfExists(driver, NEXT_BUTTON2);

        loopRows();

        if(nastaButton != null) {
            System.out.println("Redirecting to next page: " + nastaButton.getAttribute("href"));
            driver.get(nastaButton.getAttribute("href"));
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getBasicInfo();
        } else {
            WebElement visaFlerButton = getIfExists(driver, NEXT_BUTTON);

            if(visaFlerButton != null) {
                System.out.println("Redirecting to next page: " + visaFlerButton.getAttribute("href"));
                driver.get(visaFlerButton.getAttribute("href"));
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getBasicInfo();
            } else {
                System.out.println("No next button on page. Aborting.");
            }
        }

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
                text = text.replaceAll(Pattern.quote(element.getText()) + "\n*", "");
            }
            return text;
        }
    }

}
