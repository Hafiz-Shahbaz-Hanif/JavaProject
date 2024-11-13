package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.enums.Enums;

import java.util.List;

public class CreateProductListRequestBody {

    public String name;

    public Enums.ProductListPermission permission;

    public List<InstancePathBase> products;

    public CreateProductListRequestBody(String name, Enums.ProductListPermission permission, List<InstancePathBase> products) {
        this.name = name;
        this.permission = permission;
        this.products = products;
    }

    public CreateProductListRequestBody() {}
}
