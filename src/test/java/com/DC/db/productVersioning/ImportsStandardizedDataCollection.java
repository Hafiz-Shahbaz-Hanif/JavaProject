package com.DC.db.productVersioning;

import com.DC.objects.productVersioning.ImportStandardizedData;
import com.DC.utilities.MongoUtility;
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

public class ImportsStandardizedDataCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> IMPORTS_STANDARDIZED_COLLECTION =
            mongoDb.getCollection("product-variant-imports-standardized-data", BsonDocument.class);

    public ImportStandardizedData getDocument(String _id) throws IOException {
        boolean idExists = doesDocumentExist(_id);

        if (!idExists) {
            return null;
        }

        Bson filter = eq("_id", UUID.fromString(_id));
        FindIterable<BsonDocument> bson = IMPORTS_STANDARDIZED_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);

        return new ObjectMapper().readValue(document, ImportStandardizedData.class);
    }

    public boolean doesDocumentExist(String _id) {
        Bson filter = eq("_id", UUID.fromString(_id));
        return IMPORTS_STANDARDIZED_COLLECTION.countDocuments(filter) >= 1;
    }
}
