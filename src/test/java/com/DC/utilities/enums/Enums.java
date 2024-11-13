package com.DC.utilities.enums;

import com.DC.utilities.SharedMethods;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public abstract class Enums {
    public enum ProductVariantType {
        @JsonProperty("live")
        LIVE("live"),

        @JsonProperty("staged")
        STAGED("staged");

        private final String type;

        ProductVariantType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public enum ProductVariantLevel {
        @JsonProperty("global")
        GLOBAL("global"),

        @JsonProperty("retailer")
        RETAILER("retailer"),

        @JsonProperty("globalCampaign")
        GLOBAL_CAMPAIGN("globalCampaign"),

        @JsonProperty("retailerCampaign")
        RETAILER_CAMPAIGN("retailerCampaign");

        private final String level;

        public String getLevel() {
            return level;
        }

        ProductVariantLevel(String level) {
            this.level = level;
        }
    }

    public enum DeleteProductVariantLevel {
        @JsonProperty("retailer")
        RETAILER("retailer"),

        @JsonProperty("globalCampaign")
        GLOBAL_CAMPAIGN("globalCampaign"),

        @JsonProperty("retailerCampaign")
        RETAILER_CAMPAIGN("retailerCampaign");

        private final String level;

        public String getLevel() {
            return level;
        }

        DeleteProductVariantLevel(String level) {
            this.level = level;
        }
    }

    public enum KeywordBucketType {
        @JsonProperty("title")
        TITLE("title"),

        @JsonProperty("onPage")
        ON_PAGE("onPage"),

        @JsonProperty("optional")
        OPTIONAL("optional"),

        @JsonProperty("reserved")
        RESERVED("reserved"),

        @JsonProperty("branded")
        BRANDED("branded"),

        @JsonProperty("hidden")
        HIDDEN("hidden"),

        @JsonProperty("unused")
        UNUSED("unused"),

        @JsonProperty("rankTracking")
        RANK_TRACKING("rankTracking");

        private final String bucketType;

        public String getBucketType() {
            return bucketType;
        }

        KeywordBucketType(String bucketType) {
            this.bucketType = bucketType;
        }

        public static KeywordBucketType fromText(String text) {
            return Arrays.stream(values())
                    .filter(item -> item.bucketType.equalsIgnoreCase(text))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        }

        public String getBucketTypeForUI() {
            return SharedMethods.humanizeString(bucketType);
        }
    }

    public enum PropertyType {
        @JsonProperty("string")
        STRING("string"),

        @JsonProperty("number")
        NUMBER("number"),

        @JsonProperty("dropdown")
        DROPDOWN("dropdown"),

        @JsonProperty("boolean")
        BOOLEAN("boolean"),

        @JsonProperty("date")
        DATE("date"),

        @JsonProperty("digital_asset")
        DIGITAL_ASSET("digital_asset"),

        @JsonProperty("link")
        LINK("link"),

        @JsonProperty("html")
        HTML("html"),

        @JsonProperty("rich_text")
        RICH_TEXT("rich_text"),

        @JsonProperty("image_mapping")
        IMAGE_MAPPING("image_mapping"),

        @JsonProperty("image_instructions")
        IMAGE_INSTRUCTIONS("image_instructions");

        private final String type;

        PropertyType(String type) {
            this.type = type;
        }

        public String getPropertyType() {
            return type;
        }

        public static PropertyType fromText(String text) {
            return Arrays.stream(values())
                    .filter(item -> item.type.equalsIgnoreCase(text))
                    .findFirst().orElse(null);
        }

        public String getPropertyTypeForUI() {
            return SharedMethods.humanizeString(type);
        }
    }

    public enum ProductListPermission {
        @JsonProperty("private")
        PRIVATE("private"),

        @JsonProperty("public")
        PUBLIC("public");

        private final String permission;

        ProductListPermission(String permission) {
            this.permission = permission;
        }

        public String getPermissionType() {
            return permission;
        }

        public String getPermissionTypeForUI() {
            return StringUtils.capitalize(permission);
        }

        public static ProductListPermission fromText(String text) {
            return Arrays.stream(values())
                    .filter(item -> item.permission.equalsIgnoreCase(text))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        }
    }

    public enum ProductAuditorType {
        @JsonProperty("source-of-truth")
        SOT("source-of-truth"),

        @JsonProperty("retailer-capabilities")
        RCA("retailer-capabilities"),

        @JsonProperty("retailer-scraped-data")
        RSD("retailer-scraped-data");

        private final String auditType;

        ProductAuditorType(String auditType) {this.auditType = auditType;}

        public String getAuditType() {return auditType;}
    }

    public enum ImportTrackingType {
        @JsonProperty("file")
        FILE("file");

        private final String importTrackingType;

        ImportTrackingType(String importTrackingType) {
            this.importTrackingType = importTrackingType;
        }

        public String getImportTrackingType() {
            return importTrackingType;
        }
    }

    public enum ImportType {
        @JsonProperty("property")
        PROPERTY("property"),

        @JsonProperty("company")
        COMPANY("company"),

        @JsonProperty("keyword")
        KEYWORD("keyword");

        private final String importType;

        ImportType(String importType) {
            this.importType = importType;
        }

        public String getImportType() {
            return importType;
        }

        public static ImportType fromText(String text) {
            return Arrays.stream(values())
                    .filter(item -> item.importType.equalsIgnoreCase(text))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        }
    }

    public enum ExportType {
        @JsonProperty("product")
        PRODUCT("product"),

        @JsonProperty("company")
        COMPANY("company");

        private final String exportType;

        ExportType(String exportType) {
            this.exportType = exportType;
        }

        public String getExportType() {
            return exportType;
        }
    }

    public enum ExportSubType {
        @JsonProperty("property")
        PROPERTY("property"),

        @JsonProperty("keywords")
        KEYWORDS("keywords"),

        @JsonProperty("attribute")
        ATTRIBUTE("attribute"),

        @JsonProperty("digitalAsset")
        DIGITAL_ASSET("digitalAsset");

        private final String exportSubType;

        ExportSubType(String exportSubType) {
            this.exportSubType = exportSubType;
        }

        public String getExportSubType() {
            return exportSubType;
        }
        
        public String getExportSubTypeForValueInUI() {
            if (exportSubType.endsWith("y")) {
                return exportSubType.substring(0, exportSubType.length() - 1) + "ies";
            } else if (!exportSubType.equals("keywords")) {
                return exportSubType + "s";
            } else {
                return exportSubType;
            }
        }
    }

    public enum ProcessStatus {
        @JsonProperty(value = "Processing")
        PROCESSING("Processing"),

        @JsonProperty(value = "Failed")
        FAILED("Failed"),

        @JsonProperty(value = "Success")
        SUCCESS("Success"),

        @JsonProperty("Partial Failure")
        PARTIAL_FAILURE("Partial Failure"),

        @JsonProperty("Preview")
        PREVIEW("Preview"),

        @JsonProperty("Cancelled")
        CANCELLED("Cancelled");

        private final String processStatus;

        ProcessStatus(String processStatus) {
            this.processStatus = processStatus;
        }

        public String getProcessStatus() {
            return processStatus;
        }
    }

    public enum ImportBasicState {
        @JsonProperty(value = "failed")
        FAILED("failed"),

        @JsonProperty(value = "success")
        SUCCESS("success");

        private final String importDefaultStatus;

        ImportBasicState(String importStatus) {
            this.importDefaultStatus = importStatus;
        }

        public String getImportStatus() {
            return importDefaultStatus;
        }
    }

    public enum ImportStage {

        STANDARDIZE,

        TRANSFORM,

        PUBLISH
    }

    public enum ExportCoreType {
        @JsonProperty(value = "product-property")
        PRODUCT_PROPERTY("product-property"),

        @JsonProperty(value = "company-properties")
        COMPANY_PROPERTY("company-properties");

        private final String exportType;

        ExportCoreType(String exportType) {
            this.exportType = exportType;
        }

        public String getExportType() {
            return exportType;
        }
    }

    public enum ExportCoreStatus {
        @JsonProperty(value = "pending")
        PENDING("pending"),

        @JsonProperty(value = "failed")
        FAILED("failed"),

        @JsonProperty(value = "done")
        DONE("done");

        private final String exportStatus;

        ExportCoreStatus(String exportStatus) {
            this.exportStatus = exportStatus;
        }

        public String getExportStatus() {
            return exportStatus;
        }
    }

    public enum APIRequestMethod {
        POST,

        PATCH,

        PUT,

        DELETE,

        GET
    }

    public enum LandingPage {
        Dashboard,
        InsightsProductsPage,
        Tasks,
        Deployment
    }

    public enum TaskStatus {
        Available,
        InProgress,
        MyTask,
        Revision
    }

    public enum TaskFilter {
        Status,
        Contributor,
        Assignment
    }

    public enum TaskType {
        Content,
        Image,
        Attribute,
        Keyword,
        ContentCollabReview,
        Rpc,
        EnhancedContent
    }

    public enum SCTProductsColumns {
        Sales,
        ChangeInSales,
        AdSpend
    }

    public enum ColumnArrowSorting {
        Ascending,
        Descending
    }

    public enum ToggleAction {
        Show,
        Hide
    }

    public enum Platform {
        AMAZON,
        WALMART,
        INSTACART
    }

    public enum SpellCheckOption {
        ThisOccurenceOnly(0),
        AllOccurrencesInField(1),
        AllOccurrencesOnProduct(2),
        AllOccurrencesOnAllProducts(3);

        public final int value;

        SpellCheckOption(int value) {
            this.value = value;
        }
    }

    public enum KeywordFlag {
        @JsonProperty(value = "add")
        ADD,

        @JsonProperty(value = "replace")
        REPLACE
    }

    public enum CompanyPropertiesTemplateType {
        @JsonProperty(value = "test")
        TEST,
        @JsonProperty(value = "standard")
        STANDARD
    }

    public enum Property {
        STANDARD("Standard Properties"),
        DIGITAL_ASSETS("Digital Assets"),
        ALL("All Properties");

        private final String PROPERTY;

        Property(String property) {this.PROPERTY = property;}

        public String getPropertyType() {return PROPERTY;}
    }

    public enum PropertyGroupType {
        STANDARD("Standard"),
        DIGITAL_ASSET("Digital Asset");

        private final String GROUP_TYPE;

        PropertyGroupType(String property) {this.GROUP_TYPE = property;}

        public String getGroupType() {return GROUP_TYPE;}
    }

    public enum NoteType {
        SUCCESS,
        WARNING,
        ERROR,
        INFO
    }

    public enum FlightDeckShowMe {
        CAMPAIGNS,
        ASIN,
        KEYWORDS,
        KEYWORDS_ROLLED_UP,
        CUSTOMER_SEARCH_QUERY,
        AD_GROUPS_BY_CAMPAIGN

    }

    public enum AutoPauseState {
        Paused,
        Enabled
    }

    public enum TargetingType {
        Manual,
        Auto
    }

    public enum KeywordMatchType {
        Exact,
        Phrase,
        Broad,
        NegativeExact,
        NegativePhrase,
    }
}

