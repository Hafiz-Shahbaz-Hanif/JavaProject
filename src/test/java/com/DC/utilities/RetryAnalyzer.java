package com.DC.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    int counter = 0;
    int retryLimit = 1;

    @Override
    public boolean retry(ITestResult result) {
        try {
            boolean hasException = result.getThrowable().toString().contains("NoSuchElementException")
                    || result.getThrowable().toString().contains("StaleElementReferenceException")
                    || result.getThrowable().toString().contains("NullPointerException");
            System.out.println("Has Exception " + hasException);
            if (hasException)
                if (counter < retryLimit) {
                    counter++;
                    return true;
                }
                return false;
        } catch (Exception e) {
            return false;
        }
    }
}
