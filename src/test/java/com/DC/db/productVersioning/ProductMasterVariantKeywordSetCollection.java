package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywordSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProductMasterVariantKeywordSetCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> PRODUCT_MASTER_VARIANT_KEYWORD_SET_COLLECTION =
            mongoDb.getCollection("product-master-variant-keyword-set", BsonDocument.class);

    public ProductVariantKeywordSet getKeywordSet(String keywordSetId) throws IOException {
        Bson filter = getEqualsFilter(keywordSetId);

        boolean keywordSetExists = doesKeywordSetExist(keywordSetId);
        if (!keywordSetExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_VARIANT_KEYWORD_SET_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);

        return new ObjectMapper().readValue(document, ProductVariantKeywordSet.class);
    }

    public void deleteKeywordSet(String keywordSetId) {
        logger.info("Deleting keyword set: " + keywordSetId);

        Bson filter = getEqualsFilter(keywordSetId);
        PRODUCT_MASTER_VARIANT_KEYWORD_SET_COLLECTION.deleteOne(filter);
    }

    public void deleteKeywordSets(String productMasterId, String localeId) {
        logger.info("Deleting keyword sets of locale: " + localeId + " in product master: " + productMasterId);

        UUID productMasterUUID = UUID.fromString(productMasterId);
        UUID localeUUID = UUID.fromString(localeId);

        Bson filterForProductMaster = eq("productMasterId", productMasterUUID);
        Bson filterForLocale = eq("locale", localeUUID);
        Bson filter = and(filterForProductMaster, filterForLocale);

        PRODUCT_MASTER_VARIANT_KEYWORD_SET_COLLECTION.deleteMany(filter);
    }

    public boolean doesKeywordSetExist(String keywordSetId) {
        Bson filter = getEqualsFilter(keywordSetId);
        return PRODUCT_MASTER_VARIANT_KEYWORD_SET_COLLECTION.countDocuments(filter) >= 1;
    }

    private static Bson getEqualsFilter(String keywordSetId) {
        UUID keywordSetUUID = UUID.fromString(keywordSetId);
        return eq("_id", keywordSetUUID);
    }
}

