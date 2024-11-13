package com.DC.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class SeleniumDriver {
    private static final Logger LOGGER = Logger.getLogger(SeleniumDriver.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    private static final String DATE = LocalDateTime.now().format(FORMATTER) + RandomStringUtils.randomNumeric(7);
    private static final String OS = System.getProperty("os.name");
    public static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    public static String downloadFolder;

    public WebDriver initializeChromeDriver(PageLoadStrategy pageLoadStrategy, boolean headless, boolean incognito) {
        LoggingPreferences preferences = new LoggingPreferences();
        preferences.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions options = new ChromeOptions();
        options.setCapability("goog:loggingPrefs", preferences);
        
        options.setPageLoadStrategy(pageLoadStrategy);

        if (incognito) {
            options.addArguments("--incognito");
        }

        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("disable-popup-blocking");
        options.addArguments("--remote-debugging-port=0");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*"); // REQUIRED IN JAVA 8'
        options.addArguments("disable-features=DownloadBubble,DownloadBubbleV2");

        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.prompt_for_download", "false");
        chromePrefs.put("browser.enabled_labs_experiments", new String[]{"download-bubble@2", "download-bubble-v2@2"});
        options.setExperimentalOption("prefs", chromePrefs);
        options.setExperimentalOption("localState", chromePrefs);

        WebDriver webDriver;

        if (OS.contains("Windows")) {
            downloadFolder = System.getProperty("user.dir") + "\\downloads\\" + DATE;
            setupFileForDownloads(downloadFolder, true);
            chromePrefs.put("download.default_directory", downloadFolder);

            System.setProperty("webdriver.chrome.whitelistedIps", "");
            if (!headless) {
                options.addArguments("start-maximized");
                webDriver = WebDriverManager.chromedriver()
                        .capabilities(options)
                        .create();
                webDriver.manage().window().maximize();
                LOGGER.info("Starting Chrome in non-headless state on " + OS);
            } else {
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--whitelisted-ips=\"\"");
                options.addArguments("--headless=new");
                webDriver = WebDriverManager.chromedriver().capabilities(options).create();
                LOGGER.info("Starting Chrome in headless state on " + OS);
            }
        } else {
            downloadFolder = System.getProperty("user.dir") + "/downloads/" + DATE;
            setupFileForDownloads(downloadFolder, false);
            chromePrefs.put("download.default_directory", downloadFolder);

            System.setProperty("webdriver.chrome.whitelistedIps", "");
            //System.setProperty("webdriver.chrome.driver", READ_CONFIG.getLinuxChromePath());
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--headless=new");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--whitelisted-ips=\"\"");
            webDriver = WebDriverManager.chromedriver().capabilities(options).create();
            LOGGER.info("Starting Chrome in headless state on " + OS);
        }
        return webDriver;
    }

    public WebDriver initializeChromeDriver(boolean headless, boolean incognito) {
        return initializeChromeDriver(PageLoadStrategy.NORMAL, headless, incognito);
    }

    private WebDriver setDriverDefaults(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        return driver;
    }

    private static void setupFileForDownloads(String downloadPath, boolean isWindowsOS) {
        if (isWindowsOS) {
            File file = new File(downloadPath);
            if (!file.exists()) {// check existing
                file.mkdirs();// create directory
                file.setReadable(true, false); // set readable
                file.setWritable(true, false); // set writable
                file.setExecutable(true, false); // set executable
            }
        } else {
            try {
                Path path = Paths.get(downloadPath);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(path, permissions);
            } catch (IOException e) {
                LOGGER.info("Can't set permission rwxrwxrwx to " + downloadPath);
            }
        }
    }

    public static void printFetchXHRLogs(WebDriver driver) {
        LogEntries logEntries = driver.manage().logs().get(LogType.PERFORMANCE);
        LOGGER.info("/*****   FETCH/XHR REQUESTS   ***/");
        for (org.openqa.selenium.logging.LogEntry entry : logEntries) {
            if (entry.getMessage().contains("\"type\":\"Fetch\"") || entry.getMessage().contains("\"type\":\"XHR\"")) {
                JSONObject jsonObject = new JSONObject(entry.getMessage());
                jsonObject = (JSONObject) jsonObject.get("message");
                LOGGER.info(jsonObject);
            }
        }
    }
}
