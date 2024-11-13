package com.DC.utilities.apiEngine.models.requests.adc.catalog.search;

import java.util.List;

import org.apache.log4j.Logger;

import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;

public class SearchOfVoiceRequestBody {

	public int frequency;
	public boolean brandSov;
	public DateRange dateRange;
	public String startDate;
	public String endDate;
	public int businessUnitId;
	public String countryCode;
	public String reportType;
	public String platform;
	public String retailer;
	public List<String> placement;
	public List<String> keywords;
	public boolean grouped;
	public String retailerPlatform;
	public String retailerName;
	public String retailerPlatformId;

	public SearchOfVoiceRequestBody(
			int frequency,
			boolean brandSov,
			DateRange dateRange,
			String startDate,
			String endDate,
			int businessUnitId,
			String countryCode,
			String reportType,
			String platform,
			String retailer,
			List<String> placement,
			List<String> keywords,
			boolean grouped,
			String retailerPlatform,
			String retailerName,
			String retailerPlatformId
			) 
	{
		this.frequency = frequency;
		this.brandSov = brandSov;
		this.dateRange = dateRange;
		this.businessUnitId = businessUnitId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.countryCode = countryCode;
		this.reportType = reportType;
		this.platform = platform;
		this.retailer = retailer;
		this.placement = placement;
		this.keywords = keywords;
		this.grouped = grouped;
		this.retailerPlatform = retailerPlatform;
		this.retailerName = retailerName;
		this.retailerPlatformId = retailerPlatformId;
		Logger.getLogger(ReportingDashboardRequestBody.class).info("** Serializing request body for SOV request body"); 
	}

	public static class DateRange {

		public String label;

		public DateRange(String label) {
			this.label = label;
		}

	}

}
