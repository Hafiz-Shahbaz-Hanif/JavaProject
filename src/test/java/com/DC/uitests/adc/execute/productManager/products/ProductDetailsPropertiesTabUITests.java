package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.constants.ProductVersioningConstants.IMAGE_MAPPING_PROPERTY_NAME;
import static com.DC.constants.ProductVersioningConstants.MERGE_5_PROPERTIES;

public class ProductDetailsPropertiesTabUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String PREFIX_PRODUCTS_TO_TEST = "QA-STATIC-PRODUCT-";
    private final UserFriendlyInstancePath PRODUCT_TO_TEST = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_TEST + "001", "es-MX", null, null);

    private PropertiesTab propertiesTab;
    private String productToTestUrl;
    private String jwt;


    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);

        Company company = CompanyApiService.getCompany(jwt);
        var instancePath = PRODUCT_TO_TEST.convertToInstancePathBase(company, jwt);
        productToTestUrl = InsightsConstants.getProductDetailsUrl(instancePath.productMasterId, instancePath.localeId, instancePath.retailerId, instancePath.campaignId);
        driver.get(productToTestUrl);
        propertiesTab = new PropertiesTab(driver);
        propertiesTab.expandSection("Base", PRODUCT_TO_TEST.localeName);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTests() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Image mapping property has correct options in dropdown in PDP")
    public void CGEN_PropertiesTab_ImageMappingPropertyHasCorrectOptionsInDropdown() {
        var optionsInDropdown = propertiesTab.getDropdownPropertyOptions(PRODUCT_TO_TEST.getProductVersion(), PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME);
        var expectedOptions = new String[]{"None", "Required", "Optional"};
        Assert.assertEqualsNoOrder(optionsInDropdown.toArray(), expectedOptions, "Image mapping property has incorrect options in dropdown in PDP");
    }

    @Test(priority = 2, description = "Can edit image mapping property in PDP")
    public void CGEN_PropertiesTab_CanEditImageMappingProperty() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        var productVersion = PRODUCT_TO_TEST.getProductVersion();
        var currentValue = propertiesTab.getPropertyValues(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME).get(0);
        String optionToSelect = currentValue.equals("required") ? "Optional" : "Required";
        propertiesTab.editValueOfDropdownPropertyAndSave(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME, optionToSelect);
        var propertyValueHighlighted = propertiesTab.isPropertyValueHighlighted(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME);
        softAssert.assertTrue(propertyValueHighlighted, "Change to image mapping property was not highlighted in PDP");

        // Changing to 'None' marks the property as to be deleted
        propertiesTab.editValueOfDropdownPropertyAndSave(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME, "None");
        var propertyIsMarkAsToBeDeleted = propertiesTab.isPropertyMarkedAsToBeDeleted(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME);
        softAssert.assertTrue(propertyIsMarkAsToBeDeleted, "Image mapping property was not marked as to be deleted in PDP after selecting 'None' option");

        propertiesTab.clickCancelChangesButton();
        propertiesTab.editValueOfDropdownPropertyAndSave(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME, optionToSelect);
        propertiesTab.clickApplyChangesButtonAndWaitForInvisibility();
        var successMessageDisplayed = propertiesTab.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Properties Updated!");
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after applying changes in PDP");
        propertyValueHighlighted = propertiesTab.isPropertyValueHighlighted(productVersion, PRODUCT_TO_TEST.localeName, IMAGE_MAPPING_PROPERTY_NAME);
        softAssert.assertFalse(propertyValueHighlighted, "Property value was still highlighted after applying changes in PDP");

        softAssert.assertAll();
    }

    @Test(priority = 3, description = "Cannot edit/delete merge5 properties in PDP")
    public void CGEN_PropertiesTab_CannotEditOrDeleteMerge5Properties() {
        var productVersion = PRODUCT_TO_TEST.getProductVersion();

        for (var property : MERGE_5_PROPERTIES) {
            var iconsAreEnabled = propertiesTab.areDeleteAndEditIconsEnabled(productVersion, PRODUCT_TO_TEST.localeName, property);
            Assert.assertFalse(iconsAreEnabled, "One or both of the action icons are enabled for merge5 property: " + property);
        }
    }
}
