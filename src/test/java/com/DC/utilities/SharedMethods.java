package com.DC.utilities;

import com.google.common.collect.Ordering;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SharedMethods {
    public static Logger logger = Logger.getLogger(SharedMethods.class);

    public static void deleteDirectory() {
        try {
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/downloads/"));
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/log/"));
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/test-output/"));
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/allure-results/"));
        } catch (IOException e1) {
            logger.error(e1.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static String encodeFileToBase64Binary(File file) {
        String encodedFile = null;
        FileInputStream fileInputStreamReader = null;
        try {
            fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedFile = new String(Base64.encodeBase64(bytes), "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                fileInputStreamReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return encodedFile;
    }

    public static JsonWriterSettings getJsonWriterSettings() {
        return JsonWriterSettings.builder().outputMode(JsonMode.SHELL)
                .binaryConverter((value, writer) -> writer.writeString(value.asUuid().toString()))
                .dateTimeConverter(new JsonDateTimeConverter())
                .build();
    }

    public static int generateRandomNumber() {
        int bound = 999999;
        return new Random().nextInt(bound);
    }

    public static void downloadFileFromUrl(String urlStr, String filePath) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public static List<String> createList(String text) {
        if (text.equalsIgnoreCase("x")) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(text.split(",")));
    }

    public static File importFileFromUrl(String url, String path) throws Exception {
        File file = new File(path);
        FileUtils.copyURLToFile(new URL(url), file);
        return file;
    }

    public static String convertObjectToEmptyStringIfNull(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    public static Object getRandomItemFromList(List<? extends Object> list) {
        Random rand = new Random();
        int randomNumber = rand.nextInt(list.size());
        return list.get(randomNumber);
    }

    public static BigDecimal convertStringIntoDouble(String value) {
        return BigDecimal.valueOf(Double.parseDouble(value.replace("$", "")));
    }

    public static BigDecimal convertStringIntoInteger(String value) {
        return BigDecimal.valueOf(Integer.parseInt(value.replace("$", "").replace(",", "")));
    }

    public static String removeTextAfterSpecificCharacterFromString(String fullString, Character character) {
        int indexOfCharacter = fullString.indexOf(character);
        return indexOfCharacter > 0 ? fullString.substring(0, indexOfCharacter) : fullString;
    }

    // if i = 1 then it will return A
    public static char convertNumberToAlphabetLetter(int numberToConvert) {
        return numberToConvert > 0 && numberToConvert < 27 ? ((char) (numberToConvert + 64)) : null;
    }

    public static boolean hasMatchingSubstring(String str, List<String> substrings) {
        return substrings.contains(str);
    }

    public static String isFileDownloaded(String extension, String fileName, int maxWaitTime, String downloadFolder) throws Exception {

        File file = new File(downloadFolder);
        String filePath = "";
        if (file.isDirectory()) {
            // use await() to make sure the directory has a file that ends with the extension and the name contains the fileName
            FilenameFilter fileFilter = (dir, name) -> (name.endsWith(extension) && name.toLowerCase().contains(fileName.toLowerCase()));
            try {
                await().atMost(maxWaitTime, TimeUnit.SECONDS).until(() -> {
                    File[] files = file.listFiles(fileFilter);
                    return files != null && files.length > 0;
                });
                return Objects.requireNonNull(file.listFiles(fileFilter))[0].getPath();
            } catch (ConditionTimeoutException e) {
                throw new Exception("File not found in " + maxWaitTime + " seconds");
            }
        }
        return filePath;
    }

    public static List<File> getFilesInDirectory(String directoryPath, String fileName) {
        File dir = new File(directoryPath);

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory.");
        }

        File[] matchingFiles = dir.listFiles(file -> file.isFile() && file.getName().equals(fileName));

        if (matchingFiles != null) {
            return Arrays.asList(matchingFiles);
        } else {
            return new ArrayList<>();
        }
    }

    public static void deletePath(Path path) {
        try {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.delete(path);
            }
        } catch (IOException e) {
            logger.info("Something went wrong while deleting the file/folder: " + path, e);
        }
    }

    public static List<Long> extractNumbersFromString(String value) {
        return Arrays.stream(value.split("\\D+")).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
    }

    public static int extractIntegerFromString(String value) {
        String extractedNumber = value.replaceAll("\\D+", "");
        return Integer.parseInt(extractedNumber);
    }

    @DataProvider
    public static Object[][] downloadOptions() {
        return new Object[][]{
                {"Download CSV", ".csv"},
                {"Download PDF document", ".pdf"},
                {"Download PNG image", ".png"},
                {"Download XLS", ".xls"},
        };
    }

    public static int getRandomNumber(int maxValue) {
        return new Random().nextInt(maxValue);
    }

    public static int getRandomNumberBetweenRange(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static String generateRandomString() {
        LocalDateTime term = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return term.format(formatter);
    }

    // Splits camel case or Pascal case and capitalize first letter of each word
    public static String humanizeString(String value) {
        value = value.replace('_', ' ');
        return WordUtils.capitalizeFully(value.replaceAll("([a-z])([A-Z])", "$1 $2"));
    }

    public static void unzipFile(String zipFilePath, String destinationPath) throws IOException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                String filePath = destinationPath + File.separator + entryName;

                if (!zipEntry.isDirectory()) {
                    new File(new File(filePath).getParent()).mkdirs();
                    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                } else {
                    new File(filePath).mkdirs();
                }

                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
        }
    }

    public static String generateRandomString(String text) {
        LocalDateTime term = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return text + term.format(formatter);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getCsvCellValue(String filePath, int row, int col) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(filePath));
        String[] nextLine = null;
        for (int i = 0; i < row; i++) {
            nextLine = reader.readNext();
            if (nextLine == null) {
                return null;
            }
        }
        if (nextLine.length <= col) {
            return null;
        }
        return nextLine[col];
    }

    public static int sumList(List<Integer> list) {
        int sum = 0;
        for (int value : list) {
            sum += value;
        }
        return sum;
    }

    public static Double convertToNumber(String currencyValue) {
        try {
            return Double.parseDouble(currencyValue.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isSortedDescending(List<Double> columnValues) {
        return Ordering.natural().reverse().isOrdered(columnValues);
    }

    public static String checkDownloadsWithDifferentNames(String[] namesOfDownloadedFiles, String extension, int maxWaitTime, String downloadFolder) {
        String downloadedFilePath = null;
        for (String name : namesOfDownloadedFiles) {
            try {
                downloadedFilePath = SharedMethods.isFileDownloaded(extension, name, maxWaitTime, downloadFolder);
                if (downloadedFilePath != null) {
                    break;
                }
            } catch (Exception e) {
                logger.info("Exception while checking for file: " + e.getMessage() + " Trying another name from the list");
            }
        }
        return downloadedFilePath;
    }

    public static byte[] convertToUUIDAndReturnBytesArray(String id) {
        UUID uuid = UUID.fromString(id);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}