package com.DC.uitests.adc.execute.productManager.rpcDiscovery;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.rpcDiscovery.RPCDiscoveryPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class RPCDiscoveryUITests extends BaseClass {
    private final String CPG_USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();

    private RPCDiscoveryPage rpcDiscoveryPage;
    // private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(CPG_USERNAME, PASSWORD);

        //  TODO: add url
        driver.get("TODO: add url");
        rpcDiscoveryPage = new RPCDiscoveryPage(driver);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupData() {
        // TODO - REMOVE ADDED PRODUCTS?
        quitBrowser();
    }

    @Test(priority = 1, description = "Can search RPCs by RPC")
    public void RPCDiscoveryPage_CanSearchByRPC() {
        SoftAssert softAssert = new SoftAssert();

        // TODO - HOW TO GET RPC TO TEST? NEED TO GET FROM DB? GRAB ONE FROM THE TABLE?
        String searchTerm = "ABCDEF";

        rpcDiscoveryPage.refreshPage();
        var originalTableData = rpcDiscoveryPage.getTableData();

        // CLICKING SEARCH ICON
        verifySearchInputWorks(searchTerm, true, softAssert);
        verifyClearingSearchInput(softAssert, true,originalTableData);

        // HITTING ENTER
        verifySearchInputWorks(searchTerm, false, softAssert);
        verifyClearingSearchInput(softAssert, false,originalTableData);

        // NEGATIVE CASE SCENARIO
        searchTerm = "invalid rpc";
        rpcDiscoveryPage.searchForRPC(searchTerm, true);
        boolean noDataMessageIsDisplayed = rpcDiscoveryPage.tableCommonFeatures.isNoDataMessageDisplayed();
        softAssert.assertTrue(noDataMessageIsDisplayed, "No data message was not displayed when searching for a non existent rpc: " + searchTerm);
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "Product Identifier column is blank by default")
    public void RPCDiscoveryPage_ProductIdentifierColumnIsBlankByDefault(){
        rpcDiscoveryPage.refreshPage(); // TODO - MAYBE?
        var tableData = rpcDiscoveryPage.getTableData();
        var productIdentifierColumnValues = tableData.stream().map(row -> row.get("Product Identifier")).collect(Collectors.toList());
        Assert.assertTrue(productIdentifierColumnValues.stream().allMatch(String::isEmpty), "Not all cells under Product Identifier column were blank");
    }

    @Test(priority = 3, description = "All columns besides PI are sortable")
    public void RPCDiscoveryPage_CorrectColumnsAreSortable(){
        var sortableColumns = rpcDiscoveryPage.tableCommonFeatures.getSortableColumns();
        var expectedSortableColumns = List.of("RPC", "Locale", "Retailer", "Business Unit", "Date Updated");
        Assert.assertEquals(sortableColumns, expectedSortableColumns, "Sortable columns are not correct");
    }

    @Test(priority = 4, description = "All columns beside PI can be sorted")
    public void RPCDiscoveryPage_CanSortColumns(){
        // TODO - HOW TO GET ALL DATA SO WE CAN GET THE EXPECTED SORTED DATA?
    }

    @Test(priority = 5, description = "If Business Unit is provided in the message, pre-populate that column and disable editing")
    public void RPCDiscoveryPage_BusinessUnitIsPopulatedIfProvidedInMessage(){
        // TODO - HOW TO GET THE MESSAGE? IS IT AN ENDPOINT?

    }

    @Test(priority = 6, description = "Pagination works")
    public void RPCDiscoveryPage_PaginationWorks(){
        // TODO - WILL THIS PAGE HAVE A PAGINATOR?
    }

    @Test(priority = 7, description = "Banner displays when RPC discovery products are ready for review")
    public void RPCDiscoveryPage_BannerDisplaysWhenRPCDiscoveryProductsAreReadyForReview(){
        // TODO - WHERE WILL THIS BANNER BE LOCATED?
        // IS IT POSSIBLE TO AUTOMATE?
    }

    @Test(priority = 8, description = "Selected products are created. If product identifier is blank, an auto-generated one is created")
    public void RPCDiscoveryPage_SelectedProductsAreCreated(){
        // TODO - SELECT PRODUCT WITH EMPTY PI, EMPTY BU
        // TODO - SELECT PRODUCT WITH CUSTOM PI, EMPTY BU
        // TODO - SELECT PRODUCT WITH CUSTOM PI, AND ASSIGNED BU
        // TODO - SELECT PRODUCT WITH CUSTOM PI, AND ASSIGNED BU
    }

    // TODO - WHAT HAPPENS IF COMPANY ALREADY HAS A PRODUCT WITH THE SAME PRODUCT IDENTIFIER?
    // TODO - WHAT HAPPENS IF USER DOESN'T ASSIGN A BU?



    private void verifySearchInputWorks(String searchTerm, Boolean clickOnSearchIcon, SoftAssert softAssert) {
        rpcDiscoveryPage.searchForRPC(searchTerm, clickOnSearchIcon);
        boolean noDataMessageIsDisplayed = rpcDiscoveryPage.tableCommonFeatures.isNoDataMessageDisplayed();
        softAssert.assertFalse(noDataMessageIsDisplayed, "No data message was displayed when searching for rpc: " + searchTerm);

        var tableData = rpcDiscoveryPage.getTableData();
        for (var row : tableData) {
            boolean rpcContainsSearchTerm = row.get("RPC").contains(searchTerm);
            softAssert.assertTrue(rpcContainsSearchTerm, "RPC: " + row.get("RPC") + " does not contain search term: " + searchTerm);
        }
    }

    private void verifyClearingSearchInput(SoftAssert softAssert, Boolean clickOnSearchIcon, ArrayList<java.util.LinkedHashMap<String, String>> expectedTableData) {
        rpcDiscoveryPage.searchForRPC("", clickOnSearchIcon);
        var noDataMessageIsDisplayed = rpcDiscoveryPage.tableCommonFeatures.isNoDataMessageDisplayed();
        softAssert.assertFalse(noDataMessageIsDisplayed, "Table was empty after clearing search input");
        var tableData = rpcDiscoveryPage.getTableData();
        softAssert.assertEqualsNoOrder(
                tableData.toArray(),
                expectedTableData.toArray(),
                "Expected rows are not displayed after clearing search input" +
                        "\nRows displayed: " + tableData +
                        "\nExpected rows: " + expectedTableData
        );
    }
}
