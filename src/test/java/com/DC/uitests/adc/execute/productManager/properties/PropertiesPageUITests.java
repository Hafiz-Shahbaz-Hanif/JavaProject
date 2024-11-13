package com.DC.uitests.adc.execute.productManager.properties;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.pageobjects.adc.execute.productManager.properties.AddPropertyModal;
import com.DC.pageobjects.adc.execute.productManager.properties.PropertiesPage;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ImportAssertions;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.XLUtils.CompanyPropertiesXLUtils;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.productManager.ProductVersioningCommonMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.stream.Collectors;

import static com.DC.constants.InsightsConstants.INSIGHTS_PROPERTIES_URL;
import static com.DC.constants.InsightsConstants.getProductDetailsUrl;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static java.util.Arrays.asList;

public class PropertiesPageUITests extends BaseClass {
    private final String CPG_USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String GROUP_TO_TEST = "Group 1";
    private final String PREFIX_PROPERTIES_TO_TEST_REARRANGE = "rearrange_";
    private final String PREFIX_PROPERTIES_TO_TEST = "Automated Property";
    private final String ID_PROPERTY_TO_MOVE = PREFIX_PROPERTIES_TO_TEST_REARRANGE + "1";
    private final String ID_REARRANGEMENT_PROPERTY = PREFIX_PROPERTIES_TO_TEST_REARRANGE + "2";
    private final UserFriendlyInstancePath PRODUCT_TO_TEST = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "es-MX", null, null);

    private final CompanyPropertiesBase.PropertyBase PROP_TO_ADD = new CompanyPropertiesBase.PropertyBase(
            null,
            PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
            Enums.PropertyType.STRING,
            "",
            false,
            GROUP_TO_TEST
    );

    private final CompanyPropertiesBase.PropertyBase SECOND_PROP_TO_ADD = new CompanyPropertiesBase.PropertyBase(
            null,
            PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
            Enums.PropertyType.DIGITAL_ASSET,
            "help text",
            true,
            null
    );

    private final CompanyPropertiesBase.PropertyBase THIRD_PROP_TO_ADD = new CompanyPropertiesBase.PropertyBase(
            null,
            PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
            Enums.PropertyType.STRING,
            "",
            false,
            null
    );

    private final CompanyPropertiesBase.PropertyBase PROP_FOR_GROUPS_TEST = new CompanyPropertiesBase.PropertyBase(
            null,
            PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
            Enums.PropertyType.STRING,
            "help text",
            false,
            "Group to delete"
    );

    private final CompanyPropertiesBase.PropertyBase SECOND_PROP_FOR_GROUPS_TEST = new CompanyPropertiesBase.PropertyBase(
            null,
            PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
            Enums.PropertyType.DIGITAL_ASSET,
            "help text",
            false,
            "Group to delete 2"
    );

    private final LinkedHashMap<CompanyPropertiesBase.PropertyBase, Boolean> propertiesToAdd = new LinkedHashMap<>();

    private PropertiesPage propertiesPage;
    private final List<String> propertyIdsToRemove = new ArrayList<>();
    private final List<String> propertyGroupsToRemove = new ArrayList<>();
    private final List<String> propertyDigitalAssetGroupsToRemove = new ArrayList<>();
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, false);
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(CPG_USERNAME, PASSWORD);
        driver.get(INSIGHTS_PROPERTIES_URL);
        propertiesPage = new PropertiesPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);

        propertiesToAdd.put(PROP_FOR_GROUPS_TEST, true);
        propertiesToAdd.put(SECOND_PROP_FOR_GROUPS_TEST, true);

        propertyGroupsToRemove.add(PROP_FOR_GROUPS_TEST.group);
        propertyDigitalAssetGroupsToRemove.add(SECOND_PROP_FOR_GROUPS_TEST.group);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupPropertiesAndGroups() {
        try {
            CompanyApiService.deleteRegularPropertiesFromCompany(propertyIdsToRemove, jwt);
            CompanyApiService.deleteDigitalAssetPropertyFromCompany(propertyIdsToRemove, jwt);

            CompanyApiService.deletePropertyGroupsFromCompany(propertyGroupsToRemove, false, jwt);
            CompanyApiService.deletePropertyGroupsFromCompany(propertyDigitalAssetGroupsToRemove, true, jwt);
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @AfterMethod()
    public void recoverPageIfNeeded() {
        if (!Objects.equals(driver.getCurrentUrl(), INSIGHTS_PROPERTIES_URL)) {
            propertiesPage = propertiesPage.navigateToUrl(INSIGHTS_PROPERTIES_URL, PropertiesPage.class);
        } else {
            propertiesPage = propertiesPage.refreshPage(PropertiesPage.class);
        }
        propertiesPage.waitForDOMStabilization();
    }

    @Test(priority = 1, description = "Correct columns are displayed: Property Id, Name, Help Text, Type, Allow Multiple")
    public void CGEN_CompanyProperties_CorrectTableHeadersDisplayed() {
        List<String> columnsDisplayed = propertiesPage.tableCommonFeatures.getColumnsDisplayed();
        List<String> expectedColumns = asList("Property Id", "Name", "Help Text", "Type", "Allow Multiple");

        Assert.assertEquals(columnsDisplayed, expectedColumns, "Columns displayed are not the same as the expected columns");
    }

    @Test(priority = 2, description = "Add button adds property without closing modal, Cancel button closes modal, and Add & Close button adds property and closes modal")
    public void CGEN_CompanyProperties_AddPropertyModalWorksAsExpected() throws Exception {
        var addPropertyModal = propertiesPage.openModalToAddProperty();
        verifyPropertyCannotBeAddedWithoutAllRequiredFields(PROP_TO_ADD.name, addPropertyModal);
        propertiesPage = addPropertyModal.cancelCreatingAProperty(PropertiesPage.class);

        addPropertyModal = propertiesPage.openModalToAddProperty();
        verifyAddPropertyButtonsWork(PROP_TO_ADD, SECOND_PROP_TO_ADD, addPropertyModal);

        verifyPropertyWasAdded(PROP_TO_ADD);
        verifyPropertyWasAdded(SECOND_PROP_TO_ADD);
    }

    @Test(priority = 3, description = "User cannot add or edit property with duplicate id or name")
    public void CGEN_CompanyProperties_CannotHaveDuplicateProperties() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        propertiesPage = addPropertiesIfNeeded(asList(PROP_TO_ADD, SECOND_PROP_TO_ADD));

        var addPropertyModal = propertiesPage.openModalToAddProperty();
        addPropertyModal.insertPropertyName(SECOND_PROP_TO_ADD.name);
        addPropertyModal.selectDataType(SECOND_PROP_TO_ADD.type.getPropertyTypeForUI());
        addPropertyModal.clickAddButton();

        var errorMessageDisplayed = addPropertyModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "\"" + SECOND_PROP_TO_ADD.name + "\" already exists.");
        softAssert.assertTrue(errorMessageDisplayed, "Error message was not displayed after adding property with duplicate id");

        propertiesPage = addPropertyModal.cancelCreatingAProperty(PropertiesPage.class);

        var columnToTest = "name";
        var nameToDuplicate = "Rearrange 1";
        propertiesPage.searchForProperty(PROP_TO_ADD.id);
        propertiesPage.clickEditIcon(PROP_TO_ADD.id, columnToTest);
        propertiesPage.editCellValue(PROP_TO_ADD.id, columnToTest, nameToDuplicate);
        propertiesPage.clickSaveValueIcon(PROP_TO_ADD.id, columnToTest);
        propertiesPage.clickApplyAllChangesButton();

        errorMessageDisplayed = propertiesPage.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "There was an issue saving the property edits.");
        softAssert.assertTrue(errorMessageDisplayed, "Error message was not displayed after editing property with duplicate name");

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "Can edit property name and help text")
    public void CGEN_CompanyProperties_CanEditProperties() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        propertiesPage = addPropertiesIfNeeded(asList(PROP_TO_ADD, SECOND_PROP_TO_ADD, THIRD_PROP_TO_ADD));

        var firstColumnToTest = "name";
        var secondColumnToTest = "helpText";

        propertiesPage.searchForProperty(PREFIX_PROPERTIES_TO_TEST);

        var firstPropertyOriginalInfo = propertiesPage.getPropertyData(PROP_TO_ADD.id);
        var secondPropertyOriginalInfo = propertiesPage.getPropertyData(SECOND_PROP_TO_ADD.id);
        var thirdPropertyOriginalInfo = propertiesPage.getPropertyData(THIRD_PROP_TO_ADD.id);

        propertiesPage.refreshPage(PropertiesPage.class); // Refreshing because inputs are not opening after clicking the edit icon in some products - this is happening only in automated tests
        propertiesPage.searchForProperty(PREFIX_PROPERTIES_TO_TEST);

        propertiesPage.clickEditIcon(PROP_TO_ADD.id, firstColumnToTest);
        propertiesPage.clickEditIcon(PROP_TO_ADD.id, secondColumnToTest);
        propertiesPage.clickEditIcon(SECOND_PROP_TO_ADD.id, firstColumnToTest);
        propertiesPage.clickEditIcon(SECOND_PROP_TO_ADD.id, secondColumnToTest);
        propertiesPage.clickEditIcon(THIRD_PROP_TO_ADD.id, firstColumnToTest);
        propertiesPage.clickEditIcon(THIRD_PROP_TO_ADD.id, secondColumnToTest);

        var highlightedCellsBefore = propertiesPage.getNumberOfHighlightedCells();

        propertiesPage.editCellValue(PROP_TO_ADD.id, firstColumnToTest, "Automated Property Name " + SharedMethods.generateRandomNumber());
        propertiesPage.editCellValue(PROP_TO_ADD.id, secondColumnToTest, "Help Text " + SharedMethods.generateRandomNumber());
        propertiesPage.editCellValue(SECOND_PROP_TO_ADD.id, firstColumnToTest, "Automated Property Name " + SharedMethods.generateRandomNumber());
        propertiesPage.editCellValue(SECOND_PROP_TO_ADD.id, secondColumnToTest, "Help Text " + SharedMethods.generateRandomNumber());
        propertiesPage.editCellValue(THIRD_PROP_TO_ADD.id, firstColumnToTest, "Automated Property Name " + SharedMethods.generateRandomNumber());
        propertiesPage.editCellValue(THIRD_PROP_TO_ADD.id, secondColumnToTest, "Help Text " + SharedMethods.generateRandomNumber());

        propertiesPage.clickSaveValueIcon(PROP_TO_ADD.id, firstColumnToTest); // BUG - Clicking on save icon saves all the inputs

        var highlightedCellsAfter = propertiesPage.getNumberOfHighlightedCells();
        var numberOfEditedCells = 6;
        softAssert.assertEquals(highlightedCellsAfter, highlightedCellsBefore + numberOfEditedCells, "Number of highlighted cells is not correct");

        var firstPropertyUpdatedInfo = propertiesPage.getPropertyData(PROP_TO_ADD.id);
        var secondPropertyUpdatedInfo = propertiesPage.getPropertyData(SECOND_PROP_TO_ADD.id);
        var thirdPropertyUpdatedInfo = propertiesPage.getPropertyData(THIRD_PROP_TO_ADD.id);

        softAssert.assertNotEquals(firstPropertyOriginalInfo, firstPropertyUpdatedInfo, "Property: " + PROP_TO_ADD.id + " was not updated after clicking save icon");
        softAssert.assertNotEquals(secondPropertyOriginalInfo, secondPropertyUpdatedInfo, "Property: " + SECOND_PROP_TO_ADD.id + " was not updated after clicking save icon");
        softAssert.assertNotEquals(thirdPropertyOriginalInfo, thirdPropertyUpdatedInfo, "Property: " + THIRD_PROP_TO_ADD.id + " was not updated after clicking save icon");

        propertiesPage = propertiesPage.applyAllChanges();

        var successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(successMessageDisplayed, "Success message was not displayed after applying changes");

        var firstPropertyInfoAfterApplyingChanges = propertiesPage.getPropertyData(PROP_TO_ADD.id);
        var secondPropertyInfoAfterApplyingChanges = propertiesPage.getPropertyData(SECOND_PROP_TO_ADD.id);
        var thirdPropertyInfoAfterApplyingChanges = propertiesPage.getPropertyData(THIRD_PROP_TO_ADD.id);

        softAssert.assertEquals(firstPropertyUpdatedInfo, firstPropertyInfoAfterApplyingChanges, "Property: " + PROP_TO_ADD.id + " was not updated after applying changes");
        softAssert.assertEquals(secondPropertyUpdatedInfo, secondPropertyInfoAfterApplyingChanges, "Property: " + SECOND_PROP_TO_ADD.id + " was not updated after applying changes");
        softAssert.assertEquals(thirdPropertyUpdatedInfo, thirdPropertyInfoAfterApplyingChanges, "Property: " + THIRD_PROP_TO_ADD.id + " was not updated after applying changes");

        softAssert.assertAll();
    }

    @Test(priority = 5, description = "Can delete properties from modal and from delete icon")
    public void CGEN_CompanyProperties_CanDeleteProperties() throws Exception {
        var propertiesToDelete = asList(PROP_TO_ADD, SECOND_PROP_TO_ADD);

        propertiesPage = addPropertiesIfNeeded(propertiesToDelete);

        propertiesPage = propertiesPage.deleteProperties(Collections.singletonList(PROP_TO_ADD.name));
        var propertyIsDisplayed = propertiesPage.isPropertyNameDisplayed(PROP_TO_ADD.name);
        Assert.assertFalse(propertyIsDisplayed, "Property with name: " + PROP_TO_ADD.name + " was not deleted using the delete modal");

        propertiesPage = propertiesPage.deleteProperty(SECOND_PROP_TO_ADD.id);
        var successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(successMessageDisplayed, "Success message was not displayed after deleting property with id: " + SECOND_PROP_TO_ADD.id);

        var maxNumberOfRetries = 5;
        for (int i = 1; i <= maxNumberOfRetries; i++) {
            propertyIsDisplayed = propertiesPage.searchForProperty(SECOND_PROP_TO_ADD.id)
                    .isPropertyDisplayed(SECOND_PROP_TO_ADD.id);
            if (!propertyIsDisplayed) {
                break;
            } else if (propertyIsDisplayed && i == maxNumberOfRetries) {
                Assert.fail("Property: " + SECOND_PROP_TO_ADD.id + " was not deleted using the delete icon");
            }
        }
    }

    @Test(priority = 6, description = "All properties are displayed on the page")
    public void CGEN_CompanyProperties_AllPropertiesAreDisplayed() throws Exception {
        int numberOfPropertiesNextToSearchInput = propertiesPage.getNumberDisplayedNextToSearchInput();

        List<CompanyPropertiesBase.PropertyBase> standardPropertiesFromDB = CompanyApiService.getCompanyWithProperties(jwt).companyProperties.getBasePropertySchema();
        List<CompanyPropertiesBase.PropertyBase> digitalAssetPropertiesFromDB = CompanyApiService.getCompanyWithProperties(jwt).companyProperties.getBaseDigitalAssetPropertySchema();

        Assert.assertEquals(
                numberOfPropertiesNextToSearchInput,
                standardPropertiesFromDB.size() + digitalAssetPropertiesFromDB.size(),
                "No. of properties displayed is not equal to the number of all properties in DB"
        );

        verifyFilterByTypeDisplaysCorrectProperties(standardPropertiesFromDB, Enums.Property.STANDARD);
        verifyFilterByTypeDisplaysCorrectProperties(digitalAssetPropertiesFromDB, Enums.Property.DIGITAL_ASSETS);
    }

    @Test(priority = 7, description = "Can search properties by id, name, data type, help text and group. This input is case insensitive.")
    public void CGEN_CompanyProperties_CanSearchForProperties() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        var propertiesFromDB = CompanyApiService.getCompanyWithProperties(jwt).companyProperties.propertySchema;

        propertiesPage = propertiesPage.filterPropertyType(Enums.Property.STANDARD);

        String searchTerm = "test_property";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toUpperCase(), propertiesFromDB, softAssert);

        searchTerm = "Test Property";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toLowerCase(), propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toUpperCase(), propertiesFromDB, softAssert);

        searchTerm = "Date";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toLowerCase(), propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toUpperCase(), propertiesFromDB, softAssert);

        searchTerm = "helpT1";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toLowerCase(), propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toUpperCase(), propertiesFromDB, softAssert);

        searchTerm = "Task Properties";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toLowerCase(), propertiesFromDB, softAssert);
        verifySearchInputWorks(searchTerm.toUpperCase(), propertiesFromDB, softAssert);

        // Negative case scenario
        searchTerm = "Invalid Search Term";
        verifySearchInputWorks(searchTerm, propertiesFromDB, softAssert);

        searchTerm = "";
        propertiesPage.searchForProperty(searchTerm);
        var noDataMessageIsDisplayed = propertiesPage.tableCommonFeatures.isNoDataMessageDisplayed();
        softAssert.assertFalse(noDataMessageIsDisplayed, "Properties are not displayed after clearing search input");
        var propertiesDisplayed = propertiesPage.getAllPropertiesData();
        softAssert.assertEqualsNoOrder(
                propertiesDisplayed.stream().map(propertyBase -> propertyBase.id).toArray(),
                propertiesFromDB.stream().map(propertyBase -> propertyBase.id).toArray(),
                "Properties are not displayed after clearing search input" +
                        "\nProperties displayed: " + propertiesDisplayed +
                        "\nProperties from DB: " + propertiesFromDB
        );

        softAssert.assertAll();
    }

    @Test(priority = 8, description = "Properties from group are hidden when group is collapsed and displayed when group is expanded")
    public void CGEN_CompanyProperties_CanCollapseAndExpandPropertyGroup() {
        SoftAssert softAssert = new SoftAssert();

        boolean propertyGroupIsExpanded = propertiesPage.collapseGroup(GROUP_TO_TEST).isGroupExpanded(GROUP_TO_TEST);
        softAssert.assertFalse(propertyGroupIsExpanded, "Property group is not collapsed");

        propertyGroupIsExpanded = propertiesPage.expandGroup(GROUP_TO_TEST).isGroupExpanded(GROUP_TO_TEST);
        softAssert.assertTrue(propertyGroupIsExpanded, "Property group is not collapsed");

        softAssert.assertAll();
    }

    @Test(priority = 9, description = "Can rearrange properties within a group")
    public void CGEN_CompanyProperties_CanRearrangePropertiesWithingAGroup() {
        List<CompanyPropertiesBase.PropertyBase> expectedProperties = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);
        expectedProperties = rearrangeTestingPropertiesIfNeeded(expectedProperties, ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY);

        // TESTING AFTER REFRESHING PAGE
        boolean reorderingErrorDisplayed = propertiesPage.rearrangePropertyPosition(ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY)
                .isReorderingWentWrongErrorDisplayed();
        Assert.assertFalse(reorderingErrorDisplayed, "Reordering went wrong error after refreshing page");

        List<CompanyPropertiesBase.PropertyBase> propertiesAfterRearrange = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);

        CompanyPropertiesBase.PropertyBase propertyToMove = expectedProperties.stream()
                .filter(prop -> prop.id.equals(ID_PROPERTY_TO_MOVE))
                .findFirst()
                .orElse(null);
        CompanyPropertiesBase.PropertyBase rearrangementProperty = expectedProperties.stream()
                .filter(prop -> prop.id.equals(ID_REARRANGEMENT_PROPERTY))
                .findFirst()
                .orElse(null);
        int rearrangementPropertyIndex = expectedProperties.indexOf(rearrangementProperty);
        expectedProperties.remove(propertyToMove);
        expectedProperties.add(rearrangementPropertyIndex, propertyToMove);

        Assert.assertEquals(propertiesAfterRearrange, expectedProperties, "Properties are not rearranged as expected");

        // TESTING REARRANGEMENT STAYS AFTER USING SEARCH INPUT
        propertiesPage.searchForProperty(PREFIX_PROPERTIES_TO_TEST_REARRANGE);
        propertiesAfterRearrange = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);

        expectedProperties.removeIf(property -> !property.id.startsWith(PREFIX_PROPERTIES_TO_TEST_REARRANGE));
        Assert.assertEquals(propertiesAfterRearrange, expectedProperties, "Properties went to original state after using search input");

        // TODO - DEV DISABLED FUNCTIONALITY TO REORDER WHILE SEARCHING. LEAVING THIS CODE HERE TO USE IT ONCE FUNCTIONALITY IS ENABLED
        /*rearrangementPropertyIndex = expectedProperties.indexOf(rearrangementProperty);
        expectedProperties.remove(propertyToMove);
        expectedProperties.add(rearrangementPropertyIndex, propertyToMove);*/

        // TESTING CANNOT REARRANGE WHILE SEARCHING - TODO - THIS TEST SHOULD FAIL ONCE THE FUNCTIONALITY TO REARRANGE WHILE SEARCHING IS ENABLED
        propertiesPage.rearrangePropertyPosition(ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY);
        propertiesAfterRearrange = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);
        propertiesPage.isNoteDisplayedWithMessage(Enums.NoteType.WARNING, "You cannot rearrange properties while searching.");
        Assert.assertEquals(propertiesAfterRearrange, expectedProperties, "Properties were rearranged while searching");
        //propertiesPage.rearrangePropertyPosition(ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY); // TODO - UNCOMMENT THIS LINE ONCE FUNCTIONALITY IS ENABLED
    }

    @Test(priority = 10, description = "Properties in product details page are ordered as in the properties page")
    public void CGEN_CompanyProperties_RearrangedPropertiesAreDisplayedInPDP() throws Exception {
        // REARRANGE PROPERTIES
        List<CompanyPropertiesBase.PropertyBase> expectedProperties = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);
        rearrangeTestingPropertiesIfNeeded(expectedProperties, ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY);

        propertiesPage.rearrangePropertyPosition(ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY);

        // GO TO PRODUCT DETAILS PAGE AND CHECK PROPERTIES ORDER
        expectedProperties = propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);
        List<String> propertiesNamesInPDP = navigateToPropertiesTabOfProductToTest()
                .getPropertyNamesInGroup(PRODUCT_TO_TEST.getProductVersion(), PRODUCT_TO_TEST.localeName, GROUP_TO_TEST);

        expectedProperties.removeIf(property -> !propertiesNamesInPDP.contains(property.name));

        // VERIFY ORDER OF PROPERTIES IN PDP IS THE SAME AS IN THE PROPERTIES PAGE
        for (int index = 0; index < propertiesNamesInPDP.size(); index++) {
            String propertyName = propertiesNamesInPDP.get(index);
            CompanyPropertiesBase.PropertyBase property = expectedProperties.stream().filter(prop -> prop.name.equals(propertyName)).findFirst().orElse(null);
            int indexOfProperty = expectedProperties.indexOf(property);
            Assert.assertEquals(indexOfProperty, index, "Property: " + propertyName + " in PDP is not ordered as in the properties page");
        }

        propertiesPage = propertiesPage.navigateToUrl(INSIGHTS_PROPERTIES_URL, PropertiesPage.class);
        propertiesPage.rearrangePropertyPosition(ID_PROPERTY_TO_MOVE, ID_REARRANGEMENT_PROPERTY);
    }

    @Test(priority = 11, description = "Can reorder property groups. New order is reflected in PDP page")
    public void CGEN_CompanyProperties_CanRearrangePropertyGroups() throws Exception {
        CompanyPropertiesBase companyProperties = verifyReorderingGroupsInUIUpdatesTheGroupsInDB();
        List<String> expectedStandardGroups = verifyGroupsWereReorderedInUI(companyProperties, Enums.Property.STANDARD);
        List<String> expectedDigitalAssetGroups = verifyGroupsWereReorderedInUI(companyProperties, Enums.Property.DIGITAL_ASSETS);

        // GO TO PRODUCT DETAILS PAGE AND CHECK PROPERTY GROUPS DISPLAYED
        PropertiesTab propertiesTab = navigateToPropertiesTabOfProductToTest();
        List<String> standardGroupsInPDP = propertiesTab.filterPropertyType(Enums.Property.STANDARD)
                .getPropertyGroupNamesInVersion(PRODUCT_TO_TEST.getProductVersion(), PRODUCT_TO_TEST.localeName);
        List<String> digitalAssetGroupsInPDP = propertiesTab.filterPropertyType(Enums.Property.DIGITAL_ASSETS)
                .getPropertyGroupNamesInVersion(PRODUCT_TO_TEST.getProductVersion(), PRODUCT_TO_TEST.localeName);

        expectedStandardGroups.replaceAll(groupName -> groupName.equals(propertiesPage.DEFAULT_GROUP) ? "Unassigned" : groupName);
        expectedStandardGroups.removeIf(groupName -> !standardGroupsInPDP.contains(groupName));

        expectedDigitalAssetGroups.replaceAll(groupName -> groupName.equals(propertiesPage.DEFAULT_DIGITAL_ASSET_GROUP) ? "Unassigned" : groupName);
        expectedDigitalAssetGroups.removeIf(groupName -> !digitalAssetGroupsInPDP.contains(groupName));

        // VERIFY ORDER OF PROPERTIES IN PDP IS THE SAME AS IN THE PROPERTIES PAGE
        Assert.assertEquals(standardGroupsInPDP, expectedStandardGroups, "Standard groups in PDP are not ordered as in the properties page");
        Assert.assertEquals(digitalAssetGroupsInPDP, expectedDigitalAssetGroups, "Digital Asset groups in PDP are not ordered as in the properties page");

        propertiesPage = propertiesPage.navigateToUrl(INSIGHTS_PROPERTIES_URL, PropertiesPage.class);
    }

    @Test(priority = 12, description = "Can add property groups")
    public void CGEN_CompanyProperties_CanAddPropertyGroups() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        var group = new AbstractMap.SimpleEntry<>("Automated Standard Group", false);
        var group2 = new AbstractMap.SimpleEntry<>("Automated Digital Asset Group", true);

        propertyGroupsToRemove.add(group.getKey());
        propertyDigitalAssetGroupsToRemove.add(group2.getKey());

        var addPropertyGroupModal = propertiesPage.openModalToAddPropertyGroup();
        var addButtonsEnabled = addPropertyGroupModal.areAddButtonsEnabled();
        softAssert.assertFalse(addButtonsEnabled, "Add buttons are enabled when no group info is inserted");

        addPropertyGroupModal = addPropertyGroupModal.clickCancelButton()
                .openModalToAddPropertyGroup();

        addPropertyGroupModal.insertGroupInfo(group);
        addPropertyGroupModal.clickAddButton();

        var expectedMessage = "Group added successfully";
        var successMessageDisplayed = addPropertyGroupModal.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, expectedMessage);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after adding group");

        addPropertyGroupModal.insertGroupInfo(group2);
        propertiesPage = addPropertyGroupModal.clickAddAndCloseButton();

        var modal = propertiesPage.openModalToReorderGroups();
        var standardGroupsDisplayed = modal.getStandardGroupsDisplayed();
        var digitalAssetGroupsDisplayed = modal.getDigitalAssetGroupsDisplayed();

        var propertyGroupExists = standardGroupsDisplayed.contains(group.getKey());
        softAssert.assertTrue(propertyGroupExists, "Property group: " + group.getKey() + " was not displayed in reorder modal after adding it to company");

        propertyGroupExists = digitalAssetGroupsDisplayed.contains(group2.getKey());
        softAssert.assertTrue(propertyGroupExists, "Digital asset property group: " + group2.getKey() + " was not displayed in reorder modal after adding it to company");

        var standardGroupsInDB = CompanyApiService.getCompanyWithProperties(jwt)
                .companyProperties
                .groups
                .stream()
                .map(groupFromDB -> groupFromDB.name)
                .collect(Collectors.toList());

        var propertyGroupExistsInDB = standardGroupsInDB.contains(group.getKey());
        softAssert.assertTrue(propertyGroupExistsInDB, "Standard group: " + group.getKey() + " was not added to company");

        var digitalAssetGroupsInDB = CompanyApiService.getCompanyWithProperties(jwt)
                .companyProperties
                .groupsDigitalAssets
                .stream()
                .map(groupFromDB -> groupFromDB.name)
                .collect(Collectors.toList());
        propertyGroupExistsInDB = digitalAssetGroupsInDB.contains(group2.getKey());
        softAssert.assertTrue(propertyGroupExistsInDB, "Digital asset group: " + group2.getKey() + " was not added to company");

        softAssert.assertAll();
    }

    @Test(priority = 13, description = "Can delete groups. Properties from that group are moved to Unassigned Properties")
    public void CGEN_CompanyProperties_CanDeleteGroups() throws Exception {
        propertiesPage = propertiesPage.openModalToAddProperty().addProperties(propertiesToAdd, PropertiesPage.class);

        propertyIdsToRemove.addAll(propertiesToAdd.keySet().stream().map(prop -> prop.id).collect(Collectors.toList()));

        propertiesPage.deleteGroups(asList(PROP_FOR_GROUPS_TEST.group, SECOND_PROP_FOR_GROUPS_TEST.group));
        boolean successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(successMessageDisplayed, "Success message was not displayed after deleting groups");

        boolean groupExist = propertiesPage.doesGroupExist(PROP_FOR_GROUPS_TEST.group);
        Assert.assertFalse(groupExist, "Group: " + PROP_FOR_GROUPS_TEST.group + " was not deleted");

        for (CompanyPropertiesBase.PropertyBase prop : propertiesToAdd.keySet()) {
            String propertyGroup = propertiesPage.searchForProperty(prop.id)
                    .getPropertyData(prop.id)
                    .group;

            var expectedDefaultGroup = prop.type == Enums.PropertyType.DIGITAL_ASSET ? propertiesPage.DEFAULT_DIGITAL_ASSET_GROUP : propertiesPage.DEFAULT_GROUP;
            Assert.assertEquals(propertyGroup, expectedDefaultGroup, "Property: " + prop.name + " was not moved to " + expectedDefaultGroup + " after removing its group");
        }
    }

    @Test(priority = 14, description = "Import button works")
    public void CGEN_CompanyProperties_CanImportProperties() throws Exception {
        var softAssert = new SoftAssert();

        var importModal = propertiesPage.clickImportButton();

        // Testing template
        ImportAssertions.verifyCompanyPropertiesTemplateHasCorrectHeaders(importModal, softAssert, downloadFolder);

        // Testing import
        String fileToImport = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/CompanyPropertiesImportForUITests.xlsx";
        importModal.uploadFile(fileToImport);

        var successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after importing company properties");

        var expectedProperty = CompanyPropertiesXLUtils.getCompanyPropertiesInImportFile(fileToImport).get(0);
        propertyIdsToRemove.add(expectedProperty.id);

        ImportAssertions.verifyCompanyPropertiesWereImported(propertiesPage, expectedProperty.id, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 15, description = "Export button works")
    public void CGEN_CompanyProperties_CanExportProperties() throws Exception {
        var softAssert = new SoftAssert();
        var expectedMessage = "Properties exported successfully. You will receive an email shortly with the results.";

        propertiesPage.exportAllProperties();
        var successMessageDisplayed = propertiesPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, expectedMessage);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after exporting all company properties");

        propertiesPage.filterPropertyType(Enums.Property.DIGITAL_ASSETS);
        propertiesPage.exportFilteredProperties();
        successMessageDisplayed = propertiesPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, expectedMessage);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after exporting all company properties");

        softAssert.assertAll();
    }

    @Test(priority = 16, description = "C244532 - Image mapping properties are added if mapping config is selected in a digital asset property")
    public void CGEN_CompanyProperties_ImageMappingPropertiesAreAutogeneratedIfUserSelectsAMappingConfig() throws Exception {
        var imageSpecMappingProperty = new CompanyPropertiesBase.PropertyBase(
                null,
                PREFIX_PROPERTIES_TO_TEST + " " + SharedMethods.generateRandomNumber(),
                Enums.PropertyType.DIGITAL_ASSET,
                "help text",
                true,
                null,
                "Automated Test Company Mapping"
        );

        // Adding property
        SoftAssert softAssert = new SoftAssert();
        var addPropertyModal = propertiesPage.openModalToAddProperty();
        addPropertyModal.createPropertyAndCloseModal(imageSpecMappingProperty, false, PropertiesPage.class);
        propertyIdsToRemove.add(imageSpecMappingProperty.id);

        verifyPropertyWasAdded(imageSpecMappingProperty);

        // Image mapping properties are autogenerated
        var imageMappingProperties = ProductVersioningCommonMethods.generateImageMappingProperties(imageSpecMappingProperty.id, imageSpecMappingProperty.name);
        for (var imageMappingProperty : imageMappingProperties) {
            var propertyIsDisplayed = propertiesPage.isPropertyDisplayed(imageMappingProperty.id);
            softAssert.assertTrue(propertyIsDisplayed, "Property: " + imageMappingProperty.name + " was not autogenerated");
        }

        softAssert.assertAll();
    }

    private void verifyPropertyCannotBeAddedWithoutAllRequiredFields(String propertyName, AddPropertyModal addPropertyModal) {
        SoftAssert softAssert = new SoftAssert();
        addPropertyModal.insertPropertyName(propertyName);

        // Property id is auto generated
        var autoGeneratedId = addPropertyModal.getAutoGeneratedPropertyId();
        var expectedId = propertyName.toLowerCase().replaceAll(" ", "_");
        softAssert.assertEquals(autoGeneratedId, expectedId, "Auto generated id is not as expected");

        // Trying to add property without filling all required fields - clicking Add button
        addPropertyModal.clickAddButton();
        var infoMessageIsDisplayed = addPropertyModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Please fill all required fields.");
        softAssert.assertTrue(infoMessageIsDisplayed, "Info message was not displayed when trying to add property without filling in any fields");

        addPropertyModal.closeNoteIfDisplayed(Enums.NoteType.INFO);

        // Trying to add property without filling all required fields - clicking Add & Close button
        addPropertyModal.clickAddAndCloseButton();
        infoMessageIsDisplayed = addPropertyModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Please fill all required fields.");
        softAssert.assertTrue(infoMessageIsDisplayed, "Info message was not displayed when trying to add property without filling in any fields");
        addPropertyModal.closeNoteIfDisplayed(Enums.NoteType.INFO);

        softAssert.assertAll();
    }

    private void verifyAddPropertyButtonsWork(CompanyPropertiesBase.PropertyBase propertyToAdd, CompanyPropertiesBase.PropertyBase secondPropertyToAdd, AddPropertyModal addPropertyModal) throws InterruptedException {
        // Testing Add Button
        SoftAssert softAssert = new SoftAssert();

        addPropertyModal.createProperty(propertyToAdd, false);
        propertyIdsToRemove.add(propertyToAdd.id);

        var successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after adding property: " + propertyToAdd.name + " with Add button");

        // Testing Add & Close Button
        propertiesPage = addPropertyModal.createPropertyAndCloseModal(secondPropertyToAdd, false, PropertiesPage.class);
        propertyIdsToRemove.add(propertyToAdd.id);

        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after adding property: " + secondPropertyToAdd.name + " with Add & Close button");
        softAssert.assertAll();
    }

    private void verifyPropertyWasAdded(CompanyPropertiesBase.PropertyBase expectedProperty) {
        SoftAssert softAssert = new SoftAssert();
        var propertyIsDisplayed = propertiesPage.searchForProperty(expectedProperty.name)
                .isPropertyDisplayed(expectedProperty.id);
        softAssert.assertTrue(propertyIsDisplayed, "Property: " + expectedProperty.name + " was not added");

        var propertyInfo = propertiesPage.getPropertyData(expectedProperty.id);
        softAssert.assertEquals(propertyInfo, expectedProperty, "Property info is not as expected");
        softAssert.assertAll();
    }

    private PropertiesPage addPropertiesIfNeeded(List<CompanyPropertiesBase.PropertyBase> propertiesToAdd) throws InterruptedException {
        for (var property : propertiesToAdd) {
            var propertyIsDisplayed = propertiesPage.searchForProperty(property.name)
                    .isPropertyNameDisplayed(property.name);
            if (!propertyIsDisplayed) {
                propertiesPage = propertiesPage.openModalToAddProperty()
                        .createPropertyAndCloseModal(property, false, PropertiesPage.class);
            }
            propertyIdsToRemove.add(property.id);
        }

        return propertiesPage.emptyOutSearchInput();
    }

    private void verifySearchInputWorks(String searchKey, List<CompanyPropertiesBase.Property> propertiesFromDB, SoftAssert softAssert) {
        propertiesPage.searchForProperty(searchKey);
        String searchKeyInLowerCase = searchKey.toLowerCase();

        for (CompanyPropertiesBase.PropertyBase property : propertiesFromDB) {
            if (property.group == null) {
                property.group = "Unassigned Properties";
            }

            if (property.helpText == null) {
                property.helpText = "";
            }
        }

        List<String> idsOfExpectedProperties = propertiesFromDB.stream()
                .filter(property -> property.name.toLowerCase().contains(searchKeyInLowerCase)
                        || property.id.toLowerCase().contains(searchKeyInLowerCase)
                        || property.helpText.toLowerCase().contains(searchKeyInLowerCase)
                        || property.type.getPropertyType().toLowerCase().contains(searchKeyInLowerCase)
                        || property.group.toLowerCase().contains(searchKeyInLowerCase)
                )
                .map(property -> property.id)
                .collect(Collectors.toList());

        boolean noDataMessageIsDisplayed = propertiesPage.tableCommonFeatures.isNoDataMessageDisplayed();

        if (idsOfExpectedProperties.isEmpty()) {
            Assert.assertTrue(noDataMessageIsDisplayed, "No data message was not displayed when searching for a search key not found in DB. Search term: " + searchKey);
        } else {
            softAssert.assertFalse(noDataMessageIsDisplayed, "No data message was displayed when searching for: " + searchKey);

            List<String> idsOfPropertiesDisplayed = propertiesPage.getAllPropertiesData().stream()
                    .map(property -> property.id)
                    .collect(Collectors.toList());

            int numberOfPropertiesNextToSearchInput = propertiesPage.getNumberDisplayedNextToSearchInput();
            softAssert.assertEquals(
                    numberOfPropertiesNextToSearchInput,
                    idsOfPropertiesDisplayed.size(),
                    "Search term: " + searchKey + ". Number of properties displayed is not equal to the number next to search input"
            );
            softAssert.assertEqualsNoOrder(
                    idsOfPropertiesDisplayed.toArray(),
                    idsOfExpectedProperties.toArray(),
                    "Properties displayed are not the same as the expected properties\n" +
                            "Search term: " + searchKey + "\n" +
                            "Properties displayed: " + idsOfPropertiesDisplayed + "\n" +
                            "Properties from DB: " + idsOfExpectedProperties
            );
        }
    }

    private void verifyFilterByTypeDisplaysCorrectProperties(List<CompanyPropertiesBase.PropertyBase> propertiesInDatabase, Enums.Property propertyType) throws InterruptedException {
        List<CompanyPropertiesBase.PropertyBase> propertiesDisplayed = propertiesPage
                .filterPropertyType(propertyType)
                .getAllPropertiesData();

        int numberOfPropertiesNextToSearchInput = propertiesPage.getNumberDisplayedNextToSearchInput();

        Assert.assertEquals(numberOfPropertiesNextToSearchInput, propertiesInDatabase.size(), "No. of " + propertyType + " properties next to search input is not equal to the number of properties in DB");
        Assert.assertEquals(propertiesDisplayed.size(), propertiesInDatabase.size(), "No. of " + propertyType + " properties displayed is not equal to the number of all properties in DB");

        for (CompanyPropertiesBase.PropertyBase property : propertiesInDatabase) {
            if (property.group == null) {
                property.group = propertyType == Enums.Property.STANDARD ? "Unassigned Properties" : "Unassigned Digital Assets";
            }

            if (property.helpText == null) {
                property.helpText = "";
            }
        }

        LOGGER.info("Properties displayed on the page: " + propertiesDisplayed);
        LOGGER.info("Properties in DB: " + propertiesInDatabase);

        Assert.assertEqualsNoOrder(propertiesDisplayed.toArray(), propertiesInDatabase.toArray(), propertyType + " properties displayed on the page are not equal to the properties from DB");
    }

    private List<CompanyPropertiesBase.PropertyBase> rearrangeTestingPropertiesIfNeeded(List<CompanyPropertiesBase.PropertyBase> properties, String idOfPropertyToMove, String idOfRearrangementProperty) {
        CompanyPropertiesBase.PropertyBase propertyToMove = properties.stream()
                .filter(prop -> prop.id.equals(idOfPropertyToMove))
                .findFirst()
                .orElse(null);
        CompanyPropertiesBase.PropertyBase rearrangementProperty = properties.stream()
                .filter(prop -> prop.id.equals(idOfRearrangementProperty))
                .findFirst()
                .orElse(null);
        int indexOfPropertyToMove = properties.indexOf(propertyToMove);
        int rearrangementPropertyIndex = properties.indexOf(rearrangementProperty);
        int comparison = Integer.compare(indexOfPropertyToMove, rearrangementPropertyIndex);
        if (comparison > 0) {
            propertiesPage.rearrangePropertyPosition(idOfPropertyToMove, idOfRearrangementProperty);
            return propertiesPage.getPropertiesInGroup(GROUP_TO_TEST);
        }
        return properties;
    }

    private CompanyPropertiesBase verifyReorderingGroupsInUIUpdatesTheGroupsInDB() throws Exception {
        CompanyPropertiesBase companyProperties = CompanyApiService.getCompanyWithProperties(jwt).companyProperties;
        List<String> standardGroupNamesInDBBeforeReordering = companyProperties.groups.stream()
                .map(group -> group.name)
                .collect(Collectors.toList());
        List<String> digitalAssetGroupNamesInDBBeforeReordering = companyProperties.groupsDigitalAssets.stream()
                .map(group -> group.name)
                .collect(Collectors.toList());

        List<String> reversedStandardGroups = new ArrayList<>(standardGroupNamesInDBBeforeReordering);
        Collections.reverse(reversedStandardGroups);

        List<String> reversedDigitalAssetGroups = new ArrayList<>(digitalAssetGroupNamesInDBBeforeReordering);
        Collections.reverse(reversedDigitalAssetGroups);

        LOGGER.info("Original standard groups: " + standardGroupNamesInDBBeforeReordering);
        LOGGER.info("Reversed standard groups: " + reversedStandardGroups);
        LOGGER.info("Original digital asset groups: " + digitalAssetGroupNamesInDBBeforeReordering);
        LOGGER.info("Reversed digital asset groups: " + reversedDigitalAssetGroups);

        LinkedHashMap<List<String>, Enums.PropertyGroupType> propertyGroups = new LinkedHashMap<>();
        propertyGroups.put(reversedStandardGroups, Enums.PropertyGroupType.STANDARD);
        propertyGroups.put(reversedDigitalAssetGroups, Enums.PropertyGroupType.DIGITAL_ASSET);

        propertiesPage.reorderPropertyGroups(propertyGroups);
        boolean successMessageDisplayed = propertiesPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(successMessageDisplayed, "Success message was not displayed after reordering groups");

        companyProperties = CompanyApiService.getCompanyWithProperties(jwt).companyProperties;
        List<String> standardGroupsInDBAfterReordering = companyProperties.groups.stream()
                .map(group -> group.name)
                .collect(Collectors.toList());
        List<String> digitalAssetGroupsInDBAfterReordering = companyProperties.groupsDigitalAssets.stream()
                .map(group -> group.name)
                .collect(Collectors.toList());

        Assert.assertEquals(standardGroupsInDBAfterReordering, reversedStandardGroups, "Property groups are not reordered as expected");
        Assert.assertEquals(digitalAssetGroupsInDBAfterReordering, reversedDigitalAssetGroups, "Digital Asset groups are not reordered as expected");
        return companyProperties;
    }

    private List<String> verifyGroupsWereReorderedInUI(CompanyPropertiesBase companyProperties, Enums.Property groupTypeToTest) throws InterruptedException {
        List<String> expectedGroupsToBeDisplayed;

        if (groupTypeToTest == Enums.Property.DIGITAL_ASSETS) {
            propertiesPage.filterPropertyType(groupTypeToTest);
            expectedGroupsToBeDisplayed = companyProperties.groupsDigitalAssets.stream()
                    .filter(group -> !group.properties.isEmpty())
                    .map(group -> group.name)
                    .collect(Collectors.toList());
            expectedGroupsToBeDisplayed.add(propertiesPage.DEFAULT_DIGITAL_ASSET_GROUP);
        } else {
            propertiesPage.filterPropertyType(groupTypeToTest);
            expectedGroupsToBeDisplayed = companyProperties.groups.stream()
                    .filter(group -> !group.properties.isEmpty())
                    .map(group -> group.name)
                    .collect(Collectors.toList());
            expectedGroupsToBeDisplayed.add(propertiesPage.DEFAULT_GROUP);
        }
        List<CompanyPropertiesBase.PropertyBase> allPropertiesData = propertiesPage.getAllPropertiesData();
        List<String> propertyGroupsDisplayed = allPropertiesData
                .stream()
                .map(property -> property.group)
                .distinct()
                .collect(Collectors.toList());

        Assert.assertEquals(propertyGroupsDisplayed, expectedGroupsToBeDisplayed, "Correct " + groupTypeToTest + " property groups are not displayed after reordering");

        return propertyGroupsDisplayed;
    }

    private PropertiesTab navigateToPropertiesTabOfProductToTest() throws Exception {
        String localeId = CompanyApiService.getCompanyWithProperties(jwt).getLocaleId(PRODUCT_TO_TEST.localeName);
        String productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(PRODUCT_TO_TEST.productIdentifier, jwt)._id;
        String pdpPageUrl = getProductDetailsUrl(productMasterId, localeId, null, null);
        return propertiesPage.navigateToUrl(pdpPageUrl, PropertiesTab.class);
    }
}
