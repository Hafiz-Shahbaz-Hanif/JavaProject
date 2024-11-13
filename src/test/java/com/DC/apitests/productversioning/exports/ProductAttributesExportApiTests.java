package com.DC.apitests.productversioning.exports;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.objects.productVersioning.ProductAttributesDataInExcel;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ExportCoreAssertions;
import com.DC.utilities.XLUtils.ProductAttributesXLUtils;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.insights.CPGData.Segments.SegmentsService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductAttributesExportRequestBody;
import com.DC.utilities.apiEngine.models.responses.insights.CPGData.Segments.SegmentValuesWithCategory;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.*;
import static com.DC.apitests.ApiValidations.validateUnauthorizedError;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.ProductAttributesXLUtils.getProductAttributesInFile;

public class ProductAttributesExportApiTests extends BaseClass {
    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    private static final Map.Entry<String, String> FIRST_PRODUCT = new AbstractMap.SimpleEntry<>("QA-EXPORTS-001", "es-MX");
    private static final Map.Entry<String, String> SECOND_PRODUCT = new AbstractMap.SimpleEntry<>("QA-EXPORTS-002", "fr-FR");
    private static final Map.Entry<String, String> EMPTY_PRODUCT = new AbstractMap.SimpleEntry<>("QA-EXPORTS-EMPTY", "es-MX");
    private static String jwt;
    private static Company company;

    ProductAttributesExportApiTests() {
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        LOGGER.info("Setting up keyword export api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        company = CompanyApiService.getCompany(jwt);
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "product with assigned category, attributes and segment values (QA-EXPORTS-001 es-MX)")
    public void Api_ProductAttributesExport_CanExportLiveProductAttributes_FirstProduct() throws Exception {
        performTest(Collections.singletonList(FIRST_PRODUCT));
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "product with assigned category but attributes and values not set (QA-EXPORTS-002 fr-FR)")
    public void Api_ProductAttributesExport_CanExportLiveProductAttributes_SecondProduct() throws Exception {
        performTest(Collections.singletonList(SECOND_PRODUCT));
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "product with category not assigned (QA-EXPORTS-EMPTY es-MX)")
    public void Api_ProductAttributesExport_CanExportLiveProductAttributes_EmptyProduct() throws Exception {
        performTest(Collections.singletonList(EMPTY_PRODUCT));
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "mix of products with and without assigned category, attributes and segment values")
    public void Api_ProductAttributesExport_CanExportLiveProductAttributes_MixOfProducts() throws Exception {
        performTest(new ArrayList<>(Arrays.asList(FIRST_PRODUCT, SECOND_PRODUCT, EMPTY_PRODUCT)));
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "Export live attributes of all products in company")
    public void Api_ProductAttributesExport_CanExportLiveProductAttributes_AllProducts() throws Exception {
        var filePath = sendRequestAndGetPathOfExportedFile(new ArrayList<>(), "live");
        List<ProductAttributesDataInExcel> exportedAttributeData = getProductAttributesInFile(filePath);
        Assert.assertTrue(exportedAttributeData.size() >= 2, "Exported file did not have data");
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"})
    public void Api_ProductAttributesExport_CannotExportProductAttributes_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);
        Response response = ProductVersioningApiRequests.exportProductAttributes(new ArrayList<>(), "live", jwt);
        validateUnauthorizedError(response);
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "Can export staged attributes of specific products")
    public void Api_ProductAttributesExport_CanExportStagedProductAttributes_SpecificProducts() throws Exception {
        List<ProductAttributesExportRequestBody> productInstancesToTest = new ArrayList<>();

        var productMaster = ProductVersioningApiService.getProductMasterByUniqueId("QA-IMPORTS-003", jwt);
        var localeId = company.getLocaleId("fr-FR");

        var instancePath = new ProductAttributesExportRequestBody(productMaster._id, localeId, Enums.ProductVariantType.STAGED);
        productInstancesToTest.add(instancePath);

        var filePath = sendRequestAndGetPathOfExportedFile(productInstancesToTest, "staged");
        List<String> exportedHeaders = ProductAttributesXLUtils.getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, ProductAttributesXLUtils.EXPECTED_HEADERS, "Headers are not matching");
    }

    @Test(groups = {"ProductAttributesExportApiTests", "NoDataProvider"}, description = "Can export staged attributes of all products")
    public void Api_ProductAttributesExport_CanExportStagedProductAttributes_AllProducts() throws Exception {
        var filePath = sendRequestAndGetPathOfExportedFile(new ArrayList<>(), "staged");
        List<String> exportedHeaders = ProductAttributesXLUtils.getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, ProductAttributesXLUtils.EXPECTED_HEADERS, "Headers are not matching");
    }

    private String sendRequestAndGetPathOfExportedFile(List<ProductAttributesExportRequestBody> productInstancesToTest, String type) throws Exception {
        var response = ProductVersioningApiRequests.exportProductAttributes(productInstancesToTest, type, jwt);
        var exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);

        response = ProductVersioningApiRequests.getExportTrackingRecord(exportId, jwt);

        var exportStatus = response.jsonPath().getString("status");
        Assert.assertEquals(exportStatus, Enums.ProcessStatus.SUCCESS.getProcessStatus(), "Export status is not SUCCESS");

        var linkToFile = response.jsonPath().getString("exportWorkbook.link");

        var fileName = linkToFile.substring(linkToFile.lastIndexOf('/') + 1, linkToFile.lastIndexOf('?'));
        var filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/" + fileName;
        downloadFileFromUrl(linkToFile, filePath);

        return filePath;
    }

    public void performTest(List<Map.Entry<String, String>> uniqueIdsAndLocales) throws Exception {
        List<ProductAttributesDataInExcel> expectedAttributeData = new ArrayList<>();
        List<ProductAttributesExportRequestBody> productInstancesToTest = new ArrayList<>();
        for (Map.Entry<String, String> entry : uniqueIdsAndLocales) {
            String uniqueId = entry.getKey();
            String locale = entry.getValue();
            ProductMaster productMaster = ProductVersioningApiService.getProductMasterByUniqueId(uniqueId, jwt);
            String localeId = company.getLocaleId(locale);
            ProductMaster.VariantSets.Live variantSet = productMaster.variantSets.live.stream().filter(l -> l.localeId.equals(localeId)).findFirst().orElseThrow(NoSuchElementException::new);
            ProductAttributesExportRequestBody instancePath = new ProductAttributesExportRequestBody(productMaster._id, localeId, Enums.ProductVariantType.LIVE);
            productInstancesToTest.add(instancePath);
            boolean isAttributeIdNull = variantSet.invariantData.attributeSetId == null;
            if (isAttributeIdNull) {
                ProductAttributesDataInExcel expectedAttributes = new ProductAttributesDataInExcel();
                expectedAttributes.productIdentifier = uniqueId;
                expectedAttributes.locale = locale;
                expectedAttributes.productName = variantSet.instances.global.name;
                expectedAttributeData.add(expectedAttributes);
            } else {
                ProductInvariantAttributeSet attributesData = ProductVersioningApiService.getAttributeSetData(instancePath.productMasterId, instancePath.localeId, instancePath.type, jwt);
                SegmentValuesWithCategory segmentValuesWithCategory = SegmentsService.getCategorySegmentValuesWithCategory(attributesData.categoryId.toString(), jwt);
                if (attributesData.attributes.isEmpty()) {
                    ProductAttributesDataInExcel expectedAttributes = new ProductAttributesDataInExcel();
                    expectedAttributes.productIdentifier = uniqueId;
                    expectedAttributes.locale = locale;
                    expectedAttributes.productName = variantSet.instances.global.name;
                    expectedAttributes.categoryName = segmentValuesWithCategory.category.name;
                    expectedAttributes.path = segmentValuesWithCategory.category.path;
                    expectedAttributes.categoryId = segmentValuesWithCategory.category.categoryId;
                    expectedAttributeData.add(expectedAttributes);
                } else {
                    for (ProductInvariantAttribute attribute : attributesData.attributes) {
                        ProductAttributesDataInExcel expectedAttributes = new ProductAttributesDataInExcel();
                        SegmentValuesWithCategory.Segment segment = segmentValuesWithCategory.segments.stream().filter(s -> Objects.equals(s.segmentId, attribute.segmentId)).findFirst().orElseThrow(NoSuchElementException::new);
                        expectedAttributes.productIdentifier = uniqueId;
                        expectedAttributes.locale = locale;
                        expectedAttributes.productName = variantSet.instances.global.name;
                        expectedAttributes.categoryName = segmentValuesWithCategory.category.name;
                        expectedAttributes.path = segmentValuesWithCategory.category.path;
                        expectedAttributes.categoryId = segmentValuesWithCategory.category.categoryId;
                        expectedAttributes.attribute = segment.segment;
                        expectedAttributes.segmentId = segment.segmentId.intValue();
                        SegmentValuesWithCategory.Segment.SegmentValue taggedValue = segment.segmentValues.stream().filter(s -> Objects.equals(s.segmentValueId, attribute.segmentValueId)).findFirst().orElseThrow(NoSuchElementException::new);
                        expectedAttributes.taggedValue = taggedValue.segmentValue;
                        expectedAttributes.segmentValueId = Long.parseLong(taggedValue.segmentValueId.toString());
                        expectedAttributes.volume = taggedValue.volume;
                        expectedAttributeData.add(expectedAttributes);
                    }
                }
            }
        }

        var filePath = sendRequestAndGetPathOfExportedFile(productInstancesToTest, "live");
        List<ProductAttributesDataInExcel> exportedAttributeData = getProductAttributesInFile(filePath);
        List<String> exportedHeaders = ProductAttributesXLUtils.getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, ProductAttributesXLUtils.EXPECTED_HEADERS, "Headers are not matching");
        Assert.assertEqualsNoOrder(exportedAttributeData.toArray(), expectedAttributeData.toArray());
    }
}

