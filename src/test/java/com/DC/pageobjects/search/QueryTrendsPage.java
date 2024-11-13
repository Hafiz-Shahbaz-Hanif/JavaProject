//TODO - Class not used as of 7/17/23 - Will revisit once this page makes it to the merge app
// Class uses page factory - when revisit, will need to update to use PageHandler

//package com.DC.pageobjects.search;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.DC.pageobjects.PageHandler;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import com.DC.utilities.ReadConfig;

/*public class QueryTrendsPage extends PageHandler {

	private Logger logger;
	private CommonMethods common;
	private LoginPage lp;
	private ReadConfig readConfig;

	public QueryTrendsPage (WebDriver rdriver) {
		super(rdriver);
		common = new CommonMethods(rdriver);
		lp = new LoginPage(rdriver);
		readConfig = ReadConfig.getInstance();
		logger = Logger.getLogger("fila");
	}

	public static String searchTerms = "//div[@class='ag-pinned-left-cols-container' and @role='rowgroup']/div/div[@aria-colindex='2']";
	public static String defaultTextInGrid = "//div[@ref='eOverlayWrapper']/div/div";
	public static String grid = "//div[contains(@class, 'ag-root-wrapper ag-ltr ag-layout-normal')  and @role='presentation']";
	public static String gridLeftRows = "//div[@class='ag-pinned-left-cols-container' and @role='rowgroup']/div";
	public static String gridRightRows = "//div[@class='ag-center-cols-container' and @role='rowgroup']/div";
	public static String queryTrendsGridTitle = "//h6[@id='query-trends' and text()='Query Trends']";
	public static String paginationNextArrow = "//*[name()='svg' and @data-testid='NavigateNextIcon']";
	public static String paginationPreviousArrow = "//*[name()='svg' and @data-testid='NavigateBeforeIcon']";
	public static String lastPageIcon = "//*[name()='svg' and @data-testid='LastPageIcon']";
	public static String firstPageIcon = "//*[name()='svg' and @data-testid='FirstPageIcon']";
	public static String exportButton = "//*[name()='svg' and @data-testid='FileDownloadIcon']/..//span";
	public static String progressBar = "//div[@id='query-trends']//span[@role='progressbar']";
	public static String horizontalLoadingBar = "//div[@ref='eViewport']"; 
	public static String avgSalesPriceColumnHeader = "//span[text()='Avg. Sales Price']/../..";
	public static String pageSizeTxt = "//div[@aria-haspopup='listbox']";
	public static String sfrLwFilterTextbox = "//input[@name='srf-lw-filter']";
	public static String searchTermTextbox = "//input[@id='searchTerm']";
	public static String termInTitleTextbox = "//input[@id='termInTitleSearch']";
	public static String countryDepartmentDd ="//button[@title='Open']/../../input";
	private String countryDepartmentDdOpenArrow = "//button[@title='Open']";
	private String countryDepartmentDdCloseArrow = "//button[@title='Close']";
	private String filterSaveButton = "//button[text()='Save']";
	private String filterClearButton = "//button[text()='Clear']";
	private String pageSizeDd = "//p[text()='Page Size:']/../div";
//	private String pageSizeDd = "//div[@aria-haspopup='listbox']/..";
	public static String paginationRange ="//*[name()='svg' and @data-testid='NavigateNextIcon']/../span";
	public static String paginationRangeLastPage = "//span[contains(@class, 'MuiTypography')]";
	public static String paginationRangeFirstPage = "//span[contains(@class, 'MuiTypography')]";
	public static String resultsRange = "//p[text()='Page Size:']/../../../div[contains(@class, 'css-0')]";
//	public static String resultsRange = "//p[text()='Page Size:']/../../../../div[contains(@class, 'css-0')]";
	private String searchTermColumnHeader = "//span[text()='Search Term']/../..";
	private String sfrLwColumnHeader = "//span[text()='SFR-LW']/../..";
	private String sfrFourWkAgoColumnHeader = "//span[text()='SFR 4Wk Ago']/../..";
	public static String sfrLws = "//div[@class='ag-center-cols-container' and @role='rowgroup']/div/div[@aria-colindex='4']";
	public static String sfr4WksAgo = "//div[@class='ag-center-cols-container' and @role='rowgroup']/div/div[@aria-colindex='6']";
	private String avgSalesPrices ="//div[@class='ag-center-cols-container' and @role='rowgroup']/div/div[@aria-colindex='11']";
	
	private String loginEmail = "//input[@id='Login']";
	private String externalInternal = "//input[@id='External/Internal-selectized']/../..";
	private String btnApply = "//button[@id='btnApply']";
	private String internal = "//div[@class='option' and text()='Internal']";
	private String editBtn = "//span[text()='Auto_tester']//ancestor::div[@role='row']//button[@title='Edit Record']";
	private String baseUser = "//label[contains(text(), 'Internal Only: Base User')]/../input";
	private String updateUser = "//span[text()='Update User']/..";
	private String userUpdatedMsg = "//div[text() = 'User updated successfully.']";
	

	public static String gridColumnTitles = "//div[@role='columnheader' and not(@col-id='brandSOV')]";
	public static String navigationSearchDd = "//button[@id='nav-root-search']/..";
	public static String searchDdOptions = "//ul[@role='menu']/a";
	public static String queryTrendInNavigationDdOptions = "//a[@id='nav-node-query-trends']";
	public static String brandSovSearchQuery = "//input[@id='Search Query-selectized']/../div";

	public void setSfrLwFilter(String sfrLwFilterValue) {
		logger.info("** Setting SFR-LW FILTER value");
		setText(sfrLwFilterTextbox, sfrLwFilterValue, "Query Trends - SFR-LW FILTER textbox");
	}

	public void setSearchTerm(String searchTerm) {
		logger.info("** Setting Search Term value");
		setText(searchTermTextbox, searchTerm, "Query Trends - Search Term textbox");
	}
	
	public void setTermInTitle(String searchTerm) {
		logger.info("** Setting Term in Title value");
		setText(termInTitleTextbox, searchTerm, "Query Trends - Term in Title Search textbox");
	}

	public void selectCountryDepartment(String countryDepartment) {
		logger.info("** Selecting COUNTRY-DEPARTMENT option");
		click(countryDepartmentDdOpenArrow, "Query Trends - COUNTRY-DEPARTMENT dropdown arrrow to open");
		click("//ul[@role='listbox']//li[text()='" + countryDepartment + "']", "Query Trends -COUNTRY-DEPARTMENT dropdown option");
		click(countryDepartmentDdCloseArrow, "Query Trends -COUNTRY-DEPARTMENT dropdown arrrow to close");	}

	public void saveBtn() {
		logger.info("** Clicking on filters save button");
		click(filterSaveButton, "Query Trends -Save button on filters widget");	
	}
	
	public void clearBtn() {
		logger.info("** Clicking on filters Clear button");
		click(filterClearButton, "Query Trends -Clear button on filters widget");	
	}

	public void clickOnBrandSovIcon(String searchTerm) {
		logger.info("** Clicking on Brand SOV icon in grid");
		click("//span[contains(@id, 'cell-searchTerm') and text()='"+searchTerm+"']/ancestor::div[@role='row']//button", "Query Trends - Brand SOV icon in grid");
	}
	
	public void selectPageSize(String size) throws InterruptedException {
		logger.info("** Selecting page size");
		click(pageSizeDd, "Query Trends - Page size dropdown");
		click("//ul[@role='listbox']/li[text()='"+size+"']", "Query Trends - Page size dropdown option");
		Thread.sleep(500);
		waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
	}
	
	public int paginationPageNumCount() {
		String pageRange = getText(paginationRange, "Query Trends - pagination range");
		int firstPage = Integer.parseInt(pageRange.substring(0, 1));
		int lastPage = Integer.parseInt(pageRange.substring(pageRange.length()-1));
		return lastPage-firstPage;
	}
	
//	public int paginationPageNumCount() {
//		String pageRangeLastPage = getText(paginationRangeLastPage, "Query Trends - pagination range");
//		String pageRangeFirstPage = getAttribute("//input[@type='number']", "value", "");
//		int lastPage = Integer.parseInt(pageRangeLastPage.substring(3));
//		int firstPage = Integer.parseInt(pageRangeFirstPage.substring(0, 1));
//		return lastPage-firstPage;
//	}
	
	public void limitAccessToQueryTrendsPage() throws InterruptedException {
		logger.info("** Limiting access to Query Trends page");
		lp.openAdminScreen("User Management");	
		setText(loginEmail, readConfig.getUsername(), "User Management - Login textbox");
		click(externalInternal, "User Management - External/Internal dropdown");
		click(internal, "User Management - Internal option");
		click(btnApply, "User Management - Apply button");
		click("//span[text()='Auto_tester']//ancestor::div[@role='row']//button[@title='Edit Record']", "");
		if (findElementVisible(baseUser, "User Management - Base user checkbox on Edit user pop up").isSelected()) {
			click(baseUser, "User Management - Base user checkbox on Edit user pop up");
		}
		click(updateUser, "User Management - Update user button on Edit user pop up");
		findElementVisible(userUpdatedMsg, "User Management - User updated message");
	}
	
	public void grantAccessToQueryTrendsPage() throws InterruptedException {
		logger.info("** Granting access to Query Trends page");
		lp.openAdminScreen("User Management");	
		setText(loginEmail, readConfig.getUsername(), "User Management - Login textbox");
		click(externalInternal, "User Management - External/Internal dropdown");
		click(internal, "User Management - Internal option");
		click(btnApply, "User Management - Apply button");
		click(editBtn, "User Management - Edit user button");
		if (!findElementVisible(baseUser, "User Management - Base user checkbox on Edit user pop up").isSelected()) {
			click(baseUser, "User Management - Base user checkbox on Edit user pop up");
		}
		click(updateUser, "User Management - Update user button on Edit user pop up");
		findElementVisible(userUpdatedMsg, "User Management - User updated message");
	}
	
	public void paginationClickNextPageArrow() throws InterruptedException {
		logger.info("** Clicking on next page arrow");
		click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
		Thread.sleep(500);
		waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
	}
	
	public void paginationClickPreviousPageArrow() throws InterruptedException {
		logger.info("** Clicking on previous page arrow");
		click(QueryTrendsPage.paginationPreviousArrow, "Query Trends - Pagination next arrow");
		Thread.sleep(500);
		waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
	}
	
	public void paginationClickFirstPageArrow() throws InterruptedException {
		logger.info("** Clicking on first page arrow");
		click(QueryTrendsPage.firstPageIcon, "Query Trends - Pagination next arrow");
		Thread.sleep(500);
		waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
	}
	
	public void paginationClickLastPageArrow() throws InterruptedException {
		logger.info("** Clicking on last page arrow");
		click(QueryTrendsPage.lastPageIcon, "Query Trends - Pagination next arrow");
		Thread.sleep(500);
		waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
	}
	
	public void verifyCurrentAndNextPagePaginationAndResultsTexts(String sfrLwFilterValue, String pageSize, BigDecimal pageCount, SoftAssert softAssert) throws InterruptedException {
		logger.info("** Checking current and next page pagination and results texts");
		for (int i = 0 ; i < pageCount.intValue(); i++) {
			String	pageRangeActual = getPageRange();		
			String	pageRangeExpected = i + 1 + " of " + pageCount;
			softAssert.assertEquals(pageRangeActual, pageRangeExpected, "** Page range is wrong. Expected: " + pageRangeExpected + " - Actual: " +pageRangeActual);
			
			String	resultsRangeActual = getText(QueryTrendsPage.resultsRange, "Query Trends - results range");
			String	resultsRangeExpected = 1 + (i*Integer.parseInt(pageSize)) + " to " + ((i+1) * Integer.parseInt(pageSize)) + " of " + sfrLwFilterValue;
			softAssert.assertEquals(resultsRangeActual, resultsRangeExpected, "** Results range is wrong. Expected: " + resultsRangeExpected + " - Actual: " +resultsRangeActual);

			if (i==1) {
				break;
			}
			
			paginationClickNextPageArrow();
		}
	}
	
	public void verifyPreviousPagePaginationAndResultsTexts(String sfrLwFilterValue, String pageSize, BigDecimal pageCount, SoftAssert softAssert) throws InterruptedException {
		logger.info("** Checking previous page pagination and results texts");
		for (int i = 0 ; i < pageCount.intValue(); i++) {
			paginationClickPreviousPageArrow();
			
			String	pageRangeActual = getPageRange();
			String	pageRangeExpected = i + 1 + " of " + pageCount;
			softAssert.assertEquals(pageRangeActual, pageRangeExpected, "** Page range is wrong. Expected: " + pageRangeExpected + " - Actual: " +pageRangeActual);
			
			String	resultsRangeActual = getText(QueryTrendsPage.resultsRange, "Query Trends - results range");
			String	resultsRangeExpected = 1 + (i*Integer.parseInt(pageSize)) + " to " + ((i+1) * Integer.parseInt(pageSize)) + " of " + sfrLwFilterValue;
			softAssert.assertEquals(resultsRangeActual, resultsRangeExpected, "** Results range is wrong. Expected: " + resultsRangeExpected + " - Actual: " +resultsRangeActual);

			if (i==0) {
				break;
			}
		}
	}
	
	public void verifyLastPagePaginationAndResultsTexts(String sfrLwFilterValue, String pageSize, BigDecimal pageCount, SoftAssert softAssert) throws InterruptedException {
		logger.info("** Checking last page pagination and results texts");
		paginationClickLastPageArrow();
		
		String	pageRangeActual = getPageRange();
		String	pageRangeExpected = pageCount + " of " + pageCount;
		softAssert.assertEquals(pageRangeActual, pageRangeExpected, "** Page range is wrong. Expected: " + pageRangeExpected + " - Actual: " +pageRangeActual);
		
		String	resultsRangeActual = getText(QueryTrendsPage.resultsRange, "Query Trends - results range");
		String	resultsRangeExpected = 1 + (pageCount.intValue() -1)*(Integer.parseInt(pageSize))  + " to " + sfrLwFilterValue + " of " + sfrLwFilterValue;
		softAssert.assertEquals(resultsRangeActual, resultsRangeExpected, "** Results range is wrong. Expected: " + resultsRangeExpected + " - Actual: " +resultsRangeActual);
	}
	
	public void verifyFirstPagePaginationAndResultsTexts(String sfrLwFilterValue, String pageSize, BigDecimal pageCount, SoftAssert softAssert) throws InterruptedException {
		logger.info("** Checking first page pagination and results texts");
		paginationClickFirstPageArrow();
		
		String	pageRangeActual = getPageRange();
		String	pageRangeExpected = 1 + " of " + pageCount;
		softAssert.assertEquals(pageRangeActual, pageRangeExpected, "** Page range is wrong. Expected: " + pageRangeExpected + " - Actual: " +pageRangeActual);
		
		String	resultsRangeActual = getText(QueryTrendsPage.resultsRange, "Query Trends - results range");
		String	resultsRangeExpected = 1  + " to " + pageSize + " of " + sfrLwFilterValue;
		softAssert.assertEquals(resultsRangeActual, resultsRangeExpected, "** Results range is wrong. Expected: " + resultsRangeExpected + " - Actual: " +resultsRangeActual);
	}
	
//	public String getPageRange() {
//		String pageRangeLastPage = getText(paginationRangeLastPage, "Query Trends - pagination range");
//		String pageRangeFirstPage = getAttribute("//input[@type='number']", "value", "");
//		String	pageRangeActual = pageRangeFirstPage + " " + pageRangeLastPage;	
//		return pageRangeActual;
//	}
	
	public String getPageRange() {
		return getText(QueryTrendsPage.paginationRange, "Query Trends - pagination range");
	}
		
	public List<List<String>> getDataInGrid(String countryDepartment) throws InterruptedException {
		List<List<String>> gridData = new ArrayList<>();
		WebElement cell;
		List<String> row;
		int paginationCount;
		int rowCount;
		int columnCount;

		zoomInOrOutTo("80");
		paginationCount = paginationPageNumCount();

		for (int x = 0; x <= paginationCount; x++) {
			rowCount =   findElementsVisible(searchTerms, "Query Trends - Grid search terms").size();		
			columnCount =  findElementsVisible(gridColumnTitles, "Query Trends - Grid column titles").size();		

			for (int y = 0; y < rowCount; y++) {
				logger.info("** Getting grid data into a list");
				row = new ArrayList<>();
				row.add(countryDepartment);
				for (int z = 2; z <= columnCount+1; z++) {
					cell = findElementVisible("//div[@role='rowgroup']//div[@role='row' and @row-index='"+y+"']//div[@aria-colindex='"+z+"'  and not(@role='columnheader') and not(@col-id='brandSOV')]", "");
					String text = cell.getText();
					if (text.contains(".") && text.length() > 8) {
						text = text.substring(0, 5) ;
					}
					if (text.contains(",")) {
						text = text.replace(",", "");
					}
					if (z == 3) {
						text = text.replace("/", "-");
					}
					row.add(text);
				}
				gridData.add(row);
			}

			if (paginationCount>x) {
				zoomInOrOutTo("100");
				click(paginationNextArrow, "Query Trends - Pagination next arrow");
				Thread.sleep(500);
				waitForElementToBeInvisible("//div[@id='query-trends']//span[@role='progressbar']", 120, "");
				zoomInOrOutTo("80");
			}
		}
		return gridData;
	}
	
	public List<String> sortSearchTermsInAscOrDescOrder() throws InterruptedException{
		List<String> searchTermsSorted = new ArrayList<>();
		List<WebElement> searchTerm;
		int paginationCount;
				
		click(searchTermColumnHeader, "Query Trends - Search Term column header");
		Thread.sleep(500);
		waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
		
		paginationCount = paginationPageNumCount();
		
		searchTerm =   findElementsVisible(searchTerms, "Query Trends - Grid search terms");

		for (WebElement term : searchTerm) {
			searchTermsSorted.add(term.getText());
		}
		
		for (int i = 0; i < paginationCount; i++) {
			click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
			searchTerm =   findElementsVisible(searchTerms, "Query Trends - Grid search terms");
			for (WebElement term : searchTerm) {
				searchTermsSorted.add(term.getText());
			}
		}
		return searchTermsSorted;
	}
	
	public List<Integer> sortSfrLwInAscOrDescOrder() throws InterruptedException{
		List<Integer> sfrwklySorted = new ArrayList<>();
		List<WebElement> sfrLw;
		int paginationCount;
		
		click(sfrLwColumnHeader, "Query Trends - SFR-LW column header");
		Thread.sleep(500);
		waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
		
		paginationCount = paginationPageNumCount();
		
		sfrLw = findElementsVisible(sfrLws, "Query Trends - Grid SFR-LWs");

		for (WebElement term : sfrLw) {
			String cellValue = term.getText();
			if (!cellValue.isEmpty()) {
				sfrwklySorted.add(Integer.parseInt(cellValue.replace(",", "")));
			}
		}
		
		for (int i = 0; i < paginationCount; i++) {
			click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
			sfrLw = findElementsVisible(sfrLws, "Query Trends - Grid SFR-LWs");
			for (WebElement term : sfrLw) {
				String cellValue = term.getText();
				if (!cellValue.isEmpty()) {
					sfrwklySorted.add(Integer.parseInt(cellValue.replace(",", "")));
				}
			}
		}
		return sfrwklySorted;
	}
	
	public List<Integer> sortSfrFourWkAgoInAscOrDescOrder() throws InterruptedException{
		List<Integer> sfr4WkAgoSorted = new ArrayList<>();
		List<WebElement> sfr4WkAgo;
		int paginationCount;
		
		click(sfrFourWkAgoColumnHeader, "Query Trends - SFR 4Wk Ago column header");
		Thread.sleep(500);
		waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
		
		paginationCount = paginationPageNumCount();
		
		sfr4WkAgo = findElementsVisible(sfr4WksAgo, "Query Trends - Grid SFR 4Wk Ago");

		for (WebElement term : sfr4WkAgo) {
			String cellValue = term.getText();
			if (!cellValue.isEmpty()) {
				sfr4WkAgoSorted.add(Integer.parseInt(cellValue.replace(",", "")));
			}
		}
		
		for (int i = 0; i < paginationCount; i++) {
			click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
			sfr4WkAgo = findElementsVisible(sfr4WksAgo, "Query Trends - Grid SFR 4Wk Ago");
			for (WebElement term : sfr4WkAgo) {
				String cellValue = term.getText();
				if (!cellValue.isEmpty()) {
					sfr4WkAgoSorted.add(Integer.parseInt(cellValue.replace(",", "")));
				}
			}
		}
		return sfr4WkAgoSorted;
	}
	
	public List<BigDecimal> sortAvgSalesPriceInAscOrDescOrder() throws InterruptedException{
		List<BigDecimal> avgSalesPriceSorted = new ArrayList<>();
		List<WebElement> avgSalesPrice;
		int paginationCount;
		
		click(avgSalesPriceColumnHeader, "Query Trends - Avg. Sales Price column header");
		Thread.sleep(500);
		waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
		
		paginationCount = paginationPageNumCount();
		
		avgSalesPrice =   findElementsVisible(avgSalesPrices, "Query Trends - Grid Avg Sales Prices");
		
		for (WebElement term : avgSalesPrice) {
			String cellValue = term.getText();
			if (!cellValue.isEmpty()) {
				avgSalesPriceSorted.add(common.stringToBigDecimal(cellValue));
			} 
		}
		
		for (int i = 0; i < paginationCount; i++) {
			click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			waitForElementToBeInvisible(progressBar, 120, "Query Trends - Horizontal progress bar");
			avgSalesPrice =   findElementsVisible(avgSalesPrices, "Query Trends - Grid Avg Sales Prices");
			for (WebElement term : avgSalesPrice) {
				String cellValue = term.getText();
				if (!cellValue.isEmpty()) {
					avgSalesPriceSorted.add(common.stringToBigDecimal(cellValue));
				} 
			}
		}
		return avgSalesPriceSorted;
	}
	
	public List<Integer> getSfrLwDb(Map<String, String> searchTermSfrLwMap){
        List<String> latestWeekSearchTerms = new ArrayList<>(searchTermSfrLwMap.keySet());
		List<Integer> sfrLatestWeek = new ArrayList<>();
		for (String term : latestWeekSearchTerms) {
			sfrLatestWeek.add(Integer.parseInt(searchTermSfrLwMap.get(term)));
		}
		Collections.sort(sfrLatestWeek);
		return sfrLatestWeek;
	}
	
	public List<Integer> getSfr4wkAgoDb(Map<String, String> searchTermsSfrMap, Map<String, String> searchTermSfr4WkAgoMap){
        List<String> latestWeekSearchTerms = new ArrayList<>(searchTermsSfrMap.keySet());
		List<Integer> sfrFourWeeksAgo = new ArrayList<>();
		for (String term : latestWeekSearchTerms) {
			String trm = searchTermSfr4WkAgoMap.get(term);
			if (trm!=null) {
				sfrFourWeeksAgo.add(Integer.parseInt(trm));
			}
		}
		Collections.sort(sfrFourWeeksAgo);
		return sfrFourWeeksAgo;
	}
	
	public List<String> getRowWithSfrValuesUi(Map<String, String> searchTermsSfrMap){
        List<String> latestWeekSearchTerms = new ArrayList<>(searchTermsSfrMap.keySet());
		List<String> sfrRowList = new ArrayList<>();
		for (int i = 2; i < latestWeekSearchTerms.size()+2; i++) {
			for (int j = 4; j < 9; j++) {
				WebElement cell = findElementPresent("//div[@class='ag-center-cols-container' and @role='rowgroup']//div[@aria-rowindex='"+i+"']/div[@aria-colindex='"+j+"']", "SFR cell");
				if (cell.getText().isEmpty()) {
					sfrRowList.clear();
					break;
				} else {
					sfrRowList.add(cell.getText());
				}
			}
			if (sfrRowList.size() == 5) {
				logger.info("** Row found with all SFR cells have value");
				break;
			} else {
				logger.info("** No row found with all SFR cells have value");
			}
		}
		return sfrRowList;
	}
	
	public String getQueryTrendsExportedFilePath(String path) {
		logger.info("** Getting exported file path");
		String fileName = null;
		// Create an object of the File class, Replace the file path with path of the directory
		File directory = new File(path);
		// Create an object of Class MyFilenameFilter Constructor with name of file which is being searched
		FileSearch filter = new FileSearch("SEARCH_TRENDS");
		// store all names with same name  with/without extension
		String[] flist = directory.list(filter);
		// Empty array
		if (flist == null) {
			logger.info("** Empty directory or directory does not exists!!");
		} else {
			// Print all files with same name in directory as provided in object of MyFilenameFilter class
			for (int i = 0; i < flist.length; i++) {
				fileName = flist[i];
			}
		}
		return path + "/"+ fileName;
	}
	
	public List<String> formatUiColumnTitlesToMatchCsv(){
		logger.info("** Getting grid column titles and turning into CSV format");
		List<String> gridColumnTitlesActual = new ArrayList<>();
		List<WebElement> columnTitles =  findElementsVisible(gridColumnTitles, "Query Trends - Grid column titles");
		gridColumnTitlesActual.add("Country-Department");
		for (WebElement title : columnTitles) {
			String colTitle = title.getText();
			
			if (colTitle.equals("SFR - SFR4Wk Ago")) {
				colTitle = "SFR - SFR4 Wk Ago";
			} else if (colTitle.equals("SFR 4Wk Ago")) {
				colTitle = "SFR 4 Wk Ago";
			} else if (colTitle.equals("SFRLW - SFRLY")) {
				colTitle = "SFR LW - SFR LY";
			} else if (colTitle.equals("SFRLY")) {
				colTitle = "SFR LY";
			} else if (colTitle.equals("Avg. Rating")) {
				colTitle = "Avg Rating";
			} else if (colTitle.equals("Avg. Reviews")) {
				colTitle = "Avg Reviews";
			} else if (colTitle.equals("Avg. Sales Price")) {
				colTitle = "Avg Sales Price";
			}   
						
			gridColumnTitlesActual.add(colTitle);
		}
		return gridColumnTitlesActual;
	}
	
	public String getLastDayOfWeek(String firstDayOfWeek) throws SQLException {
		LocalDate date = LocalDate.parse(firstDayOfWeek);
		LocalDate targetDate = date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
		return targetDate.toString();
	}
	
	public Map<String, List<String>> getKeywordAvgRatingReviewPrice() {
		Map<String, List<String>> keywordMap = new LinkedHashMap<>();
		List<String> avgList = new ArrayList<>();
		String keyword = null;
		for (int i = 2; i < 22; i++) {
			for (int j = 9; j < 12; j++) {
				WebElement cell = findElementPresent("//div[@class='ag-center-cols-container' and @role='rowgroup']//div[@aria-rowindex='"+i+"']/div[@aria-colindex='"+j+"']", "SFR cell");
				String text = cell.getText();
				if (!text.isEmpty()) {
					text = text.replace(",", "");
					avgList.add(text);
				}
			}
			if (avgList.size() == 3) {
				logger.info("Row found with all Avg values available");
				keyword = getText("//div[@class='ag-pinned-left-cols-container' and @role='rowgroup']/div[@aria-rowindex='"+i+"']/div[@aria-colindex='"+i+"']", "Keyword cell");
				break;
			} else {
				avgList.clear();
			}
		}
		if (avgList.size()<3) {
			logger.info("No row found with all Avg values available");
		}
		keywordMap.put(keyword, avgList);
		return keywordMap;
	}

    public static class FileSearch implements FilenameFilter {
        String initials;

        // constructor to initialize object
        public FileSearch(String initials) {
            this.initials = initials;
        }

        // overriding the accept method of FilenameFilter
        // interface

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(initials);
        }
    }
}*/