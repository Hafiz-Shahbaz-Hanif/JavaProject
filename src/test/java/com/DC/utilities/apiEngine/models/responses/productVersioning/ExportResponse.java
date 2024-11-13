package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public class ExportResponse {

    public String exportId;

    public String message;

    public boolean success;

    public Data data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExportResponse)) return false;
        ExportResponse that = (ExportResponse) o;
        return success == that.success &&
                exportId.equals(that.exportId) &&
                message.equals(that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exportId, message, success, data);
    }

    public static class Data {

        public Object error;

        public String errorMessage;
    }

}
