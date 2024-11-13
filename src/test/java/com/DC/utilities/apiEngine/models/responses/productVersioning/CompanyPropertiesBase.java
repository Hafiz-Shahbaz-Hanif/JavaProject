package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompanyPropertiesBase {

    public List<DigitalAssetCompanyProperty> digitalAssetPropertySchema;

    public List<Property> propertySchema;

    public List<Group> groups;

    public List<Group> groupsDigitalAssets;

    public List<String> templateIds;

    public List<PropertyBase> getBasePropertySchema() {
        return propertySchema.stream()
                .map(prop -> new CompanyPropertiesBase.PropertyBase(prop.id, prop.name, prop.type, prop.helpText, prop.allowMultipleValues, prop.group))
                .collect(Collectors.toList());
    }

    public List<PropertyBase> getBaseDigitalAssetPropertySchema() {
        return digitalAssetPropertySchema.stream()
                .map(prop -> new CompanyPropertiesBase.PropertyBase(prop.id, prop.name, prop.type, prop.helpText, prop.allowMultipleValues, prop.group))
                .collect(Collectors.toList());
    }

    public static class Property extends PropertyBase {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<PropertyDropdownValue> dropdownValues;

        public Property() {}

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", helpText='" + helpText + '\'' +
                    ", dropdownValues=" + dropdownValues +
                    ", allowMultipleValues=" + allowMultipleValues +
                    ", group='" + group + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Property)) return false;
            if (!super.equals(o)) return false;
            Property property = (Property) o;
            return Objects.equals(dropdownValues, property.dropdownValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), dropdownValues);
        }
    }

    public static class DigitalAssetCompanyProperty extends Property {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public ImageSpecMapping imageSpecMapping;

        public DigitalAssetCompanyProperty() {}

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", helpText='" + helpText + '\'' +
                    ", dropdownValues=" + dropdownValues +
                    ", allowMultipleValues=" + allowMultipleValues +
                    ", group='" + group + '\'' +
                    ", imageSpecMapping=" + imageSpecMapping + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DigitalAssetCompanyProperty)) return false;
            if (!super.equals(o)) return false;
            DigitalAssetCompanyProperty that = (DigitalAssetCompanyProperty) o;
            return Objects.equals(imageSpecMapping, that.imageSpecMapping);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), imageSpecMapping);
        }
    }

    public static class PropertyDropdownValue {

        public String id;

        public String name;

        public PropertyDropdownValue(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public PropertyDropdownValue() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PropertyDropdownValue that = (PropertyDropdownValue) o;
            return id.equals(that.id) && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Group {

        public String name;

        public String description;

        public Number sortIndex;

        public List<GroupProperty> properties;

        public Group(String name, String description, Number sortIndex, List<GroupProperty> properties) {
            this.name = name;
            this.description = description;
            this.sortIndex = sortIndex;
            this.properties = properties;
        }

        public Group(String name) {
            this.name = name;
        }

        public Group() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Group)) return false;
            Group group = (Group) o;
            return name.equals(group.name) &&
                    Objects.equals(description, group.description) &&
                    sortIndex.equals(group.sortIndex) &&
                    properties.equals(group.properties);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, sortIndex, properties);
        }

        @Override
        public String toString() {
            return "{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", sortIndex=" + sortIndex +
                    ", properties=" + properties +
                    '}';
        }

        public static class GroupProperty {

            public String id;

            public Number sortIndex;

            public GroupProperty(String id, Number sortIndex) {
                this.id = id;
                this.sortIndex = sortIndex;
            }

            public GroupProperty() {}

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof GroupProperty)) return false;
                GroupProperty that = (GroupProperty) o;
                return id.equals(that.id) && sortIndex.equals(that.sortIndex);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, sortIndex);
            }

            @Override
            public String toString() {
                return "{" +
                        "id='" + id + '\'' +
                        ", sortIndex=" + sortIndex +
                        '}';
            }
        }
    }

    public static class ImageSpecMapping {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String imageType;
        public String mappingProperty;
        public String instructionsProperty;

        public ImageSpecMapping(String imageType, String mappingProperty, String instructionsProperty) {
            this.imageType = imageType;
            this.mappingProperty = mappingProperty;
            this.instructionsProperty = instructionsProperty;
        }

        public ImageSpecMapping(String mappingProperty, String instructionsProperty) {
            this.mappingProperty = mappingProperty;
            this.instructionsProperty = instructionsProperty;
        }

        public ImageSpecMapping() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImageSpecMapping)) return false;
            ImageSpecMapping that = (ImageSpecMapping) o;
            return Objects.equals(imageType, that.imageType) && Objects.equals(mappingProperty, that.mappingProperty) && Objects.equals(instructionsProperty, that.instructionsProperty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(imageType, mappingProperty, instructionsProperty);
        }

        @Override
        public String toString() {
            return "{" +
                    "imageType='" + imageType + '\'' +
                    ", mappingProperty='" + mappingProperty + '\'' +
                    ", instructionsProperty='" + instructionsProperty + '\'' +
                    '}';
        }
    }

    public static class GroupCreate {

        public String name;

        public String description;

        public GroupCreate(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public GroupCreate(String name) {
            this.name = name;
        }

        public GroupCreate() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Group)) return false;
            Group group = (Group) o;
            return name.equals(group.name) &&
                    Objects.equals(description, group.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description);
        }

        @Override
        public String toString() {
            return "{" +
                    "name='" + name + '\'' +
                    ", description='" + description +
                    '}';
        }
    }

    public static class PropertyBase {
        public String id;

        public String name;

        public Enums.PropertyType type;

        public String helpText;

        public boolean allowMultipleValues;

        public String group;

        @JsonIgnore
        public String mappingConfig;

        public PropertyBase(String id, String name, Enums.PropertyType type, String helpText, boolean allowMultipleValues, String group) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.helpText = helpText;
            this.allowMultipleValues = allowMultipleValues;
            this.group = group;
        }

        public PropertyBase(String id, String name, Enums.PropertyType type, String helpText, boolean allowMultipleValues, String group, String mappingConfig) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.helpText = helpText;
            this.allowMultipleValues = allowMultipleValues;
            this.group = group;
            this.mappingConfig = mappingConfig;
        }

        public PropertyBase() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PropertyBase)) return false;
            PropertyBase that = (PropertyBase) o;
            return allowMultipleValues == that.allowMultipleValues &&
                    Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name) &&
                    type == that.type &&
                    Objects.equals(helpText, that.helpText) &&
                    Objects.equals(group, that.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, type, helpText, allowMultipleValues, group);
        }

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", helpText='" + helpText + '\'' +
                    ", allowMultipleValues=" + allowMultipleValues +
                    ", group='" + group + '\'' +
                    '}';
        }
    }
}
