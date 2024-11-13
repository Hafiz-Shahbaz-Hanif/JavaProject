package com.DC.utilities.sharedElements;

import com.DC.objects.insightslegacy.FormatBatchProductConfig;
import com.DC.objects.insightslegacy.LaunchFileSettings;
import com.DC.objects.insightslegacy.ProductBasicSettings;
import org.apache.commons.lang.RandomStringUtils;

import java.util.HashMap;

public class TaskLaunchFileSettings {

    static ProductBasicSettings getProductBasicSettings(String name, String uniqueID, String productLevel) {
        ProductBasicSettings productBasicSettings = new ProductBasicSettings();
        productBasicSettings.name = name;
        productBasicSettings.uniqueID = uniqueID;
        productBasicSettings.productLevel = productLevel;
        return productBasicSettings;
    }

    static HashMap<String, String> getFieldIdsAndLabels(String taskTitle) {
        HashMap<String, String> fieldsIdsAndLabels = new HashMap<>();
        fieldsIdsAndLabels.put("Topic", "Topic");
        fieldsIdsAndLabels.put("Title", "Task Title");
        fieldsIdsAndLabels.put("Subject", "Subject");
        fieldsIdsAndLabels.put("TaskTitle", taskTitle);
        return fieldsIdsAndLabels;
    }

    static FormatBatchProductConfig getFormatBatchProductConfig(String name, String uniqueID,String productLevel) {
        FormatBatchProductConfig formatBatchProductConfig = new FormatBatchProductConfig();
        formatBatchProductConfig.product = getProductBasicSettings(name, uniqueID, productLevel);
        formatBatchProductConfig.levelsToInclude = new String[]{"brand", "parent", "child"};
        formatBatchProductConfig.taskNumber = 1;
        return formatBatchProductConfig;
    }

    public static LaunchFileSettings getContentCreateSmokeLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "Project-With-Revisions-Content-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 1;
        launchFileSettings.chainName = "Project with Revisions - 5/19/2020, 8:55:24 AM";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Test Family", "QAP-201", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("Project-With-Revisions-Content-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getImageSmokeLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "Project-With-Revisions-Image-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 1;
        launchFileSettings.chainName = "Project with Revisions - 5/19/2020, 9:07:03 AM";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Coca-Cola", "QAP-005", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("Project-With-Revisions-Image-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getContentCreateLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "Selenium-Regression-Test-Content-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "Selenium Regression Test - Separate Workflows - 7/20/2020, 2:10:12 PM";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Test Family", "QAP-201", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("Selenium-Regression-Test-Content-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getImageCreateLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "Selenium-Regression-Test-Image-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "Selenium Regression Test - Separate Workflows - 7/2/2020, 12:12:47 PM";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Test Family", "QAP-201", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("Selenium-Regression-Test-Image-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getAttributeTaggingLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "AttributeTagging-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "Attributes And Keywords Project - First Chain";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Coca-Cola", "QAP-005", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("AttributeTagging-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getKeywordResearchLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "KeywordResearch-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "Attributes And Keywords Project - Keyword Chain";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Coca-Cola", "QAP-005", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("KeywordResearch-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getCollabReviewLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "CollabReview-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "Collab Review Project Chain";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Test Family", "QAP-201", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("CollabReview-");
        return launchFileSettings;
    }

    public static LaunchFileSettings getRPCDiscoveryLaunchFileSettings() {
        LaunchFileSettings launchFileSettings = new LaunchFileSettings();
        launchFileSettings.batchName = "RPCDiscovery-Batch-" + RandomStringUtils.randomAlphabetic(4);
        launchFileSettings.numberOfTasks = 5;
        launchFileSettings.chainName = "RPC Discovery Project Chain";
        launchFileSettings.formatBatchConfig = getFormatBatchProductConfig("Coca-Cola", "QAP-005", "family");
        launchFileSettings.fieldIdsAndLabels = getFieldIdsAndLabels("RPCDiscovery-");
        return launchFileSettings;
    }
}
