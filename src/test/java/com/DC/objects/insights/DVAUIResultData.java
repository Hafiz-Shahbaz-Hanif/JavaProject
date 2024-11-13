package com.DC.objects.insights;

import java.util.List;
import java.util.Objects;

public class DVAUIResultData {

    public String retailerName;
    public String rpc;
    public List<String> issues;
    public AuditResult sotAuditResult;
    public AuditResult pdpAuditResult;

    public static class AuditResult {
        public String title;
        public String brand;
        public String category;
        public String video;
        public String uniqueId;
        public String productDescription;
        public String legalDisclaimer;
        public List<BulletData> bullets;

        public AuditResult(String title, String brand, String category, String video, String uniqueId, String productDescription, String legalDisclaimer, List<BulletData> bullets) {
            this.title = title;
            this.brand = brand;
            this.category = category;
            this.video = video;
            this.uniqueId = uniqueId;
            this.productDescription = productDescription;
            this.legalDisclaimer = legalDisclaimer;
            this.bullets = bullets;
        }

        public AuditResult() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AuditResult)) return false;
            AuditResult that = (AuditResult) o;
            return title == that.title &&
                    brand == that.brand &&
                    category == that.category &&
                    video == that.video &&
                    uniqueId == that.uniqueId &&
                    productDescription == that.productDescription &&
                    legalDisclaimer == that.legalDisclaimer &&
                    bullets == that.bullets;
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, brand, category, video, uniqueId, productDescription, legalDisclaimer, bullets);
        }

        @Override
        public String toString() {
            return "AuditResult{" +
                    ", title='" + title + '\'' +
                    ", brand='" + brand + '\'' +
                    ", category='" + category + '\'' +
                    ", video='" + video + '\'' +
                    ", uniqueId='" + uniqueId + '\'' +
                    ", productDescription='" + productDescription + '\'' +
                    ", legalDisclaimer='" + legalDisclaimer + '\'' +
                    ", bullets='" + bullets + '\'' +
                    '}';
        }
    }

   public DVAUIResultData(String retailerName, String rpc, List<String> issues, AuditResult sotAuditResult, AuditResult pdpAuditResult) {
        this.retailerName = retailerName;
        this.rpc = rpc;
        this.issues = issues;
        this.sotAuditResult = sotAuditResult;
        this.pdpAuditResult = pdpAuditResult;
   }

   public DVAUIResultData(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DVAUIResultData)) return false;
        DVAUIResultData that = (DVAUIResultData) o;
        return retailerName == that.retailerName &&
                rpc == that.rpc &&
                issues == that.issues &&
                sotAuditResult == that.sotAuditResult &&
                pdpAuditResult == that.pdpAuditResult;
    }

    @Override
    public int hashCode() {
        return Objects.hash(retailerName, rpc, issues, sotAuditResult, pdpAuditResult);
    }

    @Override
    public String toString() {
        return "DVAUIResultData{" +
                ", retailerName='" + retailerName + '\'' +
                ", rpc='" + rpc + '\'' +
                ", issues='" + issues + '\'' +
                ", sotAuditResult='" + sotAuditResult + '\'' +
                ", pdpAuditResult='" + pdpAuditResult + '\'' +
                '}';
    }

    public static class BulletData {
        public String bullet;
        public boolean isBulletHighlighted;

        public BulletData(String bullet, boolean isBulletHighlighted) {
            this.bullet = bullet;
            this.isBulletHighlighted = isBulletHighlighted;
        }

        public BulletData(){}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BulletData)) return false;
            BulletData that = (BulletData) o;
            return bullet == that.bullet &&
                    isBulletHighlighted == that.isBulletHighlighted;
        }

        @Override
        public int hashCode() {
            return Objects.hash(bullet, isBulletHighlighted);
        }

        @Override
        public String toString() {
            return "BulletData{" +
                    ", bullet='" + bullet + '\'' +
                    ", isBulletHighlighted='" + isBulletHighlighted + '\'' +
                    '}';
        }
    }
}
