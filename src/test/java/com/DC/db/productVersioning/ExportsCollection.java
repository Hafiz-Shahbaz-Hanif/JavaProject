package com.DC.db.productVersioning;

import com.DC.objects.productVersioning.ExportRecord;
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

public class ExportsCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> EXPORTS_COLLECTION = mongoDb.getCollection("product-variant-exports", BsonDocument.class);

    public ExportRecord getExportRecord(String exportId) throws IOException {
        boolean exportExists = doesExportExist(exportId);

        if (!exportExists) {
            return null;
        }

        Bson filter = eq("_id", UUID.fromString(exportId));
        FindIterable<BsonDocument> bson = EXPORTS_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);

        return new ObjectMapper().readValue(document, ExportRecord.class);
    }

    public ExportRecord getLatestExportRecord(String companyId) throws IOException {
        Bson filter = eq("companyId", UUID.fromString(companyId));
        FindIterable<BsonDocument> bson = EXPORTS_COLLECTION.find(filter)
                .sort(new BasicDBObject("startedOn", -1))
                .limit(1);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);

        return new ObjectMapper().readValue(document, ExportRecord.class);
    }

    public boolean doesExportExist(String exportId) {
        Bson filter = eq("_id", UUID.fromString(exportId));
        return EXPORTS_COLLECTION.countDocuments(filter) >= 1;
    }
}
