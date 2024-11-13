package com.DC.db.productVersioning;

import com.DC.objects.productVersioning.ImportRecord;
import com.DC.utilities.MongoUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.eq;

public class ImportsCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> IMPORTS_COLLECTION = mongoDb.getCollection("product-variant-imports", BsonDocument.class);

    public ImportRecord getImportRecord(String importId) throws IOException {
        boolean importExists = doesImportExist(importId);

        if (!importExists) {
            return null;
        }

        Bson filter = eq("_id", UUID.fromString(importId));
        FindIterable<BsonDocument> bson = IMPORTS_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);

        return new ObjectMapper().readValue(document, ImportRecord.class);
    }

    public ImportRecord getLatestImportRecord(String companyId) throws IOException {
        Bson filter = eq("companyId", UUID.fromString(companyId));
        FindIterable<BsonDocument> bson = IMPORTS_COLLECTION.find(filter)
                .sort(new BasicDBObject("startedOn", -1))
                .limit(1);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);

        return new ObjectMapper().readValue(document, ImportRecord.class);
    }

    public boolean doesImportExist(String importId) {
        Bson filter = eq("_id", UUID.fromString(importId));
        return IMPORTS_COLLECTION.countDocuments(filter) >= 1;
    }
}
