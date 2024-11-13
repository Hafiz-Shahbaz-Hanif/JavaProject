package com.DC.uitests.insightslegacy;

import com.DC.objects.insightslegacy.AssignmentDetails;
import com.DC.pageobjects.legacy.legacyinsights.DashboardPage;
import com.DC.pageobjects.legacy.legacyinsights.InsightsLoginPage;
import com.DC.pageobjects.legacy.legacyinsights.TasksPage;
import com.DC.pageobjects.legacy.legacyinsights.taskpages.ContentTaskPage;
import com.DC.pageobjects.legacy.legacyinsights.taskpages.TaskPage;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.SpellCheckAssertions;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.SpellCheckModal;
import com.DC.utilities.sharedElements.TaskLaunchFileSettings;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.DC.constants.InsightsConstants.INSIGHTS_LEGACY_ENDPOINT;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class ContentCreateFlowRegressionTests extends BaseClass {

    private DashboardPage dashboardPage;
    private TaskPage taskPage;
    private TasksPage tasksPage;
    private ContentTaskPage contentTask;
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String loginEndpoint = READ_CONFIG.getInsightsApiLoginEndpoint();
    private final String assignmentName = "Regression Test - Content Create";
    private final AssignmentDetails contentCreateAssignmentDetails = getAssignmentDetails(assignmentName, Enums.TaskType.Content);
    private final String validText = "Automated test trial word";
    private final int indexOfProductToTest = 1;
    private final String specialProperty = "product_short_description";
    private final String mappingID = READ_CONFIG.getInsightsTaskUIMappingId();
    private final String testFamilyProductId = READ_CONFIG.getInsightsFamilyProductId();
    private final String testBrandProductId = READ_CONFIG.getInsightsBrandProductId();
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(INSIGHTS_LEGACY_ENDPOINT);
        new InsightsLoginPage(driver).login(SUPPORT_USERNAME, PASSWORD);
        dashboardPage = new DashboardPage(driver);
        jwt = loginAndGetJwt(loginEndpoint, SUPPORT_USERNAME, PASSWORD);
        tasksPage = dashboardPage.clickTasks().changeTasksPerPage("100");
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getContentCreateLaunchFileSettings(), 1);
        contentTask = (ContentTaskPage) tasksPage.claimRequiredAssignmentIfExists( contentCreateAssignmentDetails );
        taskPage = contentTask;
        contentTask.displayOriginalFields( jwt, mappingID, specialProperty );
        contentTask.refreshTask();
    }

    @BeforeMethod()
    public void setUp() throws Exception {
        if (driver.getTitle().equals("Task | OneSpace") && contentTask != null) {
            contentTask.clickOnSpecificProduct( indexOfProductToTest );
        }
    }

    @AfterClass
    public void cleanUp() {
        try {
            if (driver.getTitle().equals("Task | OneSpace")) {
                tasksPage = taskPage.discardTask();
            }
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_CanFlagContentCreateTask() throws Exception {
        String reasonForFlag = "Automated test: flagging a content create taskUI";
        contentTask.flagTask( reasonForFlag );
        boolean successMessageDisplayed = contentTask.isSuccessMessageDisplayed();
        Assert.assertTrue( successMessageDisplayed, "Success message was not displayed after flagging task" );
        contentTask.closeSuccessMessage();
        contentTask.waitForNotesInvisibility();
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_ClickProductsContentCreate() throws Exception {
        Assert.assertTrue(contentTask.clickAllProductsAndCheckTaskVisibility());
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_ContentCreateCheckBackOfPackOnlyDisplays() throws Exception {
        Assert.assertTrue((contentTask.checkProductDetailsImage( "Test Parent" )));
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_ContentCreateCheckFrontOfPackOnlyDisplays() throws Exception {
        Assert.assertTrue((contentTask.checkProductDetailsImage( "Test Parent" )));
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_ContentCreateCheckFrontOfPackImageIsntDisplayed() throws Exception {
        Assert.assertFalse(contentTask.checkCorrectNumberOfImagesInProductDetails( "Test Brand", 1 ), "There were too many Product Description Images Displayed");
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void ContentCreateCheckBackOfPackImageIsntDisplayed() throws Exception {
        Assert.assertFalse(contentTask.checkCorrectNumberOfImagesInProductDetails( "Test Parent", 1 ), "There were too many Product Description Images Displayed");
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_FindProductDetailsContentCreate() throws Exception {
        List<String> productNames = contentTask.getProductsNames();
        for ( String product : productNames ) {
            Assert.assertTrue( contentTask.checkProductDetails( product ), "Details in product " + product +" were not displayed" );
        };
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_ClickPDFContentCreate() throws Exception {
        Assert.assertTrue( contentTask.verifyPDFDownLoad( "content" ) );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_TextCharacterCountValidationTooFew() throws Exception {
        Assert.assertTrue( contentTask.fillInputsWithLimitedText() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_TextCharacterCountValidationTooMany() throws Exception {
        Assert.assertTrue( contentTask.fillInputsWithTooMuchText() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_TextCharacterCountValidationCorrectAmount() throws Exception {
        Assert.assertTrue( contentTask.fillInputsWithCorrectAmountOfText() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_KeywordValidationWithKeywordsAtBeginningAndEndOfString() throws Exception {
        Assert.assertEquals( "2 | 19 | 7", contentTask.checkTitleKeywordValidationBeginningAndEndOfString() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_KeywordsHighlighting() throws Exception {
        Assert.assertTrue( contentTask.checkKeywordsHighlighting() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_MultiwordKeywordValidation() throws Exception {
        Assert.assertEquals( "1 | 19 | 7", contentTask.checkMultiWordKeywordValidation() );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_CheckCopyPasteFunctionality() throws Exception {
        String textToCopyPaste = "Copied from family level title to brand level title. test";
        Assert.assertEquals(contentTask.checkCopyPasteFunctionality("Test Family", "Test Brand", textToCopyPaste), "Copied from family level title to brand level title. test" );    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_TitleKeywordValidationOnlyRequiresTitleAndBulletIfDescriptionIsOmitted() throws Exception {
        String productDescriptionID = "product_description";
        ArrayList<String> fieldIdsToRemove = new ArrayList<>();
        fieldIdsToRemove.add( productDescriptionID );
        fieldIdsToRemove.add(specialProperty);
        contentTask.removeFieldsFromTaskUIIfExistent( jwt, mappingID, fieldIdsToRemove );
        List<String> allKeywordBucketsInitials = contentTask.getAllKeywordBucketsInitials();
        for ( String sectionKeywordBucketsInitials : allKeywordBucketsInitials ) {
            Assert.assertEquals(sectionKeywordBucketsInitials, "T | B", "Keyword buckets are not correct." );
        }
        Assert.assertFalse( contentTask.doesTaskHaveField( productDescriptionID ), "Product Description field should not be in TaskUI." );
        contentTask.emptyOutAllFields();
        Assert.assertFalse( contentTask.isTitleKeywordBucketCountValidated(), "Title bucket should be invalid." );
        contentTask.fillRequiredFields( indexOfProductToTest, validText );
        Assert.assertTrue( contentTask.isTitleKeywordBucketCountValidated(), "Title bucket should be valid after filling the required fields with valid keywords." );
        var word = "Automated test trial word";
        contentTask.fillInAllInputs( word, word.length() );
        Assert.assertFalse( contentTask.doesProductHaveAnErrorIcon( indexOfProductToTest ), "Error icon should not be displayed." );
        contentTask.addFieldToTaskUIIfNotExistent( jwt, mappingID, productDescriptionID );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_TitleKeywordValidationRequiresTitleDescriptionAndBullet() throws Exception {
        contentTask.emptyOutAllFields();
        String invalidText = "This is an auto-generated text without a Title Keyword.";
        contentTask.fillRequiredFields( indexOfProductToTest, invalidText );
        Assert.assertFalse( contentTask.isTitleKeywordBucketCountValidated(), "Title bucket should be invalid." );
        Assert.assertTrue( contentTask.doesProductHaveAnErrorIcon( indexOfProductToTest ), "Error icon should be displayed." );
        contentTask.clickAllProducts();
        contentTask.clickSubmit();
        Assert.assertTrue( contentTask.isModalOpen(), "Modal should be displayed after clicking the submit button." );
        contentTask.closeModalIfOpened();
        contentTask.clickOnSpecificProduct( indexOfProductToTest );
        contentTask.fillRequiredFields( indexOfProductToTest, validText );
        Assert.assertTrue( contentTask.isTitleKeywordBucketCountValidated(), "Title bucket should be valid after filling the required fields with valid keywords." );
        contentTask.fillInAllInputs("Automated test trial word", 20);
        Assert.assertFalse( contentTask.doesProductHaveAnErrorIcon( indexOfProductToTest ), "Error icon should not be displayed." );
    }

    @Test(groups = {"ContentCreateRegressionTest"})
    public void UI_SpellCheckFeatureWorksAsExpected() throws Exception {
        String misspelledWordAllProducts = "WROKING";
        String misspelledWordFirstProduct = "AGRESSIVE";
        String misspelledWordSecondProduct = "ACCROSS";
        String validText = "Automated test trial word";
        String titleField = "Product Title";
        String descriptionField = "Product Description";
        String bulletOneField = "Bullet 1";

        contentTask.displayOriginalFields( jwt, mappingID, specialProperty );
        contentTask.fillRequiredFields( 1, misspelledWordAllProducts + " " + misspelledWordFirstProduct + " " + validText + " " + misspelledWordAllProducts + " QA" );
        int occurrencesOfMisspelledWordInTitle = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_TITLE_ID,
                misspelledWordAllProducts
        );
        By field = By.xpath( "//textarea[@id='" + contentTask.PRODUCT_TITLE_ID + "']" );
        String titleInFirstProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInDescription = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_DESCRIPTION_ID,
                misspelledWordAllProducts
        );
        field = By.xpath( "//textarea[@id='" + contentTask.PRODUCT_DESCRIPTION_ID + "']" );
        String descriptionInFirstProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInBulletOne = contentTask.getOccurrencesOfWordInField (
                contentTask.BULLET_ONE_ID,
                misspelledWordAllProducts
        );
        field = By.xpath( "//textarea[@id='" + contentTask.BULLET_ONE_ID + "']" );
        String bulletOneInFirstProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInFirstProduct = occurrencesOfMisspelledWordInTitle
                + occurrencesOfMisspelledWordInDescription
                + occurrencesOfMisspelledWordInBulletOne;

        contentTask.fillRequiredFields( 2, validText + " " + misspelledWordAllProducts + " " + misspelledWordSecondProduct + "" );
        int occurrencesOfMisspelledWordInTitleSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_TITLE_ID,
                misspelledWordAllProducts
        );
        field = By.xpath( "//textarea[@id='" + contentTask.PRODUCT_TITLE_ID + "']" );
        String titleInSecondProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInDescriptionSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_DESCRIPTION_ID,
                misspelledWordAllProducts
        );
        field = By.xpath( "//textarea[@id='" + contentTask.PRODUCT_DESCRIPTION_ID + "']" );
        String descriptionInSecondProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInBulletOneSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.BULLET_ONE_ID,
                misspelledWordAllProducts
        );
        field = By.xpath( "//textarea[@id='" + contentTask.BULLET_ONE_ID + "']" );
        String bulletOneInSecondProduct = contentTask.getTextFromElement( field );

        int occurrencesOfMisspelledWordInSecondProduct = occurrencesOfMisspelledWordInTitleSecondProduct
                + occurrencesOfMisspelledWordInDescriptionSecondProduct
                + occurrencesOfMisspelledWordInBulletOneSecondProduct;

        int occurrencesOfSecondMisspelledWordInTitleSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_TITLE_ID,
                misspelledWordSecondProduct
        );
        int occurrencesOfSecondMisspelledWordInDescriptionSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.PRODUCT_DESCRIPTION_ID,
                misspelledWordSecondProduct
        );
        int occurrencesOfSecondMisspelledWordInBulletOneSecondProduct = contentTask.getOccurrencesOfWordInField (
                contentTask.BULLET_ONE_ID,
                misspelledWordSecondProduct
        );
        int occurrencesOfSecondMisspelledWordInSecondProduct = occurrencesOfMisspelledWordInTitleSecondProduct
                + occurrencesOfMisspelledWordInDescriptionSecondProduct
                + occurrencesOfMisspelledWordInBulletOneSecondProduct;

        contentTask.clickAllProducts();
        contentTask.saveTask();
        // BUG - 41664 - NEED TO REFRESH TASK TO AVOID RACE CONDITION
        contentTask.refreshTask();
        contentTask.clickAllProducts();
        contentTask.clickSubmit();
        SpellCheckModal spellCheckModal = contentTask.isSpellCheckModalDisplayed();
        Assert.assertNotNull( spellCheckModal, "Spell check modal was not displayed after submitting task with misspelled words" );

        SpellCheckAssertions spellCheckAssertions = new SpellCheckAssertions(spellCheckModal);
        spellCheckAssertions.verifyButtonStatesWorkAsExpected();
        spellCheckAssertions.verifyUserCanNavigateToNextAndPreviousProducts();

        boolean activeWordIsVisible = spellCheckModal.clickNextItemIcon().isThereAnActiveWord();
        Assert.assertTrue( activeWordIsVisible, "Word wasn't activated after clicking next item icon" );

        spellCheckAssertions.verifyReplacingOnlyInSingleOccurrenceWorksAsExpected( titleInFirstProduct, descriptionInFirstProduct, bulletOneInFirstProduct );
        spellCheckAssertions.verifyReplacingAllOccurrencesInFieldWorksAsExpected( titleInFirstProduct, descriptionInFirstProduct, bulletOneInFirstProduct, occurrencesOfMisspelledWordInTitle );
        spellCheckAssertions.verifyReplacingAllOccurrencesInProductWorksAsExpected( titleInFirstProduct, descriptionInFirstProduct, bulletOneInFirstProduct, titleInSecondProduct, occurrencesOfMisspelledWordInFirstProduct );
        int expectedUpdatedItemsCount = occurrencesOfMisspelledWordInFirstProduct + occurrencesOfSecondMisspelledWordInSecondProduct;
        spellCheckAssertions.verifyReplacingAllOccurrencesOnAllProductsWorksAsExpected( expectedUpdatedItemsCount );
        spellCheckAssertions.verifyTypingReplacementWorksAsExpected();

        String expectedProductTitleSecondProduct = spellCheckModal.getFieldValue( titleField );
        String expectedProductDescriptionSecondProduct = spellCheckModal.getFieldValue( descriptionField );
        String expectedBulletOneSecondProduct = spellCheckModal.getFieldValue( bulletOneField );

        spellCheckModal.clickPreviousProductButton();
        String expectedProductTitleFirstProduct = spellCheckModal.getFieldValue( titleField );
        String expectedProductDescriptionFirstProduct = spellCheckModal.getFieldValue( descriptionField );
        String expectedBulletOneFirstProduct = spellCheckModal.getFieldValue( bulletOneField );
        spellCheckModal.submitTask();
        contentTask.waitForSubmitSpinnerInvisibility();
        tasksPage = new TasksPage( driver ).refreshTasksTable();

        HashMap<String, String> expectedPropertyValuesForFirstProduct  =  new HashMap<>() {{
            put(contentTask.PRODUCT_TITLE_ID, expectedProductTitleFirstProduct);
            put(contentTask.PRODUCT_DESCRIPTION_ID, expectedProductDescriptionFirstProduct);
            put(contentTask.BULLET_ONE_ID, expectedBulletOneFirstProduct);
        }};

        HashMap<String, String> expectedPropertyValuesForSecondProduct  = new HashMap<>() {{
            put(contentTask.PRODUCT_TITLE_ID, expectedProductTitleSecondProduct);
            put(contentTask.PRODUCT_DESCRIPTION_ID, expectedProductDescriptionSecondProduct);
            put(contentTask.BULLET_ONE_ID, expectedBulletOneSecondProduct);
        }};

        spellCheckAssertions.verifyStagedChanges( expectedPropertyValuesForFirstProduct, testFamilyProductId, jwt );
        spellCheckAssertions.verifyStagedChanges( expectedPropertyValuesForSecondProduct, testBrandProductId, jwt );
    }

    public AssignmentDetails getAssignmentDetails(String assignmentName, Enums.TaskType taskType) {
        AssignmentDetails assignmentDetails = new AssignmentDetails();
        assignmentDetails.assignmentName = assignmentName;
        assignmentDetails.taskType = taskType;
        assignmentDetails.requiredStatus = Enums.TaskStatus.Available;
        return assignmentDetails;
    }
}
