package com.DC.pageobjects.adc.execute.destinations;

import com.DC.objects.insights.DestinationDefinitionSettings;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DestinationDefinitionSettingsSection extends InsightsNavigationMenu {

    private final By SETTINGS_BODY = By.xpath("//div[@data-qa='Settings']");
    private final By DESTINATION_NAME_INPUT = By.xpath("(//div[@data-qa='Settings']//span[text()='Destination Name']/following-sibling::div//input)[1]");
    private final By DESTINATION_DESCRIPTION_INPUT = By.xpath("(//div[@data-qa='Settings']//span[text()='Destination Description']/following-sibling::div//textarea)[1]");
    private final By DESTINATION_TYPE_DROPDOWN = By.xpath("(//span[text()='Destination Type']/following-sibling::div//input)[1]");
    private final By ASSOCIATED_RETAILER_DROPDOWN = By.xpath("(//span[text()='Associated Retailer']/following-sibling::div//input)[1]");
    private final By SELECT_DESTINATION_TEMPLATE_DROPDOWN = By.xpath("(//span[text()='Select a Destination Template']/following-sibling::div//input)[1]");
    private final By SELECT_DESTINATION_TEMPLATE_DROPDOWN_OPEN = By.xpath("//div[@data-qa='DestinationTemplateDD']//div[@data-qa='DropdownMenu']");
    private final By SELECT_DESTINATION_TEMPLATE_DROPDOWN_SEARCH = By.xpath("//div[@data-qa='DestinationTemplateDD']//div[@data-qa='DropdownMenu']//input");
    private final By ADD_NEW_TEMPLATE_BUTTON = By.xpath("//div[@data-qa='DestinationTemplateDD']//div[@data-qa='DropdownMenu']//button[text()='Add New Template']");
    private final By NEW_TEMPLATE_NAME_INPUT = By.xpath("//div[@data-qa='DestinationTemplateDD']//div[@data-qa='DropdownMenu']//input[@type='text']");
    private final By DESTINATION_TEMPLATE_UPLOAD = By.xpath("//div[@data-qa='DestinationTemplateDD']//div[@data-qa='DropdownMenu']//input[@type='file']");
    private final By SELECT_CLIENT_PERMISSIONS_DROPDOWN = By.xpath("//div[@data-qa='ClientPermissionsDD']");
    private final By SELECT_CLIENT_PERMISSIONS_DROPDOWN_APPLY_BUTTON = By.xpath("//div[@data-qa='ClientPermissionsDD']//button[text()='Apply']");
    private final By SELECT_CLIENT_PERMISSIONS_DROPDOWN_SEARCH = By.xpath("//div[@data-qa='ClientPermissionsDD']//input[@type='text']");
    private final By CREATE_DESTINATIONS_BUTTON = By.xpath("//div[@data-qa='BottomActionBar']//button[text()='Create']");


    public DestinationDefinitionSettingsSection(WebDriver driver) {
        super(driver);
        findElementVisible(SETTINGS_BODY);
    }

    public FieldDefinitionSection clickCreateDestinationsButton() throws InterruptedException {
        click(CREATE_DESTINATIONS_BUTTON);
        return new FieldDefinitionSection(driver);
    }

    public FieldDefinitionSection createDraftDestination(DestinationDefinitionSettings destinationDefinitionSettings) throws InterruptedException {
        insertDestinationName(destinationDefinitionSettings.destinationName);
        insertDestinationDescription(destinationDefinitionSettings.destinationDescription);
        if (destinationDefinitionSettings.isANewDestinationTemplate) {
            uploadDestinationTemplate(destinationDefinitionSettings.destinationTemplatePath, destinationDefinitionSettings.destinationTemplateName);
        } else {
            selectDestinationTemplateFromDropdown(destinationDefinitionSettings.destinationTemplateName);
        }
        selectRetailerFromDropdown(destinationDefinitionSettings.retailer);
        selectDestinationTypeFromDropdown(destinationDefinitionSettings.destinationType);
        selectClientPermissionsFromDropdown(destinationDefinitionSettings.client);
        // causes an element click intercepted if this is not clicked before creating destination
        click(By.xpath("//button[@id='hide-rc']"));
        return clickCreateDestinationsButton();
    }

    public void insertDestinationName(String destinationName) {
        scrollIntoView(DESTINATION_NAME_INPUT);
        setText(DESTINATION_NAME_INPUT, destinationName);
    }

    public void insertDestinationDescription(String descriptionText) {
        scrollIntoView(DESTINATION_DESCRIPTION_INPUT);
        setText(DESTINATION_DESCRIPTION_INPUT, descriptionText);
    }

    public boolean isSelectDestinationTemplateDropdownOpen() {
        return isElementVisible(SELECT_DESTINATION_TEMPLATE_DROPDOWN_OPEN);
    }

    public void openSelectDestinationTemplateDropdown() throws InterruptedException {
        if (!isSelectDestinationTemplateDropdownOpen()) {
            click(SELECT_DESTINATION_TEMPLATE_DROPDOWN);
        }
    }

    public boolean isSelectClientPermissionsDropdownOpen() {
        return isElementVisible(SELECT_CLIENT_PERMISSIONS_DROPDOWN_APPLY_BUTTON);
    }

    public void selectDestinationTemplateFromDropdown(String destinationTemplate) throws InterruptedException {
        openSelectDestinationTemplateDropdown();
        setText(SELECT_DESTINATION_TEMPLATE_DROPDOWN_SEARCH, destinationTemplate);
        click(By.xpath("//div[@id='items-list']//span[text()='" + destinationTemplate + "']"));
    }

    public void uploadDestinationTemplate(String destinationTemplatePath, String destinationTemplateName) throws InterruptedException {
        openSelectDestinationTemplateDropdown();
        click(ADD_NEW_TEMPLATE_BUTTON);
        setText(NEW_TEMPLATE_NAME_INPUT, destinationTemplateName);
        uploadFile(DESTINATION_TEMPLATE_UPLOAD, destinationTemplatePath);
        waitForElementToBeInvisible(SELECT_DESTINATION_TEMPLATE_DROPDOWN_OPEN);
    }

    public void selectClientPermissionsFromDropdown(String client) throws InterruptedException {
        if (!isSelectClientPermissionsDropdownOpen()) {
            click(SELECT_CLIENT_PERMISSIONS_DROPDOWN);
        }
        setText(SELECT_CLIENT_PERMISSIONS_DROPDOWN_SEARCH, client);
        click(By.xpath("//div[@data-qa='ClientPermissionsDD']//span[text()='" + client + "']"));
        click(SELECT_CLIENT_PERMISSIONS_DROPDOWN_APPLY_BUTTON);
    }

    public void selectDestinationTypeFromDropdown(String type) throws InterruptedException {
        click(DESTINATION_TYPE_DROPDOWN);
        click(By.xpath("//li[@role='menuitem' and text()='" + type + "']"));
    }

    public void selectRetailerFromDropdown(String retailer) throws InterruptedException {
        click(ASSOCIATED_RETAILER_DROPDOWN);
        click(By.xpath("//li[@role='menuitem' and text()='" + retailer + "']"));
    }
}
