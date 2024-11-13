package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class NavigationMenuLegacy extends PageHandler {
    protected final By NAVIGATION_MENU = By.xpath("//div[@data-qa='Nav']");
    private final By INFORMATION_MAIN_TITLE = By.xpath( "//div[@class='_1yqe6eth undefined']" );
    private final By MENU_CLOSED = By.xpath( "//div[@id='primary-nav-toggle' and child::i[contains(@class,'fa-bars')]]" );
    private final By DASHBOARD_LINK = By.xpath( "//a[@href='/dashboard']" );
    private final By KEYWORD_SEARCH_LINK = By.xpath( "//a[@href='/keyword-search']" );
    private final By ATTRIBUTE_INSIGHTS_LINK = By.xpath( "//a[@href='/attribute-insights']" );
    private final By CATEGORY_BRANDSHARE_LINK = By.xpath( "//a[@href='/category-brandshare']" );
    private final By WATCHLISTS_LINK = By.xpath( "//a[@href='/watchlists']" );
    private final By TASKS_LINK = By.xpath( "//a[@href='/tasks']" );
    private final By PRODUCTS_LINK = By.xpath( "//a[@href='/products']" );
    private final By PRODUCT_LISTS_LINK = By.xpath( "//a[@href='/lists']" );
    private final By PRODUCT_ENHANCEMENTS_LINK = By.xpath( "//a[@href='/bulk-tagging']" );
    private final By PROPERTIES_LINK = By.xpath( "//a[@href='/properties']" );
    private final By PROPERTY_GROUPS_LINK = By.xpath( "//a[@href='/property-groups']" );
    private final By IMPORTS_LINK = By.xpath( "//a[@href='/imports']" );
    private final By DESTINATION_MANAGER_LINK = By.xpath( "//a[@href='/destination-manager']" );
    private final By API_ADMINISTRATION_LINK = By.xpath( "//a[@href='/api-admin']" );
    private final By REQUESTS_LINK = By.xpath( "//a[@href='/requests']" );
    protected final By TABLE_CONTAINER = By.xpath( "//div[@class='table-responsive dx-g-bs4-table-container']" );
    protected final By TABLE = By.xpath( "//div[@class='table-responsive dx-g-bs4-table-container']//table[2]" );
    protected final By DEFAULT_LOADER = By.xpath( "//div[@data-qa='LoadState']" );
    protected final By SUCCESS_MESSAGE = By.xpath( "//div[@data-qa='TypeComponent' and contains(@class,'note')]//div[contains(text(),'Success')]" +
            "| //div[contains(@class,'note') and descendant::i[contains(@class,'thumbs-up')]]"
            );

    protected final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);

    public NavigationMenuLegacy(WebDriver driver) {
        super(driver);
        findElementVisible(NAVIGATION_MENU);
    }

    public void openMenu() throws InterruptedException {
        if (isElementVisible(MENU_CLOSED, MAX_WAIT_TIME_SECS)) {
            click(MENU_CLOSED);
        }
    }

    public void closeSuccessMessage() throws InterruptedException {
        By successMessageXButton = By.xpath( "//div[@data-qa='TypeComponent' and contains(@class,'note')]//div[@data-qa='IconButton']" );
        if ( isElementVisible( successMessageXButton ) ) {
            click( successMessageXButton );
        }
    }

    public boolean isSuccessMessageDisplayed() {
        return isElementVisible(SUCCESS_MESSAGE, Duration.ofSeconds(20));
    }

    public boolean waitForNotesInvisibility(  ) {
        String errorNotes = "//div[@data-qa='TypeComponent' and contains(@class,'note')]//div[contains(text(),'Error')] | //div[@data-qa='Errors']";
        String successfulNotes = "//div[@data-qa='TypeComponent' and contains(@class,'note')]//div[contains(text(),'Success')]";
        By notes = By.xpath( errorNotes +  " | " + successfulNotes );
        return !isElementVisible(notes, Duration.ofSeconds(20));
    }

    public String getMainTitleText() {
        return findElementsPresent(INFORMATION_MAIN_TITLE).get(0).getText();
    }

    public DashboardPage clickDashboard() throws InterruptedException {
        openMenu();
        click(DASHBOARD_LINK);
        return new DashboardPage(driver);
    }
    public KeywordSearchPage clickKeywordSearch() throws InterruptedException {
        openMenu();
        click(KEYWORD_SEARCH_LINK);
        return new KeywordSearchPage(driver);
    }

    public AttributeInsightsPage clickAttributeInsights() throws InterruptedException {
        openMenu();
        click(ATTRIBUTE_INSIGHTS_LINK);
        return new AttributeInsightsPage(driver);
    }

    public CategoryBrandsharePage clickCategoryBrandshare() throws InterruptedException {
        openMenu();
        click(CATEGORY_BRANDSHARE_LINK);
        return new CategoryBrandsharePage(driver);
    }

    public WatchlistsPage clickWatchlists() throws InterruptedException {
        openMenu();
        click(WATCHLISTS_LINK);
        return new WatchlistsPage(driver);
    }

    public TasksPage clickTasks() throws Exception {
        openMenu();
        click(TASKS_LINK);
        return new TasksPage(driver);
    }

    public ProductsPageLegacy clickProducts() throws InterruptedException {
        openMenu();
        click(PRODUCTS_LINK);
        return new ProductsPageLegacy(driver);
    }

    public ProductListsPage clickProductLists() throws InterruptedException {
        openMenu();
        click(PRODUCT_LISTS_LINK);
        return new ProductListsPage(driver);
    }

    public ProductEnhancementPage clickProductEnhancement() throws InterruptedException {
        openMenu();
        click(PRODUCT_ENHANCEMENTS_LINK);
        return new ProductEnhancementPage(driver);
    }

    public PropertiesPage clickPropertiesPage() throws InterruptedException {
        openMenu();
        click(PROPERTIES_LINK);
        return new PropertiesPage(driver);
    }

    public ImportsPage clickImportsPage() throws InterruptedException {
        openMenu();
        click(IMPORTS_LINK);
        return new ImportsPage(driver);
    }

    public DestinationManagerPage clickDestinationManager() throws InterruptedException {
        openMenu();
        click(DESTINATION_MANAGER_LINK);
        return new DestinationManagerPage(driver);
    }

    public APIAdministrationPage clickAPIAdministration() throws InterruptedException {
        openMenu();
        click(API_ADMINISTRATION_LINK);
        return new APIAdministrationPage(driver);
    }

    public RequestsPage clickRequests() throws InterruptedException {
        openMenu();
        click(REQUESTS_LINK);
        return new RequestsPage(driver);
    }

    public PropertyGroupsPage clickPropertyGroups() throws InterruptedException {
        openMenu();
        click(PROPERTY_GROUPS_LINK);
        return new PropertyGroupsPage(driver);
    }
}
