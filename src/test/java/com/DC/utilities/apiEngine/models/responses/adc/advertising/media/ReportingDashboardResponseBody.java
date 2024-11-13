package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.Arrays;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ReportingDashboardResponseBody {

	public String interval;
	public Sum sum;
	public BudgetSummary budgetSummary;
	public List<AmsInfo> amsInfo;
	
	public String getInterval() {
		return interval;
	}
	
	public List<AmsInfo> getAmsInfo() {
		return amsInfo;
	}
	
	public Sum getSum() {
		return sum;
	}
	
	public static class Sum {
		String impressions;
		String clicks;
		String CTR;
		String CPC;
		String SPC;
		String CVR;
		String CPA;
		String CPM;		
		String SPM;
		String sales;
		String spend;	
		String ROAS;
		String conversion;
		String dpv;
		String dpvr;
		String ecpDpv;	
		String ecpm;
		String costPerNewToBrandSale;
		String totalCost;
		String newToBrandPurchases;
		

		public List<String> getSumData(){ 
			return Arrays.asList(impressions, clicks, CTR, CPC, SPC, CVR, CPA, CPM, SPM, sales, spend, ROAS, conversion);
		}
		
		public List<String> getSumDataForCitrusAd(){ 
			return Arrays.asList(impressions, clicks, CTR, CPC, SPC, CVR, CPA, CPM, SPM, sales, spend, ROAS, conversion, dpv, dpvr, ecpDpv, ecpm, costPerNewToBrandSale, totalCost, newToBrandPurchases);
		}
		
	}

	public static class BudgetSummary {
		List<String> seriesInfo;
		String errorMsg;

	}

	public static class AmsInfo {
		String amsUnit;
		List<SeriesInfo> seriesInfo;
		
		public List<SeriesInfo> getSeriesInfo() {
			return seriesInfo;
		}

		public static class SeriesInfo{
			String dateKey;
			String impressions;
			String clicks;
			String CTR;
			String CPC;
			String CPM;
			String SPM;
			String SPC;		
			String spend;
			String sales;
			String ROAS;	
			String conRateClicks;
			String conRateSpend; 
			String conversions;
			String sameSKUSales;
			String sameBrandSales;
			String newToBrandOrders;
		    @SerializedName(value="newToBrandSales", alternate="newToBrandRevenue") 
			String newToBrandSales;
			String newToBrandUnits;
			String totalUnits;
			String newToBrandOrdersPercentage;	
		    @SerializedName(value="newToBrandSalesPercentage", alternate="newToBrandRevenuePercentage")
			String newToBrandSalesPercentage;
			String newToBrandUnitsPercentage;
			String newToBrandOrderRate;	
			String totalCost;
			String ecpm;
			String dpv;
			String ecpDpv;
			String purchases;
			String newToBrandPurchases;
			String newToBrandEcpp;
			String newSubscribeAndSave;
			String percentOfPurchasesNewSubscribeAndSave;
			String ecpNewSubscribeAndSave;
			String totalSales;
			String totalRoas;
			String costPerNewToBrandSale;
			String subscribeAndSaveSale;
			String costPerSubscribeAndSaveSale;
			String dpvr;
			String newToBrandHaloSales;
			String newToBrandDirectSales;
			String newToBrandHaloSalesPercentage;
			String newToBrandDirectSalesPercentage;
			String directSales;
			String haloSales;
			String directROAS;
			String haloROAS;
			String nonDisplaySales;

			public String getDateKey() {
				return dateKey;
			}
			
			public List<String> getDataForInstacart(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales, newToBrandSales, newToBrandHaloSales, newToBrandDirectSales, newToBrandSalesPercentage, newToBrandHaloSalesPercentage,
						newToBrandDirectSalesPercentage, directSales, haloSales, directROAS, haloROAS, nonDisplaySales);
			}
					
			public List<String> getDataForAmazon(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales, newToBrandOrders, newToBrandSales, newToBrandUnits, totalUnits, newToBrandOrdersPercentage,
						newToBrandSalesPercentage, newToBrandUnitsPercentage, newToBrandOrderRate);
			}
			
			public List<String> getDataForWalmart(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales, newToBrandOrders, newToBrandSales, newToBrandUnits, newToBrandOrdersPercentage,
						newToBrandSalesPercentage, newToBrandUnitsPercentage, newToBrandOrderRate);
			}
			
			public List<String> getDataForCriteo(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales);
			}

			public List<String> getDataForDoordash(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales);
			}
			
			public List<String> getDataForPromoteIq(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, 
						conversions, sameSKUSales, sameBrandSales);
			}
			
			public List<String> getDataForCitrusAd(){ 
				return Arrays.asList(dateKey, impressions, clicks, CTR, CPC, CPM, SPM, SPC, spend, sales, ROAS, conRateClicks, conRateSpend, conversions, sameSKUSales, sameBrandSales, 
						totalCost, ecpm, dpv, ecpDpv, purchases, newToBrandPurchases, newToBrandEcpp, newSubscribeAndSave,
						percentOfPurchasesNewSubscribeAndSave, ecpNewSubscribeAndSave, totalSales, totalRoas, costPerNewToBrandSale, subscribeAndSaveSale, costPerSubscribeAndSaveSale, dpvr );
			}
		}

	}

}