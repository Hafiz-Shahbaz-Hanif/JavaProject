package com.DC.utilities.apiEngine.models.responses.productVersioning;

public class ImportResponse {

    public String importId;

    public String message;

    public boolean success;

    public Data data;

    public static class Data {

        public String url;

        public String errorMessage;

        public Object error;
    }
}
