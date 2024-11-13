package com.DC.utilities.apiEngine.apiServices.insights;

import com.DC.objects.insightslegacy.Product;
import com.DC.utilities.apiEngine.apiRequests.insights.ProductRepoRequests;
import com.DC.utilities.apiEngine.models.responses.insights.Mappings;
import com.DC.utilities.apiEngine.models.responses.insights.TaskUIConfig;
import com.DC.utilities.apiEngine.models.responses.insights.TaskUIConfigBase;
import io.restassured.response.Response;
import java.util.ArrayList;

import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;

public class ProductService {

    public static TaskUIConfig getTaskUIMappingConfig(String jwt, String mappingId) throws Exception {
        Response response = ProductRepoRequests.getTaskUIMappingConfig(mappingId, jwt);
        return verifyEndpointReturnsCorrectObject(response, "", TaskUIConfig.class);
    }

    public static void addPropertyToMappingConfig(String jwt, String mappingId, String propertyId) throws Exception {
        TaskUIConfig taskUIMappingConfig = getTaskUIMappingConfig(jwt, mappingId);
        addPropertyToMappingConfig(jwt, mappingId, propertyId, taskUIMappingConfig);
    }

    public static void addPropertyToMappingConfig(String jwt, String mappingId, String propertyId, TaskUIConfig taskUIMappingConfig) throws Exception {
        ArrayList<String> arrayList = new ArrayList<String>();
        Mappings mappings = new Mappings(true, propertyId, arrayList);
        taskUIMappingConfig.mappings.add(mappings);
        TaskUIConfigBase taskUIConfigBase = new TaskUIConfigBase(taskUIMappingConfig.label, taskUIMappingConfig.mappings, taskUIMappingConfig.internal);
        ProductRepoRequests.updateTaskUIConfig(mappingId, taskUIConfigBase, jwt);
    }

    public static void removePropertiesFromMappingConfig( String jwt, String mappingID, ArrayList<String> propertyIDs, TaskUIConfig taskUIMappingConfig ) throws Exception {
        for ( String propertyID : propertyIDs ) {
            taskUIMappingConfig.mappings.removeIf( item -> item.property.equals(propertyID) );
        }
        TaskUIConfigBase taskUIConfigBase = new TaskUIConfigBase(taskUIMappingConfig.label, taskUIMappingConfig.mappings, taskUIMappingConfig.internal);
        ProductRepoRequests.updateTaskUIConfig(mappingID, taskUIConfigBase, jwt);
    }

    public static void removePropertyFromMappingConfig( String jwt, String mappingID, String propertyID, TaskUIConfig taskUIMappingConfig ) throws Exception {
        taskUIMappingConfig.mappings.removeIf( item -> item.property.equals(propertyID) );
        TaskUIConfigBase taskUIConfigBase = new TaskUIConfigBase(taskUIMappingConfig.label, taskUIMappingConfig.mappings, taskUIMappingConfig.internal);
        ProductRepoRequests.updateTaskUIConfig(mappingID, taskUIConfigBase, jwt);
    }

    public static Product getProductPropertiesAndStagedChanges(String jwt, String productId) throws Exception {
        Response response = ProductRepoRequests.getProductDetails(productId, jwt);
        return verifyEndpointReturnsCorrectObject(response, "", Product.class);
    }
}
