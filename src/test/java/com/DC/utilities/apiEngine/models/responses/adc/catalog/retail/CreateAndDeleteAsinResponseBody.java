package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.testng.Assert;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAndDeleteAsinResponseBody {

        public int id;
        public int asinSegmentationId;
        public String segmentationValue;
        public String segmentationLabel;
        public boolean deleted;
        public boolean active;

        public int getId() { return id;}
        public int getAsinSegmentationId(){return asinSegmentationId;}
        public  String getSegmentationValue( ){return segmentationValue;}
        public  String getSegmentationLabel( ){return segmentationLabel;}
        public boolean getDeleted(){return deleted;}
        public boolean getActive(){return active;}


        public static void verifyCreatedAsinData(CreateAndDeleteAsinResponseBody Asin) {
                Assert.assertNotNull(Asin.getSegmentationValue(), "Segmentation Value is null");
                Assert.assertNotNull(Asin.getSegmentationLabel(), "Segmentation Label is null");
                Assert.assertFalse(Asin.getDeleted(), "Deleted attribute is true");
                Assert.assertTrue(Asin.getActive(),"Active Attribute is false");
        }

        public static void verifyDeletedAsinData(CreateAndDeleteAsinResponseBody Asin) {
                Assert.assertNotNull(Asin.getSegmentationValue(), "Segmentation Value is null");
                Assert.assertNotNull(Asin.getSegmentationLabel(), "Segmentation Label is null");
                Assert.assertTrue(Asin.getDeleted(), "Deleted attribute is false");
                Assert.assertFalse(Asin.getActive(),"Active Attribute is true");
        }

}
