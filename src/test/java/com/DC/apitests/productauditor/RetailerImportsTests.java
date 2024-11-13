package com.DC.apitests.productauditor;
import com.DC.db.insights.DSAScrapedDataCollection;
import com.DC.objects.insights.RetailerScrapData;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RetailerImportsTests extends BaseClass {

    @BeforeGroups(groups = {"RetailerImportsTests"})
    public void setupTests() throws Exception {
        LOGGER.info("Setting up Retailer import tests");
    }

    @Test(groups = {"RetailerImportsTests"})
    public void Api_RetailerImports_RetailersAreUpdatedAfterDailyScrap() throws IOException {
        List<String> requiredRetailers = Arrays.asList("target.com", "amazon.co.uk", "amazon.de", "walmart.com", "kroger.com", "instacart.com", "amazon.ca", "amazon.com", "amazon.fr", "chewy.com", "meijer.com", "albertsons.com");
        String yesterday = DateUtility.getYesterday();
        DSAScrapedDataCollection dsaScrapedDataCollection = new DSAScrapedDataCollection();
        List<RetailerScrapData> retailerDataByDate = dsaScrapedDataCollection.createListOfDocumentsByDateAndAggregateCollection(yesterday);
        List<String> retailerDomainsByDate = dsaScrapedDataCollection.createStringListOfRetailersFromScrapDataList(retailerDataByDate);
        Assert.assertTrue(CollectionUtils.isEqualCollection(retailerDomainsByDate, requiredRetailers), "Not all retailers were updated with new scrap data import from " + yesterday +
                "\nEXPECTED RETAILERS:" + requiredRetailers +
                "\nACTUAL RETAILERS:" + retailerDataByDate +
                "\nRETAILERS NOT UPDATED:" + CollectionUtils.disjunction(requiredRetailers, retailerDomainsByDate));
        for (RetailerScrapData retailer : retailerDataByDate) {
            Assert.assertEquals(retailer.date, yesterday, retailer.domain + " was not updated with new scrape data import from " + yesterday);
            Assert.assertTrue(retailer.NumberOfScrapes >= 1, retailer.domain + " has no new updated scrape data from " + yesterday);
        }
    }

}
