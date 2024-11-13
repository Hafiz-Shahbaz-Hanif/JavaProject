package com.DC.utilities.apiEngine.models.requests.insights;

import com.DC.utilities.enums.Enums;

public class CreateProductAuditorRequestBody {

    public String importWorkbook;

    public Enums.ProductAuditorType auditType;

    public CreateProductAuditorRequestBody(String importWorkbook, Enums.ProductAuditorType auditType) {
        this.importWorkbook = importWorkbook;
        this.auditType = auditType;
    }

    public CreateProductAuditorRequestBody() {}
}
