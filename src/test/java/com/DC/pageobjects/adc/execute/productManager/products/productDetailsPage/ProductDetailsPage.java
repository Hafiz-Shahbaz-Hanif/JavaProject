package com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class ProductDetailsPage extends InsightsNavigationMenu {
    protected final By PAGE_BODY_LOCATOR = By.xpath("//div[@data-qa='ProductDetailsPage']");
    protected final By PROPERTIES_TAB_LOCATOR = By.xpath("//button[@role='tab' and text()='Properties']");
    protected final By KEYWORDS_TAB_LOCATOR = By.xpath("//button[@role='tab' and text()='Keywords']");
    protected final By ATTRIBUTES_TAB_LOCATOR = By.xpath("//button[@role='tab' and text()='Attributes']");
    protected final By APPLY_CHANGES_BUTTON = By.xpath("//div[contains(@data-qa,'BottomActionBar')]//button[contains(text(),'Apply All Changes')]");
    protected final By CANCEL_CHANGES_BUTTON = By.xpath("//div[contains(@data-qa,'BottomActionBar')]//button[contains(text(),'Cancel')]");
    public ProductsLeftSideFilter productsLeftSideFilter;

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY_LOCATOR);
        productsLeftSideFilter = new ProductsLeftSideFilter(driver);
    }

    public KeywordsTab clickKeywordsTab() throws InterruptedException {
        clickOnPageBody();
        clickOnPageBody();
        click(KEYWORDS_TAB_LOCATOR);
        return new KeywordsTab(driver);
    }

    public AttributesTab clickAttributesTab() throws InterruptedException {
        click(ATTRIBUTES_TAB_LOCATOR);
        return new AttributesTab(driver);
    }

    public void clickApplyChangesButtonAndWaitForInvisibility() throws InterruptedException {
        moveToElementAndClick(APPLY_CHANGES_BUTTON);
        waitForElementToBeInvisible(CANCEL_CHANGES_BUTTON);
    }

    public void clickApplyChangesButton() throws InterruptedException {
        click(APPLY_CHANGES_BUTTON);
    }

    public void clickCancelChangesButton() throws InterruptedException {
        click(CANCEL_CHANGES_BUTTON);
        waitForElementToBeInvisible(CANCEL_CHANGES_BUTTON);
    }

    public void clickCancelChangesButtonIfDisplayed() throws InterruptedException {
        if (isElementVisibleMilliseconds(CANCEL_CHANGES_BUTTON)) {
            clickCancelChangesButton();
        }
    }
}






