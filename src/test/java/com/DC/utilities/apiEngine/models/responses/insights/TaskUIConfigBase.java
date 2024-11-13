package com.DC.utilities.apiEngine.models.responses.insights;

import java.util.ArrayList;
import java.util.Objects;

public class TaskUIConfigBase {
    public String label;
    public ArrayList<Mappings> mappings;
    public boolean internal;

    public TaskUIConfigBase(String label, ArrayList<Mappings> mappings, boolean internal) {
        this.label = label;
        this.mappings = mappings;
        this.internal = internal;
    }

    public TaskUIConfigBase() {

    }

    @Override
    public String toString() {
        return "{" +
                "label:" + label +
                ", mappings:" + mappings +
                ", internal:" + internal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskUIConfigBase)) return false;
        TaskUIConfigBase that = (TaskUIConfigBase) o;
        return internal == that.internal &&
                label.equals(that.label) &&
                mappings.equals(that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, mappings, internal);
    }
}
