package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.eq;

public class CompanyPropertiesCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> COMPANY_PROPERTIES_COLLECTION = mongoDb.getCollection("company-properties", BsonDocument.class);

    public void deleteAllCompanyProperties(String companyId) {
        logger.info("Deleting all property records from company: " + companyId);

        UUID companyUUID = UUID.fromString(companyId);
        Bson filter = eq("companyId", companyUUID);
        COMPANY_PROPERTIES_COLLECTION.deleteMany(filter);
    }

    public CompanyProperties getCompanyProperties(String companyPropertiesId) throws IOException {
        Bson filter = eq("_id", UUID.fromString(companyPropertiesId));
        FindIterable<BsonDocument> bson = COMPANY_PROPERTIES_COLLECTION.find(filter);
        JsonWriterSettings settings = getJsonWriterSettings();
        String document = bson.first().toJson(settings);
        return new ObjectMapper().readValue(document, CompanyProperties.class);
    }

}
