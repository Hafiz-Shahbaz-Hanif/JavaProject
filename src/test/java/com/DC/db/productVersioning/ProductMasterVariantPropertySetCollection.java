package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantProperty;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantPropertySet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.*;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ProductMasterVariantPropertySetCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION =
            mongoDb.getCollection("product-master-variant-property-set", BsonDocument.class);

    public ProductVariantPropertySet getPropertySet(String propertySetId) throws IOException {
        Bson filter = getEqualsFilter(propertySetId);

        boolean PropertySetExists = doesPropertySetExist(propertySetId);
        if (!PropertySetExists) {
            return null;
        }

        FindIterable<BsonDocument> bson = PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();
        String document = Objects.requireNonNull(bson.first()).toJson(settings);

        return new ObjectMapper().readValue(document, ProductVariantPropertySet.class);
    }

    public void deletePropertySet(String propertySetId) {
        logger.info("Deleting property set: " + propertySetId);

        Bson filter = getEqualsFilter(propertySetId);
        PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION.deleteOne(filter);
    }

    public void deletePropertySets(String productMasterId, String localeId) {
        logger.info("Deleting property sets of locale: " + localeId + " in product master: " + productMasterId);

        UUID productMasterUUID = UUID.fromString(productMasterId);
        UUID localeUUID = UUID.fromString(localeId);

        Bson filterForProductMaster = eq("productMasterId", productMasterUUID);
        Bson filterForLocale = eq("locale", localeUUID);
        Bson filter = and(filterForProductMaster, filterForLocale);

        PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION.deleteMany(filter);
    }

    public boolean doesPropertySetExist(String propertySetId) {
        Bson filter = getEqualsFilter(propertySetId);
        return PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION.countDocuments(filter) >= 1;
    }

    public ProductVariantPropertySet updatePropertiesInSet(String propertySetId, List<ProductVariantProperty> properties, String companyId) throws IOException {
        Bson filterForCompanyID = eq("companyId", UUID.fromString(companyId));
        Bson filterForSetId = eq("_id", UUID.fromString(propertySetId));
        Bson filter = and(filterForCompanyID, filterForSetId);

        List<BsonDocument> bsonProperties = properties.stream()
                .map(property -> {
                    BsonDocument bsonDocument = new BsonDocument();
                    bsonDocument.append("id", new BsonString(property.id));
                    bsonDocument.append("values", convertValuesToBson(property.values));
                    return bsonDocument;
                })
                .collect(Collectors.toList());

        BsonArray bsonArray = new BsonArray(bsonProperties);
        Bson update = com.mongodb.client.model.Updates.set("properties", bsonArray);
        PRODUCT_MASTER_VARIANT_PROPERTY_SET_COLLECTION.findOneAndUpdate(filter, update);
        return getPropertySet(propertySetId);
    }

    private BsonArray convertValuesToBson(List<Object> values) {
        return new BsonArray(values.stream()
                .map(value -> {
                    if (value instanceof String) {
                        return new BsonString((String) value);
                    } else if (value instanceof Integer) {
                        return new BsonInt32((Integer) value);
                    } else if (value instanceof Boolean) {
                        return new BsonBoolean((Boolean) value);
                    } else if (value instanceof Date) {
                        return new BsonDateTime(((Date) value).getTime());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    private static Bson getEqualsFilter(String propertySetId) {
        UUID propertySetUUID = UUID.fromString(propertySetId);
        return eq("_id", propertySetUUID);
    }
}

