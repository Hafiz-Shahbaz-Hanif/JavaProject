package com.DC.objects.insightslegacy;

import java.util.Objects;

public class FormatBatchProductConfig {

    public ProductBasicSettings product;
    public String[] levelsToInclude;
    public int taskNumber;

    public FormatBatchProductConfig(ProductBasicSettings product, String[] levelsToInclude, int taskNumber) {
        this.product = product;
        this.levelsToInclude = levelsToInclude;
        this.taskNumber = taskNumber;
    }

    public FormatBatchProductConfig() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormatBatchProductConfig)) return false;
        FormatBatchProductConfig that = (FormatBatchProductConfig) o;
        return product == that.product &&
                levelsToInclude == that.levelsToInclude &&
                taskNumber == that.taskNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, levelsToInclude, taskNumber);
    }

    @Override
    public String toString() {
        return "FormatBatchProductConfig{" +
                ", product='" + product + '\'' +
                ", levelsToInclude='" + levelsToInclude + '\'' +
                ", taskNumber='" + taskNumber + '\'' +
                '}';
    }
}
