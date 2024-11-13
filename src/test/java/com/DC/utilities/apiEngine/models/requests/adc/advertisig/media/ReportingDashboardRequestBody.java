package com.DC.utilities.apiEngine.models.requests.adc.advertisig.media;

import java.util.List;
import org.apache.log4j.Logger;

public class ReportingDashboardRequestBody {

	public String interval;
	public String projection;
	public String instacartDisplaySaleMetricType; 
	public List<String> saleMetrics;
	public String startDate;
	public String endDate;
	public int businessUnitId;
	public String platform;
	public List<String> retailers;
	public String attribution;

	public ReportingDashboardRequestBody(
			String interval, 
			String projection,
			String instacartDisplaySaleMetricType, 
			List<String> saleMetrics,
			String startDate,
			String endDate,
			int businessUnitId,
			String platform,
			List<String> retailers,
			String attribution
			) 
	{
		this.interval = interval;
		this.projection = projection;
		this.instacartDisplaySaleMetricType = instacartDisplaySaleMetricType;		
		this.saleMetrics = saleMetrics;
		this.startDate = startDate;
		this.endDate = endDate;
		this.businessUnitId = businessUnitId;
		this.platform = platform;
		this.retailers = retailers;
		this.attribution = attribution;
		Logger.getLogger(ReportingDashboardRequestBody.class).info("** Serializing request body for Reporting Dashboard"); 
	}
	
}