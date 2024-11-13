package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class TaskUIConfigCollection extends MongoUtility {
    private final MongoCollection<BsonDocument> TASK_UI_CONFIG_COLLECTION = mongoDb.getCollection("variant-taskUI-config", BsonDocument.class);

    public void deleteTaskUIConfig(String taskUIConfigId) {
        UUID taskUIConfigUUID = UUID.fromString(taskUIConfigId);
        Bson filter = eq("_id", taskUIConfigUUID);
        TASK_UI_CONFIG_COLLECTION.deleteOne(filter);
    }
}
