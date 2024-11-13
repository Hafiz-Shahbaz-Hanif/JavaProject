package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

public class DeleteAsinRequestBody {

    public String segmentationValue;
    public int asinSegmentationId;
    public int id;
    public String segmentationLabel;
    public boolean deleted;
    public boolean active;

    public DeleteAsinRequestBody(int id, int asinSegmentationId,String segmentationValue,String segmentationLabel, boolean deleted, boolean active  )
    {
        this.id = id;
        this.asinSegmentationId = asinSegmentationId;
        this.segmentationValue = segmentationValue;
        this.segmentationLabel = segmentationLabel;
        this.deleted = deleted;
        this.active = active;
    }
}
