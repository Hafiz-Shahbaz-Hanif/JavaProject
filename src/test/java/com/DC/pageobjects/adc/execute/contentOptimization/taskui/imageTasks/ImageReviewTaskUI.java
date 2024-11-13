package com.DC.pageobjects.adc.execute.contentOptimization.taskui.imageTasks;

import com.DC.pageobjects.adc.execute.contentOptimization.taskui.TaskUIBase;
import org.openqa.selenium.WebDriver;

/** Includes image client review and image internal review since both tasks are very similar */
public class ImageReviewTaskUI extends TaskUIBase {
    public ImageReviewTaskUI(WebDriver driver) throws Exception {
        super(driver);
    }
}
