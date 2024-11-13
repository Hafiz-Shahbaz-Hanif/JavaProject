package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class ProductMasterVersionHistoryCollection extends MongoUtility {
    private final MongoCollection<BsonDocument> PRODUCT_MASTER_VERSION_HISTORY_COLLECTION =
            mongoDb.getCollection("product-master-version-history", BsonDocument.class);

    public void deleteProductVersionHistory(String productMasterId) {
        logger.info("Deleting history records of product: " + productMasterId);

        UUID productMasterUUID = UUID.fromString(productMasterId);
        Bson filter = eq("referenceId", productMasterUUID);
        PRODUCT_MASTER_VERSION_HISTORY_COLLECTION.deleteMany(filter);
    }
}

