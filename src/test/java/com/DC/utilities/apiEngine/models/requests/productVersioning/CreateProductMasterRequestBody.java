package com.DC.utilities.apiEngine.models.requests.productVersioning;

public class CreateProductMasterRequestBody {
    public String uniqueId;
    public String name;
    public String thumbnail;

    public CreateProductMasterRequestBody(String uniqueId, String name, String thumbnail) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public CreateProductMasterRequestBody() {}
}
