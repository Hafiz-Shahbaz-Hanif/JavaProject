package com.DC.pageobjects.legacy.legacyinsights;

import com.DC.objects.insightslegacy.ProductBasicSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsPageLegacy extends NavigationMenuLegacy {

    private final By ADD_PRODUCT_BUTTON = By.xpath("//div[@data-qa='Products']//span[text()='Add Product']");
    private final String PRODUCTS_TABLE_XPATH = "//table[@class='table dx-g-bs4-table']";
    public final By PRODUCT_LEVEL_DROPDOWN = By.xpath( "//div[@data-qa='Select' and ancestor::div[@data-qa='LinkBar']]/div" );
    private final By GENERATE_LAUNCH_FILE_BUTTON = By.xpath( "//div[@data-qa='BottomActionBar']//span[text()='Generate Launch File']" );
    protected final By SEARCH_INPUT = By.xpath( "//div[@data-qa='ProductPageActionBar']//input" );
    protected final By SEARCH_PRODUCT_ICON = By.xpath( "//div[@data-qa='ProductPageActionBar']//form[@data-qa='SingleInputForm']//i" );
    protected final By REMOVE_SEARCH_ICON = By.xpath( "//div[@data-qa='FilterChips']//div[div[@data-qa='TypeComponent' and contains(text(),'Search - contains')]]/following-sibling::span" );

    public ProductsPageLegacy(WebDriver driver) {
        super(driver);
        findElementVisible(ADD_PRODUCT_BUTTON);
        Assert.assertEquals("Products | OneSpace", driver.getTitle());
    }

    public String getCurrentProductLevel() {
        return getTextFromElement( PRODUCT_LEVEL_DROPDOWN );
    }

    public ProductsPageLegacy selectProductLevel( String productLevel ) throws InterruptedException {
        String currentLevel = getCurrentProductLevel();
        if ( currentLevel != productLevel ) {
            selectItemFromDropdown( PRODUCT_LEVEL_DROPDOWN, productLevel );
        }
        waitForElementToBeInvisible(DEFAULT_LOADER);
        return this;
    }

    public ProductsPageLegacy selectProduct( String uniqueId, String level ) throws InterruptedException {
        selectProductLevel( level );
        By productCheckbox = By.xpath( PRODUCTS_TABLE_XPATH + "//input[parent::td[following-sibling::td[child::a[text()=" + uniqueId + "]]]]");
        try {
            selectElement( productCheckbox );
        } catch ( Exception exception ) {
            selectElement( productCheckbox );
        }
        return this;
    }

    public ProductsPageLegacy selectProducts( List<String> uniqueIds, String level ) throws InterruptedException {
        selectProductLevel( level );
        for ( String uniqueId : uniqueIds ) {
            By productCheckbox = By.xpath( PRODUCTS_TABLE_XPATH + "//input[parent::td[following-sibling::td[child::a[text()='"+ uniqueId + "']]]]" );
            try {
                selectElement( productCheckbox );
            } catch ( Exception staleElementException ) {
                selectElement( productCheckbox );
            }
        }
        return this;
    }

    public ProductsPageLegacy clearSearch() throws InterruptedException {
        waitForElementToBeInvisible(DEFAULT_LOADER);
        if (isElementVisible(REMOVE_SEARCH_ICON, Duration.ofSeconds(15))) {
            scrollIntoViewAndClick( REMOVE_SEARCH_ICON );
            waitForElementToBeInvisible(DEFAULT_LOADER);
            waitForElementToBeInvisible(REMOVE_SEARCH_ICON, Duration.ofSeconds(8));
        }
        return this;
    }

    public ProductsPageLegacy searchProduct( String keyword, String productLevel ) throws InterruptedException {
        clearSearch();
        if ( productLevel != null ) {
            selectProductLevel( productLevel );
        }
        clearInput( SEARCH_INPUT );
        sendKeys( SEARCH_INPUT, keyword );
        scrollIntoViewAndClick( SEARCH_PRODUCT_ICON );
        return this;
    }

    public CreateBatchSection startProcessToGenerateLaunchFile(List<ProductBasicSettings> productsToUse, String level ) throws Exception {
        List<String> uniqueIds = productsToUse.stream().map( product -> product.uniqueID ).collect( Collectors.toList() );
        selectProducts( uniqueIds, level );
        scrollIntoViewAndClick( GENERATE_LAUNCH_FILE_BUTTON );
        return new CreateBatchSection( driver );
    }
}
