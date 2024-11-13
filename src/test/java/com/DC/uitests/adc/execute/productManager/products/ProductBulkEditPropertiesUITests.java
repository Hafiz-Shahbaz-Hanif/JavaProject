package com.DC.uitests.adc.execute.productManager.products;

import com.DC.objects.productVersioning.ProductInstanceUIData;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.BulkEditProductPropertiesPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.GenericMultiListModal;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.*;

import static com.DC.constants.InsightsConstants.*;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.constants.ProductVersioningConstants.*;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;

public class ProductBulkEditPropertiesUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private ProductsPage productsPage;
    private BulkEditProductPropertiesPage bulkEditProductPropertiesPage;
    private GenericMultiListModal genericMultiListModal;
    private final String PRODUCT_PREFIX = "QA-BULK-EDIT-";
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_1 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "es-MX", "Amazon.com", null);
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_2 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "en-US", null, null);
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_3 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", "Amazon.com", "Halloween");
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_4 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", null, "Christmas");
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_5 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "003", "en-US", "Amazon.com", null);

    private String uniqueIdOfInstance1;
    private String uniqueIdOfInstance2;
    private String uniqueIdOfInstance3;
    private String uniqueIdOfInstance4;
    private String uniqueIdOfInstance5;
    private List<String> instanceUniqueIdsToSelect;
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
        uniqueIdOfInstance1 = getInstanceUniqueId(company, PRODUCT_INSTANCE_1);
        uniqueIdOfInstance2 = getInstanceUniqueId(company, PRODUCT_INSTANCE_2);
        uniqueIdOfInstance3 = getInstanceUniqueId(company, PRODUCT_INSTANCE_3);
        uniqueIdOfInstance4 = getInstanceUniqueId(company, PRODUCT_INSTANCE_4);

        instanceUniqueIdsToSelect = new ArrayList<>(asList(uniqueIdOfInstance1, uniqueIdOfInstance2, uniqueIdOfInstance3, uniqueIdOfInstance4));
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        productsPage = productsPage.recoverPageIfBlankPageIsDisplayed(INSIGHTS_PRODUCTS_URL, productsPage);
        productsPage.closeReactModalIfDisplayed();
        if (driver.getTitle().contains("Products")) {
            productsPage.tableCommonFeatures.deselectAll();
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        try {
            ProductVersioningApiService.deleteProductMasterByUniqueId(PRODUCT_INSTANCE_5.productIdentifier, jwt);
        } catch (Exception ignored) {
        } finally {
            quitBrowser();
        }
    }

    @Test(priority = 1, description = "Clicking Edit > Properties takes the user to the bulk edit experience." +
            "The properties displayed on the main Products grid also show up in the edit mode" +
            "Selected products show up in the edit mode"
    )
    public void CGEN_BulkEditProperties_ClickingEditPropertiesReturnsCorrectPageAndData() throws InterruptedException {
        // Change columns before clicking edit properties button
        SoftAssert softAssert = new SoftAssert();

        List<String> columnsToDisplay = new LinkedList<>(asList("test property 1", "test property 2", "test property 3"));

        genericMultiListModal = productsPage.openModalToEditColumns();
        genericMultiListModal.moveAllOptionsToTheLeft();
        genericMultiListModal.moveOptionsToTheRight(columnsToDisplay);
        productsPage = genericMultiListModal.updateChanges(ProductsPage.class);

        // TESTING BULK EDIT EXPERIENCE IS DISPLAYED AFTER SELECTING PRODUCTS
        var columnsInProductsGrid = productsPage.tableCommonFeatures.getColumnsDisplayed();

        List<ProductInstanceUIData> productsDisplayed = productsPage.getProductsDisplayed();
        ProductsPage.BottomActionBar bottomActionBar = productsPage.selectAllProductsOnPage();
        int expectedNumberOfProducts = bottomActionBar.getNumberOfSelectedProducts();

        try {
            bulkEditProductPropertiesPage = bottomActionBar.clickEditPropertiesButton();
        } catch (Exception e) {
            String msg = "Exception occurred while clicking edit properties button: " + e.getMessage();
            LOGGER.error(msg);
            Assert.fail(msg);
        }

        // TESTING COLUMNS DISPLAYED IN BULK EDIT PAGE ARE THE SAME AS THE COLUMNS DISPLAYED IN THE MAIN PRODUCTS GRID
        List<String> columnsInEditBulkPage = bulkEditProductPropertiesPage.tableCommonFeatures.getColumnsDisplayed();
        softAssert.assertEquals(
                columnsInProductsGrid,
                columnsInEditBulkPage,
                "Columns displayed in the edit bulk page are not the same as the columns displayed in the main Products grid"
        );

        int numberOfProductsInBulkEditPage = bulkEditProductPropertiesPage.getNumberDisplayedNextToSearchInput();

        softAssert.assertEquals(
                expectedNumberOfProducts,
                numberOfProductsInBulkEditPage,
                "Number of products displayed in the bulk edit page is not the same as the number of products selected"
        );

        // TESTING SUBMIT BUTTON IS DISABLED
        boolean submitButtonEnabled = bulkEditProductPropertiesPage.isSubmitButtonEnabled();
        softAssert.assertFalse(submitButtonEnabled, "Submit button is enabled when no changes are made");

        // TESTING SELECTED PRODUCTS ARE DISPLAYED IN BULK EDIT PAGE
        List<ProductInstanceUIData> productsDisplayedInBulkEditPage = bulkEditProductPropertiesPage.getProductsDisplayed();
        softAssert.assertEquals(
                productsDisplayedInBulkEditPage,
                productsDisplayed,
                "Products displayed in the bulk edit page are not the same as the products selected"
        );

        softAssert.assertAll();
    }

    @Test(priority = 3, description = "C244428. Modal works and only selected properties are displayed in the edit bulk page")
    public void CGEN_BulkEditProperties_ManageColumnsWorksAsExpected() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);

        // TESTING UPDATE BUTTON IS DISABLED WHEN NO COLUMNS SELECTED
        genericMultiListModal = bulkEditProductPropertiesPage.clickManageColumnsButton();
        genericMultiListModal.moveAllOptionsToTheLeft();
        boolean updateColumnsButtonEnabled = genericMultiListModal.isUpdateButtonEnabled();
        Assert.assertFalse(updateColumnsButtonEnabled, "Update button is enabled when no columns are selected");

        // TESTING SEARCH FUNCTIONALITY
        String searchTerm = "test property";
        genericMultiListModal.searchForOption(searchTerm);

        List<String> unselectedOptionsAfterSearch = genericMultiListModal.getUnselectedOptions();
        boolean allUnselectedPropertiesContainSearchTerm = unselectedOptionsAfterSearch.stream().allMatch(option -> option.toLowerCase().contains(searchTerm));
        Assert.assertTrue(allUnselectedPropertiesContainSearchTerm, "Not all unselected properties contain the search term");

        // TESTING UPDATE BUTTON
        genericMultiListModal.moveAllOptionsToTheRight();
        List<String> expectedColumns = genericMultiListModal.getSelectedOptions();
        var columnToMove = expectedColumns.get(1);
        genericMultiListModal.reorderOption(columnToMove, 1);

        List<String> selectedOptions = genericMultiListModal.getSelectedOptions();

        expectedColumns.remove(columnToMove);
        expectedColumns.add(0, columnToMove);

        Assert.assertEquals(selectedOptions, expectedColumns, "Columns are not ordered as expected in Manage Columns modal");

        bulkEditProductPropertiesPage = genericMultiListModal.updateChanges(BulkEditProductPropertiesPage.class);
        List<String> columnsInBulkEditPage = bulkEditProductPropertiesPage.getNonDefaultColumnsDisplayed();

        Assert.assertEquals(
                expectedColumns,
                columnsInBulkEditPage,
                "Columns displayed in the edit bulk page are not the same as the selected columns in the manage columns modal"
        );
    }

    @Test(priority = 4, description = "Search input works as expected")
    public void CGEN_BulkEditProperties_CanSearchForAPropertyValue() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);

        String propertyValueToSearch = "Value";
        bulkEditProductPropertiesPage = bulkEditProductPropertiesPage.searchForPropertyValues(propertyValueToSearch);

        //TODO - finish this when https://flywheeldigital.atlassian.net/browse/PPV-327 is done
    }

    @Test(priority = 5, description = "If property is multi value, + icon is displayed in the cell")
    public void CGEN_BulkEditProperties_CorrectIconsAreDisplayedInCell() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);
        bulkEditProductPropertiesPage = showColumnsRequiredForTest();
        // MULTI VALUE PROPERTY HAS + ICON
        bulkEditProductPropertiesPage.clickEditIcon(uniqueIdOfInstance1, MULTI_VALUE_PROPERTY_NAME);
        verifyCorrectCellIconsAreDisplayed(uniqueIdOfInstance1, MULTI_VALUE_PROPERTY_NAME, true);

        // SINGLE VALUE PROPERTY DOESN'T HAVE + ICON
        bulkEditProductPropertiesPage.clickEditIcon(uniqueIdOfInstance1, NUMBER_PROPERTY_NAME);
        verifyCorrectCellIconsAreDisplayed(uniqueIdOfInstance1, NUMBER_PROPERTY_NAME, false);

        // EDIT ICONS FOR BU, PREVIOUS RPCS, PRODUCT NAME AND RPC ARE DISABLED FOR INVALID PRODUCT
        HashMap<String, Boolean> expectedPropertiesAndStatus = new HashMap<>();
        expectedPropertiesAndStatus.put(BUSINESS_UNITS_PROPERTY_NAME, true);
        expectedPropertiesAndStatus.put(PREVIOUS_RPCS_PROPERTY_NAME, false);
        expectedPropertiesAndStatus.put(PRODUCT_NAME_PROPERTY_NAME, true);
        expectedPropertiesAndStatus.put(RPC_PROPERTY_NAME, true);
        verifyCorrectCellIconsAreEnabled(uniqueIdOfInstance1, expectedPropertiesAndStatus);
        verifyCorrectCellIconsAreEnabled(uniqueIdOfInstance3, expectedPropertiesAndStatus);

        expectedPropertiesAndStatus.clear();
        expectedPropertiesAndStatus.put(BUSINESS_UNITS_PROPERTY_NAME, false);
        expectedPropertiesAndStatus.put(PREVIOUS_RPCS_PROPERTY_NAME, false);
        expectedPropertiesAndStatus.put(PRODUCT_NAME_PROPERTY_NAME, true);
        expectedPropertiesAndStatus.put(RPC_PROPERTY_NAME, false);
        verifyCorrectCellIconsAreEnabled(uniqueIdOfInstance2, expectedPropertiesAndStatus);
        verifyCorrectCellIconsAreEnabled(uniqueIdOfInstance4, expectedPropertiesAndStatus);
    }

    @Test(priority = 6, description = "Close cell edit mode icon only affects that cell")
    public void CGEN_BulkEditProperties_CancelingAChangeAffectsOnlyOneCell() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);
        bulkEditProductPropertiesPage = showColumnsRequiredForTest();
        SoftAssert softAssert = new SoftAssert();

        List<String> cellValuesBefore = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);
        bulkEditProductPropertiesPage.clickEditIcon(uniqueIdOfInstance2, MULTI_VALUE_PROPERTY_NAME);
        bulkEditProductPropertiesPage.clickEditIcon(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);

        int cellInEditModeBefore = bulkEditProductPropertiesPage.getNumberOfCellsInEditMode();
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME, String.valueOf(SharedMethods.generateRandomNumber()));
        bulkEditProductPropertiesPage.clickCloseIcon(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);
        List<String> cellValuesAfter = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);
        softAssert.assertEquals(
                cellValuesBefore,
                cellValuesAfter,
                "Value of cell " + NUMBER_PROPERTY_NAME + " on product version " + uniqueIdOfInstance2 + " was changed after clicking cancel button"
        );
        boolean editIconDisplayed = bulkEditProductPropertiesPage.isEditIconDisplayed(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);
        softAssert.assertTrue(editIconDisplayed, "Edit icon is not displayed for property " + NUMBER_PROPERTY_NAME + " on product version" + uniqueIdOfInstance2);
        int cellInEditModeAfter = bulkEditProductPropertiesPage.getNumberOfCellsInEditMode();
        softAssert.assertEquals(cellInEditModeAfter, cellInEditModeBefore - 1, "Number of cells in edit mode is not correct");

        softAssert.assertAll();
    }

    @Test(priority = 7, description = "Editing a property makes that cell highlight in yellow." +
            "Submit button updates the product instances with the new values"
    )
    public void CGEN_BulkEditProperties_EditingPropertiesHighlightsAndUpdatesCorrectProductInstance() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);
        bulkEditProductPropertiesPage = showColumnsRequiredForTest();

        // Get option to select in image mapping property
        var currentBusinessUnitInInstance = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance2, IMAGE_MAPPING_PROPERTY_NAME);
        String optionToSelectInPropertyMapping;
        if (currentBusinessUnitInInstance.isEmpty() | (!currentBusinessUnitInInstance.isEmpty() && currentBusinessUnitInInstance.get(0).equals("Optional"))) {
            optionToSelectInPropertyMapping = "Required";
        } else {
            optionToSelectInPropertyMapping = "Optional";
        }

        SoftAssert softAssert = new SoftAssert();

        // EDITING A PROPERTY HIGHLIGHTS ONLY THAT CELL
        var currentBooleanValues = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance1, BOOLEAN_PROPERTY_NAME);
        var booleanValueToSelect = currentBooleanValues.isEmpty() ? "false" : currentBooleanValues.get(0).equals("true") ? "False" : "True";

        var currentDropdownValues = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance1, DROPDOWN_PROPERTY_NAME);
        var dropdownValueToSelect = currentDropdownValues.isEmpty() ? "value 1" : currentDropdownValues.get(0).equals("value 1") ? "value 2" : "value 1";
        var dateToSelect = DateUtility.formattingDate(DateUtility.getYesterday());

        Map<String, List<String>> instanceAndColumnNames = new HashMap<>() {{
            put(uniqueIdOfInstance1, Arrays.asList(MULTI_VALUE_PROPERTY_NAME, NUMBER_PROPERTY_NAME, DATE_PROPERTY_NAME, BOOLEAN_PROPERTY_NAME, SINGLE_VALUE_PROPERTY_NAME, DROPDOWN_PROPERTY_NAME, LINK_PROPERTY_NAME, HTML_PROPERTY_NAME, RICH_TEXT_PROPERTY_NAME));
            put(uniqueIdOfInstance2, Arrays.asList(MULTI_VALUE_PROPERTY_NAME, NUMBER_PROPERTY_NAME, IMAGE_MAPPING_PROPERTY_NAME, DIGITAL_ASSET_PROPERTY_NAME));
        }};

        bulkEditProductPropertiesPage.clickEditIconsIfDisplayed(instanceAndColumnNames);

        int highlightedCellsBefore = bulkEditProductPropertiesPage.getNumberOfHighlightedCells();

        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, SINGLE_VALUE_PROPERTY_NAME, "QA Value " + SharedMethods.generateRandomNumber());
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, NUMBER_PROPERTY_NAME, String.valueOf(SharedMethods.generateRandomNumber()));
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, DATE_PROPERTY_NAME, dateToSelect);
        bulkEditProductPropertiesPage.selectValueFromDropdown(uniqueIdOfInstance1, BOOLEAN_PROPERTY_NAME, booleanValueToSelect);
        bulkEditProductPropertiesPage.selectValueFromDropdown(uniqueIdOfInstance1, DROPDOWN_PROPERTY_NAME, dropdownValueToSelect);
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, LINK_PROPERTY_NAME, "https://www.google.com");
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, HTML_PROPERTY_NAME, "QA Value " + SharedMethods.generateRandomNumber());
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance1, RICH_TEXT_PROPERTY_NAME, "QA Value " + SharedMethods.generateRandomNumber());

        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME, String.valueOf(SharedMethods.generateRandomNumber()));
        bulkEditProductPropertiesPage.editCellValue(uniqueIdOfInstance2, IMAGE_MAPPING_PROPERTY_NAME, optionToSelectInPropertyMapping);

        var imagePath = System.getProperty("user.dir") + "/src/test/java/com/DC/utilities/samples/images/no-remove.jpg";
        bulkEditProductPropertiesPage.uploadDigitalAsset(uniqueIdOfInstance2, DIGITAL_ASSET_PROPERTY_NAME, imagePath);

        editProductName(uniqueIdOfInstance1);
        editProductName(uniqueIdOfInstance2);
        editProductName(uniqueIdOfInstance3);
        editProductName(uniqueIdOfInstance4);

        bulkEditProductPropertiesPage.clickSaveValueIcon(uniqueIdOfInstance1, SINGLE_VALUE_PROPERTY_NAME);

        // TODO - uncomment when https://flywheeldigital.atlassian.net/browse/PPV-327 is done
         /*int highlightedCellsAfter = bulkEditProductPropertiesPage.getNumberOfHighlightedCells();
        softAssert.assertEquals(highlightedCellsAfter, highlightedCellsBefore + 1, "Number of highlighted cells is not correct");

        boolean isCellHighlighted = bulkEditProductPropertiesPage.isCellHighlighted(uniqueIdOfInstance1, SINGLE_VALUE_PROPERTY_NAME);
        softAssert.assertTrue(isCellHighlighted, "Cell " + SINGLE_VALUE_PROPERTY_NAME + " on product version " + uniqueIdOfInstance1 + " is not highlighted");

        isCellHighlighted = bulkEditProductPropertiesPage.isCellHighlighted(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME);
        softAssert.assertFalse(isCellHighlighted, "Cell " + NUMBER_PROPERTY_NAME + " on product version " + uniqueIdOfInstance2 + " is highlighted"); */


        //bulkEditProductPropertiesPage.clickSaveValueIcon(uniqueIdOfInstance2, NUMBER_PROPERTY_NAME); TODO - uncomment when https://flywheeldigital.atlassian.net/browse/PPV-327 is done

        LinkedHashMap<String, List<String>> propertiesAfterInstance1 = bulkEditProductPropertiesPage.getAllCellValuesFromInstance(uniqueIdOfInstance1);
        LinkedHashMap<String, List<String>> propertiesAfterInstance2 = bulkEditProductPropertiesPage.getAllCellValuesFromInstance(uniqueIdOfInstance2);
        LinkedHashMap<String, List<String>> propertiesAfterInstance3 = bulkEditProductPropertiesPage.getAllCellValuesFromInstance(uniqueIdOfInstance3);
        LinkedHashMap<String, List<String>> propertiesAfterInstance4 = bulkEditProductPropertiesPage.getAllCellValuesFromInstance(uniqueIdOfInstance4);

        var dateInInstance1 = propertiesAfterInstance1.get(DATE_PROPERTY_NAME);
        softAssert.assertEquals(dateInInstance1.get(0), dateToSelect, "Date picker chosen date is not correct");

        var formattedDate = java.time.LocalDate.parse(dateToSelect, ofPattern("MM/dd/yyyy")).format(ofPattern("yyyy-MM-dd")) + "T00:00:00.000Z";
        dateInInstance1.set(0, formattedDate);

        // CLICKING SUBMIT BUTTON UPDATES THE PRODUCT INSTANCES WITH THE NEW VALUES
        productsPage = bulkEditProductPropertiesPage.clickSubmitButton((ProductsPage.class));

        LinkedHashMap<String, List<String>> propertiesOfInstance1InProductsPage = productsPage.getAllCellValuesFromInstance(uniqueIdOfInstance1);
        LinkedHashMap<String, List<String>> propertiesOfInstance2InProductsPage = productsPage.getAllCellValuesFromInstance(uniqueIdOfInstance2);
        LinkedHashMap<String, List<String>> propertiesOfInstance3InProductsPage = productsPage.getAllCellValuesFromInstance(uniqueIdOfInstance3);
        LinkedHashMap<String, List<String>> propertiesOfInstance4InProductsPage = productsPage.getAllCellValuesFromInstance(uniqueIdOfInstance4);

        softAssert.assertEquals(propertiesOfInstance1InProductsPage, propertiesAfterInstance1, "Properties of instance " + uniqueIdOfInstance1 + " were not saved");
        softAssert.assertEquals(propertiesOfInstance2InProductsPage, propertiesAfterInstance2, "Properties of instance " + uniqueIdOfInstance2 + " were not saved");
        softAssert.assertEquals(propertiesOfInstance3InProductsPage, propertiesAfterInstance3, "Properties of instance " + uniqueIdOfInstance3 + " were not saved");
        softAssert.assertEquals(propertiesOfInstance4InProductsPage, propertiesAfterInstance4, "Properties of instance " + uniqueIdOfInstance4 + " were not saved");
        softAssert.assertAll();
    }

    @Test(priority = 8, description = "Verify RPC and BUs")
    public void CGEN_BulkEditProperties_RPCAndBusinessUnitsCanBeUpdated() throws Exception {
        ProductVersioningApiService.createProductVersionIfNotExistent(PRODUCT_INSTANCE_5, jwt);

        if (driver.getTitle().contains("Bulk Edit")) {
            productsPage = bulkEditProductPropertiesPage.clickCancelButton(ProductsPage.class);
        } else {
            productsPage = productsPage.refreshPage(ProductsPage.class);
        }

        productsPage.searchByProductIdentifier(PRODUCT_INSTANCE_5.productIdentifier);
        uniqueIdOfInstance5 = getInstanceUniqueId(company, PRODUCT_INSTANCE_5);
        productsPage.closeContentSuggestionsBanner().deselectAllProducts();
        performTestToEditRPCAndBusinessUnit(uniqueIdOfInstance5, true, true); // this is to test that the RPC and BU can be edited for a product that has no previous RPCs
        performTestToEditRPCAndBusinessUnit(uniqueIdOfInstance5, true, true); // this is to test that the RPC and BU can be edited for a product that has previous RPCs
        performTestToEditRPCAndBusinessUnit(uniqueIdOfInstance5, true, false); // this is to test that the BU is not removed when the RPC is edited
        performTestToEditRPCAndBusinessUnit(uniqueIdOfInstance5, false, true); // this is to test that the RPC is not removed when the BU is edited
    }

    @Test(priority = 9)
    public void CGEN_BulkEditProperties_CancelButtonTakesToProductsPage() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);

        try {
            productsPage = bulkEditProductPropertiesPage.clickCancelButton(ProductsPage.class);
        } catch (Exception e) {
            String msg = "Exception occurred after clicking cancel button: " + e.getMessage();
            LOGGER.error(msg);
            Assert.fail(msg);
        }
    }

    @Test(priority = 10, description = "Merge 5 properties cannot be edited")
    public void CGEN_BulkEditProperties_Merge5PropertiesCannotBeEdited() throws InterruptedException {
        bulkEditProductPropertiesPage = goToEditBulkPropertiesIfNeeded(instanceUniqueIdsToSelect);
        genericMultiListModal = bulkEditProductPropertiesPage.clickManageColumnsButton();
        genericMultiListModal.moveAllOptionsToTheLeft();
        genericMultiListModal.moveOptionsToTheRight(MERGE_5_PROPERTIES);
        bulkEditProductPropertiesPage = genericMultiListModal.updateChanges(BulkEditProductPropertiesPage.class);

        for (var property : MERGE_5_PROPERTIES) {
            var editIconEnabled = bulkEditProductPropertiesPage.isEditIconEnabled(instanceUniqueIdsToSelect.get(0), property);
            Assert.assertFalse(editIconEnabled, "Edit icon was enabled for merge 5 property: " + property);
        }
    }

    private void performTestToEditRPCAndBusinessUnit(String uniqueIdOfInstanceToTest, boolean editRPC, boolean editBU) throws InterruptedException {
        bulkEditProductPropertiesPage = productsPage.selectProductsByInstanceUniqueId(Collections.singletonList(uniqueIdOfInstanceToTest)).clickEditPropertiesButton();

        bulkEditProductPropertiesPage = showColumnsRequiredForTest();

        SoftAssert softAssert = new SoftAssert();

        String oldRPC = "";
        if (editRPC && !editBU) {
            oldRPC = editRPCAndSave(uniqueIdOfInstanceToTest);
        } else if (!editRPC && editBU) {
            editBusinessUnitAndSave(uniqueIdOfInstanceToTest);
        } else {
            oldRPC = editRPCAndSave(uniqueIdOfInstanceToTest);
            editBusinessUnitAndSave(uniqueIdOfInstanceToTest);
        }

        LinkedHashMap<String, List<String>> expectedPropertiesAndValues = bulkEditProductPropertiesPage.getAllCellValuesFromInstance(uniqueIdOfInstanceToTest);

        if (!oldRPC.isEmpty()) {
            expectedPropertiesAndValues.get(PREVIOUS_RPCS_PROPERTY_NAME).add(oldRPC);
        }

        productsPage = bulkEditProductPropertiesPage.clickSubmitButton((ProductsPage.class));
        var successMessageDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Properties Updated! You will be redirected shortly.");
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed");

        var attempts = 2;
        for (int i = 1; i <= attempts; i++) {
            productsPage = productsPage.refreshPage(ProductsPage.class);
            productsPage.scrollToInstance(uniqueIdOfInstanceToTest);
            LinkedHashMap<String, List<String>> propertiesOfInstanceInProductsPage = productsPage.getAllCellValuesFromInstance(uniqueIdOfInstanceToTest);
            if (propertiesOfInstanceInProductsPage.equals(expectedPropertiesAndValues)) {
                break;
            } else if (i == attempts) {
                softAssert.fail("Properties of instance " + uniqueIdOfInstanceToTest + " were not saved correctly" +
                        "\nProperties in Products page: " + propertiesOfInstanceInProductsPage +
                        "\nExpected properties: " + expectedPropertiesAndValues);
            }
        }
        softAssert.assertAll();
    }

    private String editRPCAndSave(String uniqueIdOfInstance) throws InterruptedException {
        var currentRPCCellValues = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance, RPC_PROPERTY_NAME);
        var currentRPC = currentRPCCellValues.isEmpty() ? "" : currentRPCCellValues.get(0);
        bulkEditProductPropertiesPage.clickEditIconAndEditCellValue(uniqueIdOfInstance, RPC_PROPERTY_NAME, SharedMethods.generateRandomString("RPC"));
        bulkEditProductPropertiesPage.clickSaveValueIcon(uniqueIdOfInstance, RPC_PROPERTY_NAME);
        return currentRPC;
    }

    private void editBusinessUnitAndSave(String uniqueIdOfInstance) throws InterruptedException {
        var currentBusinessUnitInInstance = bulkEditProductPropertiesPage.getCellValues(uniqueIdOfInstance, BUSINESS_UNITS_PROPERTY_NAME);
        var businessUnitToSelect = getBusinessUnitToSelect(currentBusinessUnitInInstance);
        bulkEditProductPropertiesPage.clickEditIconAndSelectValueFromDropdown(uniqueIdOfInstance, BUSINESS_UNITS_PROPERTY_NAME, businessUnitToSelect);
        bulkEditProductPropertiesPage.clickSaveValueIcon(uniqueIdOfInstance, BUSINESS_UNITS_PROPERTY_NAME);
    }

    private void editProductName(String uniqueIdOfInstance) throws InterruptedException {
        var rowId = bulkEditProductPropertiesPage.getRowId(uniqueIdOfInstance);
        var info = bulkEditProductPropertiesPage.getProductInstanceUIData(rowId);
        var productName = info.productIdentifier + " " + info.localeName + " " + info.version + " " + SharedMethods.generateRandomNumber();
        bulkEditProductPropertiesPage.clickEditIconAndEditCellValue(uniqueIdOfInstance, PRODUCT_NAME_PROPERTY_NAME, productName);
    }

    private BulkEditProductPropertiesPage showColumnsRequiredForTest() throws InterruptedException {
        List<String> columnsToDisplay = new LinkedList<>(
                asList(BUSINESS_UNITS_PROPERTY_NAME,
                        PREVIOUS_RPCS_PROPERTY_NAME,
                        PRODUCT_NAME_PROPERTY_NAME,
                        RPC_PROPERTY_NAME,
                        MULTI_VALUE_PROPERTY_NAME,
                        NUMBER_PROPERTY_NAME,
                        SINGLE_VALUE_PROPERTY_NAME,
                        IMAGE_MAPPING_PROPERTY_NAME,
                        DIGITAL_ASSET_PROPERTY_NAME,
                        DATE_PROPERTY_NAME,
                        BOOLEAN_PROPERTY_NAME,
                        DROPDOWN_PROPERTY_NAME,
                        LINK_PROPERTY_NAME,
                        HTML_PROPERTY_NAME,
                        RICH_TEXT_PROPERTY_NAME
                )
        );

        var columnsDisplayed = bulkEditProductPropertiesPage.getNonDefaultColumnsDisplayed();

        if (!columnsDisplayed.equals(columnsToDisplay)) {
            genericMultiListModal = bulkEditProductPropertiesPage.clickManageColumnsButton();
            genericMultiListModal.moveAllOptionsToTheLeft();
            genericMultiListModal.moveOptionsToTheRight(columnsToDisplay);
            bulkEditProductPropertiesPage = genericMultiListModal.updateChanges(BulkEditProductPropertiesPage.class);
        }
        return bulkEditProductPropertiesPage;
    }

    private static String getBusinessUnitToSelect(List<String> currentBusinessUnitInInstance) {
        String businessUnitToSelect;
        if (currentBusinessUnitInInstance.isEmpty() | (!currentBusinessUnitInInstance.isEmpty() && currentBusinessUnitInInstance.get(0).equals("McCormick CA"))) {
            businessUnitToSelect = "McCormick US";
        } else {
            businessUnitToSelect = "McCormick CA";
        }
        return businessUnitToSelect;
    }

    private BulkEditProductPropertiesPage goToEditBulkPropertiesIfNeeded(List<String> instanceUniqueIdsToSelect) throws InterruptedException {
        if (!driver.getTitle().contains("Bulk Edit")) {
            productsPage.searchByProductIdentifier(PRODUCT_PREFIX);
            productsPage.closeContentSuggestionsBanner().deselectAllProducts();
            bulkEditProductPropertiesPage = productsPage.selectProductsByInstanceUniqueId(instanceUniqueIdsToSelect)
                    .clickEditPropertiesButton();
        } else {
            var instancesDisplayed = bulkEditProductPropertiesPage.getInstanceUniqueIdsDisplayed();
            if (!instancesDisplayed.containsAll(instanceUniqueIdsToSelect)) {
                productsPage = bulkEditProductPropertiesPage.clickCancelButton(ProductsPage.class);
                productsPage.closeContentSuggestionsBanner().deselectAllProducts();
                bulkEditProductPropertiesPage = productsPage.selectProductsByInstanceUniqueId(instanceUniqueIdsToSelect)
                        .clickEditPropertiesButton();
            }
        }
        return bulkEditProductPropertiesPage;
    }

    private String getInstanceUniqueId(Company company, UserFriendlyInstancePath userFriendlyInstancePath) throws Exception {
        String localeId = company.getLocaleId(userFriendlyInstancePath.localeName);
        String retailerId = company.getRetailerId(userFriendlyInstancePath.retailerName);
        String campaignId = company.getCampaignId(userFriendlyInstancePath.campaignName);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal instance =
                ProductVersioningApiService.getLiveProductInstanceByUniqueId(userFriendlyInstancePath.productIdentifier, localeId, campaignId, retailerId, jwt);

        return instance.uniqueId;
    }

    private void verifyCorrectCellIconsAreDisplayed(String instanceUniqueId, String propertyName, boolean isMultiValue) {
        SoftAssert softAssert = new SoftAssert();
        boolean plusIconDisplayed = bulkEditProductPropertiesPage.isPlusIconDisplayed(instanceUniqueId, propertyName);
        if (isMultiValue) {
            softAssert.assertTrue(plusIconDisplayed, "Plus icon is not displayed for property " + propertyName + " on product version" + instanceUniqueId);
        } else {
            softAssert.assertTrue(plusIconDisplayed, "Plus icon is displayed for property " + propertyName + " on product version" + instanceUniqueId);
        }
        boolean saveIconDisplayed = bulkEditProductPropertiesPage.isSaveIconDisplayed(instanceUniqueId, propertyName);
        softAssert.assertTrue(saveIconDisplayed, "Save icon is not displayed for property " + propertyName + " on product version" + instanceUniqueId);
        boolean closeIconDisplayed = bulkEditProductPropertiesPage.isCloseIconDisplayed(instanceUniqueId, propertyName);
        softAssert.assertTrue(closeIconDisplayed, "Close icon is not displayed for property " + propertyName + " on product version" + instanceUniqueId);
    }

    private void verifyCorrectCellIconsAreEnabled(String instanceUniqueId, HashMap<String, Boolean> propertyWithExpectedStatus) {
        SoftAssert softAssert = new SoftAssert();
        for (String propertyName : propertyWithExpectedStatus.keySet()) {
            boolean shouldBeEnabled = propertyWithExpectedStatus.get(propertyName);
            boolean editIconEnabled = bulkEditProductPropertiesPage.isEditIconEnabled(instanceUniqueId, propertyName);
            if (shouldBeEnabled) {
                softAssert.assertTrue(editIconEnabled, "Edit icon is not enabled for property " + propertyName + " on product version" + instanceUniqueId);
            } else {
                softAssert.assertTrue(editIconEnabled, "Edit icon is enabled for property " + propertyName + " on product version" + instanceUniqueId);
            }
        }
    }
}
