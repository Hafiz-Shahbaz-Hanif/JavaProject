package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.eq;

public class TaskVersionHistoryCollection extends MongoUtility {
    private final MongoCollection<BsonDocument> TASK_HISTORY_COLLECTION = mongoDb.getCollection("product-variant-task-version-history", BsonDocument.class);

    public List<JSONObject> getAllTasksHistoryForCompany(String companyId) {
        UUID companyUUID = UUID.fromString(companyId);
        Bson filter = eq("meta.cpgCompanyId", companyUUID);
        var documents = TASK_HISTORY_COLLECTION.find(filter);

        var settings = getJsonWriterSettings();
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (BsonDocument document : documents) {
            var json = new JSONObject(document.toJson(settings));
            jsonObjects.add(json);
        }
        return jsonObjects;
    }
}
