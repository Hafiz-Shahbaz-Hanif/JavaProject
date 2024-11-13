package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.*;

import static java.util.Arrays.asList;

public class AdvancedFilters extends PageHandler {
    public static final String ADVANCED_FILTER_SECTION_XPATH = "//div[@data-qa='AdvancedFilters']";
    public final By APPLY_FILTERS_BUTTON = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//button[text()='Apply']");
    public final By CANCEL_BUTTON = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//button[text()='Cancel']");
    public final By ADD_ANOTHER_RULE_BUTTON = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//button[text()='Add Another Rule']");
    public final By RULES = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//span[contains(normalize-space(), 'Rule')]");
    public final By RESET_BUTTON = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//span[text()='Reset']");
    public final By ADVANCED_FILTERS_COMPONENT = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//div[@data-qa='TypeComponent' and text()='Advanced Filters']");
    public final By DROPDOWN_MENU = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//div[@data-qa='DropdownMenu']");
    public final By ADD_FIRST_RULE_BUTTON = By.xpath(ADVANCED_FILTER_SECTION_XPATH + "//button[text()='Add First Rule']");

    public final java.time.Duration MAX_WAIT_TIME_SECS = java.time.Duration.ofSeconds(3);

    public By operandInput;
    public By filterTypeDropdown;
    public By secondDropdown;
    public By thirdDropdown;

    private String filterTypeDropdownXPath;
    private String secondDropdownXPath;
    private String thirdDropdownXPath;
    private String operandInputXPath;

    public enum FilterType {
        PROPERTY,
        PRODUCT_LIST,
        DATE,
        ATTRIBUTE_VALUE,
        PRODUCT_CATEGORY,
        KEYWORD_VALUE,
        STAGED_CHANGES
    }

    public enum ThirdDropdownOption {
        EQUALS,
        DOES_NOT_EQUAL,
        HAS_ANY_VALUE,
        HAS_NO_VALUE,
        CONTAINS,
        DOES_NOT_CONTAIN,
        MATCHES_ANY,
        MATCHES_NONE
    }

    public final LinkedHashMap<FilterType, String> FILTER_TYPE_OPTIONS = new LinkedHashMap<>() {
        {
            put(FilterType.PROPERTY, "Property");
            put(FilterType.PRODUCT_LIST, "Product List");
            put(FilterType.DATE, "Date");
            put(FilterType.ATTRIBUTE_VALUE, "Attribute Value");
            put(FilterType.PRODUCT_CATEGORY, "Product Category");
            put(FilterType.STAGED_CHANGES, "Staged Changes");
            put(FilterType.KEYWORD_VALUE, "Keyword Value");
        }
    };

    public final LinkedHashMap<ThirdDropdownOption, String> THIRD_DROPDOWN_OPTIONS = new LinkedHashMap<>() {
        {
            put(ThirdDropdownOption.EQUALS, "Equals");
            put(ThirdDropdownOption.DOES_NOT_EQUAL, "Doesn't Equal");
            put(ThirdDropdownOption.HAS_ANY_VALUE, "Has Any Value");
            put(ThirdDropdownOption.HAS_NO_VALUE, "Has No Value");
            put(ThirdDropdownOption.CONTAINS, "Contains");
            put(ThirdDropdownOption.DOES_NOT_CONTAIN, "Doesn't Contain");
            put(ThirdDropdownOption.MATCHES_ANY, "Matches Any");
            put(ThirdDropdownOption.MATCHES_NONE, "Matches None");
        }
    };

    private final SingleSelectDropdown singleSelectDropdown;

    public AdvancedFilters(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(ADVANCED_FILTER_SECTION_XPATH));
        singleSelectDropdown = new SingleSelectDropdown(driver);
    }

    public AdvancedFilters selectFilterType(FilterType filterType, int ruleNumber) {
        initializeDropdownsAndDropdownMenusXPath(ruleNumber);
        openDropdownMenuAndSelectItem(filterTypeDropdownXPath, FILTER_TYPE_OPTIONS.get(filterType));
        return this;
    }

    public void applyFilters() {
        clickElement(APPLY_FILTERS_BUTTON);
        waitForElementToBeInvisible(APPLY_FILTERS_BUTTON, MAX_WAIT_TIME_SECS);
    }

    public void createPropertyRule(String property, String filterOperator, String operand, int ruleNumber) {
        initializeDropdownsAndDropdownMenusXPath(ruleNumber);
        openDropdownMenuAndSelectItem(secondDropdownXPath, property);
        openDropdownMenuAndSelectItem(thirdDropdownXPath, filterOperator);
        if (operand != null) {
            try {
                setText(operandInput, operand);
            } catch (Exception e) {
                setText(operandInput, operand);
            }
            applyFilters();
        }
    }

    public void createNewRule(FilterType firstDropdownOption, String secondDropdownOption, ThirdDropdownOption thirdDropdownOption, String filterText, int ruleNumber) throws InterruptedException {
        addFirstRuleIfNotPresent();
        initializeDropdownsAndDropdownMenusXPath(ruleNumber);
        openDropdownMenuAndSelectItem(filterTypeDropdownXPath, FILTER_TYPE_OPTIONS.get(firstDropdownOption));
        openDropdownMenuAndSelectItem(secondDropdownXPath, secondDropdownOption);
        openDropdownMenuAndSelectItem(thirdDropdownXPath, THIRD_DROPDOWN_OPTIONS.get(thirdDropdownOption));
        try {
            setText(operandInput, filterText);
        } catch (Exception e) {
            setText(operandInput, filterText);
        }
        applyFilters();
    }

    public List<String> getRuleSettings(int ruleNumber) {
        List<String> ruleSettings = new ArrayList<>();
        if (isAddFirstRuleButtonVisible()) {
            return ruleSettings;
        } else {
            initializeDropdownsAndDropdownMenusXPath(ruleNumber);
            List<By> dropDowns = asList(filterTypeDropdown, secondDropdown, thirdDropdown);
            for (By dropdown : dropDowns) {
                ruleSettings.add(getTextFromElementMilliseconds(dropdown));
            }
            return ruleSettings;
        }
    }

    //for destinationManagerTest
    public String getPropertyRuleOperand(int ruleNumber) {
        initializeDropdownsAndDropdownMenusXPath(ruleNumber);
        return getTextFromElement(thirdDropdown);
    }

    public String changeRuleOperatorForDestinationManagerTests(int ruleNumber) throws InterruptedException {
        initializeDropdownsAndDropdownMenusXPath(ruleNumber);
        scrollIntoView(thirdDropdown);
        String currentOperand = getPropertyRuleOperand(1);
        String newOperand = Objects.equals(currentOperand, "Contains") ? "Doesn't Contain" : "Contains";
        selectItemFromDropdown(thirdDropdown, newOperand);
        applyFilters();
        return newOperand;
    }

    public void addFirstRuleIfNotPresent() {
        if (isAddFirstRuleButtonVisible()) {
            clickAddFirstRuleButton();
        }
    }

    public void createRule(FilterType filterType, String filterOperator, int ruleNumber) {
        selectFilterType(filterType, ruleNumber);
        openDropdownMenuAndSelectItem(secondDropdownXPath, filterOperator);
        applyFilters();
    }

    public void addAndCreateFirstRule(FilterType filterType, String filterOperator, int ruleNumber) {
        addFirstRuleIfNotPresent();
        selectFilterType(filterType, ruleNumber);
        openDropdownMenuAndSelectItem(secondDropdownXPath, filterOperator);
        applyFilters();
    }

    public AdvancedFilters clickAddAnotherRuleButton() {
        clickElement(ADD_ANOTHER_RULE_BUTTON);
        return this;
    }

    public void openDropdownMenuAndSelectItem(String dropdownXPath, String itemToSelect) {
        var dropdownMenuXPath = "/../../..//following-sibling::div[contains(@class,'menu')]";
        singleSelectDropdown.openDropdownMenu(By.xpath(dropdownXPath), By.xpath(dropdownXPath + dropdownMenuXPath));
        singleSelectDropdown.selectOption(itemToSelect);
    }

    public AdvancedFilters deleteSpecificRule(int ruleNumber) {
        By deleteRuleIcon = By.xpath("//div[@data-qa='AdvancedFilters']//span[contains(normalize-space(), 'Rule') and contains(normalize-space(), '" + ruleNumber + "')]/parent::div/following-sibling::div//button[text()='Delete']");
        clickElement(deleteRuleIcon);
        return this;
    }

    public AdvancedFilters resetAdvancedFilters() {
        closeDropdownMenu();
        clickElement(RESET_BUTTON);
        return this;
    }

    public void cancelAdvancedFilters() {
        closeDropdownMenu();
        clickElement(CANCEL_BUTTON);
    }

    public AdvancedFilters closeDropdownMenu() {
        if (isElementVisible(DROPDOWN_MENU, MAX_WAIT_TIME_SECS)) {
            clickElement(ADVANCED_FILTERS_COMPONENT);
        }
        return this;
    }

    public int getRuleCount() {
        return getElementCount(RULES);
    }

    public void removeAllRules() {
        int ruleCount = getRuleCount();
        int defaultRuleNumber = 1;
        for (int index = 1; index <= ruleCount; index++) {
            deleteSpecificRule(defaultRuleNumber);
        }
        applyFilters();
    }

    public void clickAddFirstRuleButton() {
        clickElement(ADD_FIRST_RULE_BUTTON);
    }

    public boolean isAddFirstRuleButtonVisible() {
        return isElementVisible(ADD_FIRST_RULE_BUTTON, MAX_WAIT_TIME_SECS);
    }

    private void initializeDropdownsAndDropdownMenusXPath(int ruleNumber) {
        String dropdownsXPath = "(//div[@data-qa='AdvancedFilters']//span[contains(normalize-space(), 'Rule') and contains(normalize-space(), '" + ruleNumber + "')]/parent::div/following-sibling::div//input)";
        filterTypeDropdownXPath = dropdownsXPath + "[1]";
        secondDropdownXPath = dropdownsXPath + "[2]";
        thirdDropdownXPath = dropdownsXPath + "[3]";
        operandInputXPath = dropdownsXPath + "[last()]";

        filterTypeDropdown = By.xpath(filterTypeDropdownXPath);
        secondDropdown = By.xpath(secondDropdownXPath);
        thirdDropdown = By.xpath(thirdDropdownXPath);
        operandInput = By.xpath(operandInputXPath);
    }
}
