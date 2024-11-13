package com.DC.utilities.apiEngine.models.requests.hub.insights;

import java.util.List;

public class HubInsightsCategoriesFilterExportRequestBody {

	public int page;
	public int pageSize;
	public List<Filters> filters;

	public HubInsightsCategoriesFilterExportRequestBody(int page, int pageSize, List<Filters> filters){
		this.page = page;
		this.pageSize = pageSize;
		this.filters = filters;
	}

	public static class Filters {

	}

}