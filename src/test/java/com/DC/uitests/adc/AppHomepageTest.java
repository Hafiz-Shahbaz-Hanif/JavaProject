package com.DC.uitests.adc;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppHomepageTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private AppHomepage homePage;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        homePage = new AppHomepage(driver);
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Links are clickable and navigate to the appropriate screens", enabled = false)
    public void HP_Homepage_LinksWorkCorrectly() throws InterruptedException {

        homePage.linksVerification("Identify");
        homePage.linksVerification("Analyze");
        homePage.linksVerification("Execute");
        homePage.linksVerification("Manage");

        LOGGER.info("Links are clickable and navigate to the appropriate screens");
    }

    @Test(priority = 2, description = "Verify that Identify section has all required reports")
    public void HP_Homepage_IdentifyReportsAreDisplayed() throws InterruptedException {

        List<String> neededIdentifyReports = Arrays.asList("At a Glance", "Overview", "Section Details", "Scratchpad", "Notes", "Market View", "My Business", "Traffic", "Conversion", "Share of Voice", "Keyword Search",
                "Attribute Insights", "Category Brandshare", "Keyword Watchlists", "Brand Share of Voice", "Query Share of Voice", "Frequency, Clicks & Conversion Share", "Search Frequency Rank");

        verifySectionLinks("Identify", neededIdentifyReports);

    }

    @Test(priority = 3, description = "Verify that Analyze section has all required reports")
    public void HP_Homepage_AnalyzeReportsAreDisplayed() throws InterruptedException {

        List<String> neededAnalyzeReports = Arrays.asList("Data as a Service", "New to Brand Dashboard", "Lifetime Value", "Path to Purchase", "Reporting Dashboard", "Media Scratchpad",
                "Executive Dashboard", "Stream Dashboard", "Multiplatform View", "DSP Product Report", "DSP Video Dashboard", "Download Manager", "Sponsored Ad Placements", "Availability",
                "Product Auditor", "Profitability", "Promotion", "Ratings & Reviews", "Round Up", "Retail Scratchpad", "Retail Gainers & Drainers", "ASIN Detail", "Case Management Reporting",
                "PDP Change Dashboard", "PO & Inventory Dashboard", "Sales Correlation Model", "Retail Executive Dashboard", "Search Rank",
                "DSP Funnel Report", "Availability & Price", "Query Trends", "SFR Click & Conversion Share", "Keyword Trends", "Price", "Share & Ad Spend");

        verifySectionLinks("Analyze", neededAnalyzeReports);

    }

    @Test(priority = 4, description = "Verify that Execute section has all required reports")
    public void HP_Homepage_ExecuteReportsAreDisplayed() throws InterruptedException {

        List<String> neededExecuteReports = Arrays.asList("Tasks", "Task History", "Task UI Mapping", "Content Analyzer", "Report History", "Configure Reports",
                "Destination Manager", "Publish Set Manager", "API Administration", "Destination Setup", "FlightDeck", "Intraday Multipliers", "Eligibility Tracker", "Incrementality Reporting",
                "Sponsored Products Genius Dashboard", "Budget Forecasting", "Air Traffic Control", "Budget Manager", "Incrementality Recommendations", "Advertised (Other) ASINs",
                "Financial Protection", "Rule-Based Bidding", "Ensemble", "Campaign Segmentation", "Retailer Requirements", "Properties", "Product History",
                "Product Lists", "Products", "Retail: ASIN Segmentation", "Product Details", "PO Golden Data", "Case Management Creation", "Alerts", "Campaigns", "Product Rank Tracking",
                "DSP Control Panel", "Imports");

        verifySectionLinks("Execute", neededExecuteReports);

    }

    @Test(priority = 5, description = "Verify that Manage section has all required reports")
    public void HP_Homepage_ManageReportsAreDisplayed() throws InterruptedException {

        List<String> neededManageReports = Arrays.asList("Manage Exec Dash Data", "Search Term Management", "Report Sharing", "Catalog Classifier", "Catalog Download", "Saved Filters",
                "Toolbox API", "Toolbox UI", "Budget Management", "Category Associations", "Coverage Reporting", "Keyword Segmentation", "Data Administration", "Manage Queries", "Query Approval", "Ensemble Management",
                "Financial Protection Configuration");

        verifySectionLinks("Manage", neededManageReports);

    }

    @Test(priority = 6, description = "Verify that reports under Identify match between Homepage and NavBar")
    public void HP_Homepage_ReportsMatchForIdentifySection() throws InterruptedException {

        compareHomePageLinksToNavBarLinks("Identify");

    }

    @Test(priority = 7, description = "Verify that reports under Analyze match between Homepage and NavBar")
    public void HP_Homepage_ReportsMatchForAnalyzeSection() throws InterruptedException {

        compareHomePageLinksToNavBarLinks("Analyze");

    }

    @Test(priority = 8, description = "Verify that reports under Execute match between Homepage and NavBar")
    public void HP_Homepage_ReportsMatchForExecuteSection() throws InterruptedException {

        compareHomePageLinksToNavBarLinks("Execute");

    }

    @Test(priority = 9, description = "Central homepage and it's elements are displayed")
    public void HP_TopNav_AllRequiredElementsAreDisplayed() {

        Assert.assertTrue(homePage.isIdentifyDropdownDisplayed(), "The Identify dropdown is not displayed.");
        Assert.assertTrue(homePage.isAnalyzeDropdownDisplayed(), "The Analyze dropdown is not displayed.");
        Assert.assertTrue(homePage.isExecuteDropdownDisplayed(), "The Execute dropdown is not displayed.");
        Assert.assertTrue(homePage.isUserProfileIconDisplayed(), "The User Profile Icon is not displayed.");

        LOGGER.info("Homepage, Dropdowns and Search bar are displayed");
    }

    @Test(priority = 10, description = "Autofill in Search bar is working as intended")
    public void HP_TopNav_AutofillInSearchFieldWorksCorrectly() {

        Assert.assertTrue(homePage.isSearchBarDisplayed(), "The search bar is not displayed.");
        verifyAutofillSearchReturnsCorrectOptions("spo");
        LOGGER.info("Autofill suggestions contain input");
    }

    @Test(priority = 11, description = "Verify that clicking the Flywheel logo at the top left returns to the homepage")
    public void HP_TopNav_ClickingFlywheelLogoReturnsToHomepage() throws InterruptedException {

        Assert.assertTrue(homePage.verifyClickingOnFWLogoReturnsToHomePage(), "Clicking the Flywheel logo does not return to the homepage.");

        LOGGER.info("Clicking the Flywheel logo returns to the homepage");
    }

    @Test(priority = 12, description = "Verify that User Profile Icon dropdown has all required links")
    public void HP_TopNav_UserIconDropdownLinksAreDisplayed() throws InterruptedException {

        List<String> neededUserIconDropdownLinks = Arrays.asList("Client Management", "Role Management", "User Management", "Account Details", "Request Support", "Log Out",
                "API Credentials", "Notifications", "Profile Information");

        verifyTopNavLinks(neededUserIconDropdownLinks);

    }

    public void verifyAutofillSearchReturnsCorrectOptions(String searchTerm) {

        homePage.searchForAReportMetricOrPage(searchTerm);

        List<String> autofillOptions = homePage.getSearchResults();
        for (String option : autofillOptions) {
            boolean isOptionCorrect = option.toLowerCase().contains(searchTerm.toLowerCase());
            Assert.assertTrue(isOptionCorrect, "Autofill suggestions do not contain '" + searchTerm + "'.");
        }
    }

    public void verifySectionLinks(String sectionName, List<String> expectedLinks) throws InterruptedException {
        List<String> actualLinks = homePage.getSectionLinks(sectionName);
        Assert.assertEqualsNoOrder(actualLinks.toArray(), expectedLinks.toArray(),
                "Links in " + sectionName + " do not match expected links" +
                        "\nMissing Links: " + getMissingLinks(expectedLinks, actualLinks) +
                        "\nExtra Links: " + getExtraLinks(expectedLinks, actualLinks));

    }

    public void compareHomePageLinksToNavBarLinks(String sectionName) throws InterruptedException {
        if (sectionName.equalsIgnoreCase("Execute")) {
            homePage.scrollToBottomOfPage();
        }
        List<String> linksInHomepage = homePage.getSectionLinks(sectionName);
        List<String> linksInNavbar = homePage.getNavBarSectionLinks(sectionName);
        int linksInHomepageSize = linksInHomepage.size();
        int linksInNavbarSize = linksInNavbar.size();

        Assert.assertEquals(linksInHomepageSize, linksInNavbarSize, "The number of links in Homepage does not match the number of reports in Navbar. " +
                "Missing links: " + getMissingLinks(linksInHomepage, linksInNavbar) + "\nExtra links: " + getExtraLinks(linksInHomepage, linksInNavbar));
        Assert.assertEqualsNoOrder(linksInHomepage.toArray(), linksInNavbar.toArray(), "The links in Homepage do not match the reports in Navbar");
    }

    public void verifyTopNavLinks(List<String> expectedLinks) throws InterruptedException {
        List<String> actualLinks = homePage.getUserProfileIconLinks();
        Assert.assertEqualsNoOrder(actualLinks.toArray(), expectedLinks.toArray(),
                "Links in User Profile Icon section in NavBar do not match expected links" +
                        "\nExpected:\n" + expectedLinks +
                        "\nActual:\n" + actualLinks
        );

    }

    private List<String> getMissingLinks(List<String> expectedLinks, List<String> actualLinks) {
        List<String> missingLinks = new ArrayList<>(expectedLinks);
        missingLinks.removeAll(actualLinks);
        return missingLinks;
    }

    private List<String> getExtraLinks(List<String> expectedLinks, List<String> actualLinks) {
        List<String> extraLinks = new ArrayList<>(actualLinks);
        extraLinks.removeAll(expectedLinks);
        return extraLinks;
    }
}
