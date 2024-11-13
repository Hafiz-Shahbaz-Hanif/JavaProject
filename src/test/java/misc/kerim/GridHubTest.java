package misc.kerim;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.DC.db.hub.adc.token.Auth0DbFunctions;


public class GridHubTest {

    private static ThreadLocal<WebDriver> driverFactory = new ThreadLocal<>();

    //WebDriver driver;

    @Test
    public void chromeTest1() throws MalformedURLException, InterruptedException, SQLException {
        //DesiredCapabilities dc = new DesiredCapabilities();
        //dc.setBrowserName(BrowserType.CHROME);

        //URL url = new URL("http://34.204.13.251:4444/");

        //WebDriver driver = new RemoteWebDriver(url, options);

        URL url = new URL("http://34.204.13.251:4444/");
        ChromeOptions options = new ChromeOptions();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", Browser.CHROME);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        options.merge(capabilities);

        WebDriver driver = WebDriverManager.chromedriver().capabilities(options).remoteAddress(url).create();

        driverFactory.set(driver);
        driver.manage().window().maximize();

        driver.get("https://www.etsy.com");
        Thread.sleep(4000);
        setText(driver, "//input[@id='global-enhancements-search-query']", "craftcordterra");
        click(driver, "//span[text()='find shop names containing ']/../../../..");
        Thread.sleep(4000);
        click(driver, "//p[text()='CraftCordTerra']/../../../../..");

        driver.get("https://www.etsy.com/");

        System.out.println(driver.getTitle());
        Thread.sleep(9000);


        //        Auth0DbFunctions authDb = new Auth0DbFunctions();
        //        String auth0IdString = authDb.getAuth0Id("test_internal@flywheeldigital.com");
        //
        //        Assert.assertNotNull(auth0IdString, "** Auth0 id is null in Fila DB for the user");
        //        Assert.assertTrue(auth0IdString.contains("auth0"), "** Auth0 id for the user is available in Fila DB but does not start with auth0 prefix");

        driver.quit();
    }

    @Test
    public void chromeTest2() throws MalformedURLException, InterruptedException {

        /*DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName(BrowserType.FIREFOX);

        URL url = new URL("http://34.204.13.251:4444/");

        WebDriver driver = new RemoteWebDriver(url, dc);*/

        URL url = new URL("http://34.204.13.251:4444/");
        FirefoxOptions options = new FirefoxOptions();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", Browser.FIREFOX);
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        options.merge(capabilities);

        WebDriver driver = WebDriverManager.firefoxdriver().capabilities(options).remoteAddress(url).create();

        driverFactory.set(driver);

        driver.get("https://www.etsy.com");
        Thread.sleep(4000);
        setText(driver, "//input[@id='global-enhancements-search-query']", "craftcordterra");
        click(driver, "//span[text()='find shop names containing ']/../../../..");
        Thread.sleep(4000);
        click(driver, "//p[text()='CraftCordTerra']/../../../../..");

        driver.get("https://www.etsy.com/");

        System.out.println(driver.getTitle());
        Thread.sleep(4000);

    }

    @Test
    public void chromeTest3() throws MalformedURLException, InterruptedException {

        /*DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName(BrowserType.CHROME);

        URL url = new URL("http://34.204.13.251:4444/");

        WebDriver driver = new RemoteWebDriver(url, dc);*/

        URL url = new URL("http://34.204.13.251:4444/");
        ChromeOptions options = new ChromeOptions();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", Browser.CHROME);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        options.merge(capabilities);

        WebDriver driver = WebDriverManager.chromedriver().capabilities(options).remoteAddress(url).create();

        driverFactory.set(driver);

        driver.get("https://www.etsy.com");
        Thread.sleep(4000);
        setText(driver, "//input[@id='global-enhancements-search-query']", "craftcordterra");
        click(driver, "//span[text()='find shop names containing ']/../../../..");
        Thread.sleep(4000);
        click(driver, "//p[text()='CraftCordTerra']/../../../../..");

        driver.get("https://www.etsy.com/");

        System.out.println(driver.getTitle());
        Thread.sleep(9000);

    }

    @Test
    public void chromeTest4() throws MalformedURLException, InterruptedException {

       /* DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName(BrowserType.FIREFOX);

        URL url = new URL("http://34.204.13.251:4444/");

        WebDriver driver = new RemoteWebDriver(url, dc);*/

        URL url = new URL("http://34.204.13.251:4444/");
        FirefoxOptions options = new FirefoxOptions();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", Browser.FIREFOX);
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
        options.merge(capabilities);

        WebDriver driver = WebDriverManager.firefoxdriver().capabilities(options).remoteAddress(url).create();

        driverFactory.set(driver);

        driver.get("https://www.etsy.com");
        Thread.sleep(4000);
        setText(driver, "//input[@id='global-enhancements-search-query']", "craftcordterra");
        click(driver, "//span[text()='find shop names containing ']/../../../..");
        Thread.sleep(4000);
        click(driver, "//p[text()='CraftCordTerra']/../../../../..");

        driver.get("https://www.etsy.com/");

        System.out.println(driver.getTitle());
        Thread.sleep(4000);

    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Test closing driver");
        getDriver().quit();
    }

    public void click(WebDriver driver, String xpath) {
        try {
            WebDriverWait wait = getWait(driver);
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            el.click();
        } catch (Exception e) {
            throw e;
        }
    }

    public WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void setText(WebDriver driver, String xpath, String text) {
        try {
            WebDriverWait wait = getWait(driver);
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            el.clear();
            el.sendKeys(text);
        } catch (Exception e) {
            throw e;
        }
    }

    private WebDriver getDriver() {
        return driverFactory.get();
    }

}