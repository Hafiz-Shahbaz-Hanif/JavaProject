package com.DC.utilities.apiEngine.models.responses.productVersioning;

public class ImportTemplatesResponse {
    
    public String companyId;

    public String message;

    public boolean success;

    public Data data;

    public static class Data {

        public String s3Link;
    }
}
