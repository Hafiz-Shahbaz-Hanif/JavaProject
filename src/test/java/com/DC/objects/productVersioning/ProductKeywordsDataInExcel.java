package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;

public class ProductKeywordsDataInExcel {

    public String productIdentifier;

    public String locale;

    public String retailer;

    public String campaign;

    public Enums.KeywordBucketType keywordBucket;

    public String searchPhrase;

    public long volume;

    @Override
    public String toString() {
        return "{" +
                "productIdentifier='" + productIdentifier + '\'' +
                ", locale='" + locale + '\'' +
                ", retailer='" + retailer + '\'' +
                ", campaign='" + campaign + '\'' +
                ", keywordBucket=" + keywordBucket +
                ", searchPhrase='" + searchPhrase + '\'' +
                ", volume=" + volume +
                '}';
    }
}
