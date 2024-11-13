package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.eq;

public class CompanyCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> COMPANY_COLLECTION = mongoDb.getCollection("company", BsonDocument.class);

    private static final HashMap<String, String> RETAILER_NAMES_AND_IDS = new HashMap<>();

    private static final HashMap<String, String> CAMPAIGN_NAMES_AND_IDS = new HashMap<>();

    private static final HashMap<String, String> LOCALE_NAMES_AND_IDS = new HashMap<>();

    public Company getCompany(String companyId) throws IOException {
        Bson filter = eq("_id", UUID.fromString(companyId));

        boolean companyExists = doesCompanyExist(companyId);
        if (!companyExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = COMPANY_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);
        return new ObjectMapper().readValue(document, Company.class);
    }

    public Company getCompanyByName(String companyName) throws IOException {
        boolean companyExists = doesCompanyNameExist(companyName);

        if (!companyExists) {
            return null;
        }

        Bson filter = eq("name", companyName);
        FindIterable<BsonDocument> bson = COMPANY_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);
        return new ObjectMapper().readValue(document, Company.class);
    }

    public boolean doesCompanyExist(String companyId) {
        Bson filter = eq("_id", UUID.fromString(companyId));
        return COMPANY_COLLECTION.countDocuments(filter) >= 1;
    }

    public boolean doesCompanyNameExist(String companyName) {
        Bson filter = eq("name", companyName);
        return COMPANY_COLLECTION.countDocuments(filter) >= 1;
    }

    public void deleteCompany(String companyId) {
        logger.info("Deleting company: " + companyId);

        Bson filter = eq("_id", UUID.fromString(companyId));
        COMPANY_COLLECTION.deleteOne(filter);
    }

    public String getRetailerId(Company company, String retailerName) {
        String retailerId = RETAILER_NAMES_AND_IDS.get(retailerName);
        if (retailerId == null) {
            retailerId = company.retailers
                    .stream()
                    .filter(retailer -> retailer.clientRetailerName.equals(retailerName))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .systemRetailerId;
            RETAILER_NAMES_AND_IDS.put(retailerName, retailerId);
        }
        return retailerId;
    }

    public String getLocaleId(Company company, String localeName) {
        String localeId = LOCALE_NAMES_AND_IDS.get(localeName);
        if (localeId == null) {
            localeId = company.locales
                    .stream()
                    .filter(locale -> locale.localeName.equals(localeName))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .localeId;
            LOCALE_NAMES_AND_IDS.put(localeName, localeId);
        }
        return localeId;
    }

    public String getCampaignId(Company company, String campaignName) {
        String campaignId = CAMPAIGN_NAMES_AND_IDS.get(campaignName);
        if (campaignId == null) {
            campaignId = company.campaigns
                    .stream()
                    .filter(campaign -> campaign.name.equals(campaignName))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .id;
            CAMPAIGN_NAMES_AND_IDS.put(campaignName, campaignId);
        }
        return campaignId;

    }
}

