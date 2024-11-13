package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.AttributesTab;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.enums.Enums;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ProductDetailsAttributesTabUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String UNIQUE_ID_OF_PRODUCT_TO_TEST = "QA-STATIC-PRODUCT-001";
    private final String LOCALE_TO_TEST = "es-MX";
    private AttributesTab attributesTab;
    private String productMasterId;
    private String productToTestUrl;
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(UNIQUE_ID_OF_PRODUCT_TO_TEST, jwt)._id;

        var company = CompanyApiService.getCompany(jwt);
        String localeId = company.getLocaleId(LOCALE_TO_TEST);

        productToTestUrl = InsightsConstants.getProductDetailsUrl(productMasterId, localeId, null, null);
        driver.get(productToTestUrl);
        attributesTab = new PropertiesTab(driver).clickAttributesTab();
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTests() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Testing CSCAT-32. Table is updated automatically when updates are made to attributes")
    public void CSCAT_AttributesTab_CanAddAndRemoveAttributeValues() {
        var softAssert = new SoftAssert();
        var valueToTag = "1 pack";
        var attributesBefore = attributesTab.getAttributesTableData();

        var attributeIsTagged = attributesBefore.stream().anyMatch(attribute -> attribute.taggedValues.contains(valueToTag));
        LOGGER.info("Value is tagged: " + attributeIsTagged);
        performTestToAddAndRemoveAttributeValues(valueToTag, !attributeIsTagged, softAssert);
        performTestToAddAndRemoveAttributeValues(valueToTag, attributeIsTagged, softAssert);

        softAssert.assertAll();
    }

    private void performTestToAddAndRemoveAttributeValues(String valueToTag, boolean addAttribute, SoftAssert softAssert) {
        var attributeTaggingOverlay = attributesTab.clickEditAttributeButton();
        attributeTaggingOverlay.selectOrDeselectValueFromCheckbox(valueToTag, addAttribute);
        attributesTab = attributeTaggingOverlay.clickSaveAttributesButton(AttributesTab.class);

        var isSuccessMessageDisplayed = attributesTab.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Attribute values have been successfully tagged.");
        softAssert.assertTrue(isSuccessMessageDisplayed, "Success message was not displayed after making updates");

        var attributesAfter = attributesTab.getAttributesTableData();

        if (addAttribute) {
            softAssert.assertTrue(attributesAfter.stream().anyMatch(attribute -> attribute.taggedValues.contains(valueToTag)), "Attribute value was not tagged to product");
        } else {
            softAssert.assertTrue(attributesAfter.stream().noneMatch(attribute -> attribute.taggedValues.contains(valueToTag)), "Attribute value was not removed from product");
        }
    }
}
