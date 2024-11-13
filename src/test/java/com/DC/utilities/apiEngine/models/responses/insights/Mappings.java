package com.DC.utilities.apiEngine.models.responses.insights;

import java.util.ArrayList;
import java.util.Objects;

public class Mappings {

    public Mappings() {}
    public boolean writeable;
    public String property;
    public ArrayList<String> assignments;

    public Mappings(boolean writeable, String property, ArrayList<String> assignments) {
        this.writeable = writeable;
        this.property = property;
        this.assignments = assignments;
    }

    @Override
    public String toString() {
        return "{" +
                "writeable:" + writeable +
                ", property:" + property +
                ", assignments:" + assignments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mappings)) return false;
        Mappings that = (Mappings) o;
        return writeable == that.writeable &&
                property.equals(that.property) &&
                assignments.equals(that.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(writeable, property, assignments);
    }
}
