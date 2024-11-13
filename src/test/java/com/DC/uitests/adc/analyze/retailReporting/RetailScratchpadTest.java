package com.DC.uitests.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.RetailScratchpadPage;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.testcases.BaseClass;
import com.DC.uitests.hub.marketshare.HubMarketShareTokenExchangeForUsersTest;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.hub.FilaUser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.Dictionary;
import java.util.List;

public class RetailScratchpadTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    String dcAppUrl;

    @BeforeClass
    public void setupTests() throws Exception {
        readConfig = ReadConfig.getInstance();
        logger = Logger.getLogger(HubMarketShareTokenExchangeForUsersTest.class);
        PropertyConfigurator.configure("log4j.properties");
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @Test(priority = 1, dataProvider = "Product_Titles", dataProviderClass = RetailScratchpadDataProvider.class, description = "RAR-502 (493-494-495-496-497-500) - RS Product title column for different client account types")
    public void Retail_ScratchPad_Product_Title_Column_For_Different_Client_Account_Types_Test(Dictionary<String, Object> client) throws Exception {
        RetailScratchpadPage retailScratchpadPage = new RetailScratchpadPage(driver);
        SoftAssert softAssert = new SoftAssert();
        FilaUser filaUser = new FilaUser();
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());
        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);

        String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: Base User", "Internal Only: User Administration", "Internal Only: Role Administration");
        String buIds = filaUser.getFilaBus(authToken,"3M UK", "3M", "Hersheys - US", "Runa",  "Nielsen-Massey", "Haleon-Consumer-Healthcare UAE", "Bayer UK" ,  "Newell Rubbermaid - US");
        filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);

        AppHomepage hp = new AppHomepage(driver);
        hp.openPage("Analyze", "Retail Scratchpad");
        DCFilters dcFilters = new DCFilters(driver);

        List<String> buList = (List<String>) client.get("client");
        retailScratchpadPage.selectBU(buList.toArray(new String[0]));
        retailScratchpadPage.selectInterval(client.get("interval").toString());

        if (buList.get(0).equalsIgnoreCase("Haleon-Consumer-Healthcare UAE")){
            dcFilters.selectAmazonRetailerAccount(client.get("accountType").toString());
        }

        retailScratchpadPage.selectSummarySliceByOption("ASIN");
        dcFilters.apply();
        softAssert.assertTrue(retailScratchpadPage.waitProductTitleVisibleOnUi(), "Product Title is not present in the table.");
        List<WebElement> summaryTableRows = retailScratchpadPage.getSummaryTableRows();

        if (!summaryTableRows.isEmpty()){
            String productTitleUi = retailScratchpadPage.getProductTitleOnUi(summaryTableRows.get(0));
            softAssert.assertTrue(!productTitleUi.isEmpty(), "Product title is empty.");
            if (buList.size() == 1 && buList.get(0).equalsIgnoreCase("3M")){
                retailScratchpadPage.exportAs("CSV");
                String columnTitle = SharedMethods.getCsvCellValue(downloadFolder + "/export.csv", 1, 1);
                softAssert.assertEquals(columnTitle, "Product Title", "Product Title is not present in CSV file.");
                String columnTitleCsv = SharedMethods.getCsvCellValue(downloadFolder + "/export.csv", 2, 1);
                softAssert.assertEquals(productTitleUi, columnTitleCsv, "Product title values do not match (UI vs. CSV).");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "RAR-502-642 - RS Product title column filters")
    public void Retail_ScratchPad_Product_Title_Column_Filter_Test() throws Exception {
        List<String> filterOptions = List.of("Contains", "Not contains", "Equals", "Starts with", "Ends with");
        RetailScratchpadPage retailScratchpadPage = new RetailScratchpadPage(driver);
        SoftAssert softAssert = new SoftAssert();
        DCLoginPage lp = new DCLoginPage(driver);
        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaQaUserEmail(), readConfig.getHubFilaQaUserPassword());

        AppHomepage hp = new AppHomepage(driver);
        hp.openPage("Analyze", "Retail Scratchpad");
        DCFilters dcFilters = new DCFilters(driver);

        retailScratchpadPage.selectBU("3M");
        retailScratchpadPage.selectSummarySliceByOption("ASIN");
        dcFilters.apply();

        retailScratchpadPage.waitProductTitleVisibleOnUi();
        List<WebElement> summaryTableRows = retailScratchpadPage.getSummaryTableRows();
        String productTitleUi = retailScratchpadPage.getRandomProductTitle(summaryTableRows);

        retailScratchpadPage.verifyProductTitleHeaderFilter(softAssert, summaryTableRows, (String) SharedMethods.getRandomItemFromList(filterOptions), productTitleUi);
        softAssert.assertAll();
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod()
    public void initializeBrowser(ITestContext testContext) {
        driver = initializeBrowser(testContext, readConfig.getHeadlessMode());
    }

}