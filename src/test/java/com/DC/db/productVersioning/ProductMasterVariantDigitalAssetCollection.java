package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.responses.productVersioning.DigitalAssetProperty;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantDigitalAssetSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProductMasterVariantDigitalAssetCollection extends MongoUtility {
    private final MongoCollection<BsonDocument> PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION =
            mongoDb.getCollection("product-master-variant-digital-asset", BsonDocument.class);

    public ProductVariantDigitalAssetSet getDigitalAsset(String digitalAssetId) throws IOException {
        Bson filter = getEqualsFilter(digitalAssetId);

        boolean digitalAssetExists = doesDigitalAssetExist(digitalAssetId);
        if (!digitalAssetExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);

        return new ObjectMapper().readValue(document, ProductVariantDigitalAssetSet.class);
    }

    public void deleteDigitalAsset(String digitalAssetId) {
        logger.info("Deleting digital asset: " + digitalAssetId);

        Bson filter = getEqualsFilter(digitalAssetId);
        PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION.deleteOne(filter);
    }

    public void deleteDigitalAssets(String productMasterId, String localeId) {
        logger.info("Deleting digital assets of locale: " + localeId + " in product master: " + productMasterId);

        UUID productMasterUUID = UUID.fromString(productMasterId);
        UUID localeUUID = UUID.fromString(localeId);

        Bson filterForProductMaster = eq("productMasterId", productMasterUUID);
        Bson filterForLocale = eq("locale", localeUUID);
        Bson filter = and(filterForProductMaster, filterForLocale);

        PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION.deleteMany(filter);
    }

    public boolean doesDigitalAssetExist(String digitalAssetId) {
        Bson filter = getEqualsFilter(digitalAssetId);
        return PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION.countDocuments(filter) >= 1;
    }

    public ProductVariantDigitalAssetSet updateDigitalAssetsInSet(String digitalAssetSetId, List<DigitalAssetProperty> digitalAssets, String companyId) throws IOException {
        Bson filterForCompanyID = eq("companyId", UUID.fromString(companyId));
        Bson filterForSetId = eq("_id", UUID.fromString(digitalAssetSetId));
        Bson filter = and(filterForCompanyID, filterForSetId);

        List<BsonDocument> bsonDigitalAssets = new ArrayList<>();

        for (var digitalAsset : digitalAssets) {
            BsonDocument bsonDigitalAsset = new BsonDocument();
            bsonDigitalAsset.put("id", new BsonString(digitalAsset.id));

            List<BsonDocument> assetsBson = new ArrayList<>();

            for (var asset : digitalAsset.assets) {
                BsonDocument assetBson = new BsonDocument();
                assetBson.put("url", new BsonString(asset.url));
                assetBson.put("mediaAssetId", new BsonBinary((byte) 0x04, SharedMethods.convertToUUIDAndReturnBytesArray(asset.mediaAssetId)));
                assetsBson.add(assetBson);

            }
            BsonArray bsonArray = new BsonArray(assetsBson);
            bsonDigitalAsset.put("assets", bsonArray);
            bsonDigitalAssets.add(bsonDigitalAsset);
        }

        BsonArray bsonArray = new BsonArray(bsonDigitalAssets);
        Bson update = com.mongodb.client.model.Updates.set("digitalAssets", bsonArray);
        PRODUCT_MASTER_VARIANT_DIGITAL_ASSET_COLLECTION.findOneAndUpdate(filter, update);
        return getDigitalAsset(digitalAssetSetId);
    }

    private static Bson getEqualsFilter(String digitalAssetId) {
        UUID digitalAssetUUID = UUID.fromString(digitalAssetId);
        return eq("_id", digitalAssetUUID);
    }

}

