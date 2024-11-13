package com.DC.objects.catalog;

import com.DC.utilities.DateUtility;

public class RetailBase {

    //Client Categories and Account Types
    public static final String BASIC_CLIENT_CATEGORY = "BASIC";
    public static final String PREMIUM_CLIENT_CATEGORY = "PREMIUM";
    public static final String SELLER_CLIENT_ACCOUNT_TYPE = "SELLER";
    public static final String VENDOR_CLIENT_ACCOUNT_TYPE = "VENDOR";

    //Time Period Constants
    public static String fytdStartDate = DateUtility.getFirstDayOfTheYear().replace("-", "");
    public static String lfytdStartDate = DateUtility.getFirstDayOfLastYear().replace("-", "");

    //Misc
    public static final String SHIPPED_COGS_METRIC = "SHIPPED_COGS";
    public static final String AMAZON_RETAILER_PLATFORM = "AMAZON RETAIL";
    public static final String MANUFACTURING_DISTRIBUTION_VIEW = "Manufacturing";
    public static final String SOURCING_DISTRIBUTION_VIEW = "Sourcing";
    public static final String YOY_COMPARISON_TYPE = "YOY";
    public static final String ARAP_TITLE = "Shipped COGS & YOY % Comparison | ARAP";
    public static final String OBSOLETE_ASIN_TYPE = "DEFAULT OBSOLETE ASIN SEGMENT";
}
