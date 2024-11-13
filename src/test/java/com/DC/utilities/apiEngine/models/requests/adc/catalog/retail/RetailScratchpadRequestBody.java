package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.log4j.Logger;

import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;

public class RetailScratchpadRequestBody {

	public PagingAttributes pagingAttributes;
	public boolean showDataStatus;
	public String interval;
	public boolean requiresAsinLevel;
	public boolean isSlicerView;
	public int isValue;
	public String metric;
	public DateRange dateRange;
	public String startDate;
	public String endDate;
	public boolean segmentationFiltersExist;
	public int businessUnitId;
	public String clientCategory;
	public String clientAccountType;
	public boolean isClientsRequired;
	public List<FilterString> filterString;
	public String comparisonType;
	public int viewById;
	public String viewByName;
	public String groupBy;
	public List<String> segmentationFilters;
	public int obsoleteAsinTypeId;
	public boolean requiresProfitability;
	public String obsoleteAsinType;
	public String retailerPlatform;
	public String distributorView;
	public String currencies;
	public List<String> asinIds;


	public RetailScratchpadRequestBody(
			boolean showDataStatus,
			String interval,
			boolean requiresAsinLevel,
			boolean isSlicerView,
			int isValue,
			String metric,
			DateRange dateRange,
			String startDate,
			String endDate,
			boolean segmentationFiltersExist,
			int businessUnitId,
			String clientCategory,
			String clientAccountType,
			boolean isClientsRequired,
			List<FilterString> filterString,
			String comparisonType,
			int viewById,
			String viewByName,
			String groupBy,
			List<String> segmentationFilters,
			int obsoleteAsinTypeId,
			boolean requiresProfitability,
			String retailerPlatform,
			String distributorView
	)
	{

		this.showDataStatus = showDataStatus;
		this.interval = interval;
		this.requiresAsinLevel = requiresAsinLevel;
		this.isSlicerView = isSlicerView;
		this.isValue = isValue;
		this.metric = metric;
		this.dateRange = dateRange;
		this.startDate = startDate;
		this.endDate = endDate;
		this.segmentationFiltersExist = segmentationFiltersExist;
		this.businessUnitId = businessUnitId;
		this.clientCategory = clientCategory;
		this.clientAccountType = clientAccountType;
		this.isClientsRequired = isClientsRequired;
		this.filterString = filterString;
		this.comparisonType = comparisonType;
		this.viewById = viewById;
		this.viewByName = viewByName;
		this.groupBy = groupBy;
		this.segmentationFilters = segmentationFilters;
		this.obsoleteAsinTypeId = obsoleteAsinTypeId;
		this.requiresProfitability = requiresProfitability;
		this.retailerPlatform = retailerPlatform;
		this.distributorView = distributorView;

		Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
	}

	public RetailScratchpadRequestBody(
			PagingAttributes pagingAttributes,
			boolean showDataStatus,
			String interval,
			boolean requiresAsinLevel,
			boolean isSlicerView,
			int isValue,
			String metric,
			DateRange dateRange,
			String startDate,
			String endDate,
			boolean segmentationFiltersExist,
			int businessUnitId,
			String clientCategory,
			String clientAccountType,
			boolean isClientsRequired,
			List<FilterString> filterString,
			String comparisonType,
			int viewById,
			String viewByName,
			String groupBy,
			List<String> segmentationFilters,
			int obsoleteAsinTypeId,
			boolean requiresProfitability,
			String retailerPlatform,
			String distributorView
	)
	{
		this.pagingAttributes = pagingAttributes;
		this.showDataStatus = showDataStatus;
		this.interval = interval;
		this.requiresAsinLevel = requiresAsinLevel;
		this.isSlicerView = isSlicerView;
		this.isValue = isValue;
		this.metric = metric;
		this.dateRange = dateRange;
		this.startDate = startDate;
		this.endDate = endDate;
		this.segmentationFiltersExist = segmentationFiltersExist;
		this.businessUnitId = businessUnitId;
		this.clientCategory = clientCategory;
		this.clientAccountType = clientAccountType;
		this.isClientsRequired = isClientsRequired;
		this.filterString = filterString;
		this.comparisonType = comparisonType;
		this.viewById = viewById;
		this.viewByName = viewByName;
		this.groupBy = groupBy;
		this.segmentationFilters = segmentationFilters;
		this.obsoleteAsinTypeId = obsoleteAsinTypeId;
		this.requiresProfitability = requiresProfitability;
		this.retailerPlatform = retailerPlatform;
		this.distributorView = distributorView;

		Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
	}

	public RetailScratchpadRequestBody(
			PagingAttributes pagingAttributes,
			boolean showDataStatus,
			String interval,
			boolean requiresAsinLevel,
			boolean isSlicerView,
			int isValue,
			String metric,
			DateRange dateRange,
			String startDate,
			String endDate,
			boolean segmentationFiltersExist,
			String clientCategory,
			String clientAccountType,
			boolean isClientsRequired,
			List<FilterString> filterString,
			String comparisonType,
			int viewById,
			String viewByName,
			String groupBy,
			int obsoleteAsinTypeId,
			boolean requiresProfitability,
			String retailerPlatform,
			String distributorView,
			String currencies
	)
	{
		this.pagingAttributes = pagingAttributes;
		this.showDataStatus = showDataStatus;
		this.interval = interval;
		this.requiresAsinLevel = requiresAsinLevel;
		this.isSlicerView = isSlicerView;
		this.isValue = isValue;
		this.metric = metric;
		this.dateRange = dateRange;
		this.startDate = startDate;
		this.endDate = endDate;
		this.segmentationFiltersExist = segmentationFiltersExist;
		this.clientCategory = clientCategory;
		this.clientAccountType = clientAccountType;
		this.isClientsRequired = isClientsRequired;
		this.filterString = filterString;
		this.comparisonType = comparisonType;
		this.viewById = viewById;
		this.viewByName = viewByName;
		this.groupBy = groupBy;
		this.obsoleteAsinTypeId = obsoleteAsinTypeId;
		this.requiresProfitability = requiresProfitability;
		this.retailerPlatform = retailerPlatform;
		this.distributorView = distributorView;
		this.currencies = currencies;

		Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
	}

	public static class DateRange {

		public String label;

		public DateRange(String label) {
			this.label = label;
		}

	}

	public static class FilterString {

		public String label;
		public String value;

		public FilterString(String label, String value) {
			this.label = label;
			this.value = value;
		}

	}

	public static class PagingAttributes {
		public int pageSize;
		public int page;


		public PagingAttributes(int pageSize, int page) {
			this.pageSize = pageSize;
			this.page = page;
		}
	}

}
