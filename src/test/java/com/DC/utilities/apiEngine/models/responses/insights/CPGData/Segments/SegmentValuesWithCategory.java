package com.DC.utilities.apiEngine.models.responses.insights.CPGData.Segments;

import java.util.List;

public class SegmentValuesWithCategory {

    public Category category;
    public long totalCount;
    public List<Segment> segments;

    public static class Category {

        public long categoryId;

        public String name;
        public long parentCategoryId;
        public boolean isInternal;
        public String foreignSystemId;

        public String path;
    }

    public static class Segment {
        public Number segmentId;
        public String segment;
        public String segmentIdentifier;
        public long keywordCount;
        public long totalVolume;
        public List<SegmentValue> segmentValues;

        public static class SegmentValue {
            public Number segmentValueId;
            public String segmentValue;
            public long keywordCount;

            public long volume;
        }
    }
}
