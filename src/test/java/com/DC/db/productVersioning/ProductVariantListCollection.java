package com.DC.db.productVersioning;

import com.DC.utilities.MongoUtility;
import com.DC.utilities.apiEngine.models.responses.productVersioning.FriendlyProductVariantList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.DC.utilities.SharedMethods.getJsonWriterSettings;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;

public class ProductVariantListCollection extends MongoUtility {

    private final MongoCollection<BsonDocument> PRODUCT_LIST_COLLECTION = mongoDb.getCollection("product-variant-list", BsonDocument.class);

    public void deleteProductList(String productListId) {
        logger.info("Deleting product list: " + productListId);

        UUID productListUUID = UUID.fromString(productListId);
        Bson filter = eq("_id", productListUUID);
        PRODUCT_LIST_COLLECTION.deleteOne(filter);
    }

    public void deleteProductList(String listName, String companyId) {
        UUID companyUUID = UUID.fromString(companyId);
        Bson filterForCompanyID = eq("companyId", companyUUID);
        Bson filterForListName = eq("name", listName);
        Bson filter = and(filterForCompanyID, filterForListName);
        PRODUCT_LIST_COLLECTION.deleteOne(filter);
    }

    public FriendlyProductVariantList getProductList(String productListId) throws IOException {
        boolean listExists = doesProductListExist(productListId, false);

        if (!listExists) {
            return null;
        }

        Bson filter = eq("_id", UUID.fromString(productListId));
        FindIterable<BsonDocument> bson = PRODUCT_LIST_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);
        return new ObjectMapper().findAndRegisterModules().readValue(document, FriendlyProductVariantList.class);
    }

    public FriendlyProductVariantList getProductListByName(String listName) throws IOException {
        boolean productListExists = doesProductListExist(listName, true);

        if (!productListExists) {
            return null;
        }

        Bson filter = eq("name", listName);
        FindIterable<BsonDocument> bson = PRODUCT_LIST_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        String document = bson.first().toJson(settings);
        return new ObjectMapper().findAndRegisterModules().readValue(document, FriendlyProductVariantList.class);
    }

    public List<FriendlyProductVariantList> getAllProductListsInCompany(String companyId) throws IOException {
        Bson filter = eq("companyId", UUID.fromString(companyId));
        FindIterable<BsonDocument> bson = PRODUCT_LIST_COLLECTION.find(filter);

        JsonWriterSettings settings = getJsonWriterSettings();

        MongoCursor<BsonDocument> iterator = bson.iterator();

        List<String> listsInCompany = new ArrayList<>();

        while (iterator.hasNext()) {
            String doc = iterator.next().toJson(settings);
            listsInCompany.add(doc);
        }

        TypeReference<List<FriendlyProductVariantList>> productVariantListType = new TypeReference<List<FriendlyProductVariantList>>() {};
        return new ObjectMapper().findAndRegisterModules().readValue(listsInCompany.toString(), productVariantListType);
    }

    public boolean doesProductListExist(String listNameOrId, boolean findByName) {
        Bson filter;
        if (findByName) {
            filter = eq("name", listNameOrId);
        } else {
            filter = eq("_id", UUID.fromString(listNameOrId));
        }
        return PRODUCT_LIST_COLLECTION.countDocuments(filter) >= 1;
    }

    public void editProductListName(String productListToUpdate, String newProductListName, String companyId) {
        UUID companyUUID = UUID.fromString(companyId);
        Bson filterForCompanyID = eq("companyId", companyUUID);
        Bson filterForListName = eq("name", productListToUpdate);
        Bson filter = and(filterForCompanyID, filterForListName);
        Bson update = com.mongodb.client.model.Updates.set("name", newProductListName);
        PRODUCT_LIST_COLLECTION.findOneAndUpdate(filter, update);
    }
}
