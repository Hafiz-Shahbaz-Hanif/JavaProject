package com.DC.uitests.adc.execute.productManager.products;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.stream.Collectors;

import static com.DC.constants.InsightsConstants.INSIGHTS_PRODUCTS_URL;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static java.util.Arrays.asList;

public class ProductBulkAssignBusinessUnitUITests extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();

    private ProductsPage productsPage;
    private ProductsPage.BottomActionBar bottomActionBar;
    private ProductsPage.AssignBusinessUnitModal assignBusinessUnitModal;

    private final String PRODUCT_PREFIX = "QA-BULK-EDIT-";
    private final UserFriendlyInstancePath BASE_PRODUCT = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "en-US", null, null);
    private final UserFriendlyInstancePath RETAILER_PRODUCT_NO_RPC = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "es-MX", "Amazon.com", null);
    private final UserFriendlyInstancePath RETAILER_CAMPAIGN_PRODUCT_WITH_RPC = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "en-US", "Amazon.com", "Halloween");
    private final UserFriendlyInstancePath CAMPAIGN_PRODUCT = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", null, "Christmas");
    private final UserFriendlyInstancePath RETAILER_PRODUCT_WITH_RPC = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", "Amazon.com", null);
    private final UserFriendlyInstancePath RETAILER_CAMPAIGN_PRODUCT_NO_RPC = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", "Amazon.com", "Halloween");

    private final List<UserFriendlyInstancePath> PRODUCTS_TO_SELECT = asList(
            BASE_PRODUCT,
            RETAILER_PRODUCT_NO_RPC,
            RETAILER_CAMPAIGN_PRODUCT_WITH_RPC,
            CAMPAIGN_PRODUCT,
            RETAILER_PRODUCT_WITH_RPC,
            RETAILER_CAMPAIGN_PRODUCT_NO_RPC
    );

    private HashMap<String, String> idsAndNamesOfBUsFromAPI;
    private Company company;

    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);

        driver.get(INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        productsPage.paginator.selectNumberOfItemsPerPage(50);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);

        company = CompanyApiService.getCompany(jwt);
        idsAndNamesOfBUsFromAPI = CompanyApiService.getIdsAndNamesOfAvailableBusinessUnits(jwt);
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        productsPage = productsPage.recoverPageIfBlankPageIsDisplayed(INSIGHTS_PRODUCTS_URL, productsPage);
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "C237235 - Clicking Edit > Assign Business Unit opens a modal to assign BUs to products selected")
    public void PPV_BulkAssignBusinessUnit_ClickingEditAssignBusinessUnitOpensModal() {
        productsPage.searchByProductIdentifier(PRODUCT_PREFIX);
        bottomActionBar = productsPage.selectProducts(PRODUCTS_TO_SELECT);
        try {
            assignBusinessUnitModal = bottomActionBar.clickEditAssignBusinessUnitButton();
        } catch (Exception e) {
            Assert.fail("Failed to open modal to assign business unit to products selected");
        }
        var modalTitle = assignBusinessUnitModal.getModalTitle();
        Assert.assertEquals(modalTitle, "Assign Business Unit");
    }

    @Test(priority = 2, description = "Canceling changes does not update products.")
    public void PPV_BulkAssignBusinessUnit_CancelingChangesDoesNotUpdateProducts() throws InterruptedException {
        assignBusinessUnitModal = openModalIfNeeded();
        try {
            productsPage = assignBusinessUnitModal.clickCancelButton();
        } catch (Exception e) {
            Assert.fail("Failed to close modal by clicking the cancel button");
        }

        var isSuccessMessageDisplayed = productsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertFalse(isSuccessMessageDisplayed, "Success message was displayed after canceling changes to products");

        var numberOfSelectedProducts = bottomActionBar.getNumberOfSelectedProducts();
        Assert.assertEquals(numberOfSelectedProducts, PRODUCTS_TO_SELECT.size(), "Products were deselected after canceling changes to products");

        assignBusinessUnitModal = bottomActionBar.clickEditAssignBusinessUnitButton();
        try {
            productsPage = assignBusinessUnitModal.clickCloseIcon();
        } catch (Exception e) {
            Assert.fail("Failed to close modal by clicking the close icon");
        }
    }

    @Test(priority = 3, description = "C243432 - All available business units are displayed in the dropdown. Can open/close dropdown menu")
    public void PPV_BulkAssignBusinessUnit_AllAvailableBusinessUnitAreDisplayedInDropdown() throws Exception {
        assignBusinessUnitModal = openModalIfNeeded();

        assignBusinessUnitModal.openDropdownMenu();

        List<String> businessUnitsDisplayed = assignBusinessUnitModal.singleSelectDropdown.getDropdownOptions();
        List<String> namesOfBusinessUnitsFromAPI = new ArrayList<>(idsAndNamesOfBUsFromAPI.values());

        Collections.sort(businessUnitsDisplayed);
        Collections.sort(namesOfBusinessUnitsFromAPI);

        Assert.assertEquals(
                businessUnitsDisplayed,
                namesOfBusinessUnitsFromAPI,
                "All available business units are not displayed in the dropdown" +
                        "\nBusiness units displayed: " + businessUnitsDisplayed +
                        "\nExpected business units: " + namesOfBusinessUnitsFromAPI
        );

        assignBusinessUnitModal.closeDropdownMenu();
    }

    @Test(priority = 4, description = "C243433 - Search functionality works.")
    public void PPV_BulkAssignBusinessUnit_SearchFunctionalityWorksAsExpected() throws Exception {
        var namesOfBusinessUnitsFromAPI = idsAndNamesOfBUsFromAPI.values().stream().map(String::toLowerCase).collect(Collectors.toList());

        assignBusinessUnitModal = openModalIfNeeded();
        assignBusinessUnitModal.closeDropdownMenu();

        var softAssert = new SoftAssert();
        var searchTerm = "McCormick";
        verifySearchInputWorks(searchTerm, namesOfBusinessUnitsFromAPI, softAssert);

        searchTerm = "MCCORMICK";
        verifySearchInputWorks(searchTerm, namesOfBusinessUnitsFromAPI, softAssert);

        searchTerm = "mccormick";
        verifySearchInputWorks(searchTerm, namesOfBusinessUnitsFromAPI, softAssert);

        // Negative test - search for a business unit that does not exist
        searchTerm = "QA Not A Business Unit";
        assignBusinessUnitModal.searchForBusinessUnit(searchTerm);
        List<String> businessUnitsDisplayed = assignBusinessUnitModal.singleSelectDropdown.getDropdownOptions();
        softAssert.assertTrue(businessUnitsDisplayed.isEmpty(), "Some business units were displayed when searching for a business unit that does not exist");

        softAssert.assertAll();
    }

    @Test(priority = 5, description = "C243434 - Error message displays if user tries to assign a business unit that does not exist")
    public void PPV_BulkAssignBusinessUnit_CannotAssignNonExistentBusinessUnit() throws Exception {
        if (productsPage.isAssignBusinessUnitModalDisplayed()) {
            productsPage = assignBusinessUnitModal.clickCancelButton();
        }

        assignBusinessUnitModal = openModalIfNeeded();

        var searchTerm = "Invalid Business Unit QA";
        assignBusinessUnitModal.searchForBusinessUnit(searchTerm);

        try {
            assignBusinessUnitModal.clickSaveButton();
        } catch (Exception ignored) {
        }

        var expectedErrorMessage = "The provided request parameters were invalid.";
        var errorIsDisplayed = assignBusinessUnitModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, expectedErrorMessage);
        Assert.assertTrue(errorIsDisplayed, "Error message was not displayed when trying to assign a business unit that does not exist");

        var modalIsOpen = productsPage.isAssignBusinessUnitModalDisplayed();
        Assert.assertTrue(modalIsOpen, "Modal was closed after trying to assign a business unit that does not exist");
    }

    @Test(priority = 6, description = "C241836 - BUs are assigned to products if they are retailer/retailer-campaign. RPC is not removed from products that have an RPC assigned.")
    public void PPV_BulkAssignBusinessUnit_CanAssignBusinessUnit() throws Exception {
        List<HashMap.Entry<String, String>> entryList = new ArrayList<>(idsAndNamesOfBUsFromAPI.entrySet());
        var randomIndex = SharedMethods.getRandomNumber(entryList.size() - 1);
        var businessUnitToSelect = entryList.get(randomIndex);

        setupProducts(businessUnitToSelect.getKey());
        productsPage = productsPage.refreshPage(ProductsPage.class);
        assignBusinessUnitModal = openModalIfNeeded();

        assignBusinessUnitModal.searchForBusinessUnit(businessUnitToSelect.getValue());
        assignBusinessUnitModal.singleSelectDropdown.selectOption(businessUnitToSelect.getValue());
        productsPage = assignBusinessUnitModal.clickSaveButton();

        // Note type will change when CSCAT-223 is being worked on
        var isSuccessMessageDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Business units have been applied to " + PRODUCTS_TO_SELECT.size() + " products.");
        Assert.assertTrue(isSuccessMessageDisplayed, "Success message was not displayed after assigning a business unit to products");

        productsPage = productsPage.refreshPage(ProductsPage.class); // Remove this when CSCAT-223 is being worked on
        productsPage.closeContentSuggestionsBanner();
        productsPage.searchByProductIdentifier(PRODUCT_PREFIX);

        verifyBusinessUnitWasAssignedToCorrectProducts(BASE_PRODUCT, "", false);
        verifyBusinessUnitWasAssignedToCorrectProducts(RETAILER_PRODUCT_NO_RPC, businessUnitToSelect.getKey(), false);
        verifyBusinessUnitWasAssignedToCorrectProducts(RETAILER_CAMPAIGN_PRODUCT_WITH_RPC, businessUnitToSelect.getKey(), true);
        verifyBusinessUnitWasAssignedToCorrectProducts(CAMPAIGN_PRODUCT, "", false);
        verifyBusinessUnitWasAssignedToCorrectProducts(RETAILER_PRODUCT_WITH_RPC, businessUnitToSelect.getKey(), true);
        verifyBusinessUnitWasAssignedToCorrectProducts(RETAILER_CAMPAIGN_PRODUCT_NO_RPC, businessUnitToSelect.getKey(), false);
    }

    private void verifyBusinessUnitWasAssignedToCorrectProducts(UserFriendlyInstancePath instancePath, String expectedBusinessUnitId, boolean rpcShouldHaveValue) {
        var instanceId = productsPage.getInstanceId(instancePath);
        var businessUnitCellValue = productsPage.getCellValues(instanceId, "Business Units").stream().findFirst().orElse("");
        var rpcCellValue = productsPage.getCellValues(instanceId, "RPC").stream().findFirst().orElse("");

        if (expectedBusinessUnitId.isEmpty()) {
            Assert.assertTrue(
                    businessUnitCellValue.isEmpty(),
                    "Business unit was assigned to a product that should not have a business unit assigned." +
                            "\nProduct version: " +  instancePath.toString()
            );
            return;
        }

        Assert.assertEquals(
                businessUnitCellValue,
                expectedBusinessUnitId,
                "Business unit was not assigned to product with version " + instancePath.toString()
        );

        if (rpcShouldHaveValue) {
            Assert.assertFalse(rpcCellValue.isEmpty(),"RPC was removed from product " + instancePath);
        }
    }

    private ProductsPage.AssignBusinessUnitModal openModalIfNeeded() throws InterruptedException {
        if (!productsPage.isAssignBusinessUnitModalDisplayed()) {
            productsPage.closeContentSuggestionsBanner().deselectAllProducts();
            productsPage.searchByProductIdentifier(PRODUCT_PREFIX);
            bottomActionBar = productsPage.selectProducts(PRODUCTS_TO_SELECT);
            assignBusinessUnitModal = bottomActionBar.clickEditAssignBusinessUnitButton();
        }
        return assignBusinessUnitModal;
    }

    private void verifySearchInputWorks(String searchTerm, List<String> businessUnitsFromAPI, SoftAssert softAssert) {
        assignBusinessUnitModal.searchForBusinessUnit(searchTerm);
        List<String> businessUnitsDisplayed = assignBusinessUnitModal.singleSelectDropdown.getDropdownOptions().stream().map(String::toLowerCase).collect(Collectors.toList());

        String searchTermInLowerCase = searchTerm.toLowerCase();
        List<String> expectedBusinessUnits = businessUnitsFromAPI.stream().filter(bu -> bu.contains(searchTermInLowerCase)).collect(Collectors.toList());
        softAssert.assertEqualsNoOrder(
                businessUnitsDisplayed.toArray(),
                expectedBusinessUnits.toArray(),
                "Business units displayed do not match expected business units" +
                        "\nSearch term: " + searchTerm +
                        "\nBusiness units displayed: " + businessUnitsDisplayed +
                        "\nExpected business units: " + expectedBusinessUnits
        );
    }

    private void setupProducts(String idOfBUToBeSelected) throws Exception {
        var instancePathBase = RETAILER_PRODUCT_NO_RPC.convertToInstancePathBase(company, jwt);
        ProductVersioningApiService.updateInstanceRPCAndBusinessUnits(instancePathBase, null, null, jwt);

        instancePathBase = RETAILER_CAMPAIGN_PRODUCT_WITH_RPC.convertToInstancePathBase(company, jwt);
        var businessUnitId = idsAndNamesOfBUsFromAPI.entrySet().stream().filter(entry -> entry.getValue().equals("McCormick US")).findFirst().orElseThrow().getKey();
        ProductVersioningApiService.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry("RPC123456", true), Map.entry(Collections.singletonList(businessUnitId), true), jwt);

        instancePathBase = RETAILER_PRODUCT_WITH_RPC.convertToInstancePathBase(company, jwt);
        ProductVersioningApiService.updateInstanceRPCAndBusinessUnits(instancePathBase, Map.entry("RPC6789", true), Map.entry(Collections.singletonList(idOfBUToBeSelected), true), jwt);

        instancePathBase = RETAILER_CAMPAIGN_PRODUCT_NO_RPC.convertToInstancePathBase(company, jwt);
        ProductVersioningApiService.updateInstanceRPCAndBusinessUnits(instancePathBase, null, null, jwt);
    }
}
