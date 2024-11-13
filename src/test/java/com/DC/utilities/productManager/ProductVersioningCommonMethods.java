package com.DC.utilities.productManager;

import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.DigitalAssetProperty;
import com.DC.utilities.enums.Enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ProductVersioningCommonMethods {

    public static List<CompanyPropertiesBase.Property> generateImageMappingProperties(String propertyId, String propertyName) {
        CompanyPropertiesBase.Property imageMappingProperty = new CompanyPropertiesBase.Property();
        imageMappingProperty.id = propertyId + "_mapping";
        imageMappingProperty.name = propertyName + " Mapping";
        imageMappingProperty.type = Enums.PropertyType.IMAGE_MAPPING;
        imageMappingProperty.dropdownValues = asList(
                new CompanyProperties.PropertyDropdownValue("required", "required"),
                new CompanyProperties.PropertyDropdownValue("optional", "optional"),
                new CompanyProperties.PropertyDropdownValue("none", "none")
        );
        imageMappingProperty.helpText = "Define if image should be collected in task";
        imageMappingProperty.allowMultipleValues = true;
        imageMappingProperty.group = "Image Workflow Mapping";

        CompanyPropertiesBase.Property imageInstructionsProperty = new CompanyPropertiesBase.Property();
        imageInstructionsProperty.id = propertyId + "_instructions";
        imageInstructionsProperty.name = propertyName + " Instructions";
        imageInstructionsProperty.type = Enums.PropertyType.IMAGE_INSTRUCTIONS;
        imageInstructionsProperty.helpText = "Instructions for collecting image in task";
        imageInstructionsProperty.allowMultipleValues = true;
        imageInstructionsProperty.group = "Image Workflow Instructions";

        return asList(imageMappingProperty, imageInstructionsProperty);
    }

    private static final Map<String, Map<String, String>> ASSETS_MAP = new HashMap<>() {{
        put("dev", Map.of(
                "small.jpg", "825d3bc4-88ee-4467-b9ea-0c93c3ef48ef",
                "small-modified.jpg", "a700b997-72cc-4396-a1a0-70d4a0bff25c",
                "testing_meme13.jpg", "f7d2c7e7-4ecb-4544-80dd-41e459abb92f"
        ));

        put("validation", Map.of(
                "small.jpg", "c08ddff8-0fa0-446f-bd73-2475ba5cd006",
                "small-modified.jpg", "884d385a-ac92-49f7-a6fe-4eb4c0d10771",
                "testing_meme13.jpg", "d7d8c138-6ba4-44b5-9940-09832ec86225"
        ));

        put("sandbox", Map.of(
                "small.jpg", "0888335b-90ca-4122-aa94-7c9d096969a7",
                "small-modified.jpg", "9f53525a-3880-4c7b-8761-1aabb507e7a4",
                "testing_meme13.jpg", "6ee2a714-a8c9-4ff1-8cf1-eeaa33e49363"
        ));
    }};

    public static DigitalAssetProperty.Assets getDigitalAsset(String insightsEnvironment, String imageName) {
        Map<String, String> assets = ASSETS_MAP.get(insightsEnvironment);

        if (assets == null || !assets.containsKey(imageName)) {
            throw new IllegalArgumentException("Invalid environment or image");
        }

        var mediaId = assets.get(imageName);
        var imageUrl = String.format("https://media.%s.onespace.com/assets/%s/%s", insightsEnvironment, mediaId, imageName);
        return new DigitalAssetProperty.Assets(imageUrl, mediaId);
    }

    public static DigitalAssetProperty.Assets getSmallImageDigitalAsset(String insightsEnvironment) {
        return getDigitalAsset(insightsEnvironment, "small.jpg");
    }

    public static DigitalAssetProperty.Assets getSmallModifiedImageDigitalAsset(String insightsEnvironment) {
        return getDigitalAsset(insightsEnvironment, "small-modified.jpg");
    }

    public static DigitalAssetProperty.Assets getTestingMemeImageDigitalAsset(String insightsEnvironment) {
        return getDigitalAsset(insightsEnvironment, "testing_meme13.jpg");
    }
}
