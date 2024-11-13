package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductVariantKeywords {

    public List<String> title;

    public List<String> onPage;

    public List<String> optional;

    public List<String> reserved;

    public List<String> branded;

    public List<String> hidden;

    public List<String> unused;

    public List<String> rankTracking;

    public ProductVariantKeywords(
            List<String> title, List<String> onPage, List<String> optional, List<String> reserved,
            List<String> branded, List<String> hidden, List<String> unused, List<String> rankTracking) {
        this.title = title;
        this.onPage = onPage;
        this.optional = optional;
        this.reserved = reserved;
        this.branded = branded;
        this.hidden = hidden;
        this.unused = unused;
        this.rankTracking = rankTracking;
    }

    public ProductVariantKeywords() {}


    @Override
    public String toString() {
        return "ProductVariantKeywords{" +
                "title=" + title +
                ", onPage=" + onPage +
                ", optional=" + optional +
                ", reserved=" + reserved +
                ", branded=" + branded +
                ", hidden=" + hidden +
                ", unused=" + unused +
                ", rankTracking=" + rankTracking +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantKeywords)) return false;
        ProductVariantKeywords that = (ProductVariantKeywords) o;
        return title.equals(that.title) &&
                onPage.equals(that.onPage) &&
                optional.equals(that.optional) &&
                reserved.equals(that.reserved) &&
                branded.equals(that.branded) &&
                hidden.equals(that.hidden) &&
                unused.equals(that.unused) &&
                rankTracking.equals(that.rankTracking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, onPage, optional, reserved, branded, hidden, unused, rankTracking);
    }
}
