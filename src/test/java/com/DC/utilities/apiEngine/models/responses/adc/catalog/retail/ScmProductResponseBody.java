package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScmProductResponseBody extends BaseClassAsinSegmentationResponseBody{

    public List<Product> products;
    public List<Product> getProducts(){
        return products;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product{
         String product;
         String image;
         String profitablilty;
         String sales;
         String salesPercentChange;
         String adSpend;
         String mostImpactedBy;
         String productTitle;
         String orderedRevenues;
         String orderedRevenuesPercentChange;
         String priority;

         public List<String> getProduct(){
             return  Arrays.asList (product,image,sales,salesPercentChange,mostImpactedBy,productTitle,orderedRevenues,
        orderedRevenuesPercentChange,priority);
    }
    }

    public List<SalesImpacts> salesImpacts;
    public List<SalesImpacts> getSalesImpacts(){
        return salesImpacts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SalesImpacts{
        String score;
        String eventName;
        String eventDescription;

        public List<String> getSalesImpact(){
            return  Arrays.asList (eventName,eventDescription);
        }

    }
}
