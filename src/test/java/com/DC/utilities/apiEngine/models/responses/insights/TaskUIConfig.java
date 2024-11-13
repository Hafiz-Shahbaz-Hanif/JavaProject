package com.DC.utilities.apiEngine.models.responses.insights;

import java.util.ArrayList;
import java.util.Objects;

public class TaskUIConfig extends TaskUIConfigBase{

    public TaskUIConfig() {
        super();
    }
    public TaskUIConfig(String _id, int _version, String label, ArrayList<Mappings> mappings, boolean internal) {
        super(label, mappings, internal);
        this._id = _id;
        this._version = _version;
    }

    public String _id;
    public int _version;

    @Override
    public String toString() {
        return "{" +
                "_id:" + _id +
                ", _version:" + _version +
                ", label:" + label +
                ", mappings:" + mappings +
                ", internal:" + internal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskUIConfig)) return false;
        TaskUIConfig that = (TaskUIConfig) o;
        return internal == that.internal &&
                _id.equals(that._id) &&
                _version == that._version &&
                label.equals(that.label) &&
                mappings.equals(that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, label, mappings, internal);
    }
}
