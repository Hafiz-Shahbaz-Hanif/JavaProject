package com.DC.utilities.XLUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class XLUtils {
    public static Logger logger = Logger.getLogger("XLUtils.class");

    public static XSSFWorkbook getWorkbook(String path) throws IOException {
        File file = new File(path);
        InputStream excelFile = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
        return new XSSFWorkbook(excelFile);
    }

    public static int getRowCount(String xlfile, String xlsheet) throws IOException, InvalidFormatException {
        File file = new File(xlfile);
        InputStream in = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet ws = wb.getSheet(xlsheet);
        int rowcount = ws.getLastRowNum();
        wb.close();
        return rowcount;
    }

    public static int getCellCount(String xlfile, String xlsheet, int rownum) throws IOException, InvalidFormatException {
        File file = new File(xlfile);
        InputStream in = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet ws = wb.getSheet(xlsheet);
        XSSFRow row = ws.getRow(rownum);
        int cellcount = row.getLastCellNum();
        wb.close();
        return cellcount;
    }

    public static Double getHeaderAndTotalCountOfData(String filePath, String header) throws IOException, InvalidFormatException {
        //File file = new File(xlfile);
        logger.info(filePath);
        FileInputStream fis = new FileInputStream(filePath);
        //Workbook workbook = new XSSFWorkbook(fis);
        //String sheetName = workbook.getSheetName(1);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet ws = wb.getSheetAt(1);
        logger.info(ws.getSheetName());
        XSSFRow header_row = ws.getRow(0);
        int cellcount = header_row.getLastCellNum();
        String sheet_header;
        int rowNum = 1;
        Double totalCellDataValue = 0.0;

        for (int i = 0; i < cellcount; i++) {
            XSSFCell header_cell = header_row.getCell(i);
            sheet_header = header_cell.getStringCellValue();
            if (sheet_header.equals(header)) {
                while (header_row.iterator().hasNext()) {
                    Double cellData = Double.parseDouble(getCellData(filePath, ws.getSheetName(), rowNum, i));
                    totalCellDataValue = totalCellDataValue + cellData;
                    rowNum = rowNum + 1;
                }
                break;
            }
        }

        wb.close();
        return totalCellDataValue;
    }

    public static String getCellData(String xlfile, String xlsheet, int rownum, int colnum) throws IOException, InvalidFormatException {
        File file = new File(xlfile);
        InputStream in = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet ws = wb.getSheet(xlsheet);
        XSSFRow row = ws.getRow(rownum);
        XSSFCell cell = row.getCell(colnum);
        String data;
        try {
            DataFormatter formatter = new DataFormatter();
            String cellData = formatter.formatCellValue(cell);
            return cellData;
        } catch (Exception e) {
            data = "";
        } finally {
            wb.close();
        }
        return data;
    }

    public static List<List<String>> getSheetDataByRow(String filePath, boolean markEmptyCellValueAsNull) throws IOException {
        File file = new File(filePath);
        InputStream excelFile = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
        XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int columnsCount = sheet.getRow(0).getLastCellNum();
        List<List<String>> sheetData = new ArrayList<>();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            List<String> rowData = new ArrayList<>();

            for (int cellNum = 0; cellNum < columnsCount; cellNum++) {
                String cellValue = getCellValueAsString(row, cellNum);
                if (cellValue.isEmpty() && markEmptyCellValueAsNull) {
                    rowData.add(null);
                } else {
                    rowData.add(cellValue);
                }
            }

            sheetData.add(rowData);
        }
        workbook.close();
        return sheetData;
    }

    public static List<List<String>> getSheetColumnData(String filePath, boolean markEmptyCellValueAsNull) throws IOException {
        try (InputStream excelFile = Files.newInputStream(Paths.get(filePath), StandardOpenOption.READ)) {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();
            int columnsCount = sheet.getRow(0) != null ? sheet.getRow(0).getLastCellNum() : 0;

            List<List<String>> sheetColumnData = new ArrayList<>();
            for (int cellNum = 0; cellNum < columnsCount; cellNum++) {
                sheetColumnData.add(new ArrayList<>());
            }

            for (int cellNum = 0; cellNum < columnsCount; cellNum++) {
                for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    String cellValue = getCellValueAsString(row, cellNum);

                    if (cellValue.isEmpty() && markEmptyCellValueAsNull) {
                        sheetColumnData.get(cellNum).add(null);
                    } else {
                        sheetColumnData.get(cellNum).add(cellValue);
                    }
                }
            }

            workbook.close();
            return sheetColumnData;
        }
    }

    private static String getCellValueAsString(Row row, int cellNum) {
        Cell cell = row != null ? row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK) : null;
        if (cell == null || StringUtils.isEmpty(((XSSFCell) cell).getRawValue())) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        String cellValue = "";

        switch (cell.getCellTypeEnum()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                cellValue = formatter.formatCellValue(cell);
                break;
            case BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue = formatter.formatCellValue(cell, evaluator);
                break;
            case BLANK:
            case _NONE:
            case ERROR:
                cellValue = "";
                break;
        }

        return cellValue;
    }

    public static void setCellData(String xlfile, String xlsheet, int rownum, int colnum, String data) throws IOException, InvalidFormatException {
        File file = new File(xlfile);
        InputStream in = Files.newInputStream(file.toPath());
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet ws = wb.getSheet(xlsheet);
        XSSFRow row = ws.getRow(rownum);
        XSSFCell cell = row.createCell(colnum);
        cell.setCellValue(data);
        FileOutputStream fo = new FileOutputStream(xlfile);
        wb.write(fo);
        wb.close();
        fo.close();
    }

    public int getColIndexByCellValue(String filePath, String sheet, int rowNum, String cellValue) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sh = wb.getSheet(sheet);
        XSSFRow row = sh.getRow(rowNum);
        int cellCount = row.getLastCellNum();
        String cellData = null;
        int colIndex = -1;

        for (int i = 0; i < cellCount; i++) {

            XSSFCell cell = row.getCell(i);
            DataFormatter formatter = new DataFormatter();
            cellData = formatter.formatCellValue(cell);

            if (cellData.equals(cellValue)) {
                colIndex = i;
                wb.close();
                return colIndex;
            }

        }

        wb.close();
        return colIndex;
    }

    public static void replaceCellValue(String filePath, int rowNum, int cellNum, String newValue) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sh = wb.getSheet(wb.getSheetAt(0).getSheetName());
        XSSFRow row = sh.getRow(rowNum);
        XSSFCell cell = row.getCell(cellNum);
        cell.setCellValue(newValue);
        fis.close();

        FileOutputStream outputStream = new FileOutputStream(filePath);
        wb.write(outputStream);

        wb.close();
        outputStream.close();
    }

    public static List<String> getFileHeaders(String path) throws IOException {
        XSSFWorkbook workbook = getWorkbook(path);
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<String> headers = getSheetHeaders(sheet);
        workbook.close();
        return headers;
    }

    public static List<String> getSheetHeaders(XSSFSheet sheet) {
        XSSFRow row = sheet.getRow(0);
        List<String> headers = new ArrayList<>();

        for (int i = 0; i < row.getLastCellNum(); i++) {
            XSSFCell cell = row.getCell(i);
            headers.add(cell.getStringCellValue());
        }

        return headers;
    }

    public static List<String> getCellDropdownValues(XSSFSheet sheet, String expectedRange) {
        List<XSSFDataValidation> dataValidations = sheet.getDataValidations();

        for (XSSFDataValidation validation : dataValidations) {
            boolean isExpectedValidation = validation.getRegions().getCellRangeAddress(0).formatAsString().contains(expectedRange);
            if (isExpectedValidation) {
                return asList(validation.getValidationConstraint().getExplicitListValues());
            }
        }
        return null;
    }

    public static String createExcelFileFromHeaderColumns(
            Map<String, List<Object>> data, String filePath, List<String> dateColumns) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            CreationHelper creationHelper = workbook.getCreationHelper();

            short dateFormat = creationHelper.createDataFormat().getFormat("m/d/yy");
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(dateFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            int headerCellNum = 0;
            for (String header : data.keySet()) {
                Cell cell = headerRow.createCell(headerCellNum++);
                cell.setCellValue(header);
            }

            // Fill in the data
            int maxNumRows = data.values().stream().mapToInt(List::size).max().orElse(0);

            for (int rowNum = 1; rowNum <= maxNumRows; rowNum++) {
                Row row = sheet.createRow(rowNum);
                int cellNum = 0;
                for (Map.Entry<String, List<Object>> entry : data.entrySet()) {
                    String header = entry.getKey();
                    List<Object> columnData = entry.getValue();
                    Cell cell = row.createCell(cellNum++);
                    if (rowNum <= columnData.size()) {
                        Object value = columnData.get(rowNum - 1);
                        if (dateColumns.contains(header) && value instanceof String) {
                            String dateStr = (String) value;
                            try {
                                LocalDate date = LocalDate.parse(dateStr, outputFormatter);
                                cell.setCellValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                                cell.setCellStyle(dateCellStyle);
                            } catch (Exception e) {
                                cell.setCellValue(dateStr);
                            }
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        }
                    }
                }
            }

            // Autosize columns
            for (int i = 0; i < headerCellNum; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }

        return filePath;
    }
}
