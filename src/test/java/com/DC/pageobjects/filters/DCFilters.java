package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.CommonFeatures;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DCFilters extends PageHandler {
    private final By FILTER_EXPANDED = By.xpath("//button[@aria-label='filter-collapse']/following-sibling::h6");
    private final By FILTER_ICON = By.xpath("//button[@aria-label='filter-collapse'] | //*[@aria-label='Close Panel']");
    private final By CLEAR_RETAILERS = By.xpath("//a[text()='Clear']");
    private final By OPEN_RETAILER_DROPDOWN = By.xpath("//button[@title='Open']");
    private final By CLOSE_RETAILER_DROPDOWN = By.xpath("//button[@title='Close']");
    private final By OPEN_METRICS_DROPDOWN = By.xpath("//div[@id='sidebar-filter-metrics-displayed']//button[@title='Open']");
    private final By OPEN_DEPARTMENTS_DROPDOWN = By.xpath("//div[@id='sidebar-filter-department']//button[@title='Open']");
    private final By CLOSE_DEPARTMENTS_DROPDOWN = By.xpath("//div[@id='sidebar-filter-department']//button[@title='Close']");
    private final By RETAILER_DROPDOWN_OPTIONS = By.xpath("//ul[@role='listbox']/span");
    private final By SELECTED_RETAILER_IN_DROPDOWN = By.xpath("//div[@role='button' and @aria-label=*]/../div/span");
    private final By SELECT_RETAILER_ALERT = By.xpath("//div[@role='alert']");
    private final By APPLY_BUTTON = By.xpath("//button[@type='button' and text()='Apply'] | //button[@type='submit' and contains(text(),'Apply')]");
    private final By CANCEL_BUTTON = By.xpath("//button[@type='button' and text() = 'Cancel']");
    private final By RETAILER_FILTER = By.xpath("//div[@id='sidebar-filter-retailer']//input");
    private final By BRAND_FILTER = By.id("sidebar-filter-brand");
    private final By CATEGORY_FILTER = By.id("sidebar-filter-category");
    private final By SUBCATEGORY_FILTER = By.id("sidebar-filter-subcategory");
    private final By SEGMENT = By.id("sidebar-filter-segment");
    private final By SKU = By.id("sidebar-filter-sku");
    private final By ALL_RETAILERS = By.xpath("//span[text()='All Retailers']");
    private final By METRICS_DROPDOWN_OPTIONS = By.xpath("//ul[@role='listbox']/p");
    private final By SELECTED_METRICS_IN_DROPDOWN = By.xpath("//div[@id='sidebar-filter-metrics-displayed']//div[@role='button' and @aria-label=*]/../div/span");
    private final By SELECTED_DEPARTMENTS_IN_DROPDOWN = By.xpath("//div[@id='sidebar-filter-department']//div[@role='button' and @aria-label=*]/../div/span");
    private final By CLEAR_BRAND = By.xpath("//div[@id='sidebar-filter-brand']//a[text()='Clear']");
    private final By AMAZON_RETAIL_ACCOUNTS_DROPDOWN = By.xpath("//h6[text()='Amazon Retail Accounts']/..//a[text()='Clear']");
    private final By AMAZON_RETAIL_ACCOUNTS_FIELD = By.xpath("//div[@id='sidebar-filter-amazon-retail-accounts']//input");
    private final By STATUS_FILTER = By.xpath("//input[@id='Status-selectized'] | //input[@id='RBB Status-selectized']");
    private final By STATUS_FILTER_CLEAR_BUTTON = By.id("clearstatus");
    private final By FILTER_PLATFORM_CLEAR = By.xpath("//span[./label[contains(text(),'Platform')]]/a[contains(@class,'filter-clear')]");
    public final By FILTER_PLATFORM = By.xpath("//app-selectize[./select[@id='Platform']]/div/div/input");
    private final By QUERY_FILTER = By.id("Query");
    private final By QUERY_FILTER_CLEAR_BUTTON = By.id("clearquery");
    private final By CLEAR_COGS_UNITS = By.xpath("//div[@id='sidebar-filter-cogs-units-revenue']//a[text()='Clear']");
    private final By COGS_UNITS_FILTER = By.id("sidebar-filter-cogs-units-revenue");
    private final By AUTOMATION_FILTER = By.cssSelector("[id='sidebar-filter-automation-filter'] button[title='Open']");

    public DCFilters(WebDriver driver) {
        super(driver);
        findElementVisible(FILTER_ICON);
    }

    public void expandFilters() throws InterruptedException {
        if (!isElementVisible(FILTER_EXPANDED)) {
            click(FILTER_ICON);
        }
    }

    public void collapseFilters() throws InterruptedException {
        UI_LOGGER.info("Collapsing the filters");
        if (isElementVisible(FILTER_EXPANDED)) {
            click(FILTER_ICON);
        }
    }

    public boolean retailerSelected(String retailer) {
        return findElementsVisible(By.xpath("//div[@role='button' and @aria-label='" + retailer + "']/../div"), "Retailer Dropdown in Filter").size() == 1;
    }

    public void clearRetailers() throws InterruptedException {
        click(CLEAR_RETAILERS);
    }

    public void openRetailerDropdown() throws InterruptedException {
        click(OPEN_RETAILER_DROPDOWN);
    }

    public void closeRetailerDropdown() throws InterruptedException {
        click(CLOSE_RETAILER_DROPDOWN);
    }

    public void openMetricsDropdown() throws InterruptedException {
        click(OPEN_METRICS_DROPDOWN);
    }

    public void openDepartmentDropdown() throws InterruptedException {
        click(OPEN_DEPARTMENTS_DROPDOWN);
    }

    public void closeDepartmentsDropdown() throws InterruptedException {
        click(CLOSE_DEPARTMENTS_DROPDOWN);
    }

    public int selectMultipleRetailers(String... retailers) throws InterruptedException {
        if (isElementVisible(CLEAR_RETAILERS)) {
            clearRetailers();
        }
        openRetailerDropdown();
        List<WebElement> allDropdownOptions = findElementsVisible(RETAILER_DROPDOWN_OPTIONS, "");
        List<WebElement> selectedRetailers = new ArrayList<>();
        for (String retailerName : retailers) {
            for (WebElement option : allDropdownOptions) {
                if (option.getText().contains(retailerName)) {
                    selectedRetailers.add(option);
                    break;
                }
            }
        }
        for (int i = 0; i < selectedRetailers.size(); i++) {
            selectedRetailers.get(i).click();
        }
        click(APPLY_BUTTON);
        return selectedRetailers.size();
    }

    public int selectMultipleMetrics(String... metrics) throws InterruptedException {
        openMetricsDropdown();
        List<WebElement> allDropdownOptions = findElementsVisible(METRICS_DROPDOWN_OPTIONS, "");
        List<WebElement> selectedMetrics = new ArrayList<>();
        for (String metricsName : metrics) {
            for (WebElement option : allDropdownOptions) {
                if (option.getText().contains(metricsName)) {
                    selectedMetrics.add(option);
                    break;
                }
            }
        }
        for (int i = 0; i < selectedMetrics.size(); i++) {
            selectedMetrics.get(i).click();
        }
        closeRetailerDropdown();
        return selectedMetrics.size();
    }

    public int selectMultipleDepartments(String... depart) throws InterruptedException {
        openDepartmentDropdown();
        List<WebElement> allDropdownOptions = findElementsVisible(METRICS_DROPDOWN_OPTIONS, "");
        List<WebElement> selectedDepartments = new ArrayList<>();
        for (String departmentsName : depart) {
            for (WebElement option : allDropdownOptions) {
                if (option.getText().contains(departmentsName)) {
                    selectedDepartments.add(option);
                    break;
                }
            }
        }
        for (int i = 0; i < selectedDepartments.size(); i++) {
            selectedDepartments.get(i).click();
        }
        closeDepartmentsDropdown();
        return selectedDepartments.size();
    }

    public void selectAllRetailers() throws InterruptedException {
        openRetailerDropdown();
        List<WebElement> allDropdownOptions = findElementsVisible(RETAILER_DROPDOWN_OPTIONS, "");
        List<WebElement> retailers = new ArrayList<>();
        for (WebElement option : allDropdownOptions) {
            if (!option.getText().equals("All Retailers")) {
                retailers.add(option);
            }
        }
        for (int i = 0; i < retailers.size(); i++) {
            retailers.get(i).click();
        }
        closeRetailerDropdown();
    }

    public List<String> getRetailersSelected() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        List<WebElement> optionsWebElement = findElementsVisible(SELECTED_RETAILER_IN_DROPDOWN, "");
        List<String> optionsString = new ArrayList<>();
        for (WebElement option : optionsWebElement) {
            optionsString.add(option.getText());
        }
        return optionsString;
    }

    public List<String> getMetricsSelected() {
        List<WebElement> optionsWebElement = findElementsVisible(SELECTED_METRICS_IN_DROPDOWN, "");
        List<String> optionsString = new ArrayList<>();
        for (WebElement option : optionsWebElement) {
            //Using .getText() method because I am using the Web Element not locator.
            optionsString.add(option.getText());
        }
        return optionsString;
    }

    public List<String> getDepartmentsSelected() {
        List<WebElement> optionsWebElement = findElementsVisible(SELECTED_DEPARTMENTS_IN_DROPDOWN, "");
        List<String> optionsString = new ArrayList<>();
        for (WebElement option : optionsWebElement) {
            //Using .getText() method because I am using the Web Element not locator.
            optionsString.add(option.getText());
        }
        return optionsString;
    }

    public void apply() throws InterruptedException {
        UI_LOGGER.info("** Clicking on filters save button");
        click(APPLY_BUTTON);
    }

    public boolean selectRetailerAlert() {
        return isElementVisible(SELECT_RETAILER_ALERT);
    }

    public boolean verifyCollapseOfLeftSideFilters() throws InterruptedException {
        click(FILTER_ICON);
        return isElementVisible(RETAILER_FILTER) && isElementVisible(BRAND_FILTER);
    }

    public boolean verifyPresenceOfAllLeftSideFilters() {
        return isElementVisible(RETAILER_FILTER) && isElementVisible(BRAND_FILTER);
    }

    public boolean verifyDisplayOfCancelButton() {
        return isElementVisible(CANCEL_BUTTON);
    }

    public boolean verifyDisplayOfApplyButton() {
        return isElementVisible(APPLY_BUTTON);
    }

    public void selectRetailer(String... retailers) throws InterruptedException {
        if (isElementVisible(ALL_RETAILERS)) {
            click(CLEAR_RETAILERS);
            click(RETAILER_FILTER);
        } else {
            click(RETAILER_FILTER);
        }

        for (String retailer : retailers) {
            WebElement retailerSelected = findElementVisible(By.xpath("//p[text()='" + retailer + "'] | //span[text()='" + retailer + "']"));
            retailerSelected.click();
        }
        click(APPLY_BUTTON);
    }

    public void filterPlatform(String platform) throws InterruptedException {
        Thread.sleep(10);
        if (isElementVisible(FILTER_PLATFORM_CLEAR)) {
            click(FILTER_PLATFORM_CLEAR);
            click(FILTER_PLATFORM);
        }
        typeInField(driver.findElement(FILTER_PLATFORM), platform + Keys.TAB);
        UI_LOGGER.info(platform + " selected");
    }

    public boolean verifyCollapseOfLeftSideFiltersSFR(String filter, String filter1, String filter2) throws InterruptedException {
        UI_LOGGER.info("Verify Left filter Displayed");
        click(FILTER_ICON);
        UI_LOGGER.info("Filter icon Invisible");

        return isElementVisible(By.id("sidebar-filter-" + filter)) && isElementVisible(By.id("sidebar-filter-" + filter1)) && isElementVisible(By.id("sidebar-filter-" + filter2));
    }

    public boolean verifyPresenceOfAllLeftSideFiltersSFR(String filter, String filter1) throws InterruptedException {
        if (isElementNotVisible(RETAILER_FILTER)) {
            click(FILTER_ICON);
            UI_LOGGER.info("Filter icon visible");
        }
        return isElementVisible(By.id("sidebar-filter-" + filter)) && isElementVisible(By.id("sidebar-filter-" + filter1));
    }

    public boolean verifySingleRetailerSelection(String retailer) {
        return isElementVisible(By.xpath("//span[text()='" + retailer + "']"));
    }

    public String getSelectedRetailer(String retailer) {
        return getTextFromElement(By.xpath("//span[text()='" + retailer + "']"), Duration.ofSeconds(3));
    }

    public void clickCancelButton() throws InterruptedException {
        UI_LOGGER.info("Click on Cancel Button");
        click(CANCEL_BUTTON);
    }

    public boolean verifySFRRetailerSelection(String retailer) {
        return isElementVisible(By.xpath("//input[@value='" + retailer + "']"));
    }

    public boolean verifyCollapseOfLeftSideFiltersSearchVolume(String filter) throws InterruptedException {
        UI_LOGGER.info("Verify Left filter Displayed");
        click(FILTER_ICON);
        UI_LOGGER.info("Filter icon Invisible");

        return isElementVisible(By.id("sidebar-filter-" + filter));
    }

    public boolean verifyPresenceOfAllLeftSideFiltersSearchVolume(String filter) throws InterruptedException {
        if (isElementNotVisible(RETAILER_FILTER)) {
            click(FILTER_ICON);
            UI_LOGGER.info("Filter icon visible");
        }
        return isElementVisible(By.id("sidebar-filter-" + filter));
    }

    public boolean isFilterEmpty(String filterType) {
        By filterLocator = By.xpath("//div[@id='sidebar-filter-" + filterType + "']//input[@placeholder='Type to Filter']");
        String inputValue = getAttribute(filterLocator, "value");
        return inputValue.equals("");
    }

    public boolean verifyNoCatalogFiltersSelectedByDefault(String filterType) {
        By filterLocator = By.xpath("//div[@id='sidebar-filter-" + filterType + "']//input[@placeholder='Type to Filter']/preceding-sibling::div//span");
        return isElementVisible(filterLocator);
    }

    public List<String> getRetailersAssignedToBUFromUI() throws InterruptedException {
        if (isElementVisible(ALL_RETAILERS)) {
            click(CLEAR_RETAILERS);
            click(RETAILER_FILTER);
        } else {
            click(RETAILER_FILTER);
        }
        List<WebElement> retailers = findElementsVisible(By.xpath("//ul[@role='listbox']/span"));
        List<String> retailersAssignedToBU = new ArrayList<>();
        for (WebElement retailer : retailers) {
            String retailerText = retailer.getText();
            if (!retailerText.equalsIgnoreCase("All Retailers")) {
                retailersAssignedToBU.add(retailerText);
            }
        }
        UI_LOGGER.info("Retailers assigned to BU are: " + retailersAssignedToBU);
        return retailersAssignedToBU;
    }

    public void selectBrand(String brand) throws InterruptedException {
        if (isElementVisible(CLEAR_BRAND)) {
            click(CLEAR_BRAND);
            click(BRAND_FILTER);
        } else {
            click(BRAND_FILTER);
            Thread.sleep(500);
        }

        WebElement retailerSelected = findElementVisible(By.xpath("//span[text()='" + brand + "']"));
        retailerSelected.click();

        click(APPLY_BUTTON);
    }

    public void clickClearButton(String filter) throws InterruptedException {
        click(By.xpath("//div[@id='sidebar-filter-" + filter + "']//a[text()='Clear']"));
    }

    public void selectAmazonRetailerAccount(String accountType) throws InterruptedException {
        click(AMAZON_RETAIL_ACCOUNTS_DROPDOWN);
        click(AMAZON_RETAIL_ACCOUNTS_FIELD);
        click(By.xpath("//ul[contains(@class, 'css-1wqez7i')]/span[text()='" + accountType + "']"));
    }

    public void selectStatus(String status) throws InterruptedException {
        if (isElementVisible(STATUS_FILTER_CLEAR_BUTTON)) {
            click(STATUS_FILTER_CLEAR_BUTTON);
            click(STATUS_FILTER);
        } else {
            click(STATUS_FILTER);
            Thread.sleep(500);
        }
        List<WebElement> statusList = findElementsVisible(By.xpath("//div[@class='selectize-dropdown-content']/div"));
        for (WebElement statusElement : statusList) {
            if (statusElement.getText().equalsIgnoreCase(status)) {
                statusElement.click();
                break;
            }
        }
        click(APPLY_BUTTON);
    }

    public void enterQuery(String query) throws InterruptedException {
        if (isElementVisible(QUERY_FILTER_CLEAR_BUTTON)) {
            click(QUERY_FILTER_CLEAR_BUTTON);
        }
        sendKeys(QUERY_FILTER, query);
        Thread.sleep(1000);
        click(APPLY_BUTTON);
    }

    public void selectCogsUnitsRevenue(String option) throws InterruptedException {
        click(CLEAR_COGS_UNITS);
        click(COGS_UNITS_FILTER);
        click(By.xpath("//span[text()='" + option + "']"));
    }

    public void openAutomationFilterDropdown() throws InterruptedException {
        click(AUTOMATION_FILTER);
    }
}
