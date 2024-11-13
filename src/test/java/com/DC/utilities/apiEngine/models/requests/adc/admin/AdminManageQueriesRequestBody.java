package com.DC.utilities.apiEngine.models.requests.adc.admin;


import org.apache.log4j.Logger;

import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;

public class AdminManageQueriesRequestBody {

	public PagingAttributes pagingAttributes;
	public int businessUnitId;
	public int userId;
	public String keyword; 
	public boolean externalFacingQuery;

	public AdminManageQueriesRequestBody(
			PagingAttributes pagingAttributes, 
			int businessUnitId,
			int userId,
			String keyword,
			boolean externalFacingQuery

			) 
	{
		this.pagingAttributes = pagingAttributes;
		this.businessUnitId = businessUnitId;
		this.userId = userId;
		this.keyword = keyword;		
		this.externalFacingQuery = externalFacingQuery;	
		Logger.getLogger(ReportingDashboardRequestBody.class).info("** Serializing request body for Manage Queries"); 
	}


	public static class PagingAttributes{

		public String orderAttribute;

		public PagingAttributes(String orderAttribute) {
			this.orderAttribute = orderAttribute;		
		}

	}

}
