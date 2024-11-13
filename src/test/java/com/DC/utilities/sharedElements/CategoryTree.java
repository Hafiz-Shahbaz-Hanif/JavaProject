package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CategoryTree extends PageHandler {
    private final String CATEGORY_TREE_XPATH = "//div[@data-qa='CategoryTree']";
    private final By CATEGORY_TREE = By.xpath(CATEGORY_TREE_XPATH);
    private final By SEARCH_CATEGORY_INPUT_LOCATOR = By.xpath(CATEGORY_TREE_XPATH + "//input");
    private final By SEARCH_CATEGORY_AUTOFILL_FIELD_LOCATOR = By.xpath("(//div[@data-qa='CategorySearch']//span)[2]");
    private final By SELECTED_CATEGORY_LOCATOR = By.xpath(CATEGORY_TREE_XPATH + "//button//h6");
    private final By ASSIGN_CATEGORY_BUTTON_LOCATOR = By.xpath(CATEGORY_TREE_XPATH + "//button[text()='Assign Category']");
    private final By VIEW_CATEGORY_BUTTON_LOCATOR = By.xpath(CATEGORY_TREE_XPATH + "//button[text()='View Category']");
    private final By CHOOSE_CATEGORY_BUTTON_LOCATOR = By.xpath(CATEGORY_TREE_XPATH + "//button[text()='Choose Category']");
    private final By ASSIGN_CATEGORY_CONFIRMATION_BUTTON = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Assign Category']");

    private final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);

    public CategoryTree(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(CATEGORY_TREE_XPATH));
    }

    public CategoryTree selectCategory(String category) {
        expandCategoryTree();
        var categoryToSelect = By.xpath(String.format("//div[@data-qa='CategoryTree']/div[2]/div[2]//span[text()='%s']", category));
        scrollIntoView(categoryToSelect);
        clickElement(categoryToSelect);
        return this;
    }

    public CategoryTree waitForCategoriesTableToLoad() {
        findElementVisible(CATEGORY_TREE, Duration.ofSeconds(55));
        return this;
    }

    public CategoryTree waitForSearchBarToLoad() {
        findElementVisible(SEARCH_CATEGORY_INPUT_LOCATOR, Duration.ofSeconds(55));
        return this;
    }

    public boolean isCategoryTreeExpanded() {
        return isElementVisibleMilliseconds(VIEW_CATEGORY_BUTTON_LOCATOR)
                | isElementVisibleMilliseconds(ASSIGN_CATEGORY_BUTTON_LOCATOR)
                | isElementVisibleMilliseconds(CHOOSE_CATEGORY_BUTTON_LOCATOR);
    }

    public void expandCategoryTree() {
        if (!isCategoryTreeExpanded()) {
            clickElement(CATEGORY_TREE);
        }
    }

    public boolean isCategorySearchbarDisplayed() {
        return isElementVisible(SEARCH_CATEGORY_INPUT_LOCATOR, MAX_WAIT_TIME_SECS);
    }

    public boolean isCategoryAutofillDisplayed() {
        return isElementVisible(SEARCH_CATEGORY_AUTOFILL_FIELD_LOCATOR, MAX_WAIT_TIME_SECS);
    }

    public void selectCategoryTree(List<String> categoryTree) {
        waitForCategoriesTableToLoad();
        var categoryTreeText = getCategoryTabValue();
        var expectedCategoryTree = String.join(">", categoryTree);
        if (!Objects.equals(categoryTreeText, expectedCategoryTree)) {
            for (var category : categoryTree) {
                selectCategory(category);
            }
        }
    }

    public void viewCategoryTree(List<String> categoryTree) {
        selectCategoryTree(categoryTree);
        clickViewCategoryButton();
    }

    public void clickViewCategoryButton() {
        clickElement(VIEW_CATEGORY_BUTTON_LOCATOR);
    }

    public void clickAssignCategoryButton() {
        clickElement(ASSIGN_CATEGORY_BUTTON_LOCATOR);
        findElementVisible(ASSIGN_CATEGORY_CONFIRMATION_BUTTON);
        clickElement(ASSIGN_CATEGORY_CONFIRMATION_BUTTON);
    }

    public void clickChooseCategoryButton() {
        clickElement(CHOOSE_CATEGORY_BUTTON_LOCATOR);
    }

    public void selectOptionFromAutofillOptions(List<String> categoryTree) {
        var categoryTreeString = String.join(" > ", categoryTree);
        var optionToSelect = By.xpath(String.format("//div[@data-qa='CategorySearch']//span[parent::div and text()='%s']", categoryTreeString));
        clickElement(optionToSelect);
    }

    public String getCategorySearchValue() {
        return getAttribute(SEARCH_CATEGORY_INPUT_LOCATOR, "value");
    }

    public String getCategorySearchAutoFillValue() {
        return getAttribute(SEARCH_CATEGORY_AUTOFILL_FIELD_LOCATOR, "value");
    }

    public String getCategoryTabValue() {
        return getTextFromElement(SELECTED_CATEGORY_LOCATOR);
    }

    public List<String> getCategoryTreeFromTabValue() {
        var categoryTabValue = getTextFromElement(SELECTED_CATEGORY_LOCATOR);
        return List.of(categoryTabValue.split(" > "));
    }

    public boolean categorySearchResultsHasValues(String searchTerm) {
        By autofillValueInDropdown = By.xpath(String.format("//div[@data-qa='CategorySearch']//div[@data-qa='TypeComponent' and contains(text(), '%s')]", searchTerm));
        return isElementVisible(autofillValueInDropdown, MAX_WAIT_TIME_SECS);
    }

    public List<String> getOptionsInCategorySearchDropdown() {
        By autofillValuesInDropdown = By.xpath("//div[@data-qa='CategorySearch']//span[parent::div]");
        return getTextFromElementsMilliseconds(autofillValuesInDropdown);
    }

    public void searchCategory(String category) {
        setText(SEARCH_CATEGORY_INPUT_LOCATOR, category);
    }

    public void changeCategory(List<String> newCategoryTree) {
        var categoryTreeString = String.join(" > ", newCategoryTree);
        var categoryValue = getCategoryTabValue();
        if (!Objects.equals(categoryValue, categoryTreeString)) {
            for (var category : newCategoryTree) {
                selectCategory(category);
            }
            clickAssignCategoryButton();
            waitForElementToBeInvisible(ASSIGN_CATEGORY_CONFIRMATION_BUTTON);
            refreshPage();
        }
    }
}
