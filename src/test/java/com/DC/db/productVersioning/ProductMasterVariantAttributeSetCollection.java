package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductInvariantAttributeSet;

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

public class ProductMasterVariantAttributeSetCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> PRODUCT_MASTER_VARIANT_ATTRIBUTE_SET_COLLECTION =
            mongoDb.getCollection("product-master-invariant-attribute-set", BsonDocument.class);

    public ProductInvariantAttributeSet getAttributeSet(String attributeSetId) throws IOException {
        Bson filter = getEqualsFilter(attributeSetId);

        boolean attributeSetExists = doesAttributeSetExist(attributeSetId);
        if (!attributeSetExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_VARIANT_ATTRIBUTE_SET_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);
        return new ObjectMapper().readValue(document, ProductInvariantAttributeSet.class);
    }

    public void deleteAttributeSet(String attributeSetId) {
        logger.info("Deleting attribute set: " + attributeSetId);

        Bson filter = getEqualsFilter(attributeSetId);
        PRODUCT_MASTER_VARIANT_ATTRIBUTE_SET_COLLECTION.deleteOne(filter);
    }

    public void deleteAttributeSets(String productMasterId, String localeId) {
        logger.info("Deleting attribute sets of locale: " + localeId + " in product master: " + productMasterId);

        UUID productMasterUUID = UUID.fromString(productMasterId);
        UUID localeUUID = UUID.fromString(localeId);

        Bson filterForProductMaster = eq("productMasterId", productMasterUUID);
        Bson filterForLocale = eq("locale", localeUUID);
        Bson filter = and(filterForProductMaster, filterForLocale);

        PRODUCT_MASTER_VARIANT_ATTRIBUTE_SET_COLLECTION.deleteMany(filter);
    }

    public boolean doesAttributeSetExist(String attributeSetId) {
        Bson filter = getEqualsFilter(attributeSetId);
        return PRODUCT_MASTER_VARIANT_ATTRIBUTE_SET_COLLECTION.countDocuments(filter) >= 1;
    }

    private static Bson getEqualsFilter(String attributeSetId) {
        UUID attributeSetUUID = UUID.fromString(attributeSetId);
        return eq("_id", attributeSetUUID);
    }
}

