//TODO - Class not used as of 7/17/23 - Will revisit once this page makes it to the merge app

//package com.DC.testcases.search;

// import com.DC.pageobjects.search.QueryTrendsPage;


/*public class QueryTrends extends BaseClass {

	@TmsLink("148004")
	@Test(priority=1, dataProvider = "C148004", dataProviderClass = SearchDataProvider.class, description = "UI: Populate Query Trends dashboard")
	public void Search_QueryTrends_C148004_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextInGrid) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		Page pg = new Page(driver);
		CommonMethods common = new CommonMethods(driver);
		SoftAssert softAssert = new SoftAssert();

		List<String> gridColumnTitlesExpected = common.createList(gridColTitles);
		List<String> gridColumnTitlesActual = new ArrayList<>();
		List<WebElement> gridColumnTitles;
		List<WebElement> searchTerms;
		List<String> searchTermsSortedAscendingActual = new ArrayList<>();
		List<String> searchTermsSortedAscendingExpected;
		int paginationCount;

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn(); 

		pg.zoomInOrOutTo("80");

		logger.info("** Getting grid column titles on UI");
		gridColumnTitles =  pg.findElementsVisible(QueryTrendsPage.gridColumnTitles, "Query Trends - Grid column titles");

		for (WebElement title : gridColumnTitles) {
			gridColumnTitlesActual.add(title.getText());
		}

		pg.zoomInOrOutTo("100");

		logger.info("** Verifying expected/actual grid column titles");
		softAssert.assertEquals(gridColumnTitlesActual, gridColumnTitlesExpected, "** Error in grid column titles. Expected: " + gridColumnTitlesExpected + " - Actual: " + gridColumnTitlesActual);

		qtp.selectPageSize("10");
		paginationCount = qtp.paginationPageNumCount();
		
		logger.info("** Getting search terms in grid");
		
		searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");

		for (WebElement term : searchTerms) {
			searchTermsSortedAscendingActual.add(term.getText());
		}
		
		for (int i = 0; i < paginationCount; i++) {
			pg.click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			pg.waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
			searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");
			for (WebElement term : searchTerms) {
				searchTermsSortedAscendingActual.add(term.getText());
			}
		}

		searchTermsSortedAscendingExpected = searchTermsSortedAscendingActual;
		Collections.sort(searchTermsSortedAscendingExpected);

		logger.info("** Verifying whether searc terms are in asceding order");
		softAssert.assertEquals(searchTermsSortedAscendingActual, searchTermsSortedAscendingExpected, "** Expected Search Term in ascending order, but error in oder. Expected: " + searchTermsSortedAscendingExpected + " - Actual: " + searchTermsSortedAscendingActual);

		softAssert.assertAll();

		logger.info("** Execution for test case " + tcId + " completed successfully");   

	}

	@TmsLink("150472")
	@Test(priority=2, dataProvider = "C150472", dataProviderClass = SearchDataProvider.class, description = "Verify 'No Data To Display' should populate until user inputs required filters")
	public void Search_QueryTrends_C150472_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		SoftAssert softAssert = new SoftAssert();
		String defaultTextInGridActual;

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	

		defaultTextInGridActual = pg.getText(QueryTrendsPage.defaultTextInGrid, "Query Trends - Default text in the middle of grid before any filters applied");
		
		logger.info("** Verifying expected/actual default text in grid");
		softAssert.assertEquals(defaultTextInGridActual, defaultTextGridExpected, "** Default text in grid is not as expected. Expected: " + defaultTextGridExpected + " - Actual: " + defaultTextInGridActual);

		softAssert.assertAll();

		logger.info("** Execution for test case " + tcId + " completed successfully");   

	}
	
	@TmsLink("150474")
	@Test(priority=3, dataProvider = "C150474", dataProviderClass = SearchDataProvider.class, description = "Verify that Apply button remains blank until user choose options from Country-Department & SFR-LW filters")
	public void Search_QueryTrends_C150474_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.saveBtn();
		Thread.sleep(500);

		logger.info("** Checking to see if data populate in grid without required filter values entered");
		softAssert.assertTrue(pg.elementVisible(QueryTrendsPage.grid, 1), "** Data populate in grid even without entering required filters (SFR-LW and COUNTRY-DEPARTMENT)");

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
	}
	
	@TmsLink("150475")
	@Test(priority=4, dataProvider = "C150475", dataProviderClass = SearchDataProvider.class, description = "Verify that user can view data when Country-Department & SFR-LW filters are selected")
	public void Search_QueryTrends_C150475_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
		int rowsLeft;
		int rowsRight;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn(); 
		Thread.sleep(200);

		logger.info("** Checking to see if data populate in grid after required filter values entered");
		softAssert.assertTrue(pg.elementVisible(QueryTrendsPage.grid, 120), "** Data does not populate in grid even although required filters (SFR-LW and COUNTRY-DEPARTMENT) entered");

		rowsLeft = pg.findElementsVisible(QueryTrendsPage.gridLeftRows, "Query Trends - Grid left rows").size();
		rowsRight = pg.findElementsVisible(QueryTrendsPage.gridRightRows, "Query Trends - Grid right rows").size();
		
		softAssert.assertEquals(rowsLeft, Integer.parseInt(sfrLwFilter), "** Grid row count does not match SFR-LW filter value entered. SFR-LW: " + sfrLwFilter + " - Row count: " + rowsLeft);
		softAssert.assertEquals(rowsRight, Integer.parseInt(sfrLwFilter), "** Grid row count does not match SFR-LW filter value entered. SFR-LW: " + sfrLwFilter + " - Row count: " + rowsRight);

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("150476")
	@Test(priority=5, dataProvider = "C150476", dataProviderClass = SearchDataProvider.class, description = "Verify that user can use Search Term filter by applying a keyword")
	public void Search_QueryTrends_C150476_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
		List<String> searchTermsInGrid = new ArrayList<>();
		List<WebElement> searchTerms;
		int paginationCount;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSearchTerm(searchTerm);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn(); 
		
		qtp.selectPageSize("10");
		paginationCount = qtp.paginationPageNumCount();
		
		logger.info("** Getting search terms in grid (all pages in pagination)");
		searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");

		for (WebElement term : searchTerms) {
			searchTermsInGrid.add(term.getText().toLowerCase());
		}
		
		for (int i = 0; i < paginationCount; i++) {
			pg.click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			pg.waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
			searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");
			for (WebElement term : searchTerms) {
				searchTermsInGrid.add(term.getText().toLowerCase());
			}
		}
		
		logger.info("** Checking to see if search term is included each result");
		for (String term : searchTermsInGrid) {
			softAssert.assertTrue(term.contains(searchTerm.toLowerCase()), "** Record ("+term+") does not contain the search term ("+searchTerm+")");
		}
		
		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("150479")
	@Test(priority=6, dataProvider = "C150479", dataProviderClass = SearchDataProvider.class, description = "Verify that Icon on the left of the Seach Term navigates user to Share of Voice - Brand screen")
	public void Search_QueryTrends_C150479_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSearchTerm(searchTerm);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn(); 
		qtp.clickOnBrandSovIcon(searchTerm);
		
		logger.info("** Switching tab to Brand Sov");
		lp.switchToTab(2, 1);
		
		logger.info("** Checking Url for Brand Sov page");
		String currentUrl = lp.getCurrentUrl();
		softAssert.assertTrue(currentUrl.contains("share-of-voice"), "** Current URL does not belong to Brand SOV page. Current URL: " + currentUrl);
				
		logger.info("** Checking if Search Query text box populated with the search term");
		String termInTextbox = lp.getAttribute(QueryTrendsPage.brandSovSearchQuery, "data-value", "Brand SOV - Search query textbox");

		softAssert.assertEquals(termInTextbox, searchTerm, "** Search Query text box in Brand SOV not populated with the search term. Search term: " + searchTerm + " - Term in textbox: " + termInTextbox);

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("150471")
	@Test(priority=7, dataProvider = "C150471", dataProviderClass = SearchDataProvider.class, description = "Verify that user should be able to navigate to the screen via Search dropdown")
	public void Search_QueryTrends_C150471_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		SoftAssert softAssert = new SoftAssert();
		List<String> searchTabOptions = new ArrayList<>();
		List<WebElement> searchOptions;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		
		logger.info("** Hovering over Search dropdown");
		lp.hoverOverElement(QueryTrendsPage.navigationSearchDd, "Navigation bar - Search dropdown");
		
		logger.info("** Getting options in Search dropdown");
		searchOptions =  pg.findElementsVisible(QueryTrendsPage.searchDdOptions, "Search dropdown options");

		for (WebElement title : searchOptions) {
			searchTabOptions.add(title.getText());
		}
		
		logger.info("** Checking if Query Trends is among the options");
		softAssert.assertTrue(searchTabOptions.contains("Query Trends"), "** 'Query Trends' is not among the Search navigation dropdown options. Options: " + searchTabOptions);
		
		logger.info("** Clicking on Query Trends option");
		pg.click(QueryTrendsPage.queryTrendInNavigationDdOptions, "Navigation bar - Query Trends option in Search dropdown");
		
		logger.info("** Checking if Query Trends page loaded");
		softAssert.assertTrue(pg.elementVisible(QueryTrendsPage.queryTrendsGridTitle, 20), "** Query Trends page may not be loaded. Query Trend title on grid could not be located.");

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("150478")
	@Test(priority=8, dataProvider = "C150478", dataProviderClass = SearchDataProvider.class, description = "Verify that user can manually input Term in Title search filter")
	public void Search_QueryTrends_C150478_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
        SearchDBFunctions sdb = new SearchDBFunctions();
		List<String> searchTermsList = new ArrayList<>();
        Page pg = new Page(driver);
		List<WebElement> searchTerms;
		List<String> searchTermsInDb;
		String startDateForSearchTerms;
		int paginationCount;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setTermInTitle(termInTitleSearch);
		qtp.saveBtn(); 
		
		qtp.selectPageSize("10");
		paginationCount = qtp.paginationPageNumCount();
		
		logger.info("** Getting search terms in grid (all pages in pagination)");
		searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");

		for (WebElement term : searchTerms) {
			searchTermsList.add(term.getText());
		}
		
		for (int i = 0; i < paginationCount; i++) {
			pg.click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			pg.waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
			searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");
			for (WebElement term : searchTerms) {
				searchTermsList.add(term.getText());
			}
		}
		
		logger.info("** Getting search terms in DB");
		startDateForSearchTerms = sdb.getStartDayForSearchTermsWeeklyFromDb().get(0);
		searchTermsInDb =	sdb.getSearchTermsForTermInTitleSearchDb(termInTitleSearch, sfrLwFilter, startDateForSearchTerms);
		
		logger.info("** UI and DB search terms being sorted for comparison");
		Collections.sort(searchTermsList);
		Collections.sort(searchTermsInDb);

		softAssert.assertEquals(searchTermsList, searchTermsInDb, "** Search Terms for Term in Title Search on IU does not match with DB. UI: "+ searchTermsList +" - DB " + searchTermsInDb);
		
		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
				
	}
	
	@TmsLink("150485")
	@Test(priority=9, dataProvider = "C150485", dataProviderClass = SearchDataProvider.class, description = "Verify that user can export the data")
	public void Search_QueryTrends_C150485_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
        CommonMethods common = new CommonMethods(driver);
        Page pg = new Page(driver);
        
		List<String> gridColumnTitlesActual = new ArrayList<>();
		List<List<String>> csvData;
		List<List<String>> gridData;
		String path = null;
		boolean fileExists;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setSearchTerm(searchTerm);
		qtp.saveBtn(); 
		
		qtp.selectPageSize("10");
		pg.zoomInOrOutTo("80");
				
		gridColumnTitlesActual =  qtp.formatUiColumnTitlesToMatchCsv();
						
		gridData = qtp.getDataInGrid(countryDepartment);
		gridData.add(0, gridColumnTitlesActual);

		pg.clickJsExecuter(QueryTrendsPage.exportButton, "Query Trends-Export button");
		fileExists = common.fileExists(DownloadFolder);
		
    	Assert.assertTrue(fileExists, "** CSV file could not be downloaded. No file exists in path!");
		
		path = qtp.getQueryTrendsExportedFilePath(DownloadFolder);
		csvData = CSVUtil.getCsvRowsForQueryTrends(path);

        softAssert.assertEquals(gridData, csvData, "UI: " + gridData+ " - CSV: " + csvData);
        
		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
	}
	
	@TmsLink("150483")
	@Test(priority=10, dataProvider = "C150483", dataProviderClass = SearchDataProvider.class, description = "Verify combination of filters selected")
	public void Search_QueryTrends_C150483_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
        SearchDBFunctions sdb = new SearchDBFunctions();
		List<String> searchTermsList = new ArrayList<>();
        Page pg = new Page(driver);
		List<WebElement> searchTerms;
		List<String> searchTermsInDb;
		String startDateForSearchTerms;
		int paginationCount;
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setSearchTerm(searchTerm);
		qtp.setTermInTitle(termInTitleSearch);
		qtp.saveBtn(); 
		
		qtp.selectPageSize("10");
		paginationCount = qtp.paginationPageNumCount();
		
		logger.info("** Getting search terms in grid (all pages in pagination)");
		searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");

		for (WebElement term : searchTerms) {
			searchTermsList.add(term.getText());
		}
		
		for (int i = 0; i < paginationCount; i++) {
			pg.click(QueryTrendsPage.paginationNextArrow, "Query Trends - Pagination next arrow");
			Thread.sleep(500);
			pg.waitForElementToBeInvisible(QueryTrendsPage.progressBar, 120, "Query Trends - Horizontal progress bar");
			searchTerms =   pg.findElementsVisible(QueryTrendsPage.searchTerms, "Query Trends - Grid search terms");
			for (WebElement term : searchTerms) {
				searchTermsList.add(term.getText());
			}
		}
		
		logger.info("** Getting search terms in DB");
		startDateForSearchTerms = sdb.getStartDayForSearchTermsWeeklyFromDb().get(0);
		searchTermsInDb =	sdb.getSearchTermsDb(searchTerm, sfrLwFilter, startDateForSearchTerms);
		
		logger.info("** UI and DB search terms being sorted for comparison");
		Collections.sort(searchTermsList);
		Collections.sort(searchTermsInDb);
		
		softAssert.assertEquals(searchTermsList, searchTermsInDb, "** Search Terms on IU does not match with DB. UI: "+ searchTermsList +" - DB " + searchTermsInDb);
		
		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
				
	}
	
	@TmsLink("150482")
	@Test(priority=11, dataProvider = "C150482", dataProviderClass = SearchDataProvider.class, description = "Verify that user can Sort asc/desc all the columns")
	public void Search_QueryTrends_C150482_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextInGrid) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		Page pg = new Page(driver);
		SoftAssert softAssert = new SoftAssert();

		List<String> searchTermsSortedAscendingUi = new ArrayList<>();
		List<String> searchTermsSortedDescendingUi = new ArrayList<>();
		List<Integer> sfrwklySortedAscendingUi = new ArrayList<>();
		List<Integer> sfrwklySortedDescendingUi = new ArrayList<>();
		List<BigDecimal> avgSalesPriceSortedAscendingUi = new ArrayList<>();
		List<BigDecimal> avgSalesPriceSortedDescendingUi = new ArrayList<>();
		List<String> searchTermsSortedAscendingExpected;
		List<String> searchTermsSortedDescendingExpected;
		List<Integer> sfrwklySortedAscendingExpected;
		List<Integer> sfrwklySortedDescendingExpected;
		List<BigDecimal> avgSalesPriceSortedAscendingExpected;
		List<BigDecimal> avgSalesPriceSortedDescendingExpected;

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setSearchTerm(searchTerm);
		qtp.saveBtn(); 

		qtp.selectPageSize("10");
			
		logger.info("** Getting search terms in grid in desc order and checking the order");
		searchTermsSortedDescendingUi = qtp.sortSearchTermsInAscOrDescOrder();
		searchTermsSortedDescendingExpected = new ArrayList<>(searchTermsSortedDescendingUi);
		Collections.sort(searchTermsSortedDescendingExpected, Collections.reverseOrder());
		softAssert.assertEquals(searchTermsSortedDescendingUi, searchTermsSortedDescendingExpected, "** Search Terms are not in descending order. UI: " +searchTermsSortedDescendingUi+" - Expected: " +searchTermsSortedDescendingExpected);
		
		logger.info("** Getting search terms in grid in asc order and checking the order");
		searchTermsSortedAscendingUi = qtp.sortSearchTermsInAscOrDescOrder();
		searchTermsSortedAscendingExpected = new ArrayList<>(searchTermsSortedAscendingUi);
		Collections.sort(searchTermsSortedAscendingExpected);
		softAssert.assertEquals(searchTermsSortedAscendingUi, searchTermsSortedAscendingExpected,  "** Search Terms are not in ascending order. UI: " +searchTermsSortedAscendingUi+" - Expected: " +searchTermsSortedAscendingExpected);

		logger.info("** Getting SFR-LW in grid in asc order and checking the order");
		sfrwklySortedAscendingUi = qtp.sortSfrLwInAscOrDescOrder();
		sfrwklySortedAscendingExpected = new ArrayList<>(sfrwklySortedAscendingUi);
		Collections.sort(sfrwklySortedAscendingExpected);
		softAssert.assertEquals(sfrwklySortedAscendingUi, sfrwklySortedAscendingExpected,  "** SFRs-LW are not in descending order. UI: " +sfrwklySortedAscendingExpected+" - Expected: " +searchTermsSortedDescendingExpected);

		logger.info("** Getting SFR-LW in grid in desc order and checking the order");
		sfrwklySortedDescendingUi = qtp.sortSfrLwInAscOrDescOrder();
		sfrwklySortedDescendingExpected = new ArrayList<>(sfrwklySortedDescendingUi);
		Collections.sort(sfrwklySortedDescendingExpected, Collections.reverseOrder());
		softAssert.assertEquals(sfrwklySortedDescendingUi, sfrwklySortedDescendingExpected,  "** SFRs-LW are not in descending order. UI: " +sfrwklySortedDescendingUi+" - Expected: " +sfrwklySortedDescendingExpected);

		pg.scrollRightToElement(QueryTrendsPage.horizontalLoadingBar, QueryTrendsPage.avgSalesPriceColumnHeader, 5, 70, "Query Trends - Avg. Sales Column Header");
		
		logger.info("** Getting Avg. Sales Price in grid in asc order and checking the order");
		avgSalesPriceSortedAscendingUi = qtp.sortAvgSalesPriceInAscOrDescOrder();
		avgSalesPriceSortedAscendingExpected = new ArrayList<>(avgSalesPriceSortedAscendingUi);
		Collections.sort(avgSalesPriceSortedAscendingExpected);
		softAssert.assertEquals(avgSalesPriceSortedAscendingUi, avgSalesPriceSortedAscendingExpected,  "** Avg Sales Prices are not in descending order. UI: " +avgSalesPriceSortedAscendingUi+" - Expected: " +avgSalesPriceSortedAscendingExpected);
		
		logger.info("** Getting Avg. Sales Price in grid in desc order and checking the order");
		avgSalesPriceSortedDescendingUi = qtp.sortAvgSalesPriceInAscOrDescOrder();
		avgSalesPriceSortedDescendingExpected = new ArrayList<>(avgSalesPriceSortedDescendingUi);
		Collections.sort(avgSalesPriceSortedDescendingExpected, Collections.reverseOrder());
		softAssert.assertEquals(avgSalesPriceSortedDescendingUi, avgSalesPriceSortedDescendingExpected,  "** Avg Sales Prices are not in descending order. UI: " +avgSalesPriceSortedDescendingUi+" - Expected: " +avgSalesPriceSortedDescendingExpected);

		softAssert.assertAll();

		logger.info("** Execution for test case " + tcId + " completed successfully");   

	}
	
	@TmsLink("150489")
	@Test(priority=12, dataProvider = "C150489", dataProviderClass = SearchDataProvider.class, description = "Verify that user can use Clear button to remove selected filters")
	public void Search_QueryTrends_C150489_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
		List<String> filterValues = new ArrayList<>();
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setSearchTerm(searchTerm);
		qtp.setTermInTitle(termInTitleSearch);
		
		logger.info("** Getting values in filters");
		filterValues.add(pg.getAttribute(QueryTrendsPage.countryDepartmentDd, "value", "Query Trends - Country-Department dropdown"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.sfrLwFilterTextbox, "value", "Query Trends - SFR-LW FILTER textbox"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.searchTermTextbox, "value", "Query Trends - Search Term textbox"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.termInTitleTextbox, "value", "Query Trends - Term in Title Search textbox"));
		
		softAssert.assertTrue(filterValues.size() == 4, "** All 4 filters expected to be populated, but only " +filterValues.size() + " were populated. Filters populated: " + filterValues);

		qtp.clearBtn();
		filterValues.clear();
		
		logger.info("** Getting values in filters after clearing");
		filterValues.add(pg.getAttribute(QueryTrendsPage.countryDepartmentDd, "value", "Query Trends - Country-Department dropdown"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.sfrLwFilterTextbox, "value", "Query Trends - SFR-LW FILTER textbox"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.searchTermTextbox, "value", "Query Trends - Search Term textbox"));
		filterValues.add(pg.getAttribute(QueryTrendsPage.termInTitleTextbox, "value", "Query Trends - Term in Title Search textbox"));
		
		for (String filter : filterValues) {
			softAssert.assertTrue(filter.isEmpty(), "** Filter expected to be cleared, but value available." + " Filter: " + filter);
		}
				
		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
	}
	
	
	@TmsLink("150477")
	@Test(priority=13, dataProvider = "C150477", dataProviderClass = SearchDataProvider.class, description = "Verify that pagination aligned with SFR-LW filter")
	public void Search_QueryTrends_C150477_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		CommonMethods common = new CommonMethods(driver);
		SoftAssert softAssert = new SoftAssert();

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn();
		
		logger.info("** Getting page size and page count");
		String pageSize = pg.getText(QueryTrendsPage.pageSizeTxt, "Query Trends - Page size text in dropdown");
		BigDecimal pageCount = (common.stringToBigDecimal(sfrLwFilter).divide(new BigDecimal(Integer.parseInt(pageSize))).setScale(0, RoundingMode.CEILING));
		
		qtp.verifyCurrentAndNextPagePaginationAndResultsTexts(sfrLwFilter, pageSize, pageCount, softAssert);
		qtp.verifyPreviousPagePaginationAndResultsTexts(sfrLwFilter, pageSize, pageCount, softAssert);
		qtp.verifyLastPagePaginationAndResultsTexts(sfrLwFilter, pageSize, pageCount, softAssert);
		qtp.verifyFirstPagePaginationAndResultsTexts(sfrLwFilter, pageSize, pageCount, softAssert);

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("150486")
	@Test(priority=13, dataProvider = "C150486", dataProviderClass = SearchDataProvider.class, description = "Verify that user can not select multiple BUs")
	public void Search_QueryTrends_C150486_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		SoftAssert softAssert = new SoftAssert();
		List<String> businessUnits = new ArrayList<>();
		List<WebElement> businessUnitElements;

		lp.loginTemp("fila");
		lp.openSearchScreen("Query Trends");
		
		logger.info("** Openning business unit selection dropdown and setting " + businessUnit + " to filter");						
		pg.click(LoginPage.businessUnitSelectionDdArrow, "Business unit selection dropdown arrow");
		pg.click(LoginPage.businessUnitSelectionClearBtn, "Business unit selection Clear button");
		pg.setText(LoginPage.businessUnitSelectorFilter, businessUnit, "Businiess unit selector filter textbox");
		
		logger.info("** Getting all business units available");						
		businessUnitElements = pg.findElementsVisible(LoginPage.businessUnitsInDropdown, "Business units in selection dropdown");
		businessUnits = new ArrayList<>();
		
		for (WebElement el : businessUnitElements) {
			businessUnits.add(el.getText());
		}
		
		logger.info("** Selectig each business unit. Business units: " + businessUnits);						
		for (String busUnit : businessUnits) {
			pg.click("//h6[text()='"+ busUnit +"']/ancestor::li", "One of the options in BU selection dropdown: " + busUnit);
			Thread.sleep(300);
		}
		
		logger.info("** Checking to see if multiple business unites can be selected");						
		for (int i = 0; i < businessUnits.size(); i++) {
			String busUnit = businessUnits.get(i);
			if (i == businessUnits.size() -1) {
				softAssert.assertTrue(pg.findElementPresent("//h6[text()='"+busUnit+"']/ancestor::li//input", "Business unit checkbox: " + busUnit).isSelected(), "** Only one business unit can be selected. '" + busUnit + "' is exected to be the only one selected, but found not selected.");
			} else {
				softAssert.assertFalse(pg.findElementPresent("//h6[text()='"+busUnit+"']/ancestor::li//input", "Business unit checkbox: " + busUnit).isSelected(), "** Only one business unit can be selected. '" + busUnit + " is expected to not be selected, but found selected.'");
			}		}

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
	
	@TmsLink("148007")
	@Test(priority=10, dataProvider = "C148007", dataProviderClass = SearchDataProvider.class, description = "UI: Ensure calculations on Search Terms report are correct")
	public void Search_QueryTrends_C148007_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
        SearchDBFunctions sdb = new SearchDBFunctions();
        Page pg = new Page(driver);
        
		List<Integer> sfrLwListUi = new ArrayList<>();
		List<Integer> sfr4wkAgoListUi = new ArrayList<>();
		List<Integer> sfrLatestWeek;
		List<Integer> sfrFourWeeksAgo;
		List<String> sfrRowList = new ArrayList<>();
        Map<String, String> searchTermSfrLwMap;
        Map<String, String> searchTermSfr4WkAgoMap;
		List<String> startDatesForSearchTerms;
				
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.setSearchTerm(searchTerm);
		qtp.selectPageSize("100");
		qtp.saveBtn(); 
		
		logger.info("** Getting start dates for the test");
		startDatesForSearchTerms = sdb.getStartDayForSearchTermsWeeklyFromDb();
		
		logger.info("** Getting random search term from UI to check SFR-LW against DB");
		int elementIndex = new Random().nextInt(10 - 2) + 2;
		String randomSearchTerm = pg.findElementVisible("//div[@class='ag-pinned-left-cols-container' and @role='rowgroup']/div[@aria-rowindex='"+elementIndex+"']/div[@aria-colindex='2']", "").getText();
		String sfrForSearchTermDb = sdb.getSfrLwForSearchTerm(randomSearchTerm, startDatesForSearchTerms.get(0));
		String sfrForSearchTermUi = pg.findElementVisible("//div[@class='ag-center-cols-container' and @role='rowgroup']//div[@aria-rowindex='"+elementIndex+"']/div[@aria-colindex='4']", "").getText();
		softAssert.assertEquals(sfrForSearchTermUi, sfrForSearchTermDb, "** SFR-LW UI vs DB values do not match for search term '"+sfrForSearchTermUi+"'. UI: " + sfrForSearchTermUi + " - DB: " + sfrForSearchTermDb);

		logger.info("** Getting SFR-LWs from UI");
		sfrLwListUi =	qtp.sortSfrLwInAscOrDescOrder();
		Collections.sort(sfrLwListUi);
		
		logger.info("** Getting SFR-LWs from DB");
		searchTermSfrLwMap = sdb.getSearchTermAndSfrWeeklyFromDb(searchTerm, sfrLwFilter, startDatesForSearchTerms.get(0));
		sfrLatestWeek = qtp.getSfrLwDb(searchTermSfrLwMap);
		
		logger.info("** Comparing SFR-LW UI vs DB");
		softAssert.assertEquals(sfrLwListUi, sfrLatestWeek, "** SFR-LWs on UI and DB do not match. UI: " + sfrLwListUi + " - DB: " + sfrLatestWeek);

		logger.info("** Getting SFR 4Wk Ago from UI");
		sfr4wkAgoListUi = qtp.sortSfrFourWkAgoInAscOrDescOrder();
		Collections.sort(sfr4wkAgoListUi);
		
		logger.info("** Getting SFR 4Wk Ago from DB");
		searchTermSfr4WkAgoMap = sdb.getSearchTermAndSfrWeeklyFromDb(searchTerm, sfrLwFilter, startDatesForSearchTerms.get(4));
		sfrFourWeeksAgo = qtp.getSfr4wkAgoDb(searchTermSfrLwMap, searchTermSfr4WkAgoMap);

		logger.info("** Comparing SFR 4Wk Ago UI vs DB");
		softAssert.assertEquals(sfr4wkAgoListUi, sfrFourWeeksAgo, "** SFR 4Wk Agos on UI and DB do not match. UI: " + sfr4wkAgoListUi + " - DB: " + sfrFourWeeksAgo);

		logger.info("** Findind a row with all SFR values available");
		sfrRowList = qtp.getRowWithSfrValuesUi(searchTermSfrLwMap);
		
		logger.info("** Calculating SFR-SFR 4Wk Ago and SFR-SFR LY values");
		int sfrLw = Integer.parseInt(sfrRowList.get(0));
		int sfrLw_sfr4wkAgoUi = Integer.parseInt(sfrRowList.get(1));
		int sfr4wkAgo = Integer.parseInt(sfrRowList.get(2));
		int sfrLw_sfrLyUi = Integer.parseInt(sfrRowList.get(3));
		int sfrLy = Integer.parseInt(sfrRowList.get(4));
		int sfrLw_sfr4wkAgo = sfrLw - sfr4wkAgo;
		int sfrLw_sfrLy = sfrLw - sfrLy ;

		softAssert.assertEquals(sfrLw_sfr4wkAgoUi, sfrLw_sfr4wkAgo, "** SFRLW-SFR 4WK AGO calculation is wrong. Expected: " + sfrLw_sfr4wkAgo + " - Actual: " + sfrLw_sfr4wkAgoUi);
		softAssert.assertEquals(sfrLw_sfrLyUi, sfrLw_sfrLy, "** SFRLW-SFR LY calculation is wrong. Expected: " + sfrLw_sfrLy + " - Actual: " + sfrLw_sfrLyUi);

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
				
	}
	
	@TmsLink("148013")
	@Test(priority=13, dataProvider = "C148013", dataProviderClass = SearchDataProvider.class, description = "UI & DB: Avg Rating, Reviews and Price matching for UI and DB")
	public void Search_QueryTrends_C148013_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SearchDBFunctions sdb = new SearchDBFunctions();
		SoftAssert softAssert = new SoftAssert();
		
		Map<String, List<String>> searchTermAvgsMap;
		List<String> avgRatingReviewPriceForSearchTerm;
		List<String> uiAverages;
		String searchTermFromUi;
		String firstDayOfWeek;
		String lastDayOfWeek;

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSearchScreen("Query Trends");	
		qtp.selectCountryDepartment(countryDepartment);
		qtp.setSfrLwFilter(sfrLwFilter);
		qtp.saveBtn();
		
		pg.scrollRightToElement(QueryTrendsPage.horizontalLoadingBar, QueryTrendsPage.avgSalesPriceColumnHeader, 5, 70, "Query Trends - Avg. Sales Column Header").click();
		Thread.sleep(500);
		pg.waitForElementToBeInvisible("//div[@id='query-trends']//span[@role='progressbar']", 120, "");
		
		logger.info("** Getting a keyword from UI with avg rating, review and price");
		searchTermAvgsMap = qtp.getKeywordAvgRatingReviewPrice();
		searchTermFromUi = new ArrayList<>(searchTermAvgsMap.keySet()).get(0);
		uiAverages = searchTermAvgsMap.get(searchTermFromUi);
		
		logger.info("** Getting date range");
		firstDayOfWeek = sdb.getStartDayForSearchTermsWeeklyFromDb().get(0);
		lastDayOfWeek = qtp.getLastDayOfWeek(firstDayOfWeek);
		
		logger.info("** Getting keyword avarages from DB to compare");
		avgRatingReviewPriceForSearchTerm = sdb.getAvgRatingReviewPriceForSearchTerm(searchTermFromUi, firstDayOfWeek, lastDayOfWeek);
		
		softAssert.assertEquals(uiAverages, avgRatingReviewPriceForSearchTerm, "** AVG UI vs DB values do not match for searc term '"+ searchTermFromUi +"'. UI: " + uiAverages + " - DB: " + avgRatingReviewPriceForSearchTerm);

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");
	}
	
	@TmsLink("150487")
	@Test(priority=100, dataProvider = "C150487", dataProviderClass = SearchDataProvider.class, description = "Verify that user should have access to Query Trends in order to view the screen")
	public void Search_QueryTrends_C150487_Test(String tcId, String testDescription, String businessUnit, String client, String searchTerm, String sfrLwFilter, String termInTitleSearch, String countryDepartment, String gridColTitles, String defaultTextGridExpected) throws Exception {

		logger.info("** Query Trends test case "+tcId + " has started.");

		LoginPage lp = new LoginPage(driver);
		Page pg = new Page(driver);
		QueryTrendsPage qtp = new QueryTrendsPage(driver);
		SoftAssert softAssert = new SoftAssert();
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		qtp.limitAccessToQueryTrendsPage();
		lp.loadDcScreen("home");
				
		logger.info("** Hovering over Search dropdown");
		lp.hoverOverElement(QueryTrendsPage.navigationSearchDd, "Navigation bar - Search dropdown");
		
		logger.info("** Checking if Query Trends is available as an option in the Search dropdown");
		softAssert.assertFalse(pg.elementVisible("//ul[@role='menu']/a[text()='Query Trends']", 5), "** Access limited to Query Trends, but user able see it as option in Search dropdown");
		
		lp.loadDcScreen("querytrends");
		
		logger.info("** Checking if 'Access Denied' text available on Query Trend page");
		softAssert.assertTrue(pg.elementVisible("//h4[contains(text(), 'Access Denied')]", 10), "** 'Access Denied' text expected when Query Trends url loaded directly, but not found");
		
		qtp.grantAccessToQueryTrendsPage();

		lp.loadDcScreen("home");
		
		logger.info("** Hovering over Search dropdown");
		lp.hoverOverElement(QueryTrendsPage.navigationSearchDd, "Navigation bar - Search dropdown");
		
		logger.info("** Checking if Query Trends is available as an option in the Search dropdown");
		softAssert.assertTrue(pg.elementVisible("//ul[@role='menu']/a[text()='Query Trends']", 10), "** Access granted to Query Trends, but user not able see it as option in Search dropdown");

		logger.info("** Clicking on Query Trends option");
		pg.click(QueryTrendsPage.queryTrendInNavigationDdOptions, "Navigation bar - Query Trends option in Search dropdown");
		
		logger.info("** Checking if Query Trends page loaded");
		softAssert.assertTrue(pg.elementVisible(QueryTrendsPage.queryTrendsGridTitle, 20), "** Query Trends page may not be loaded. Query Trend title on grid could not be located.");

		softAssert.assertAll();
		logger.info("** Execution for test case " + tcId + " completed successfully");

	}
}
*/