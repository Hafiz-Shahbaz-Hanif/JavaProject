package com.DC.objects.insights;

import java.util.Objects;

public class DestinationDefinitionSettings {
    public String destinationName;
    public String destinationDescription;
    public String destinationType;
    public String retailer;
    public boolean isANewDestinationTemplate;
    public String destinationTemplateName;
    public String destinationTemplatePath;
    public String client;

    public DestinationDefinitionSettings() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDefinitionSettings)) return false;
        DestinationDefinitionSettings that = (DestinationDefinitionSettings) o;
        return destinationName == that.destinationName &&
                destinationDescription == that.destinationDescription &&
                destinationType == that.destinationType &&
                retailer == that.retailer &&
                isANewDestinationTemplate == that.isANewDestinationTemplate &&
                client == that.client &&
                destinationTemplateName == that.destinationTemplateName &&
                destinationTemplatePath == that.destinationTemplatePath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationName, destinationDescription, destinationType, retailer, isANewDestinationTemplate, destinationTemplateName, destinationTemplatePath, client);
    }

    @Override
    public String toString() {
        return "DestinationDefinitionSettings{" +
                ", destinationName='" + destinationName + '\'' +
                ", destinationDescription='" + destinationDescription + '\'' +
                ", destinationType='" + destinationType + '\'' +
                ", retailer='" + retailer + '\'' +
                ", isANewDestinationTemplate='" + isANewDestinationTemplate + '\'' +
                ", destinationTemplateName='" + destinationTemplateName + '\'' +
                ", destinationTemplatePath='" + destinationTemplatePath + '\'' +
                ", client='" + client + '\'' +
                '}';
    }

}
