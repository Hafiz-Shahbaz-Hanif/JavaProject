package com.DC.objects.insights;
import java.util.Objects;

public class RetailerScrapData {
    public int NumberOfScrapes;
    public String date;
    public String domain;

    public RetailerScrapData(int NumberOfScrapes, String date, String domain) {
        this.NumberOfScrapes = NumberOfScrapes;
        this.date = date;
        this.domain = domain;
    }

    public RetailerScrapData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetailerScrapData)) return false;
        RetailerScrapData that = (RetailerScrapData) o;
        return NumberOfScrapes == that.NumberOfScrapes &&
                date == that.date &&
                domain == that.domain;
    }

    @Override
    public int hashCode() {
        return Objects.hash(NumberOfScrapes, date, domain);
    }

    @Override
    public String toString() {
        return "RetailerScrapData{" +
                ", NumberOfScrapes='" + NumberOfScrapes + '\'' +
                ", date='" + date + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }

}
