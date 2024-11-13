package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkAddDeleteKeywordsModal;
import com.DC.utilities.sharedElements.Paginator;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BulkEditSelectProductsPage extends ProductsPageBase {
    protected final String BOTTOM_ACTION_BAR_XPATH = "//div[@data-qa='BottomActionBarPV']";
    private final By CONTINUE_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[text()='Continue']");

    public Paginator paginator;
    public SingleSelectDropdown moreActionsDropdown;

    public BulkEditSelectProductsPage(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(BOTTOM_ACTION_BAR_XPATH));
        paginator = new Paginator(driver);
        moreActionsDropdown = new SingleSelectDropdown(driver);
    }

    public BulkAddDeleteKeywordsModal clickContinueButton() {
        clickElement(CONTINUE_BUTTON_LOCATOR);
        return new BulkAddDeleteKeywordsModal(driver);
    }
}
