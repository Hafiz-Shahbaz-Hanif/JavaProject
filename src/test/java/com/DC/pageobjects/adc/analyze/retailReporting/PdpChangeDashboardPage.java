package com.DC.pageobjects.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PdpChangeDashboardPage extends NetNewNavigationMenu {

    public static final By CHANGE_DASHBOARD_BREADCRUMB = By.xpath("//a[text()='PDP Change Dashboard']");

    public PdpChangeDashboardPage(WebDriver driver) {
        super(driver);
    }
}