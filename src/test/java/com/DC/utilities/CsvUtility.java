package com.DC.utilities;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

public class CsvUtility {

    public static String getCellContent(String filePath, int rowNumber, String columnName) {
        Map<String, Integer> columnIndices = new HashMap<>();
        String[] selectedRow = null;
        InputStreamReader reader = null;

        try {
            String defaultEncoding = "UTF-8";
            InputStream inputStream = new FileInputStream(filePath);
            BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
            ByteOrderMark bom = bOMInputStream.getBOM();
            String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
            reader = new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CSVReader csvReader = new CSVReader(reader)) {
            if (filePath.contains("roundup")) {
                csvReader.readNext();
            }
            String[] header = csvReader.readNext();

            for (int i = 0; i < header.length; i++) {
                String headerName = header[i];
                columnIndices.put(headerName, i);
            }

            for (int currentRow = 1; currentRow <= rowNumber; currentRow++) {
                selectedRow = csvReader.readNext();
                if (selectedRow == null) {
                    throw new IllegalArgumentException("Row not found: " + rowNumber);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        Integer columnIndex = columnIndices.get(columnName);

        if (columnIndex == null) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }

        return selectedRow[columnIndex];
    }

    public static List<String> getAllCellValuesInColumn(String filePath, String columnName) {
        List<String> columnValues = new ArrayList<>();
        Set<String> set = new LinkedHashSet<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            if (!filePath.contains("air_traffic_control")) {
                reader.readNext();
            }

            String[] header = reader.readNext();
            int columnIndex = -1;

            for (int i = 0; i < header.length; i++) {
                if (header[i].equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column not found: " + columnName);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > columnIndex) {
                    columnValues.add(row[columnIndex]);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        set.addAll(columnValues);
        return new ArrayList<>(set);
    }

    public static int countNonEmptyRowsInColumn(String filePath, String columnName) {
        int nonEmptyRowCount = 0;

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext();

            String[] header = reader.readNext();

            int columnIndex = IntStream.range(0, header.length).filter(i -> header[i].equals(columnName)).findFirst().orElse(-1);

            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column not found: " + columnName);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > columnIndex && !row[columnIndex].isBlank()) {
                    nonEmptyRowCount++;
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return nonEmptyRowCount;
    }

    public static int getRowCount(String filePath) throws IOException, CsvValidationException {
        int rowCount = 0;
        CSVReader reader = new CSVReader(new FileReader(filePath));
        while (reader.readNext() != null) {
            rowCount++;
        }
        return rowCount;
    }

    public static List<String> getAllColumnNames(String filePath) {
        List<String> columnNames = new ArrayList<>();

        String defaultEncoding = "UTF-8";

        try (InputStream inputStream = new FileInputStream(filePath)) {
            BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
            ByteOrderMark bom = bOMInputStream.getBOM();
            String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
            InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);

            CSVReader csvReader = new CSVReader(reader);
            String[] header = csvReader.readNext();
            if (header != null) {
                columnNames.addAll(Arrays.asList(header));
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return columnNames;

    }

    public static double getColumnSum(String filePath, String columnName) {
        double sum = 0.0;
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] header = reader.readNext();
            int columnIndex = -1;
            for (int i = 0; i < header.length; i++) {
                if (header[i].equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }
            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column not found: " + columnName);
            }
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > columnIndex) {
                    try {
                        double columnValue = Double.parseDouble(row[columnIndex]);
                        sum += columnValue;
                        sum = Math.round(sum * 100.0) / 100.0;
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Value is not a number: " + row[columnIndex]);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return sum;
    }

    public static List<List<String>> getContentOfAllRows(String filePath) {
        List<List<String>> allRowsContent = new ArrayList<>();
        String defaultEncoding = "UTF-8";

        try (InputStream inputStream = new FileInputStream(filePath)) {
            BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
            ByteOrderMark bom = bOMInputStream.getBOM();
            String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
            InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);

            CSVReader csvReader = new CSVReader(reader);
            String[] nextRow;
            while ((nextRow = csvReader.readNext()) != null) {
                allRowsContent.add(Arrays.asList(nextRow));
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return allRowsContent;
    }

    public static void updateCsvCell(String csvFilePath, String columnName, int rowNumber, String newValue) throws IOException {

        String defaultEncoding = "UTF-8";
        InputStream inputStream = new FileInputStream(csvFilePath);
        BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
        ByteOrderMark bom = bOMInputStream.getBOM();
        String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
        InputStreamReader reader = new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName);

        try (CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> csvData = csvReader.readAll();

            // Find the column index
            int columnIndex = -1;
            String[] header = csvData.get(0);
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            // Check if the column exists
            if (columnIndex == -1) {
                System.out.println("Column not found: " + columnName);
                return;
            }

            // Check if the row number is valid
            if (rowNumber < 1 || rowNumber >= csvData.size()) {
                System.out.println("Invalid row number: " + rowNumber);
                return;
            }

            // Update the specified cell
            String[] row = csvData.get(rowNumber);
            row[columnIndex] = newValue;

            // Write the updated data back to the CSV file
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
                csvWriter.writeAll(csvData);
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public static File createCsvFile(String downloadFolder, String fileName,  String [] ... rows) throws IOException {
        File file = new File(downloadFolder + "\\"+ fileName +".csv");
        CSVWriter write = new CSVWriter(new FileWriter(file));
        List<String[]> data = new ArrayList<>();

        for (String[] row : rows) {
            data.add(row);
        }

        write.writeAll(data);
        write.flush();
        return file;
    }

    public static Map<String, List<Object>> readBulkUploadCsvFileByColumnsAndSkipEqualDates(
            String filePath, String headersLine, List<String> dateColumns, int numberOfRowsToRead) throws IOException {
        Map<String, List<Object>> dataMap = new LinkedHashMap<>();
        String[] headers = headersLine.split(",");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        int startDateIndex = Arrays.asList(headers).indexOf("Start Date");
        int endDateIndex = Arrays.asList(headers).indexOf("End Date");

        // Initialize the map with empty lists for each header
        for (String header : headers) {
            dataMap.put(header, new ArrayList<>());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            int currentRow = 0;
            while ((line = br.readLine()) != null && currentRow < numberOfRowsToRead) {
                // Skip the header line in the CSV, if it exists
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");

                if (startDateIndex != -1 &&
                        endDateIndex != -1 &&
                        startDateIndex < values.length &&
                        endDateIndex < values.length &&
                        values[startDateIndex].equals(values[endDateIndex])) {
                    continue;
                }

                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i];
                    String value = values.length > i ? values[i] : "";
                    // Check if this column is a date column
                    if (dateColumns.contains(header) && !value.isEmpty()) {
                        try {
                            LocalDate date = LocalDate.parse(value, inputFormatter);
                            value = date.format(outputFormatter);
                        } catch (Exception e) {
                            // Handle the case where the date is not in the expected format or is empty
                        }
                    } else {
                        // Attempt to parse numeric values, otherwise treat as String
                        try {
                            double dValue = Double.parseDouble(value);
                            // Check if it's actually an integer value
                            if (dValue == (int) dValue) {
                                value = Integer.toString((int) dValue);
                            } else {
                                value = Double.toString(dValue);
                            }
                        } catch (NumberFormatException e) {
                            // Value remains a String
                        }
                    }
                    dataMap.get(header).add(value);
                }
                currentRow++;
            }
        }
        return dataMap;
    }
}