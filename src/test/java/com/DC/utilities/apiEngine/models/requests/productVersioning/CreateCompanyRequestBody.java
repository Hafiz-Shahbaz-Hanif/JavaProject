package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class CreateCompanyRequestBody {

    public String name;

    public String cpgCompanyId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<CompanyRetailers> retailers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> locales;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> campaigns;

    public CreateCompanyRequestBody(String name, String cpgCompanyId) {
        this.name = name;
        this.cpgCompanyId = cpgCompanyId;
    }

    public CreateCompanyRequestBody(String name,
                                    String cpgCompanyId,
                                    List<CompanyRetailers> retailers,
                                    List<String> locales,
                                    List<String> campaigns
    ) {
        this.name = name;
        this.cpgCompanyId = cpgCompanyId;
        this.retailers = retailers;
        this.locales = locales;
        this.campaigns = campaigns;
    }

    public CreateCompanyRequestBody() {}

    public static class CompanyRetailers {
        public int retailerDomainId;
        public String clientRetailerName;

        public CompanyRetailers(int retailerDomainId, String clientRetailerName) {
            this.retailerDomainId = retailerDomainId;
            this.clientRetailerName = clientRetailerName;
        }
    }
}
