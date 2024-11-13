package com.DC.utilities.apiEngine.models.requests.insights;

import com.DC.utilities.enums.Enums;

public class CreateProductAuditorExportRequestBody {

    public String importWorkbook;

    public Enums.ProductAuditorType auditType;

    public boolean skipQueue;

    public CreateProductAuditorExportRequestBody(String importWorkbook, Enums.ProductAuditorType auditType, boolean skipQueue) {
        this.importWorkbook = importWorkbook;
        this.auditType = auditType;
        this.skipQueue = skipQueue;
    }

    public CreateProductAuditorExportRequestBody() {}
}
