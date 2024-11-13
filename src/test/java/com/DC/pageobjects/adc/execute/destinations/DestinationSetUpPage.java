package com.DC.pageobjects.adc.execute.destinations;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DestinationSetUpPage extends InsightsNavigationMenu {

    private final By PAGE_MAIN_TABLE = By.xpath("//div[@data-cy='destination_definitions']");
    private final By CREATE_DEFINITIONS_BUTTON = By.xpath("//button[@data-qa='Button' and text()='Create Definition']");
    private final By DESTINATION_SETUP_SEARCH_BAR = By.xpath("//div[@data-qa='DestinationSetups']//input[@placeholder='Search...']");
    private final By DESTINATION_SETUP_FILTER_DROPDOWN = By.xpath("//div[@data-qa='DestinationSetups']//input[@placeholder='Type to filter']");
    private final By DELETE_DESTINATION_BUTTON_IN_MODAL = By.xpath("//button[text()='Delete Destination']");

    public DestinationSetUpPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_MAIN_TABLE);
    }

    public void searchForDestination(String destinationName) {
        clearInput(DESTINATION_SETUP_SEARCH_BAR);
        setTextAndHitEnter(DESTINATION_SETUP_SEARCH_BAR, destinationName);
    }

    public boolean doesDestinationExist(String destinationName) {
        By destinationNameInCell = By.xpath("//div[@data-cy='destination_definitions']//tr//td[@colspan='1']//span[text()='" + destinationName + "']");
        searchForDestination(destinationName);
        return isElementVisibleMilliseconds(destinationNameInCell);
    }

    public void deleteDraftDestination(String destinationName) throws InterruptedException {
        By destinationNameInCell = By.xpath("//div[@data-cy='destination_definitions']//tr//td[@colspan='1']//span[text()='" + destinationName + "']");
        By deleteDestinationButton = By.xpath("//div[@data-cy='destination_definitions']//tr//td[@colspan='1']//span[text()='" + destinationName + "']/parent::td/following-sibling::td//button");
        searchForDestination(destinationName);
        isElementVisible(destinationNameInCell);
        click(deleteDestinationButton);
        click(DELETE_DESTINATION_BUTTON_IN_MODAL);
    }

    public DestinationDefinitionSettingsSection clickCreateDefinitionsButton() throws InterruptedException {
        click(CREATE_DEFINITIONS_BUTTON);
        return new DestinationDefinitionSettingsSection(driver);
    }
}
