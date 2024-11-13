package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class CompanyPropertiesTemplateCollection extends MongoUtility {
    private final MongoCollection<BsonDocument> COMPANY_PROPERTIES_TEMPLATE_COLLECTION = mongoDb.getCollection("company-properties-template", BsonDocument.class);

    public void deleteCompanyPropertyTemplate(String templateName) {
        logger.info("Deleting property template: " + templateName);

        Bson filter = eq("name", templateName);
        COMPANY_PROPERTIES_TEMPLATE_COLLECTION.deleteOne(filter);
    }
}
