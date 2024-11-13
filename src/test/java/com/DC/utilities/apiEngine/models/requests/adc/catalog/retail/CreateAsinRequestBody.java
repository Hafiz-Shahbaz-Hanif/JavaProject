package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

public class CreateAsinRequestBody {

    public String segmentationTypeId;
    public String segmentationValue;
    public int asinSegmentationId;

    public CreateAsinRequestBody(String segmentationTypeId, String segmentationValue, int asinSegmentationId) {
        this.segmentationTypeId = segmentationTypeId;
        this.segmentationValue = segmentationValue;
        this.asinSegmentationId = asinSegmentationId;
    }

}
