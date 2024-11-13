package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class CompanyVersionHistoryCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> COMPANY_VERSION_HISTORY_COLLECTION = mongoDb.getCollection("company-version-history", BsonDocument.class);

    public void deleteCompanyVersionHistory(String companyId) {
        logger.info("Deleting history records of company: " + companyId);

        UUID companyUUID = UUID.fromString(companyId);
        Bson filter = eq("referenceId", companyUUID);
        COMPANY_VERSION_HISTORY_COLLECTION.deleteMany(filter);
    }
}
