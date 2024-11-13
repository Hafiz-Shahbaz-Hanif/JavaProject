package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.*;

public class ProductMasterCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> PRODUCT_MASTER_COLLECTION = mongoDb.getCollection("product-master", BsonDocument.class);
    private static final byte BYTE_0x04 = (byte) 0x04;

    public MongoCollection<BsonDocument> getCollection() {
        return PRODUCT_MASTER_COLLECTION;
    }

    public ProductMaster getProductMaster(String productMasterId) throws IOException {
        Bson filter = getEqualsFilter(productMasterId);

        boolean productMasterExists = doesProductMasterExist(productMasterId);
        if (!productMasterExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);

        return new ObjectMapper().readValue(document, ProductMaster.class);
    }

    public ProductMaster getProductMaster(String uniqueId, String companyId) throws IOException {
        Bson filter = getEqualsFilter(uniqueId, companyId);

        boolean productMasterExists = doesProductMasterExist(uniqueId, companyId);
        if (!productMasterExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);

        return new ObjectMapper().readValue(document, ProductMaster.class);
    }

    public void deleteProductMaster(String uniqueId, String companyId) {
        logger.info("Deleting product master with uniqueId: " + uniqueId + " and companyId: " + companyId);

        Bson filter = getEqualsFilter(uniqueId, companyId);
        PRODUCT_MASTER_COLLECTION.deleteOne(filter);
    }

    public void deleteProductMaster(String productMasterId) {
        logger.info("Deleting product master: " + productMasterId);

        Bson filter = getEqualsFilter(productMasterId);
        PRODUCT_MASTER_COLLECTION.deleteOne(filter);
    }

    public boolean doesProductMasterExist(String uniqueId, String companyId) {
        Bson filter = getEqualsFilter(uniqueId, companyId);
        return PRODUCT_MASTER_COLLECTION.countDocuments(filter) >= 1;
    }

    public boolean doesProductMasterExist(String productMasterId) {
        Bson filter = getEqualsFilter(productMasterId);
        return PRODUCT_MASTER_COLLECTION.countDocuments(filter) >= 1;
    }

    public static Bson getEqualsFilter(String uniqueId, String companyId) {
        UUID companyUUID = UUID.fromString(companyId);
        Bson filterForCompanyID = eq("companyId", companyUUID);
        Bson filterForUniqueId = eq("uniqueId", uniqueId);
        return and(filterForCompanyID, filterForUniqueId);
    }

    public static Bson getEqualsFilter(String productMasterId) {
        UUID productMasterUUID = UUID.fromString(productMasterId);
        return eq("_id", productMasterUUID);
    }

    public ProductMaster replaceStagedSetOfProduct(String productMasterId, String localeId, String propertySetId, String digitalAssetId, String chainItemId) throws IOException {
        Bson filter = ProductMasterCollection.getEqualsFilter(productMasterId);
        BsonDocument originalBson = PRODUCT_MASTER_COLLECTION.find(filter).first();

        var currentTime = new Date().getTime();
        
        var invariantDataToAdd = new BsonDocument();
        invariantDataToAdd.put("attributeSetId", BsonNull.VALUE);

        BsonDocument instanceSetToAdd = new BsonDocument();

        byte[] propertySetIdBytes = null;
        byte[] digitalAssetsSetIdBytes = null;

        if (propertySetId != null) {
            propertySetIdBytes = SharedMethods.convertToUUIDAndReturnBytesArray(propertySetId);
        }

        if (digitalAssetId != null) {
            digitalAssetsSetIdBytes = SharedMethods.convertToUUIDAndReturnBytesArray(digitalAssetId);
        }

        instanceSetToAdd.put("id", new BsonBinary(BYTE_0x04, SharedMethods.convertToUUIDAndReturnBytesArray(SharedMethods.generateUUID())));
        instanceSetToAdd.put("dateCreated", new BsonDateTime(currentTime));
        instanceSetToAdd.put("dateUpdated", new BsonDateTime(currentTime));
        instanceSetToAdd.put("propertySetId", propertySetId == null ? BsonNull.VALUE : new BsonBinary(BYTE_0x04, propertySetIdBytes));
        instanceSetToAdd.put("digitalAssetSetId", digitalAssetId == null ? BsonNull.VALUE : new BsonBinary(BYTE_0x04, digitalAssetsSetIdBytes));
        instanceSetToAdd.put("keywordSetId", BsonNull.VALUE);

        BsonDocument instances = new BsonDocument();
        instances.put("global", instanceSetToAdd);
        instances.put("retailer", new BsonArray());
        instances.put("globalCampaign", new BsonArray());
        instances.put("retailerCampaign", new BsonArray());

        BsonDocument stagedSetToAdd = new BsonDocument();
        stagedSetToAdd.put("id", new BsonBinary(BYTE_0x04, SharedMethods.convertToUUIDAndReturnBytesArray(SharedMethods.generateUUID())));
        stagedSetToAdd.put("localeId", new BsonBinary(BYTE_0x04, SharedMethods.convertToUUIDAndReturnBytesArray(localeId)));
        stagedSetToAdd.put("chainItemId", new BsonBinary(BYTE_0x04, SharedMethods.convertToUUIDAndReturnBytesArray(chainItemId)));
        stagedSetToAdd.put("invariantData", invariantDataToAdd);
        stagedSetToAdd.put("instances", instances);

        BsonDocument originalVariantSets = originalBson.get("variantSets").asDocument();
        BsonArray originalStagedVariantSets = originalVariantSets.get("staged").asArray();
        originalStagedVariantSets.clear();
        originalStagedVariantSets.add(stagedSetToAdd);

        PRODUCT_MASTER_COLLECTION.replaceOne(filter, originalBson);
        return getProductMaster(productMasterId);
    }
}

