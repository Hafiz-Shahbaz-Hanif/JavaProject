package com.DC.uitests.adc.execute.destinations;

import com.DC.objects.insights.DestinationDefinitionSettings;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.destinations.DestinationDefinitionSettingsSection;
import com.DC.pageobjects.adc.execute.destinations.DestinationSetUpPage;
import com.DC.pageobjects.adc.execute.destinations.FieldDefinitionSection;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.enums.Enums;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.DC.constants.InsightsConstants.INSIGHTS_BASE_ENDPOINT;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class DestinationsSetUpTests extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    public static final String INSIGHTS_DESTINATION_SETUP_URL = INSIGHTS_BASE_ENDPOINT + "/destination-setup";
    public DestinationSetUpPage destinationSetUpPage;
    public DestinationDefinitionSettingsSection destinationDefinitionSettingsSection;
    public FieldDefinitionSection fieldDefinitionSection;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(INSIGHTS_DESTINATION_SETUP_URL);
        destinationSetUpPage = new DestinationSetUpPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that a destination with status Draft can be deleted")
    public void destinationsInDraftStatusCanBeDeleted() throws InterruptedException {
        destinationDefinitionSettingsSection = destinationSetUpPage.clickCreateDefinitionsButton();
        DestinationDefinitionSettings destinationDefinitionSettings = new DestinationDefinitionSettings();
        destinationDefinitionSettings.destinationName = "Auto-Generated Destination " + RandomStringUtils.randomAlphabetic(4).toUpperCase();
        destinationDefinitionSettings.destinationDescription = "Auto-Generated Description " + DateUtility.getCurrentDateTime("M/d/yyyy, h:mm");
        destinationDefinitionSettings.destinationType = "Excel Template";
        destinationDefinitionSettings.retailer = "Amazon";
        destinationDefinitionSettings.isANewDestinationTemplate = false;
        destinationDefinitionSettings.destinationTemplateName = "Amazon Destination Template";
        destinationDefinitionSettings.client = "Automated Test Company";

        fieldDefinitionSection = destinationDefinitionSettingsSection.createDraftDestination(destinationDefinitionSettings);
        Assert.assertTrue(destinationDefinitionSettingsSection.isNoteDisplayed(Enums.NoteType.SUCCESS), "Destination draft creation success message was not visible after clicking on create draft destination");
        destinationDefinitionSettingsSection.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        destinationSetUpPage = fieldDefinitionSection.returnToDestinationSetUpPage();
        Assert.assertTrue(destinationSetUpPage.doesDestinationExist(destinationDefinitionSettings.destinationName), destinationDefinitionSettings.destinationName + " was not visible on destination setup page after draft was created");
        verifyDraftCanBeDeleted(destinationDefinitionSettings.destinationName);
    }

    public void verifyDraftCanBeDeleted(String destinationName) throws InterruptedException {
        destinationSetUpPage.deleteDraftDestination(destinationName);
        Assert.assertFalse(destinationSetUpPage.doesDestinationExist(destinationName),destinationName + " was not deleted after clicking on delete icon");
    }
}
