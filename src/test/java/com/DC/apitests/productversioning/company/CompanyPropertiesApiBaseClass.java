package com.DC.apitests.productversioning.company;

import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateCompanyPropertiesRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateCompanyRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import org.awaitility.core.ConditionTimeoutException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static org.awaitility.Awaitility.await;

public class CompanyPropertiesApiBaseClass extends CompanyApiBaseClass {

    CreateCompanyPropertiesRequestBody createCompanyPropertiesReqBody;

    @BeforeClass()
    public void setupCompanyPropertiesApiTests() {
        createCompanyPropertiesReqBody = getCompanyPropertiesToAdd();
    }

    // TODO - This method is a workaround for the fact that we need to manually apply templates to new companies
    //  We need to setup the company to have our properties and the templates applied
    protected Company setupCompanyForTest(CreateCompanyRequestBody companyToAdd) throws Exception {
        Company company = COMPANY_COLLECTION.getCompanyByName(companyToAdd.name);

        if (company == null) {
            company = CompanyApiService.createCompany(companyToAdd, jwt);
        }

        String currentCompanyFromJWT = CompanyApiService.getCompany(jwt).name;
        if (!currentCompanyFromJWT.equals(companyToAdd.name)) {
            jwt = changeInsightsCompanyAndGetJwt(jwt, company._id, company.name);
        }

        CompanyApiService.replaceCompanyProperties(createCompanyPropertiesReqBody, jwt);
        return CompanyApiService.getCompany(jwt);
    }

    protected void verifyPropertyWasRemovedFromGroup(CompanyProperties.Property removedProperty, CompanyProperties.Group groupAfterRemovingProperty, CompanyProperties.Group expectedGroup) {
        boolean propertyWasRemovedFromGroup = groupAfterRemovingProperty.properties
                .stream()
                .noneMatch(property -> property.id.equals(removedProperty.id));

        Assert.assertTrue(propertyWasRemovedFromGroup, "Property " + removedProperty.id + " was not removed from group" + removedProperty.group);

        int propertyToRemoveIndex = IntStream.range(0, expectedGroup.properties.size())
                .filter(i -> Objects.equals(expectedGroup.properties.get(i).id, removedProperty.id))
                .findFirst()
                .orElse(-1);


        expectedGroup.properties.remove(propertyToRemoveIndex);

        for (int i = propertyToRemoveIndex; i < expectedGroup.properties.size(); i++) {
            expectedGroup.properties.get(i).sortIndex = expectedGroup.properties.get(i).sortIndex.intValue() - 1;
        }

        Assert.assertEquals(
                groupAfterRemovingProperty,
                expectedGroup,
                "Group in company doesn't match with expected group after removing a property from company" +
                        "\nExpected Group:\n" + expectedGroup +
                        "\nGroup After Removing Property:\n" + groupAfterRemovingProperty
        );
    }

    protected ProductVariantPropertySet waitForPropertySetToChange(ProductVariantInstancePath instancePath, ProductVariantPropertySet variantPropertySetBefore, String errorMsgToShowInFailure) throws Exception {
        try {
            ProductVariantPropertySet[] propertySetAfter = new ProductVariantPropertySet[1];
            await().atMost(10, TimeUnit.SECONDS).until(() -> {
                ProductVariantPropertySet propertySet = ProductVersioningApiService.getPropertySetData(instancePath, null, jwt);
                propertySetAfter[0] = propertySet;
                return !propertySet.companyPropertiesId.equals(variantPropertySetBefore.companyPropertiesId);
            });
            return propertySetAfter[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail(errorMsgToShowInFailure + " after 10 seconds");
        }
        return null;
    }

    protected ProductVariantDigitalAssetSet waitForDigitalAssetPropertySetToChange(ProductVariantInstancePath instancePath, ProductVariantDigitalAssetSet variantPropertySetBefore, String errorMsgToShowInFailure) throws Exception {
        try {
            ProductVariantDigitalAssetSet[] propertySetAfter = new ProductVariantDigitalAssetSet[1];
            await().atMost(10, TimeUnit.SECONDS).until(() -> {
                ProductVariantDigitalAssetSet propertySet = ProductVersioningApiService.getDigitalAssetSetData(instancePath, null, jwt);
                propertySetAfter[0] = propertySet;
                return !propertySet.companyPropertiesId.equals(variantPropertySetBefore.companyPropertiesId);
            });
            return propertySetAfter[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail(errorMsgToShowInFailure + " after 10 seconds");
        }
        return null;
    }
}
