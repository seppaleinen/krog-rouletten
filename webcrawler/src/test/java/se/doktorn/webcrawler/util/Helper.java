package se.doktorn.webcrawler.util;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Helper {
    public WebElement getIfExists(SearchContext searchContext, By by) {
        List<WebElement> list = searchContext.findElements(by);
        return list.isEmpty() ? null : list.get(0);
    }
}
