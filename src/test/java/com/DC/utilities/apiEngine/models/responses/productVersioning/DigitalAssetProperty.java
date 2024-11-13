package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class DigitalAssetProperty {

    public String id;

    public List<Assets> assets;

    public DigitalAssetProperty(String id, List<Assets> assets) {
        this.id = id;
        this.assets = assets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DigitalAssetProperty)) return false;
        DigitalAssetProperty that = (DigitalAssetProperty) o;
        return Objects.equals(id, that.id) && Objects.equals(assets, that.assets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, assets);
    }

    public DigitalAssetProperty() {}

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", assets=" + assets +
                '}';
    }

    public static class Assets {
        public String url;
        public String mediaAssetId;

        public Assets(String url, String mediaAssetId) {
            this.url = url;
            this.mediaAssetId = mediaAssetId;
        }

        public Assets() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Assets)) return false;
            Assets assets = (Assets) o;
            return Objects.equals(url, assets.url) && Objects.equals(mediaAssetId, assets.mediaAssetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, mediaAssetId);
        }

        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", mediaAssetId='" + mediaAssetId + '\'' +
                    '}';
        }
    }
}
