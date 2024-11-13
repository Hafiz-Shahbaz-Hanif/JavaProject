package com.DC.pageobjects;

import io.qameta.allure.Allure;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PageHandler {

    public final WebDriver driver;

    public final Logger UI_LOGGER = Logger.getLogger(PageHandler.class);

    private final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(10);

    private final Duration DEFAULT_TIMEOUT_MILLISECONDS = Duration.ofMillis(500);

    public PageHandler(WebDriver driver) {
        this.driver = driver;
    }

    public void addScreenshotToAllureReport(String screenshotName) {
        UI_LOGGER.info("Taking Screenshot");
        Allure.addAttachment(screenshotName, new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
    }

    public <T> T getPage(Class<T> pageClass) {
        try {
            return pageClass.getDeclaredConstructor(WebDriver.class).newInstance(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Actions getActions() {
        return new Actions(driver);
    }

    public WebDriverWait getWait() {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT_SECONDS);
    }

    public WebDriverWait getWait(Duration duration) {
        return new WebDriverWait(driver, duration);
    }

    public void executeJavascript(String js) {
        ((JavascriptExecutor) driver).executeScript(js);
    }

    public double getHeightOfElement(By locator) {
        String str = executeJavascript("return arguments[0].scrollHeight;", locator).toString();
        return Double.parseDouble(str);
    }

    public double getWidthOfElement(By locator) {
        String str = executeJavascript("return arguments[0].scrollWidth;", locator).toString();
        return Double.parseDouble(str);
    }

    public double getPixelsToScroll(By locatorOfElementToScroll, int maxTimesToScroll) {
        double scrollBarHeight = getHeightOfElement(locatorOfElementToScroll);
        return scrollBarHeight / maxTimesToScroll;
    }

    public double getPixelsToScrollHorizontal(By locatorOfElementToScroll, int maxTimesToScroll) {
        double scrollBarWidth = getWidthOfElement(locatorOfElementToScroll);
        return scrollBarWidth / maxTimesToScroll;
    }

    public void scrollToTopOfPage() {
        executeJavascript("window.scrollTo(0, 0);");
    }

    public void scrollToBottomOfPage() {
        executeJavascript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollVerticallyToCenterOfPage() {
        executeJavascript("window.scrollTo(0, document.body.scrollHeight/2);");
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    public <T> T refreshPage(Class<T> pageClass) {
        driver.navigate().refresh();
        return getPage(pageClass);
    }

    public void navigateToUrl(String url) {
        driver.navigate().to(url);
    }

    public <T> T navigateToUrl(String url, Class<T> pageClass) {
        driver.navigate().to(url);
        return getPage(pageClass);
    }

    public void navigateBack() throws InterruptedException {
        driver.navigate().back();
        Thread.sleep(1000);
    }

    public boolean isElementVisible(By locator, Duration timeout) {
        try {
            findElementVisible(locator, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementVisible(By locator) {
        return isElementVisible(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementNotVisible(By locator, Duration timeout) {
        try {
            waitForElementToBeInvisible(locator, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementNotVisible(By locator) {
        return isElementNotVisible(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementVisibleMilliseconds(By locator) {
        return isElementVisible(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public boolean isElementPresent(By locator, Duration timeout) {
        try {
            findElementPresent(locator, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementPresent(By locator) {
        return isElementPresent(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementPresentMilliseconds(By locator) {
        return isElementPresent(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void waitForElementToBeInvisible(By locator, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForElementToBeInvisible(By locator) {
        waitForElementToBeInvisible(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public void waitForElementToBeInvisibleInMilliseconds(By locator) {
        waitForElementToBeInvisible(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void waitForElementClickable(By locator, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForElementClickable(By locator) {
        waitForElementClickable(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public void waitForElementNonClickable(By locator, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)));
    }

    public void waitForElementNonClickable(By locator) {
        waitForElementNonClickable(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementClickable(By locator, Duration timeout) {
        try {
            waitForElementClickable(locator, timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementClickable(By locator) {
        return isElementClickable(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public WebElement findElementVisible(By locator, Duration timeout) {
        return getWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement findElementVisible(By locator) {
        return findElementVisible(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public WebElement findElementVisibleMilliseconds(By locator) {
        return findElementVisible(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public List<WebElement> findElementsVisible(By locator, Duration timeout, String elementDescription) {
        elementDescription = elementDescription == null ? "" : " : " + elementDescription;
        List<WebElement> elements = new ArrayList<>();
        try {
            elements = getWait(timeout).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            UI_LOGGER.info("** Found elements visible" + elementDescription + " **");
            return elements;
        } catch (Exception e) {
            UI_LOGGER.info("** Elements were not found" + elementDescription + " **");
            return elements;
        }
    }

    public List<WebElement> findElementsVisible(By locator, Duration timeout) {
        try {
            return getWait(timeout).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<WebElement> findElementsVisible(By locator) {
        return findElementsVisible(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public List<WebElement> findElementsVisible(By locator, String elementDescription) {
        return findElementsVisible(locator, DEFAULT_TIMEOUT_SECONDS, elementDescription);
    }

    public List<WebElement> findElementsVisibleMilliseconds(By locator) {
        return findElementsVisible(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public WebElement findElementPresent(By locator, Duration timeout) {
        return getWait(timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement findElementPresent(By locator) {
        return findElementPresent(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public WebElement findElementPresentMilliseconds(By locator) {
        return findElementPresent(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public List<WebElement> findElementsPresent(By locator, Duration timeout) {
        try {
            return getWait(timeout).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<WebElement> findElementsPresent(By locator) {
        return findElementsPresent(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public List<WebElement> findElementsPresentMilliseconds(By locator) {
        return findElementsPresent(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public WebElement findElementWithinAnotherElement(WebElement parentElement, By locator, Duration timeout) {
        return getWait(timeout).until(ExpectedConditions.visibilityOf(parentElement.findElement(locator)));
    }

    public List<WebElement> findElementsWithinAnotherElement(WebElement parentElement, By locator, Duration timeout) {
        try {
            return getWait(timeout).until(ExpectedConditions.visibilityOfAllElements(parentElement.findElements(locator)));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<WebElement> findElementsWithinAnotherElement(WebElement parentElement, By locator) {
        return findElementsWithinAnotherElement(parentElement, locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementEnabled(By locator, Duration timeout) {
        try {
            WebElement element = findElementPresent(locator, timeout);
            String elementCursor = element.getCssValue("cursor");
            if (elementCursor.equals("not-allowed")) {
                return false;
            }
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        return isElementEnabled(locator, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementEnabledMilliseconds(By locator) {
        return isElementEnabled(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void waitForElementToBeEnabled(By locator, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean isCursorAllowed(WebElement element) {
        return !element.getCssValue("cursor").equals("not-allowed");
    }

    public boolean isCursorAllowed(By locator) {
        WebElement element = findElementVisible(locator);
        return isCursorAllowed(element);
    }

    private void hoverOver(WebElement element) throws InterruptedException {
        Thread.sleep(300);
        Actions actions = getActions();
        actions.moveToElement(element).perform();
    }

    public void hoverOverElement(WebElement element) {
        try {
            hoverOver(element);
        } catch (Exception e) {
            UI_LOGGER.info("** Error trying to hover over element **");
        }
    }

    public void hoverOverElement(By locator) {
        try {
            WebElement element = findElementVisibleMilliseconds(locator);
            hoverOver(element);
        } catch (Exception e) {
            UI_LOGGER.info("** Error trying to hover over: " + locator.toString() + " **");
        }
    }

    public void performControlClickOnElement(By locator) {
        getActions().keyDown(Keys.CONTROL)
                .click(findElementVisibleMilliseconds(locator))
                .keyUp(Keys.CONTROL)
                .perform();
    }

    public void clickElement(By locator) {
        WebElement element = findElementPresent(locator);
        hoverOverElement(locator); // Hover over element to avoid failures in headless mode
        element.click();
    }

    public void click(By locator) throws InterruptedException {
        String errorPrefix = "** Exception while clicking: ";
        try {
            clickElement(locator);
        } catch (StaleElementReferenceException e) {
            UI_LOGGER.info(errorPrefix + "StaleElementReferenceException **");
            Thread.sleep(500);
            clickElement(locator);
        } catch (ElementClickInterceptedException e) {
            UI_LOGGER.info(errorPrefix + "ElementClickInterceptedException **");
            scrollToTopOfPage();
            clickElement(locator);
        } catch (ElementNotInteractableException e) {
            UI_LOGGER.info(errorPrefix + "ElementNotInteractableException **");
            refreshPage();
            findElementVisible(locator, Duration.ofSeconds(15));
            clickElement(locator);
        }
    }

    public void clickToCoordinates(int xOffset, int yOffset) {
        Actions actions = getActions();
        actions.moveByOffset(xOffset, yOffset).click().build().perform();
        actions.click().build().perform();
    }

    public void doubleClick(By locator) {
        getActions().doubleClick(findElementVisible(locator)).build().perform();
    }

    protected Object executeJavascript(String js, By locator) {
        return ((JavascriptExecutor) driver).executeScript(js, findElementPresent(locator));
    }

    protected Object executeJavascript(String js, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript(js, element);
    }

    public void clickElementUsingJavascriptExecutor(By locator) {
        WebElement element = findElementPresent(locator);
        clickElementUsingJavascriptExecutor(element);
    }

    public void clickElementUsingJavascriptExecutor(WebElement element) {
        executeJavascript("arguments[0].click();", element);
    }

    public void scrollToElement(By locator) {
        WebElement element = findElementPresent(locator);
        Point p = element.getLocation();
        executeJavascript("window.scrollTo(" + p.x + "," + p.y + ");");

        int elementHeight = element.getSize().getHeight();

        int windowHeight = driver.manage().window().getSize().getHeight();
        int scrollOffset = p.y - (windowHeight / 2) + (elementHeight / 2);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, arguments[0]);", scrollOffset);
    }

    public void scrollAndClick(By locator) throws InterruptedException {
        scrollToElement(locator);
        click(locator);
    }

    public void scrollIntoView(By locator) {
        executeJavascript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'nearest'})", findElementPresent(locator));
        waitForDOMStabilization();
    }

    public void scrollIntoViewAndClick(By locator) throws InterruptedException {
        scrollIntoView(locator);
        click(locator);
    }

    public void scrollIntoView(WebElement element) {
        executeJavascript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'nearest'})", element);
    }

    public void scrollIntoViewAndClick(WebElement element) {
        scrollIntoView(element);
        element.click();
    }

    public WebElement scrollRightToElement(By scrollbarLocator, By elementLocator, int maxTimesToMove) {
        boolean elementExists = isElementPresentMilliseconds(elementLocator);
        if (!elementExists) {
            throw new NotFoundException("Element doesn't exist");
        }
        double scrollBarCurrentPosition;
        double pixelsToScroll = getPixelsToScrollHorizontal(scrollbarLocator, maxTimesToMove);
        do {
            scrollBarCurrentPosition = getHorizontalScrollPosition(scrollbarLocator);
            boolean elementIsVisible = isElementVisibleMilliseconds(elementLocator);
            if (elementIsVisible) {
                return findElementVisibleMilliseconds(elementLocator);
            }
            scrollElementHorizontally(pixelsToScroll, scrollbarLocator);
        } while (scrollBarCurrentPosition != getHorizontalScrollPosition(scrollbarLocator));
        throw new NotFoundException("Element: " + elementLocator.toString() + " was not visible after scrolling horizontally " + maxTimesToMove + " times");
    }

    public void scrollToCenterIfTableScrollable(By tableLocator) {
        waitForDOMStabilization();

        boolean containerScrollable = isContainerScrollable(tableLocator);
        if (containerScrollable) {
            scrollMainBarToCenterAndInnerBarToTop(tableLocator);
        } else {
            scrollToTopOfPage();
        }
    }

    public void scrollDownToElement(By scrollbarLocator, By elementLocator, int maxTimesToMove, String errorMsg) {
        boolean elementExists = isElementPresentMilliseconds(elementLocator);
        if (elementExists) {
            scrollIntoView(elementLocator);
            return;
        }

        scrollToCenterIfTableScrollable(scrollbarLocator);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(scrollbarLocator, maxTimesToMove);
        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(scrollbarLocator);
            boolean rowIsPresent = isElementPresentMilliseconds(elementLocator);
            if (rowIsPresent) {
                scrollIntoView(elementLocator);
                return;
            }
            scrollElementVertically(pixelsToScroll, scrollbarLocator);
        } while (currentPosition != getVerticalScrollPosition(scrollbarLocator));
        throw new NotFoundException(errorMsg);
    }

    public void scrollElementToTop(By locator) {
        WebElement element = findElementVisible(locator);
        executeJavascript("arguments[0].scrollTo(0,0);", element);
    }

    public void scrollElementVertically(double pixelsToScroll, By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollBy(0,arguments[1]);", findElementVisible(locator), String.valueOf(pixelsToScroll));
    }

    public void scrollElementHorizontally(double pixelsToScroll, By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollBy(arguments[1],0);", findElementVisible(locator), String.valueOf(pixelsToScroll));
    }

    public double getVerticalScrollPosition(By locator) {
        WebElement element = findElementVisible(locator);
        String str = executeJavascript("return arguments[0].scrollTop;", element).toString();
        return Double.parseDouble(str);
    }

    public double getHorizontalScrollPosition(By locator) {
        WebElement element = findElementVisible(locator);
        String str = executeJavascript("return arguments[0].scrollLeft;", element).toString();
        return Double.parseDouble(str);
    }

    public void scrollBarToLeftEnd(By locator) {
        WebElement element = findElementVisible(locator);
        executeJavascript("return arguments[0].scrollLeft = 0;", element);
    }

    public void scrollMainBarToCenterAndInnerBarToTop(By locator) {
        findElementVisible(locator);
        scrollVerticallyToCenterOfPage();
        scrollElementToTop(locator);
    }

    public boolean isContainerScrollable(By containerLocator) {
        WebElement container = findElementVisibleMilliseconds(containerLocator);
        int scrollHeight = Integer.parseInt((container.getAttribute("scrollHeight")));
        int offsetHeight = Integer.parseInt((container.getAttribute("offsetHeight")));
        return scrollHeight > offsetHeight;
    }

    public void hitEscKey() {
        getActions().sendKeys(Keys.ESCAPE).perform();
    }

    public List<String> getTextFromElements(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getTextFromElements(By locator) {
        return findElementsPresent(locator).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getTextFromElementsMilliseconds(By locator) {
        List<WebElement> elements = findElementsPresentMilliseconds(locator);
        return getTextFromElements(elements);
    }

    public String getTextFromElementMilliseconds(By locator) {
        return findElementVisibleMilliseconds(locator).getText();
    }

    public String getTextFromPresentElement(By locator, Duration timeout) {
        return findElementPresent(locator, timeout).getText();
    }

    public String getTextFromPresentElement(By locator) {
        return findElementPresent(locator, DEFAULT_TIMEOUT_SECONDS).getText();
    }

    public String getTextFromElement(By locator, Duration timeout) {
        return findElementVisible(locator, timeout).getText();
    }

    public int getElementCount(By locator) {
        return findElementsVisible(locator).size();
    }

    public int getElementCount(By locator, Duration timeout) {
        return findElementsPresent(locator, timeout).size();
    }

    public int getElementCountMilliseconds(By locator) {
        return findElementsPresent(locator, DEFAULT_TIMEOUT_MILLISECONDS).size();
    }

    public void clickOnPageBody() throws InterruptedException {
        click(By.tagName("body"));
    }

    public void sendKeys(By locator, String text) {
        findElementVisible(locator).sendKeys(text);
    }

    public void sendKeysAndHitEnter(By locator, String text) {
        WebElement element = findElementVisible(locator);
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
    }

    public void clearInput(By locator) {
        WebElement element = findElementVisibleMilliseconds(locator);
        element.sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE);
    }

    public void setTextAndHitEnter(By locator, String text) {
        clearInput(locator);
        sendKeysAndHitEnter(locator, text);
    }

    public void setText(By locator, String text) {
        clearInput(locator);
        sendKeys(locator, text);
    }

    public void setText(WebElement element, String text) {
        WebElement el = getWait().until(ExpectedConditions.visibilityOf(element));
        el.sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE);
        el.sendKeys(text);
    }

    public void selectOptionFromDropdownByText(By locator, String text) {
        WebElement element = findElementVisible(locator);
        new Select(element).selectByVisibleText(text);
    }

    public void selectOptionFromDropdownByIndex(By locator, int index) {
        WebElement element = findElementVisible(locator);
        new Select(element).selectByIndex(index);
    }

    public String getSelectedOptionFromDropdown(By locator) {
        findElementVisible(locator);
        WebElement element = findElementVisible(locator);
        return new Select(element).getFirstSelectedOption().getText();
    }

    public void selectItemFromDropdown(By dropdownLocator, String text) throws InterruptedException {
        click(dropdownLocator);
        By dropdownTextXpath = By.xpath("//div[text()='" + text + "']");
        click(dropdownTextXpath);
    }

    public void selectItemFromDropdownForListElements(By dropdownLocator, String text) throws InterruptedException {
        click(dropdownLocator);
        By dropdownTextXpath = By.xpath("//li[text()='" + text + "']");
        click(dropdownTextXpath);
    }

    public void selectItemsFromDropdownFilter(By dropdownLocator, List<String> optionsToSelect) throws
            InterruptedException {
        findElementVisible(dropdownLocator);
        click(dropdownLocator);
        optionsToSelect.forEach(
                line -> {
                    String searchText = line.contains("'") ? line.replace("'", "''") : line;
                    By dropdownTextXpath = By.xpath(String.format("//span[text()='%s']", searchText));
                    try {
                        click(dropdownTextXpath);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public List<String> openSelectedFilterAndGetAllValues(By filterDropdown, By dropdownElementXpath) throws
            InterruptedException {
        click(filterDropdown);
        List<String> filterValues = getTextFromElements(findElementsVisible(dropdownElementXpath));
        click(filterDropdown);

        return filterValues;
    }

    public void waitTextInElementToBe(By locator, String text, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.textToBe(locator, text));
    }

    public void waitTextInElementToMatch(By locator, Pattern pattern, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.textMatches(locator, pattern));
    }

    public void waitTextInElementToBe(By locator, String text) {
        waitTextInElementToBe(locator, text, DEFAULT_TIMEOUT_SECONDS);
    }

    public void waitForTextOfElementToChange(By locator, String text, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }

    public void waitForTextOfElementToChange(By locator, String text) {
        waitForTextOfElementToChange(locator, text, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isElementSelected(By locator) {
        return findElementPresentMilliseconds(locator).isSelected();
    }

    public void selectAllElements(By locator, Duration timeout) {
        List<WebElement> checkboxesElements = findElementsPresent(locator, timeout);
        for (WebElement checkbox : checkboxesElements) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }

    public void deselectAllElements(By locator, Duration timeout) {
        List<WebElement> checkboxesElements = findElementsPresent(locator, timeout);
        for (WebElement checkbox : checkboxesElements) {
            if (checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }

    public void deselectAllElements(By locator) {
        deselectAllElements(locator, DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void deselectElement(By locator) {
        WebElement element = findElementPresentMilliseconds(locator);
        if (element.isSelected()) {
            element.click();
        }
    }

    public void selectElement(By locator) {
        WebElement checkbox = findElementPresentMilliseconds(locator);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    public void moveToElement(WebElement element) {
        getActions().moveToElement(element).perform();
    }

    public void moveToElement(By locator) {
        WebElement element = findElementPresentMilliseconds(locator);
        moveToElement(element);
    }

    public void moveToElementAndClick(By locator) {
        moveToElement(locator);
        clickElement(locator);
    }

    public void moveOutOfElement(By locator) {
        String jse = "var event = new MouseEvent(\"mouseout\",{" +
                "\"view\": window," +
                "\"bubbles\": true});" +
                "arguments[0].dispatchEvent(event);";
        executeJavascript(jse, locator);
    }

    public void switchToTab(int totalTabCountExpected, int tabIndexToSwitchTo) {
        getWait().until(ExpectedConditions.numberOfWindowsToBe(totalTabCountExpected));
        Set<String> windowHandles = driver.getWindowHandles();
        List<String> handles = new ArrayList<>(windowHandles);
        driver.switchTo().window(handles.get(tabIndexToSwitchTo));
        UI_LOGGER.info("Switched to tab with index: " + tabIndexToSwitchTo + " **");
    }

    protected void switchToTab(int tabIndex) {
        driver.switchTo().window(driver.getWindowHandles().toArray()[tabIndex].toString());
    }

    protected WebDriver switchToTab(String windowHandle) {
        return driver.switchTo().window(windowHandle);
    }

    protected void switchToMainTab() {
        switchToTab(0);
    }

    public void switchToMainTabAndRefreshPage() {
        switchToMainTab();
        refreshPage();
    }

    public WebDriver openNewTabAndSwitchToMakeItActive(String url) {
        executeJavascript("window.open('','_blank');");
        Set<String> windowHandles = driver.getWindowHandles();
        List<String> handles = new ArrayList<>(windowHandles);
        driver.switchTo().window(handles.get(handles.size() - 1));
        driver.navigate().to(url);
        return driver;
    }

    public void waitForNewTabToOpen(int expectedTabs, Duration timeout) {
        try {
            getWait(timeout).until(ExpectedConditions.numberOfWindowsToBe(expectedTabs));
        } catch (Exception e) {
            UI_LOGGER.info("** Error: New tab is not opened **");
            throw e;
        }
    }

    public void waitForNewTabToOpen(int expectedTabs) {
        waitForNewTabToOpen(expectedTabs, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean isPopupPresent() {
        try {
            getWait().until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void acceptPopup() {
        boolean popupPresent = isPopupPresent();
        if (popupPresent) {
            driver.switchTo().alert().accept();
        }
    }

    public String getAttribute(By locator, String attributeName) {
        return findElementPresent(locator).getAttribute(attributeName);
    }

    public String waitUntilAttributeValuePresentInElement(By locator, String attributeName) throws
            InterruptedException {
        String value = getAttribute(locator, attributeName);
        int count = 0;
        while (value == null || value.isEmpty()) {
            Thread.sleep(500);
            value = getAttribute(locator, attributeName);
            if (count == 5) {
                break;
            }
            count++;
        }
        return value;
    }

    public String getCssValue(By locator, String cssValue) {
        return findElementVisible(locator).getCssValue(cssValue);
    }

    public void zoomInOrOutTo(String percentage) {
        UI_LOGGER.info("Zooming: " + percentage + "%");
        executeJavascript("document.body.style.zoom='" + percentage + "%'");
    }

    public void uploadFile(By target, String filePath) throws InterruptedException {
        findElementPresent(target).sendKeys(filePath);
        Thread.sleep(3000);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void dragAndDrop(By source, By target) {
        WebElement sourceElement = findElementPresentMilliseconds(source);
        WebElement targetElement = findElementPresentMilliseconds(target);
        getActions().dragAndDrop(sourceElement, targetElement).build().perform();
    }

    public void dragAndDrop_SecondAlternative(By source, By target) {
        WebElement sourceElement = findElementPresentMilliseconds(source);
        WebElement targetElement = findElementPresentMilliseconds(target);
        getActions().clickAndHold(sourceElement)
                .moveByOffset(10, 0)
                .moveByOffset(-10, 0)
                .moveToElement(targetElement)
                .release()
                .build().perform();
        waitForDOMStabilization();
    }

    public void dragAndDropBy(By source, int xOffset, int yOffset) {
        WebElement sourceElement = findElementPresentMilliseconds(source);
        getActions().dragAndDropBy(sourceElement, xOffset, yOffset).perform();
    }

    public void closeCurrentTabAndSwitchToMainTab() {
        driver.close();
        switchToMainTab();
    }

    public String getTextFromElement(By locator) {
        return findElementVisible(locator).getText();
    }

    public String getLocalStorageItemValue(String key) {
        return (String) ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem('" + key + "');");
    }

    public void waitForElementCountToEqual(By locator, int expectedCount, Duration timeout) {
        getWait(timeout).until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    public boolean isElementChecked(By locator, Duration timeout) {
        WebElement element = findElementVisible(locator, timeout);
        String className = element.getAttribute("class");
        return className.contains("checked");
    }

    public WebElement findElementVisibleNearRelativeElement(By targetLocator, By relativeLocator) {
        By locator = RelativeLocator.with(targetLocator).near(relativeLocator);
        return findElementVisible(locator);
    }

    public WebElement findElementVisibleAboveRelativeElement(By targetLocator, By relativeLocator) {
        By locator = RelativeLocator.with(targetLocator).above(relativeLocator);
        return findElementVisible(locator);
    }

    public WebElement findElementVisibleToLeftOfRelativeElement(By targetLocator, By relativeLocator) {
        By locator = RelativeLocator.with(targetLocator).toLeftOf(relativeLocator);
        return findElementVisible(locator);
    }

    public WebElement findElementVisibleToRightOfRelativeElement(By targetLocator, By relativeLocator) {
        By locator = RelativeLocator.with(targetLocator).toRightOf(relativeLocator);
        return findElementVisible(locator);
    }

    public void waitForDOMStabilization(Duration stabilizationTimeout) {
        WebDriverWait wait = getWait(stabilizationTimeout);
        long startTime = System.currentTimeMillis();
        String previousDOMState = getCurrentDOMState();

        while ((System.currentTimeMillis() - startTime) / 1000 < stabilizationTimeout.getSeconds()) {
            String currentDOMState = getCurrentDOMState();
            if (currentDOMState.equals(previousDOMState)) {
                break; // DOM has stabilized
            }
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));
            previousDOMState = currentDOMState;
        }
    }

    public void waitForDOMStabilization() {
        waitForDOMStabilization(DEFAULT_TIMEOUT_SECONDS);
    }

    private String getCurrentDOMState() {
        return (String) ((JavascriptExecutor) driver).executeScript("return document.documentElement.innerHTML");
    }

    public String getAttribute(WebElement element, String attributeName) {
        getWait().until(ExpectedConditions.attributeToBeNotEmpty(element, attributeName));
        return element.getAttribute(attributeName);
    }

    public void clearLocalStorageItem(String key) {
        ((JavascriptExecutor) driver).executeScript("localStorage.removeItem('" + key + "');");
    }

    public static void typeInField(WebElement element, String value) {
        element.clear();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            String s = String.valueOf(c);
            element.sendKeys(s);
        }
    }
}