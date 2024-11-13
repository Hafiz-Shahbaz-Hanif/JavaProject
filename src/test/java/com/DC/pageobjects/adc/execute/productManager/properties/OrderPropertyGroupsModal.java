package com.DC.pageobjects.adc.execute.productManager.properties;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPropertyGroupsModal extends InsightsNavigationMenu {
    protected final String MODAL_BODY_XPATH = "//div[@data-qa='OrderPropertyGroups']";
    protected final By SAVE_BUTTON_LOCATOR = By.xpath("//button[text()='Save Group Order']");

    public OrderPropertyGroupsModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(By.xpath(MODAL_BODY_XPATH));
    }

    public void rearrangeStandardPropertyGroups(List<String> propertyGroups) throws Exception {
        for (int i = 0; i < propertyGroups.size(); i++) {
            rearrangePropertyPosition(propertyGroups.get(i), i + 1, Enums.PropertyGroupType.STANDARD);
        }
    }

    public void rearrangeDigitalAssetPropertyGroups(List<String> propertyGroups) throws Exception {
        for (int i = 0; i < propertyGroups.size(); i++) {
            rearrangePropertyPosition(propertyGroups.get(i), i + 1, Enums.PropertyGroupType.DIGITAL_ASSET);
        }
    }

    public void rearrangePropertyGroups(LinkedHashMap<List<String>, Enums.PropertyGroupType> propertyGroups) throws Exception {
        for (Map.Entry<List<String>, Enums.PropertyGroupType> group : propertyGroups.entrySet()) {
            if (group.getValue() == Enums.PropertyGroupType.DIGITAL_ASSET) {
                rearrangeDigitalAssetPropertyGroups(group.getKey());
            } else {
                rearrangeStandardPropertyGroups(group.getKey());
            }
        }
    }

    public void rearrangePropertyPosition(String groupToMove, int position, Enums.PropertyGroupType groupType) throws Exception {
        By dragIconLocator = By.xpath(MODAL_BODY_XPATH + "//div[text()='" + groupToMove + "']");
        By newPositionLocator = By.xpath("(//h3[contains(text(),'" + groupType.getGroupType() + "')]//following-sibling::div//div[@draggable])[" + position + "]");

        dragAndDrop_SecondAlternative(dragIconLocator, newPositionLocator);
        waitForDOMStabilization(MAX_WAIT_TIME_SECS);
        throwErrorIfPositionIsNotCorrect(groupToMove, position, groupType);
    }

    public PropertiesPage saveGroupOrder() {
        clickElement(SAVE_BUTTON_LOCATOR);
        waitForElementToBeInvisibleInMilliseconds(SAVE_BUTTON_LOCATOR);
        return new PropertiesPage(driver);
    }

    public List<String> getStandardGroupsDisplayed() {
        By groupLocator = By.xpath("(//h3[contains(text(),'Standard')]//following-sibling::div//div[@draggable])//div[text()]");
        return getTextFromElementsMilliseconds(groupLocator);
    }

    public List<String> getDigitalAssetGroupsDisplayed() {
        By groupLocator = By.xpath("(//h3[contains(text(),'Digital Asset')]//following-sibling::div//div[@draggable])//div[text()]");
        return getTextFromElementsMilliseconds(groupLocator);
    }

    private void throwErrorIfPositionIsNotCorrect(String groupToMove, int position, Enums.PropertyGroupType groupType) throws Exception {
        By groupLocator = By.xpath("(//h3[contains(text(),'" + groupType.getGroupType() + "')]//following-sibling::div//div[@draggable])//div[text()]");
        int positionOfGroup = getTextFromElementsMilliseconds(groupLocator).indexOf(groupToMove) + 1;
        boolean groupInCorrectPosition = positionOfGroup == position;
        if (!groupInCorrectPosition) {
            throw new Exception("The " + groupType + " group " + groupToMove + " was not moved to correct position\nExpected position: " + position + ".\nActual position: " + positionOfGroup);
        }
    }
}
