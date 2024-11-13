package com.DC.utilities.apiEngine.models.responses.insights;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class AuditResult {

    public AuditResult() {}
    @JsonProperty("Retailer Domain")
    public AuditRow retailerDomain;
    @JsonProperty("Company Name")
    public AuditRow companyName;
    @JsonProperty("Unique ID")
    public AuditRow uniqueID;
    @JsonProperty("Quantity of SOT Images")
    public AuditRow quantityOfSOTImages;
    @JsonProperty("Product Status")
    public AuditRow productStatus;
    @JsonProperty("Scraped Zipcode")
    public AuditRow scrapedZipcode;
    @JsonProperty("Scraped Date")
    public AuditRow scrapedDate;
    @JsonProperty("SOT Upload Date")
    public AuditRow sotUploadDate;
    @JsonProperty("Retailer")
    public AuditRow retailer;
    @JsonProperty("RPC")
    public AuditRow rpc;
    @JsonProperty("Parent RPC")
    public AuditRow parentRPC;
    @JsonProperty("Brand")
    public AuditRow brand;
    @JsonProperty("Category")
    public AuditRow category;
    @JsonProperty("Title")
    public AuditRow title;
    @JsonProperty("Product Description")
    public AuditRow productDescription;
    @JsonProperty("Bullet Point 1")
    public AuditRow bulletPoint1;
    @JsonProperty("Bullet Point 2")
    public AuditRow bulletPoint2;
    @JsonProperty("Bullet Point 3")
    public AuditRow bulletPoint3;
    @JsonProperty("Bullet Point 4")
     public AuditRow bulletPoint4;
     @JsonProperty("Bullet Point 5")
    public AuditRow bulletPoint5;
    @JsonProperty("Bullet Point 6")
    public AuditRow bulletPoint6;
    @JsonProperty("Bullet Point 7")
    public AuditRow bulletPoint7;
    @JsonProperty("Bullet Point 8")
    public AuditRow bulletPoint8;
    @JsonProperty("Bullet Point 9")
    public AuditRow bulletPoint9;
    @JsonProperty("Bullet Point 10")
    public AuditRow bulletPoint10;
    @JsonProperty("Video")
    public AuditRow video;
    @JsonProperty("Legal Disclaimer")
    public AuditRow legalDisclaimer;
    @JsonProperty("SOT Image Count")
    public AuditRow sotImageCount;

    @JsonProperty("SOT Image 1 URL")
    public AuditRow sotImage1URL;

    @JsonProperty("SOT Image 1 URL Match Percent")
    public AuditRow sotImage1URLMatchPercent;

    @JsonProperty("SOT Image 2 URL")
    public AuditRow sotImage2URL;

    @JsonProperty("SOT Image 2 URL Match Percent")
    public AuditRow sotImage2URLMatchPercent;

    @JsonProperty("SOT Image 3 URL")
    public AuditRow sotImage3URL;

    @JsonProperty("SOT Image 3 URL Match Percent")
    public AuditRow sotImage3URLMatchPercent;

    @JsonProperty("SOT Image 4 URL")
    public AuditRow sotImage4URL;

    @JsonProperty("SOT Image 4 URL Match Percent")
    public AuditRow sotImage4URLMatchPercent;

    @JsonProperty("SOT Image 5 URL")
    public AuditRow sotImage5URL;

    @JsonProperty("SOT Image 5 URL Match Percent")
    public AuditRow sotImage5URLMatchPercent;

    @JsonProperty("SOT Image 6 URL")
    public AuditRow sotImage6URL;

    @JsonProperty("SOT Image 6 URL Match Percent")
    public AuditRow sotImage6URLMatchPercent;

    @JsonProperty("SOT Image 7 URL")
    public AuditRow sotImage7URL;

    @JsonProperty("SOT Image 7 URL Match Percent")
    public AuditRow sotImage7URLMatchPercent;

    @JsonProperty("SOT Image 8 URL")
    public AuditRow sotImage8URL;

    @JsonProperty("SOT Image 8 URL Match Percent")
    public AuditRow sotImage8URLMatchPercent;

    @JsonProperty("SOT Image 9 URL")
    public AuditRow sotImage9URL;

    @JsonProperty("SOT Image 9 URL Match Percent")
    public AuditRow sotImage9URLMatchPercent;

    @JsonProperty("SOT Image 10 URL")
    public AuditRow sotImage10URL;

    @JsonProperty("SOT Image 10 URL Match Percent")
    public AuditRow sotImage10URLMatchPercent;

    @JsonProperty("SOT Image 11 URL")
    public AuditRow sotImage11URL;

    @JsonProperty("SOT Image 11 URL Match Percent")
    public AuditRow sotImage11URLMatchPercent;

    @JsonProperty("SOT Image 12 URL")
    public AuditRow sotImage12URL;

    @JsonProperty("SOT Image 12 URL Match Percent")
    public AuditRow sotImage12URLMatchPercent;

    @JsonProperty("SOT Image 13 URL")
    public AuditRow sotImage13URL;

    @JsonProperty("SOT Image 13 URL Match Percent")
    public AuditRow sotImage13URLMatchPercent;

    @JsonProperty("SOT Image 14 URL")
    public AuditRow sotImage14URL;

    @JsonProperty("SOT Image 14 URL Match Percent")
    public AuditRow sotImage14URLMatchPercent;

    @JsonProperty("SOT Image 15 URL")
    public AuditRow sotImage15URL;

    @JsonProperty("SOT Image 15 URL Match Percent")
    public AuditRow sotImage15URLMatchPercent;

    @JsonProperty("SOT Image 16 URL")
    public AuditRow sotImage16URL;

    @JsonProperty("SOT Image 16 URL Match Percent")
    public AuditRow sotImage16URLMatchPercent;

    @JsonProperty("SOT Image 17 URL")
    public AuditRow sotImage17URL;

    @JsonProperty("SOT Image 17 URL Match Percent")
    public AuditRow sotImage17URLMatchPercent;

    @JsonProperty("Summary")
    public AuditRow summary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditResult that = (AuditResult) o;
        return retailerDomain == that.retailerDomain
                && companyName.equals(that.companyName)
                && uniqueID.equals(that.uniqueID)
                && quantityOfSOTImages.equals(that.quantityOfSOTImages)
                && productStatus.equals(that.productStatus)
                && scrapedZipcode.equals(that.scrapedZipcode)
                && scrapedDate.equals(that.scrapedDate)
                && sotUploadDate == that.sotUploadDate
                && retailer == that.retailer
                && rpc == that.rpc
                && parentRPC == that.parentRPC
                && brand == that.brand
                && category == that.category
                && title == that.title
                && productDescription == that.productDescription
                && bulletPoint1 == that.bulletPoint1
                && bulletPoint2 == that.bulletPoint2
                && bulletPoint3 == that.bulletPoint3
                && bulletPoint4 == that.bulletPoint4
                && bulletPoint5 == that.bulletPoint5
                && bulletPoint6 == that.bulletPoint6
                && bulletPoint7 == that.bulletPoint7
                && bulletPoint8 == that.bulletPoint8
                && bulletPoint9 == that.bulletPoint9
                && bulletPoint10 == that.bulletPoint10
                && video == that.video
                && legalDisclaimer == that.legalDisclaimer
                && sotImageCount == that.sotImageCount
                && sotImage1URL == that.sotImage1URL
                && sotImage1URLMatchPercent == that.sotImage1URLMatchPercent
                && sotImage2URL == that.sotImage2URL
                && sotImage2URLMatchPercent == that.sotImage2URLMatchPercent
                && sotImage3URL == that.sotImage3URL
                && sotImage3URLMatchPercent == that.sotImage3URLMatchPercent
                && sotImage4URL == that.sotImage4URL
                && sotImage4URLMatchPercent == that.sotImage4URLMatchPercent
                && sotImage5URL == that.sotImage5URL
                && sotImage5URLMatchPercent == that.sotImage5URLMatchPercent
                && sotImage6URL == that.sotImage6URL
                && sotImage6URLMatchPercent == that.sotImage6URLMatchPercent
                && sotImage7URL == that.sotImage7URL
                && sotImage7URLMatchPercent == that.sotImage7URLMatchPercent
                && sotImage8URL == that.sotImage8URL
                && sotImage8URLMatchPercent == that.sotImage8URLMatchPercent
                && sotImage9URL == that.sotImage9URL
                && sotImage9URLMatchPercent == that.sotImage9URLMatchPercent
                && sotImage10URL == that.sotImage10URL
                && sotImage10URLMatchPercent == that.sotImage10URLMatchPercent
                && sotImage11URL == that.sotImage11URL
                && sotImage11URLMatchPercent == that.sotImage11URLMatchPercent
                && sotImage12URL == that.sotImage12URL
                && sotImage12URLMatchPercent == that.sotImage12URLMatchPercent
                && sotImage13URL == that.sotImage13URL
                && sotImage13URLMatchPercent == that.sotImage13URLMatchPercent
                && sotImage14URL == that.sotImage14URL
                && sotImage14URLMatchPercent == that.sotImage14URLMatchPercent
                && sotImage15URL == that.sotImage15URL
                && sotImage15URLMatchPercent == that.sotImage15URLMatchPercent
                && sotImage16URL == that.sotImage16URL
                && sotImage16URLMatchPercent == that.sotImage16URLMatchPercent
                && sotImage17URL == that.sotImage17URL
                && sotImage17URLMatchPercent == that.sotImage17URLMatchPercent
                && summary.equals(that.summary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retailerDomain, companyName, uniqueID, quantityOfSOTImages, productStatus, scrapedZipcode, scrapedDate, sotUploadDate, retailer,
                rpc, parentRPC, brand, category, title, productDescription, bulletPoint1,bulletPoint2, bulletPoint3, bulletPoint4, bulletPoint5, bulletPoint6,
                bulletPoint7, bulletPoint8, bulletPoint9, bulletPoint10, video, legalDisclaimer, sotImageCount,
                sotImage1URL, sotImage1URLMatchPercent, sotImage2URL, sotImage2URLMatchPercent, sotImage3URL, sotImage3URLMatchPercent, sotImage4URL,
                sotImage4URLMatchPercent, sotImage5URL, sotImage5URLMatchPercent, sotImage6URL, sotImage6URLMatchPercent, sotImage7URL, sotImage7URLMatchPercent,
                sotImage8URL, sotImage8URLMatchPercent, sotImage9URL, sotImage9URLMatchPercent, sotImage10URL, sotImage10URLMatchPercent, sotImage11URL,
                sotImage11URLMatchPercent, sotImage12URL, sotImage12URLMatchPercent, sotImage13URL, sotImage13URLMatchPercent, sotImage14URL,
                sotImage14URLMatchPercent, sotImage15URL, sotImage15URLMatchPercent, sotImage16URL, sotImage16URLMatchPercent, sotImage17URL, sotImage17URLMatchPercent,
                summary);
    }

    @Override
    public String toString() {
        return "{" +
                "retailerDomain:" + retailerDomain + '\'' +
                ", companyName:" + companyName +
                ", uniqueID:" + uniqueID +
                ", quantityOfSOTImages:" + quantityOfSOTImages +
                ", productStatus:" + productStatus + '\'' +
                ", scrapedZipcode:" + scrapedZipcode + '\'' +
                ", scrapedDate:" + scrapedDate + '\'' +
                ", sotUploadDate:" + sotUploadDate +
                ", retailer:" + retailer +
                ", rpc:" + rpc +
                ", parentRPC:" + parentRPC +
                ", brand:" + brand +
                ", category:" + category +
                ", title:" + title +
                ", productDescription:" + productDescription +
                ", bulletPoint1:" + bulletPoint1 +
                ", bulletPoint2:" + bulletPoint2 +
                ", bulletPoint3:" + bulletPoint3 +
                ", bulletPoint4:" + bulletPoint4 +
                ", bulletPoint5:" + bulletPoint5 +
                ", bulletPoint6:" + bulletPoint6 +
                ", bulletPoint7:" + bulletPoint7 +
                ", bulletPoint8:" + bulletPoint8 +
                ", bulletPoint9:" + bulletPoint9 +
                ", bulletPoint10:" + bulletPoint10 +
                ", video:" + video +
                ", legalDisclaimer:" + legalDisclaimer +
                ", sotImageCount:" + sotImageCount +
                ", sotImage1URL:" + sotImage1URL +
                ", sotImage1URLMatchPercent:" + sotImage1URLMatchPercent +
                ", sotImage2URL:" + sotImage2URL +
                ", sotImage2URLMatchPercent:" + sotImage2URLMatchPercent +
                ", sotImage3URL:" + sotImage3URL +
                ", sotImage3URLMatchPercent:" + sotImage3URLMatchPercent +
                ", sotImage4URL:" + sotImage4URL +
                ", sotImage4URLMatchPercent:" + sotImage4URLMatchPercent +
                ", sotImage5URL:" + sotImage5URL +
                ", sotImage5URLMatchPercent:" + sotImage5URLMatchPercent +
                ", sotImage6URL:" + sotImage6URL +
                ", sotImage6URLMatchPercent:" + sotImage6URLMatchPercent +
                ", sotImage7URL:" + sotImage7URL +
                ", sotImage7URLMatchPercent:" + sotImage7URLMatchPercent +
                ", sotImage8URL:" + sotImage8URL +
                ", sotImage8URLMatchPercent:" + sotImage8URLMatchPercent +
                ", sotImage9URL:" + sotImage9URL +
                ", sotImage9URLMatchPercent:" + sotImage9URLMatchPercent +
                ", sotImage10URL:" + sotImage10URL +
                ", sotImage10URLMatchPercent:" + sotImage10URLMatchPercent +
                ", sotImage11URL:" + sotImage11URL +
                ", sotImage11URLMatchPercent:" + sotImage11URLMatchPercent +
                ", sotImage12URL:" + sotImage12URL +
                ", sotImage12URLMatchPercent:" + sotImage12URLMatchPercent +
                ", sotImage13URL:" + sotImage13URL +
                ", sotImage13URLMatchPercent:" + sotImage13URLMatchPercent +
                ", sotImage14URL:" + sotImage14URL +
                ", sotImage14URLMatchPercent:" + sotImage14URLMatchPercent +
                ", sotImage15URL:" + sotImage15URL +
                ", sotImage15URLMatchPercent:" + sotImage15URLMatchPercent +
                ", sotImage16URL:" + sotImage16URL +
                ", sotImage16URLMatchPercent:" + sotImage16URLMatchPercent +
                ", sotImage17URL:" + sotImage17URL +
                ", sotImage17URLMatchPercent:" + sotImage17URLMatchPercent +
                ", summary:" + summary +
                '}';
    }
}
