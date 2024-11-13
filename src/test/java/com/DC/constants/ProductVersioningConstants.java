package com.DC.constants;

import com.DC.utilities.enums.Enums;

import java.util.*;

public class ProductVersioningConstants {

    public static final List<String> PRODUCT_IMPORT_EXPORT_BASE_HEADERS = Arrays.asList("product identifier", "locale", "retailer", "campaign");
    public static final List<String> PRODUCT_KEYWORD_BUCKETS = Arrays.asList("title", "onPage", "optional", "reserved", "branded", "hidden", "unused", "rankTracking");

    public static final LinkedHashMap<Enums.KeywordBucketType, String> PRODUCT_KEYWORD_BUCKETS_UI = new LinkedHashMap<>() {
        {
            put(Enums.KeywordBucketType.TITLE, "Title");
            put(Enums.KeywordBucketType.ON_PAGE, "On Page");
            put(Enums.KeywordBucketType.OPTIONAL, "Optional");
            put(Enums.KeywordBucketType.RESERVED, "Reserved");
            put(Enums.KeywordBucketType.BRANDED, "Branded");
            put(Enums.KeywordBucketType.HIDDEN, "Hidden");
            put(Enums.KeywordBucketType.UNUSED, "Unused");
            put(Enums.KeywordBucketType.RANK_TRACKING, "Rank Tracking");
        }
    };

    public static final String MULTI_VALUE_PROPERTY_NAME = "test property 1";
    public static final String NUMBER_PROPERTY_NAME = "test property 2";
    public static final String DATE_PROPERTY_NAME = "test property 3";
    public static final String BOOLEAN_PROPERTY_NAME = "test property 4";
    public static final String SINGLE_VALUE_PROPERTY_NAME = "test property 5";
    public static final String DROPDOWN_PROPERTY_NAME = "test property 6";
    public static final String LINK_PROPERTY_NAME = "test property 7";
    public static final String HTML_PROPERTY_NAME = "test property 8";
    public static final String RICH_TEXT_PROPERTY_NAME = "test property 9";
    public static final String BUSINESS_UNITS_PROPERTY_NAME = "Business Units";
    public static final String PREVIOUS_RPCS_PROPERTY_NAME = "Previous RPCs";
    public static final String PRODUCT_NAME_PROPERTY_NAME = "Product Name";
    public static final String RPC_PROPERTY_NAME = "RPC";
    public static final String IMAGE_MAPPING_PROPERTY_NAME = "test digital asset 5 Image Mapping";
    public static final String DIGITAL_ASSET_PROPERTY_NAME = "digital asset property 1";

    public enum ReviewVerdict {
        APPROVE,
        REVIEW,
    }

    public static final List<String> MERGE_5_PROPERTIES = Arrays.asList(
            "Normalized Brand", "Normalized Manufacturer", "Classifier Categorization Level 1", "Classifier Categorization Level 2", "Classifier Categorization Level 3"
    );
}