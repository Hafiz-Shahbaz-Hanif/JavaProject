package com.DC.utilities.apiEngine.models.requests.hub.insights;

import java.util.List;

public class HubInsightsProductChainProgressRequestBody {

	public int page;
	public int pageSize;
	public List<Filters> filters;
	public List<ProductFilters> productFilters;

	public HubInsightsProductChainProgressRequestBody(int page, int pageSize, List<Filters> filters, List<ProductFilters> productFilters){
		this.page = page;
		this.pageSize = pageSize;
		this.filters = filters;
		this.productFilters = productFilters;
	}


	public static class Filters {

	}

	public static class ProductFilters {

	}

}