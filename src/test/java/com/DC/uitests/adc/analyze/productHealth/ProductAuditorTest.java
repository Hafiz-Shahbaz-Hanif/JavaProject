package com.DC.uitests.adc.analyze.productHealth;
import com.DC.objects.insights.DVAUIResultData;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.productHealth.ProductAuditorPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import static com.DC.constants.InsightsConstants.INSIGHTS_BASE_ENDPOINT;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ProductAuditorTest extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    public static final String INSIGHTS_PRODUCT_AUDITOR_URL = INSIGHTS_BASE_ENDPOINT + "/product-auditor";
    ProductAuditorPage productAuditorPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(INSIGHTS_PRODUCT_AUDITOR_URL);
        productAuditorPage = new ProductAuditorPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that audit results in UI display correctly and non matching data is highlighted")
    public void verifyDVAResultsInUIFunctionCorrectly() throws InterruptedException {
        Assert.assertFalse(productAuditorPage.isNextButtonEnabled(), "Next button was enabled before audit type was selected");
        productAuditorPage.selectAuditType("source-of-truth");
        Assert.assertTrue(productAuditorPage.isNextButtonEnabled(), "Next button was not enabled after audit type was selected");
        productAuditorPage.clickNextButton();
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/SOTAmazonDVANoImages.xlsx";
        int panelToSelect = 1;
        productAuditorPage.uploadSourceOfTruth(validFilePath);
        productAuditorPage.clickViewButton();
        getAuditTableDataAndVerifyHighlightingFunctionsAsExpected(panelToSelect);
    }

    public void getAuditTableDataAndVerifyHighlightingFunctionsAsExpected(int panelToSelect) {
        DVAUIResultData auditResultData = productAuditorPage.getDVAResultDataFromPanel(panelToSelect);
        verifyTextDifferencesAreHighlightedInUI("Title", auditResultData.sotAuditResult.title, auditResultData.pdpAuditResult.title, panelToSelect);
        verifyTextDifferencesAreHighlightedInUI("Brand", auditResultData.sotAuditResult.brand, auditResultData.pdpAuditResult.brand, panelToSelect);
        verifyTextDifferencesAreHighlightedInUI("Category", auditResultData.sotAuditResult.category, auditResultData.pdpAuditResult.category, panelToSelect);
        verifyTextDifferencesAreHighlightedInUI("Video", auditResultData.sotAuditResult.video, auditResultData.pdpAuditResult.video, panelToSelect);
        verifyTextDifferencesAreHighlightedInUI("Product Description", auditResultData.sotAuditResult.productDescription, auditResultData.pdpAuditResult.productDescription, panelToSelect);
        verifyIssuesListShowsCorrectColumns(auditResultData.issues, panelToSelect);
        int bulletCount = productAuditorPage.getGreaterBulletCount(1);
        verifyBulletHighlightingFunctionsCorrectly(bulletCount, auditResultData);
    }

    public void verifyTextDifferencesAreHighlightedInUI(String column, String sotAuditText, String pdpAuditText, int panelToSelect) {
        if (!sotAuditText.equals("") && !pdpAuditText.equals("")) {
            if (!sotAuditText.equals(pdpAuditText)) {
                Assert.assertTrue(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, true), "Text was highlighted in SOT column to show differences despite SOT and PDP values being equal");
                Assert.assertTrue(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, false), "Text was highlighted in PDP column to show differences despite SOT and PDP values being equal");
            } else {
                Assert.assertFalse(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, true), "Text was not highlighted in SOT column to show differences despite SOT and PDP values being equal");
                Assert.assertFalse(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, false), "Text was not highlighted in SOT column to show differences despite SOT and PDP values being equal");
            }
        }
        if (sotAuditText.equals("")) {
            Assert.assertTrue(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, false), "Text was not highlighted in PDP column to show differences when data was not in SOT");
        }
        if (pdpAuditText.equals("")) {
            Assert.assertTrue(productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, column, true), "Text was not highlighted in SOT column to show differences when data was not in PDP");
        }
    }

    public void verifyBulletHighlightingFunctionsCorrectly( int bulletCount, DVAUIResultData auditResultData) {
        for (var i = 0; i < bulletCount - 1; i ++) {
            var sotBullet = auditResultData.sotAuditResult.bullets.get(i);
            var pdpBullet = auditResultData.pdpAuditResult.bullets.get(i);
            var sotBulletText = sotBullet.bullet;
            var pdpBulletText = pdpBullet.bullet;
            if (!sotBulletText.equals("") && !pdpBulletText.equals("")) {
                if (!sotBulletText.equals(pdpBulletText)) {
                    Assert.assertTrue(sotBullet.isBulletHighlighted, "Bullet text was not highlighted in SOT column when the value of the SOT Bullet and PDP bullet were not equal");
                    Assert.assertTrue(pdpBullet.isBulletHighlighted, "Bullet text was not highlighted in PDP column when the value of the SOT Bullet and PDP bullet were not equal");
                } else {
                    Assert.assertFalse(sotBullet.isBulletHighlighted, "Bullet text was highlighted in SOT column when the value of the SOT Bullet and PDP bullet were equal");
                    Assert.assertFalse(pdpBullet.isBulletHighlighted, "Bullet text was highlighted in PDP column when the value of the SOT Bullet and PDP bullet were equal");
                }
            }
            if (sotBulletText.equals("")) {
                Assert.assertTrue(pdpBullet.isBulletHighlighted, "Bullet text was not highlighted in SOT column when the value of the PDP bullet was blank");
            }
            if (pdpBulletText.equals("")) {
                Assert.assertTrue(sotBullet.isBulletHighlighted, "Bullet text was not highlighted in PDP column when the value of the SOT bullet was blank");
            }
        }
    }

    public void verifyIssuesListShowsCorrectColumns(List<String> issues, int panelToSelect) {
        for (String issue: issues) {
            if (!issue.contains("Image") && !issue.contains("Bullet")) {
                Assert.assertTrue((productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, issue, true)) || productAuditorPage.isTextInAuditResultHighlighted(panelToSelect, issue, false));
            }
            if (issue.contains("Bullet")) {
                Assert.assertTrue(productAuditorPage.isAnyBulletHighlighted(panelToSelect));
            }
        }
    }
}
