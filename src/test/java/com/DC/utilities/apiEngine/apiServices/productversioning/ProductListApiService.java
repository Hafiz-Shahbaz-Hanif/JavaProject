package com.DC.utilities.apiEngine.apiServices.productversioning;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductListApiRequests;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductListRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.FriendlyProductVariantList;
import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantList;
import io.restassured.response.Response;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListApiService {

    private static final Logger LOGGER = Logger.getLogger(ProductListApiService.class);

    public static FriendlyProductVariantList getProductList(String productListId, String jwt) throws Exception {
        LOGGER.info("Getting product list");
        Response response = ProductListApiRequests.getProductList(productListId, jwt);
        return response.getBody().as(FriendlyProductVariantList.class);
    }

    public static List<FriendlyProductVariantList> getAllCompanyProductLists(String jwt) throws Exception {
        LOGGER.info("Getting all product lists in company");
        Response response = ProductListApiRequests.getAllCompanyProductLists(jwt);
        return Arrays.asList(response.getBody().as(FriendlyProductVariantList[].class));
    }

    public static FriendlyProductVariantList createProductList(CreateProductListRequestBody requestBody, String jwt) throws Exception {
        LOGGER.info("Creating product list");
        Response response = ProductListApiRequests.createProductList(requestBody, jwt);
        return response.getBody().as(FriendlyProductVariantList.class);
    }

    public static FriendlyProductVariantList updateProductList(CreateProductListRequestBody requestBody, String listId, String jwt) throws Exception {
        LOGGER.info("Updating product list");
        Response response = ProductListApiRequests.updateProductList(requestBody, listId, jwt);
        return response.getBody().as(FriendlyProductVariantList.class);
    }

    public static ProductVariantList deleteProductList(String listId, String jwt) throws Exception {
        LOGGER.info("Deleting product list");
        Response response = ProductListApiRequests.deleteProductList(listId, jwt);
        return response.getBody().as(ProductVariantList.class);
    }

    public static void deleteProductLists(List<String> listNames, String jwt) throws Exception {
        LOGGER.info("Deleting product lists by name");
        List<FriendlyProductVariantList> allProductLists = getAllCompanyProductLists(jwt);
        List<String> idsOfListsToRemove = allProductLists.stream()
                .filter(listInCompany -> listNames.contains(listInCompany.name))
                .map(listInCompany -> listInCompany._id)
                .collect(Collectors.toList());

        for (var listId : idsOfListsToRemove) {
            deleteProductList(listId, jwt);
        }
    }

    public static ProductVariantList removeProductsFromList(List<String> instanceIds, String listId, String jwt) throws Exception {
        LOGGER.info("Removing products from list");
        Response response = ProductListApiRequests.removeProductsFromList(instanceIds, listId, jwt);
        return response.getBody().as(ProductVariantList.class);
    }

    public static ProductVariantList addProductInstancesToList(List<InstancePathBase> instancesToAdd, String listId, String jwt) throws Exception {
        LOGGER.info("Adding products to list");
        Response response = ProductListApiRequests.addProductInstancesToList(instancesToAdd, listId, jwt);
        return response.getBody().as(ProductVariantList.class);
    }

}
