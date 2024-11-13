package com.DC.uitests.adc.manage.dataManagement;

import java.sql.*;
import java.util.*;

import com.DC.constants.NetNewConstants;
import com.DC.db.manage.STMDBFunctions;
import com.DC.db.manage.SearchTermManagementQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.manage.dataManagement.SearchTermManagementPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.PostgreSqlUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.models.responses.adc.manage.SearchTermManagementResponseBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.DC.pageobjects.adc.DCLoginPage;

public class SearchTermManagementTest extends BaseClass {

    int random = (int) (Math.random() * 1000 + 1);
    private final String SEARCH_TERM_NAME = "autotest";
    private String combinedName = SEARCH_TERM_NAME + random;
    private String frequency = "DAILY";
    private String priority = "t";
    List<String> retailer = new ArrayList<>(Arrays.asList("Walmart"));
    List<String> searchTermGroups = new ArrayList<>(List.of("AutoTest Search Term Group" + random));
    private String mccormick = "288d2722-2185-4b83-ac66-1e6e635c35f7";
    private String superFizz = "48d10c85-ef18-487a-9aca-20b9db194ce8";
    private String amazon = "e5da6a83-4f1a-4e01-90ef-2a0a6a33d30c";
    private String walmart = "cd6380d5-6ccd-4a4c-8083-9877bb54068b";
    private String target = "33e7c160-965d-4b87-9cd5-eb3446f4f766";
    private String baseUrl = "https://external-gateway-service-api.staging.dc.flywheeldigital.com";
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private SearchTermManagementPage searchTermManagementPage;
    private AppHomepage appHomepage;
    SoftAssert softAssert = new SoftAssert();

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("SuperFizz US");

        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Manage");
        appHomepage.clickLink("Search Term Management");
        searchTermManagementPage = new SearchTermManagementPage(driver);
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

    //DO NOT RUN ON PRODUCTION
    @Test(priority = 1, description = "Verify that Add Search Term functionality is working")
    public void STM_AddSearchTermFunctionalityIsWorking() throws InterruptedException {
        searchTermManagementPage.createNewSearchTerm(combinedName, retailer, frequency, priority, searchTermGroups);
        Assert.assertEquals(searchTermManagementPage.getAlertMessageText("Search term updates were made successfully. The results can be seen in 1-2 business days."),
                "Search term updates were made successfully. The results can be seen in 1-2 business days.", "Create Search Term alert is not correct");
        LOGGER.info("Search Term added successfully");

        LOGGER.info("Verifying the new Search Term fields in the table");
        searchTermManagementPage.findSearchTermInSearchBarAndClick(combinedName);
        verifySearchTermFieldsInUI(combinedName, retailer, frequency, priority, searchTermGroups);
        searchTermManagementPage.clearSearchBar();

        LOGGER.info("Verifying the new Search Term fields in the DB");
        getSearchTermFromDB(false);

        LOGGER.info("Add Search Term functionality is working as expected");
    }

    //DO NOT RUN ON PRODUCTION
    @Test(priority = 2, description = "Verify that Edit Search Term functionality is working")
    public void STM_EditSearchTermFunctionalityIsWorking() throws InterruptedException {

        frequency = "HOURLY";
        priority = "f";
        retailer.add("Amazon");
        LOGGER.info("Retailers: " + retailer);

        Assert.assertTrue(searchTermManagementPage.isEditSearchTermIconDisplayed(), "Edit Search Term Icon is not displayed");
        searchTermManagementPage.clickEditSearchTermIcon();
        Assert.assertTrue(searchTermManagementPage.isEditSearchTermPopupIsDisplayed(), "Edit Search Term Header is not displayed");
        Assert.assertTrue(searchTermManagementPage.checkIfCorrectSearchTermChosen(combinedName), "Search Term is not correct");
        searchTermManagementPage.editSearchTerm(combinedName, retailer, frequency, priority, searchTermGroups);
        searchTermManagementPage.clickUpdateButton();
        Assert.assertEquals(searchTermManagementPage.getAlertMessageText("Search term updates were made successfully. The results can be seen in 1-2 business days."),
                "Search term updates were made successfully. The results can be seen in 1-2 business days.", "Update Search Term alert is not correct");
        LOGGER.info("Search Term updated successfully");

        LOGGER.info("Verifying Search Term was updated in the table");
        verifySearchTermFieldsInUI(combinedName, retailer, frequency, priority, searchTermGroups);
        searchTermManagementPage.clearSearchBar();

        LOGGER.info("Verifying Search Term was updated in the DB");
        getSearchTermFromDB(false);

        LOGGER.info("Edit Search Term functionality is working as expected");
    }

    //DO NOT RUN ON PRODUCTION
    @Test(priority = 3, description = "Verify that Delete Search Term functionality is working")
    public void STM_DeleteSearchTermFunctionalityIsWorking() throws InterruptedException {

        Assert.assertTrue(searchTermManagementPage.isDeleteSearchTermIconDisplayed(), "Delete Search Term Icon is not displayed");
        searchTermManagementPage.clickDeleteSearchTermIcon();
        Assert.assertTrue(searchTermManagementPage.isDeleteSearchTermPopupIsDisplayed(), "Delete Search Term Header is not displayed");
        searchTermManagementPage.clickContinueButton();
        LOGGER.info("Verifying Search Term was deleted from the table");
        verifySearchTermDeletedFromUI();

        LOGGER.info("Verifying Search Term was deleted from the DB");
        getSearchTermFromDB(true);

        LOGGER.info("Delete Search Term functionality is working as expected");
        searchTermManagementPage.refreshPage();
    }

    @Test(priority = 4, description = "Verify that after choosing a retailer, Search Term table has only Search Terms for that retailer")
    public void STM_SearchTermTableHasOnlySearchTermsForChosenRetailer() throws InterruptedException {
        searchTermManagementPage.refreshPage();
        String[] retailers = {"Amazon"};
        LOGGER.info("Checking with single retailer");
        searchTermManagementPage.filters.selectRetailer(retailers);
        LOGGER.info("Verifying DB has Search Terms for selected retailer");
        Assert.assertTrue(STMDBFunctions.getRetailersFromDB("SuperFizz", retailers), "DB has Search Terms for other retailers");
        int numberOfRetailersInUI = searchTermManagementPage.verifySearchTermTableHasOnlySearchTermsForRetailers(retailers);
        int numberOfRetailersInDB = STMDBFunctions.getNumberOfRecordsFromDB("SuperFizz", retailers);
        LOGGER.info("Number of retailers in UI: " + numberOfRetailersInUI + " Number of retailers in DB: " + numberOfRetailersInDB);
        Assert.assertEquals(numberOfRetailersInUI, numberOfRetailersInDB, "DB has Search Terms for other retailers");
        searchTermManagementPage.filters.clearRetailers();

        //        LOGGER.info("Checking with multiple retailers");
        //        retailers = new String[]{"Target", "Walmart"};
        //        searchTermManagementPage.filters.selectRetailer(retailers);
        //        LOGGER.info("Verifying DB has only Search Terms for selected retailers");
        //        Assert.assertTrue(STMDBFunctions.getRetailersFromDB("SuperFizz", retailers), "DB has Search Terms for other retailers");
        //        numberOfRetailersInUI = searchTermManagementPage.verifySearchTermTableHasOnlySearchTermsForRetailers(retailers);
        //        numberOfRetailersInDB = STMDBFunctions.getNumberOfRecordsFromDB("SuperFizz", retailers);
        //        LOGGER.info("Number of retailers in UI: " + numberOfRetailersInUI + " Number of retailers in DB: " + numberOfRetailersInDB);
        //        Assert.assertEquals(numberOfRetailersInUI, numberOfRetailersInDB, "DB has Search Terms for other retailers");
        //        searchTermManagementPage.filters.clearRetailers();
        //        LOGGER.info("Filtering by retailer functionality is working as expected in UI and DB");

        searchTermManagementPage.filters.selectRetailer("All Retailers");
    }

    @Test(priority = 5, description = "Verify that all filters are present and working as expected")
    public void STM_LeftSideFiltersWorking() throws Exception {

        softAssert.assertTrue(searchTermManagementPage.filters.retailerSelected("All Retailers"), "All Retailers is not selected");
        softAssert.assertEquals(searchTermManagementPage.filters.getRetailersSelected().size(), 1, "All Retailers is not selected");

        searchTermManagementPage.filters.clearRetailers();

        searchTermManagementPage.filters.selectAllRetailers();
        softAssert.assertTrue(searchTermManagementPage.filters.retailerSelected("All Retailers"), "All Retailers is not selected");

        searchTermManagementPage.filters.clearRetailers();
        searchTermManagementPage.filters.apply();

        softAssert.assertTrue(searchTermManagementPage.filters.selectRetailerAlert(), "No data message is not displayed");

        softAssert.assertAll();

        searchTermManagementPage.filters.selectMultipleRetailers("Walmart", "Amazon");
        List<String> retailersInFilter = searchTermManagementPage.filters.getRetailersSelected();
        searchTermManagementPage.filters.apply();

        List<String> retailersIn = searchTermManagementPage.getDataRetailers();

        Collections.sort(retailersInFilter);
        Collections.sort(retailersIn);

        softAssert.assertEquals(retailersInFilter, retailersIn, "Retailers in filter and in table are not the same");

        softAssert.assertAll();

    }

    @Test(priority = 6, description = "Verify that main table has 4 required columns")
    public void STM_RequiredColumnsArePresent() {

        Assert.assertTrue(searchTermManagementPage.isSearchTermColumnHeaderDisplayed(), "Search Term column header is not displayed");
        Assert.assertTrue(searchTermManagementPage.isPriorityColumnHeaderDisplayed(), "Priority column header is not displayed");
        Assert.assertTrue(searchTermManagementPage.isAssociatedRetailersColumnHeaderDisplayed(), "Associated Retailers column header is not displayed");
        Assert.assertTrue(searchTermManagementPage.isFrequencyColumnHeaderDisplayed(), "Frequency column header is not displayed");
    }

    @Test(priority = 7, description = "Verify that pagination is present and set to 100 by default")
    public void STM_PaginationIsPresent() {

        Assert.assertTrue(searchTermManagementPage.commonFeatures.verifyPaginationIsPresent(), "Pagination is not present");
        Assert.assertTrue(searchTermManagementPage.commonFeatures.getDefaultNumberOfTermsDisplayed().equals("100"), "Pagination is not set to 100 by default");

    }

    @Test(priority = 8, description = "Verify that all Create Search Term required fields are present")
    public void STM_STRequiredFieldsPresent() throws InterruptedException {

        searchTermManagementPage.clickAddASearchTermButton();
        Assert.assertTrue(searchTermManagementPage.verifyAddSearchTermPopupIsDisplayed(), "Add Search Term Header is not displayed");
        Assert.assertTrue(searchTermManagementPage.verifyToggleIsDisplayed(), "Toggle is not displayed");
        Assert.assertTrue(searchTermManagementPage.isFrequencyDropdownDisplayed(), "Frequency dropdown is not displayed");
        Assert.assertTrue(searchTermManagementPage.isSearchTermGroupsFieldDisplayed(), "Search Term Groups field is not displayed");
        Assert.assertTrue(searchTermManagementPage.isCreateButtonDisplayed(), "Create button is not displayed");
        Assert.assertTrue(searchTermManagementPage.isCancelButtonDisplayed(), "Cancel button is not displayed");
        searchTermManagementPage.clickCancelButton();
    }

    @Test(priority = 9, description = "Verify that Frequency dropdown is working correctly")
    public void STM_FrequencyDropdownIsWorking() throws InterruptedException {
        searchTermManagementPage.clickAddASearchTermButton();
        searchTermManagementPage.chooseRetailer(Collections.singletonList("Walmart"));
        Assert.assertTrue(searchTermManagementPage.verifyFrequencyDropdownOptions(), "Frequency dropdown options are not correct");
        Assert.assertTrue(searchTermManagementPage.getDefaultValueOfFrequencyDropdown(), "Frequency is not set to Daily by default");
        searchTermManagementPage.chooseFrequency("HOURLY");
        searchTermManagementPage.clickCancelButton();
    }

    @Test(priority = 11, description = "Verify that page has Add a Search Term, Upload and Download buttons are present")
    public void STM_AddImportExportButtonsArePresent() {

        Assert.assertTrue(searchTermManagementPage.isAddASearchTermButtonDisplayed(), "Add a Search Term button is not displayed");
        Assert.assertTrue(searchTermManagementPage.isUploadButtonDisplayed(), "Upload button is not displayed");
        Assert.assertTrue(searchTermManagementPage.isDownloadButtonDisplayed(), "Download button is not displayed");

    }

    @Test(priority = 12, description = "Verify that when adding a new Search Term and not filling in required fields, error messages are displayed")
    public void STM_ErrorMessagesAreDisplayed() throws InterruptedException {
        searchTermManagementPage.clickAddASearchTermButton();
        searchTermManagementPage.clickCreateButton();
        Assert.assertTrue(searchTermManagementPage.verifyErrorMessagesAreDisplayed(), "Error messages are not displayed");
        Assert.assertTrue(searchTermManagementPage.isErrorMessageIsCorrectForSearchTerm(), "Error message is not correct for Search Term");
        Assert.assertTrue(searchTermManagementPage.isErrorMessageIsCorrectForRetailers(), "Error message is not correct for Retailers");
        searchTermManagementPage.clickCancelButton();
    }

    @Test(priority = 13, description = "Verify that after clicking Upload button, Upload Search Terms popup is displayed")
    public void STM_UploadSearchTermsPopupIsDisplayed() throws InterruptedException {
        searchTermManagementPage.clickUploadButton();
        Assert.assertTrue(searchTermManagementPage.isUploadSearchTermsPopupIsDisplayed(), "Upload Search Terms popup is not displayed");
        Assert.assertTrue(searchTermManagementPage.isUploadSearchTermsPopUpHeaderCorrect(), "Upload Search Terms popup header is not correct");
        searchTermManagementPage.clickCancelButtonInUploadPopUp();
    }

    @Test(priority = 14, description = "Verify that Upload popup has all required buttons and they prompt user to correct screen popups")
    public void STM_UploadPopupHasAllRequiredButtons() throws InterruptedException {
        searchTermManagementPage.clickUploadButton();
        Assert.assertTrue(searchTermManagementPage.verifyAllThreeButtonsAreDisplayedInUploadPopup(), "Upload popup does not have all required buttons");

        searchTermManagementPage.clickImportButtonInUploadPopup();
        Assert.assertTrue(searchTermManagementPage.isImportSearchTermsPopupIsDisplayed(), "Import Search Terms popup is not displayed");
        Assert.assertTrue(searchTermManagementPage.isImportSearchTermsPopUpHeaderCorrect(), "Import Search Terms popup header is not correct");
        searchTermManagementPage.clickCancelButtonInImportPopup();

        searchTermManagementPage.clickUploadButton();
        searchTermManagementPage.clickDeleteButtonInImportPopup();
        Assert.assertTrue(searchTermManagementPage.isDeleteSearchTermsPopupIsDisplayed(), "Delete Search Terms popup is not displayed");
        Assert.assertTrue(searchTermManagementPage.isDeleteSearchTermsPopUpHeaderCorrect(), "Delete Search Terms popup header is not correct");
        searchTermManagementPage.clickCancelButtonInImportPopup();
    }

    @Test(priority = 15, description = "Verify that after clicking Search Term Management link on Home Page user is redirected to the STM page")
    public void STM_UserIsRedirectedToSTMPage() throws InterruptedException {
        appHomepage = searchTermManagementPage.clickFWLogo();
        LOGGER.info("After clicking HomePage link user is redirected to the Home Page");
        appHomepage.clickOnSection("Manage");
        appHomepage.clickLink("Search Term Management");
        Assert.assertTrue(searchTermManagementPage.isSearchTermHeaderDisplayed(), "Search Term Management header is not displayed");
        String currentUrl = searchTermManagementPage.getCurrentUrl();
        LOGGER.info("Current page url: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("search-term-management"), "Page URL does not contain 'search-term-management'");
    }

    @Test(priority = 16, description = "Verify that Search bar is displayed on the page and autocomplete is working correctly")
    public void STM_SearchBarIsDisplayedAndWorkingCorrectly() throws InterruptedException {
        Assert.assertTrue(searchTermManagementPage.isSearchTermSearchBarDisplayed(), "Search bar is not displayed");
        Assert.assertTrue(searchTermManagementPage.verifyAutocompleteIsWorkingCorrectly("pasta sauces"), "Autocomplete is not working correctly");
    }

    @Test(priority = 17, description = "Verify that BU has only retailers assigned to it (UI and DB)")
    public void STM_BUHasOnlyRetailersAssignedToIt() throws InterruptedException {
        List<String> assignedRetailersFromUI = searchTermManagementPage.filters.getRetailersAssignedToBUFromUI();
        List<String> assignedRetailersFromDB = STMDBFunctions.getRetailersAssignedToBUFromDB("SuperFizz");
        LOGGER.info("Retailers assigned to BU from DB: " + assignedRetailersFromDB);
        Assert.assertEqualsNoOrder(assignedRetailersFromUI.toArray(), assignedRetailersFromDB.toArray(), "Retailers assigned to BU are not correct");
        LOGGER.info("Retailers assigned to BU are correct");
        searchTermManagementPage.filters.clickCancelButton();
    }

    @Test(priority = 18, description = "API call to get search terms with specific retailer in Filter")
    public void STM_APICallToGetSearchTermsWithSpecificRetailerInFilter() throws InterruptedException {
        LOGGER.info("Checking with all retailers thru API");
        String authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        String businessUnitContext = mccormick;
        List<String> retailerPlatformsToChoose = Arrays.asList(amazon, walmart, target);

        LOGGER.info("Making API call to fetch all retailers");
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .header("x-businessunitcontext", businessUnitContext)
                .header("x-retailerplatformcontext", retailerPlatformsToChoose)
                .header("authorization", authToken)
                .queryParam("orderBy", "searchTerm")
                .queryParam("sortOrder", "ASC")
                .queryParam("page", 4)
                .queryParam("pageSize", 100)
                .get("/search/search-term/extended");

        LOGGER.info("Status code is: " + response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200);

        SearchTermManagementResponseBody responseModel = response.getBody().as(SearchTermManagementResponseBody.class);
        List<SearchTermManagementResponseBody.Items> items = responseModel.items;

        for (SearchTermManagementResponseBody.Items item : items) {
            String searchTerm = item.searchTerm;
            List<SearchTermManagementResponseBody.Items.RetailerPlatforms> retailerPlatforms = item.retailerPlatforms;
            for (SearchTermManagementResponseBody.Items.RetailerPlatforms retailerPlatform : retailerPlatforms) {
                String retailerName = retailerPlatform.retailerName;
                if (!retailerName.equals("Amazon") && !retailerName.equals("Walmart") && !retailerName.equals("Target")) {
                    Assert.fail("***Failure! Retailer name " + retailerName + " is not expected for search term: " + searchTerm + "***");
                }
            }
        }
        LOGGER.info("Only selected retailers are present in the response");
    }

    @Test(priority = 19, description = "Verify that user can't create Search Term Group with existing name and correct error message is displayed")
    public void STM_UserCanNotCreateSearchTermGroupWithExistingName() throws InterruptedException {
        String searchTermGroupExisting = "AutoTest Search Term Group204";
        Assert.assertTrue(searchTermManagementPage.searchTermGroupAlreadyExists(combinedName, retailer, searchTermGroupExisting),
                "Uniqueness check of Search Term Group name is not working correctly");
        searchTermManagementPage.clickCancelButton();
    }

    public void verifySearchTermFieldsInUI(String expectedName, List<String> expectedRetailer, String expectedFrequency, String expectedPriority, List<String> expectedSearchTermGroups) {
        Assert.assertEquals(searchTermManagementPage.getSearchTermFromUI(combinedName), expectedName, "Search Term Name is not correct");
        Assert.assertEqualsNoOrder(searchTermManagementPage.getRetailerFromUI(combinedName), expectedRetailer.toArray(), "Search Term Retailer is not correct");
        Assert.assertEquals(searchTermManagementPage.getFrequencyFromUI(combinedName), expectedFrequency, "Search Term Frequency is not correct");
        Assert.assertEquals(searchTermManagementPage.getPriorityStatusFromUI(combinedName), expectedPriority, "Search Term Priority status is not correct");
        String searchTermGroupsFromUI = searchTermManagementPage.getSearchTermGroupFromUI(combinedName);
        if (searchTermGroups.size() == 1) {
            LOGGER.info("Search term group is: " + searchTermGroups);
            Assert.assertEquals(searchTermGroupsFromUI, expectedSearchTermGroups.get(0), "Search Term Group is not correct");
        } else if (searchTermGroups.size() > 1) {
            LOGGER.info("Search term group is: " + searchTermGroups);
            Assert.assertEquals(searchTermGroupsFromUI, expectedSearchTermGroups.size() + " Groups", "Search Term Group is not correct");
        } else {
            LOGGER.info("Search term group is: " + searchTermGroups);
            Assert.assertEquals(searchTermGroupsFromUI, "", "Search Term Group is not correct");
        }
    }

    public void verifySearchTermDeletedFromUI() throws InterruptedException {
        Assert.assertFalse(searchTermManagementPage.isSearchTermDisplayedInUI(combinedName), "Search Term is still displayed");
    }

    public void getSearchTermFromDB(boolean isDeleteAction) {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        List<String> searchTermGroups = new ArrayList<>();
        try {
            con = pu.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchTermManagementQueries.getQueryToFetchSearchTerms(combinedName))) {

                boolean searchTermExistsBeforeDeletion = false;
                String searchTerm = null;
                String scrapeFrequency = null;
                String priorityTerm = null;

                while (resultSet.next()) {
                    searchTermExistsBeforeDeletion = true;
                    searchTerm = resultSet.getString("name");
                    scrapeFrequency = resultSet.getString("scrape_frequency");
                    priorityTerm = resultSet.getString("priority");
                    String searchTermGroup = resultSet.getString("search_term_group");
                    searchTermGroups.add(searchTermGroup);
                }

                if (isDeleteAction) {
                    if (!searchTermExistsBeforeDeletion) {
                        LOGGER.info("Item was deleted from the database.");
                    } else {
                        LOGGER.info("Item was not deleted from the database.");
                        Assert.fail("Item was not deleted from the database.");
                    }
                } else {
                    LOGGER.info("Search term is: " + searchTerm);
                    Assert.assertEquals(searchTerm, combinedName, "Search Term Name is not correct");
                    LOGGER.info("Expected search term is: " + combinedName);

                    LOGGER.info("Scrape frequency is: " + scrapeFrequency);
                    Assert.assertEquals(scrapeFrequency, frequency, "Search Term Frequency is not correct");
                    LOGGER.info("Expected scrape frequency is: " + frequency);

                    LOGGER.info("Priority term is: " + priorityTerm);
                    Assert.assertEquals(priorityTerm, priority, "Search Term Priority status is not correct");
                    LOGGER.info("Expected priority term is: " + priority);

                    LOGGER.info("Search term groups are: " + searchTermGroups);
                    LOGGER.info("Expected search term groups are: " + searchTermGroups);
                    Assert.assertEqualsNoOrder(searchTermGroups.toArray(), searchTermGroups.toArray(), "Search Term Group is not correct");
                }
            } catch (SQLException e) {
                LOGGER.error("Exception running the query. Exception: " + e.getMessage());
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                pu.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




