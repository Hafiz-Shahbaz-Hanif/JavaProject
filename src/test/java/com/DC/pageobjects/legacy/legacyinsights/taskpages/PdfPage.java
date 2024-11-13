package com.DC.pageobjects.legacy.legacyinsights.taskpages;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PdfPage extends PageHandler {
    public PdfPage(WebDriver driver) {
        super(driver);
    }

    public boolean verifyPdf() {
        try {
            waitForPDFToOpen();
            String windowHandle = driver.getWindowHandles().stream().filter( x -> !x.equals( driver.getWindowHandle() ) ).findFirst().orElseThrow();
            switchToTab( windowHandle );
            if ( Objects.equals(driver.getTitle(), "Task | OneSpace") ) {
                switchToMainTab();
                return false;
            }
            switchToTab( windowHandle ).close();
            switchToMainTab();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean waitForPDFToOpen() {
        AtomicInteger openedTabs = new AtomicInteger(driver.getWindowHandles().size());
        int expectedTabs = openedTabs.get() + 1;
        WebDriverWait wait = new WebDriverWait( driver, Duration.ofSeconds(30) )   ;
        wait.until((driver) -> {
                openedTabs.set(driver.getWindowHandles().size());
        return openedTabs.get() == expectedTabs;
			} );
        return false;
    }
}
