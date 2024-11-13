package com.DC.pageobjects.adc.analyze.productHealth;

import com.DC.objects.insights.DVAUIResultData;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductAuditorPage extends InsightsNavigationMenu {

    private final By PRODUCT_AUDITOR_PAGE_BODY = By.xpath("//div[@data-qa='ProductAuditor']");
    private final By PRODUCT_AUDITOR_NEXT_BUTTON = By.xpath("//button[text()='Next']");
    private final By SOURCE_OF_TRUTH_UPLOAD = By.xpath("//input[@type='file']");
    private final By EXPORT_BUTTON = By.xpath("//button[text()='Export']");
    private final By VIEW_BUTTON = By.xpath("//button[text()='View']");
    private final By DEPLOYMENT_ADUIT_RESULTS = By.xpath("//div[@data-qa='DeploymentAuditResults']");

    public ProductAuditorPage(WebDriver driver) {
        super(driver);
        findElementVisible(PRODUCT_AUDITOR_PAGE_BODY);
    }

    public void selectAuditType(String auditType) throws InterruptedException {
        click(By.xpath("//input[@value='" + auditType + "']"));
    }

    public boolean isNextButtonEnabled() {
        return isElementEnabled(PRODUCT_AUDITOR_NEXT_BUTTON);
    }

    public void clickNextButton() throws InterruptedException {
        click(PRODUCT_AUDITOR_NEXT_BUTTON);
    }

    public void uploadSourceOfTruth(String file) throws InterruptedException {
        uploadFile(SOURCE_OF_TRUTH_UPLOAD, file);
    }

    public void clickViewButton() throws InterruptedException {
        click(VIEW_BUTTON);
        findElementVisible(DEPLOYMENT_ADUIT_RESULTS);
    }

    public void clickExportButtonAndWaitForDownload() throws InterruptedException {
        click(EXPORT_BUTTON);
    }

    public DVAUIResultData getDVAResultDataFromPanel(int panelToSelect) {
        DVAUIResultData dvaUIResultData = new DVAUIResultData();
        DVAUIResultData.AuditResult sotAuditResult = new DVAUIResultData.AuditResult();
        DVAUIResultData.AuditResult pdpAuditResult = new DVAUIResultData.AuditResult();
        dvaUIResultData.retailerName = getRetailerName(panelToSelect);
        dvaUIResultData.issues = getIssues(panelToSelect);
        sotAuditResult.title = getTextFromAuditResult(panelToSelect, "Title", true);
        sotAuditResult.brand = getTextFromAuditResult(panelToSelect, "Brand", true);
        sotAuditResult.category = getTextFromAuditResult(panelToSelect, "Category", true);
        sotAuditResult.video = getTextFromAuditResult(panelToSelect, "Video", true);
        sotAuditResult.uniqueId = getTextFromAuditResult(panelToSelect, "Unique ID", true);
        sotAuditResult.productDescription = getTextFromAuditResult(panelToSelect, "Product Description", true);
        sotAuditResult.legalDisclaimer = getTextFromAuditResult(panelToSelect, "Legal Disclaimer", true);
        sotAuditResult.bullets = getBulletsFromAuditResult(panelToSelect, true);

        pdpAuditResult.title = getTextFromAuditResult(panelToSelect, "Title", false);
        pdpAuditResult.brand = getTextFromAuditResult(panelToSelect, "Brand", false);
        pdpAuditResult.category = getTextFromAuditResult(panelToSelect, "Category", false);
        pdpAuditResult.video = getTextFromAuditResult(panelToSelect, "Video", false);
        pdpAuditResult.uniqueId = getTextFromAuditResult(panelToSelect, "Unique ID", false);
        pdpAuditResult.productDescription = getTextFromAuditResult(panelToSelect, "Product Description", false);
        sotAuditResult.legalDisclaimer = getTextFromAuditResult(panelToSelect, "Legal Disclaimer", false);
        pdpAuditResult.bullets = getBulletsFromAuditResult(panelToSelect, false);
        dvaUIResultData.sotAuditResult = sotAuditResult;
        dvaUIResultData.pdpAuditResult = pdpAuditResult;
        return dvaUIResultData;
    }

    public String getTextFromAuditResult(int panelToSelect, String column, boolean isSOT) {
        if (!isSOT) {
            return getTextFromElement(By.xpath("(//span[text()='" + column + "']/following-sibling::a)[" + panelToSelect + "]"));
        }
        return getTextFromElement(By.xpath("(//span[text()='" + column + "']/following-sibling::span)[" + panelToSelect + "]"));
    }

    public boolean isTextInAuditResultHighlighted(int panelToSelect, String column, boolean isSOT) {
        if (!isSOT) {
            return isElementVisible(By.xpath("(//span[text()='" + column + "']/following-sibling::a//ins)[" + panelToSelect + "]"));
        }
        return isElementVisible(By.xpath("(//span[text()='" + column + "']/following-sibling::span/div/ins)[" + panelToSelect + "]"));
    }

    public List<DVAUIResultData.BulletData> getBulletsFromAuditResult(int panelToSelect, boolean isSOT) {
        List<DVAUIResultData.BulletData> bulletsInAuditResult = new ArrayList<>();
        int elementPosition;
        int bulletCount = getTotalBulletCount(panelToSelect);
        for (elementPosition = isSOT ? 1 : 2; elementPosition <= bulletCount; elementPosition += 2) {
            DVAUIResultData.BulletData bulletData = new DVAUIResultData.BulletData();
            bulletData.bullet = getTextFromElement(By.xpath("((//span[text()='•']/following-sibling::span//div)[" + elementPosition + "])[" + panelToSelect + "]"));
            bulletData.isBulletHighlighted = isElementVisible(By.xpath("((//span[text()='•']/following-sibling::span//div)[" + elementPosition + "])[" + panelToSelect + "]/ins"));
            bulletsInAuditResult.add(bulletData);
        }
        return bulletsInAuditResult;
    }

    public boolean isAnyBulletHighlighted(int panelToSelect) {
        return isElementVisible(By.xpath("(//span[text()='•']/following-sibling::span/div/ins)[" + panelToSelect + "]"));
    }

    public int getTotalBulletCount(int panelToSelect) {
        String sotBulletText =  getTextFromElement(By.xpath("((//span[text()='Bullets']/following-sibling::span)[1])[" + panelToSelect + "]")).replace(" ", "");
        int sotBulletCount = Integer.parseInt(sotBulletText.substring(sotBulletText.indexOf(":") + 1));
        String pdpBulletText =  getTextFromElement(By.xpath("((//span[text()='Bullets']/following-sibling::span)[2])[" + panelToSelect + "]")).replace(" ", "");
        int pdpBulletCount = Integer.parseInt(pdpBulletText.substring(sotBulletText.indexOf(":") + 1));
        return sotBulletCount + pdpBulletCount;
    }

    public int getGreaterBulletCount(int panelToSelect) {
        String sotBulletText =  getTextFromElement(By.xpath("((//span[text()='Bullets']/following-sibling::span)[1])[" + panelToSelect + "]")).replace(" ", "");
        int sotBulletCount = Integer.parseInt(sotBulletText.substring(sotBulletText.indexOf(":") + 1));
        String pdpBulletText =  getTextFromElement(By.xpath("((//span[text()='Bullets']/following-sibling::span)[2])[" + panelToSelect + "]")).replace(" ", "");
        int pdpBulletCount = Integer.parseInt(pdpBulletText.substring(sotBulletText.indexOf(":") + 1));
        return Math.max(sotBulletCount, pdpBulletCount);
    }

    public String getRetailerName(int panelToSelect) {
        return getTextFromElement(By.xpath("(//div[@class='MuiCardHeader-root css-1wqnbvh']//span)[" + panelToSelect + "]")).replaceAll("\\s.*", "");
    }

    public List<String> getIssues(int panelToSelect) {
        return Arrays.asList(getTextFromElement(By.xpath("(//span[text()='Issues:']/parent::div/following-sibling::div/span)[" + panelToSelect + "]")).split("\\s*,\\s*"));
    }
}
