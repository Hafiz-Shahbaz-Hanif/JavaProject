package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.time.Duration;

public class InsightsLoginPage extends PageHandler {

    private final By EMAIL_ADDRESS_TEXT_BOX = By.xpath( "//input[@type='text']" );
    private final By PASSWORD_TEXT_BOX = By.xpath( "//input[@type='password']" );
    private final By LOGIN_BUTTON = By.className( "submit" );
    private final By FORGOT_PASSWORD_LINK = By.xpath( "//a[@href='/forgotpassword']" );

    public InsightsLoginPage( WebDriver driver ) {
        super(driver);
        findElementVisible(LOGIN_BUTTON, Duration.ofSeconds(30));
        Assert.assertEquals( "Log In | OneSpace", driver.getTitle() );
    }

    private void insertUsername( String username ) {
        sendKeys( EMAIL_ADDRESS_TEXT_BOX, username );
    }

    private void insertPassword( String password ) {
        sendKeys( PASSWORD_TEXT_BOX, password );
    }

    private void clickLogin() throws InterruptedException {
        click( LOGIN_BUTTON );
    }

    public DashboardPage login(String username, String password ) throws InterruptedException {
        insertUsername( username );
        insertPassword( password );
        clickLogin();
        return new DashboardPage(driver);
    }

    public NavigationMenuLegacy Login(String username, String password, Enums.LandingPage landingPage ) throws Exception {
        insertUsername( username );
        insertPassword( password );
        clickLogin();
        waitForElementToBeInvisible(LOGIN_BUTTON, Duration.ofSeconds(25));
        return LandingPageFactory.Build(landingPage, driver);
    }
}

class LandingPageFactory {
    public static NavigationMenuLegacy Build(Enums.LandingPage landingPage, WebDriver webDriver ) throws Exception {
        switch ( landingPage ) {
            case Dashboard:
                return new DashboardPage( webDriver );
            case InsightsProductsPage:
                return new InsightsProductsPage( webDriver );
            case Tasks:
                return new TasksPage( webDriver );
            case Deployment:
                return new DestinationManagerPage( webDriver );
        }
        throw new Exception( "Invalid Landing Page" );
    }
}
