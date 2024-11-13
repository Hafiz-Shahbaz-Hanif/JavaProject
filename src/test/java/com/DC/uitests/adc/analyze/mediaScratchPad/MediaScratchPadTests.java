package com.DC.uitests.adc.analyze.mediaScratchPad;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.mediaScratchPad.MediaScratchPadPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.routes.adc.analyze.paidMediaReporting.MediaScratchpadRoutes;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v121.network.Network;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import org.openqa.selenium.devtools.DevTools;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaScratchPadTests extends BaseClass {
    private MediaScratchPadPage mediaScratchPadPage;
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();
    private static final String MEDIA_SCRATCHPAD_URL = LOGIN_ENDPOINT + "/reporting/media-scratchpad/AMAZON";

    private SoftAssert softAssert = new SoftAssert();
    private DevTools devTools;

    @BeforeMethod
    public void setupTests(ITestContext testContext, ITestResult tr) throws Exception {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName.get() + " ***************");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Logitech");
        driver.get(MEDIA_SCRATCHPAD_URL);

        if (!driver.getTitle().contains("Scratchpad - Flywheel")) {
            driver.get(MEDIA_SCRATCHPAD_URL);
        }

        mediaScratchPadPage = new MediaScratchPadPage(driver);
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify Title and Values are dynamically changing in Summary Table ")
    public void mediaScratchPadDynamicTitleTest() throws InterruptedException {

        List<String> periodComparisonDropdownValues = mediaScratchPadPage.getPeriodComparisonDropdownValues();
        List<String> sliceByDropdownValues = mediaScratchPadPage.getSliceByDropdownValues();

        for (String periodComparisonDropdownValue : periodComparisonDropdownValues) {
            mediaScratchPadPage.selectValuePeriodComparisonDropDown(periodComparisonDropdownValue);
            for (String sliceByDropdownValue : sliceByDropdownValues) {
                mediaScratchPadPage.selectValueSliceByDropDownAndApplySearch(sliceByDropdownValue);

                Assert.assertEquals(mediaScratchPadPage.getDynamicPeriodComparisonText(), periodComparisonDropdownValue);
                Assert.assertEquals(mediaScratchPadPage.getDynamicSliceByText(), sliceByDropdownValue);
            }
        }
    }

    @Test(description = "MDI-64: Verify that query is optimized for inventory widget on media scratchpad for DSP platform")
    public void MediaScratchpad_InventoryBackendQueryOptimizationTest() throws InterruptedException {

        softAssert.assertTrue(mediaScratchPadPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");
        mediaScratchPadPage.dateAndIntervalPickerPage.clickWeeklyIntervalDropdown();
        mediaScratchPadPage.dcFilters.filterPlatform("AMAZON_DSP");

        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.requestWillBeSent(), request -> {

        });

        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.INVENTORY_DSP_SITES);
        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.INVENTORY_DSP_SUPPLY);
        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.DSP_REPORTING);

        mediaScratchPadPage.clickOnInventoryWidget();
        softAssert.assertTrue(mediaScratchPadPage.isInventoryWidgetDataLoaded());

        softAssert.assertAll();
    }

    @Test(description = "MDI-65: Verify that query is optimized for geography widget on media scratchpad for DSP platform")
    public void MediaScratchpad_GeographyBackendQueryOptimizationTest() throws InterruptedException {

        softAssert.assertTrue(mediaScratchPadPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");
        mediaScratchPadPage.dateAndIntervalPickerPage.clickWeeklyIntervalDropdown();
        mediaScratchPadPage.dcFilters.filterPlatform("AMAZON_DSP");

        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));
        devTools.addListener(Network.requestWillBeSent(), request -> {

        });

        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.GEOGRAPHY_DSP_CITY);
        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.GEOGRAPHY_DSP_REGION);
        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.GEOGRAPHY_DSP_COUNTRY);
        verifyUrlResponseStatusFromNetwork(MediaScratchpadRoutes.DSP_REPORTING);

        mediaScratchPadPage.clickOnGeographyWidget();
        softAssert.assertTrue(mediaScratchPadPage.isGeographWidgetDataLoaded() );

        softAssert.assertAll();
    }


    private void verifyUrlResponseStatusFromNetwork(String url) {
        AtomicInteger statusCode = new AtomicInteger();
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            if (responseReceived.getType().toString().equals("XHR") & responseReceived.getResponse().getUrl().contains(url)) {
                System.out.println("URL: " + responseReceived.getResponse().getUrl());
                statusCode.set(responseReceived.getResponse().getStatus());
                softAssert.assertTrue(statusCode.compareAndSet(200, statusCode.intValue()), url + " status code is not correct");
            }
        });
    }
}