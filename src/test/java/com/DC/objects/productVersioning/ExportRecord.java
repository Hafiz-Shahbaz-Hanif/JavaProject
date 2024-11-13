package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;

public class ExportRecord extends ImportExportBase {

    public Enums.ExportType trackingType;

    public Enums.ExportSubType trackingSubType;

    public ExportWorkbook exportWorkbook;

    public ExportDigitalAssets exportDigitalAssets;

    public static class ExportWorkbook {

        public String createdBy;

        public String fileId;

        public String link;
    }

    public static class ExportDigitalAssets {

        public String link;
    }
}
