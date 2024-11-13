package com.DC.utilities.hub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomepageReports {

    public class Identify {
        List<String> osSearchInsights = new ArrayList<>(List.of("Attribute Insights", "Category Brandshare", "Keyword Search", "Keyword Watchlists"));
        List<String> filaSearchInsights = new ArrayList<>(List.of("Brand Share of Voice", "Frequency, Clicks & Conversion Share", "Query Share of Voice"));
        List<String> msSearchInsights;
        List<String> connectSearchInsights;
        List<String> netNewSearchInsights = new ArrayList<>(List.of("Search Frequency Rank", "Share of Voice")) ;

        List<String> osExecutiveDashboard;
        List<String> filaExecutiveDashboard;
        List<String> msExecutiveDashboard;
        List<String> connectExecutiveDashboardUnrestrictedUser = new ArrayList<>(List.of("At a Glance", "Notes", "Overview", "Scratchpad", "Section Details"));
        List<String> connectExecutiveDashboardRestrictedUser = new ArrayList<>(List.of("At a Glance", "Overview", "Scratchpad", "Section Details"));
        List<String> netNewExecutiveDashboard;

        List<String> osSalesShare;
        List<String> filaSalesShare;
        List<String> msSalesShare = new ArrayList<>(List.of("Conversion", "Market View", "My Business", "Traffic"));
        List<String> connectSalesShare;
        List<String> netNewSalesShare;

        public List<String> getNetNewSearchInsights() {
            Collections.sort(netNewSearchInsights);
            return netNewSearchInsights;
        }

        public List<String> getMsSalesShare() {
            Collections.sort(msSalesShare);
            return msSalesShare;
        }

        public List<String> getConnectExecutiveDashboardUnrestrictedUser() {
            Collections.sort(connectExecutiveDashboardUnrestrictedUser);
            return connectExecutiveDashboardUnrestrictedUser;
        }

        public List<String> getConnectExecutiveDashboardRestrictedUser() {
            Collections.sort(connectExecutiveDashboardRestrictedUser);
            return connectExecutiveDashboardRestrictedUser;
        }

        public List<String> getOsNetNewSearchInsights() {
            List<String> osNetNewSearchInsights = new ArrayList<>(osSearchInsights);
            osNetNewSearchInsights.addAll(netNewSearchInsights);
            Collections.sort(osNetNewSearchInsights);
            return osNetNewSearchInsights;
        }
    }

    public class Analyze {
        List<String> osPaidMediaReporting;
        List<String> filaPaidMediaReporting = new ArrayList<>(List.of("Download Manager", "DSP Funnel Report", "DSP Product Report", "DSP Video Dashboard", "Executive Dashboard", "Media Scratchpad", "Multiplatform View", "Reporting Dashboard", "Sponsored Ad Placements", "Stream Dashboard"));
        List<String> msPaidMediaReporting = new ArrayList<>(); //List.of("Share & Ad Spend")
        List<String> connectPaidMediaReporting;
        List<String> netNewPaidMediaReporting;

        List<String> osRetailReporting;
        List<String> filaRetailReporting = new ArrayList<>(List.of("ASIN Detail", "Case Management Reporting", "PDP Change Dashboard", "PO & Inventory Dashboard", "Retail Gainers & Drainers", "Retail Scratchpad", "Round Up", "Sales Correlation Model"));
        List<String> msRetailReporting;
        List<String> connectRetailReporting;
        List<String> netNewRetailReporting;
        List<String> filaRetailReportingForAllFilaUsers = new ArrayList<>(List.of("Retail Executive Dashboard"));

        List<String> osProductHealth = new ArrayList<>(List.of("Product Auditor"));
        List<String> filaProductHealth = new ArrayList<>(List.of("Availability & Price"));
        List<String> msProductHealth = new ArrayList<>(List.of("Promotion", "Ratings & Reviews")); //"Profitability"
        List<String> connectProductHealth;
        List<String> netNewProductHealth = new ArrayList<>(List.of("Availability", "Price"));

        List<String> osMarketingCloudAnalytics;
        List<String> filaMarketingCloudAnalytics = new ArrayList<>(List.of("Lifetime Value", "New to Brand Dashboard", "Path to Purchase"));
        List<String> msMarketingCloudAnalytics;
        List<String> connectMarketingCloudAnalytics;
        List<String> netNewMarketingCloudAnalytics;

        List<String> osSearchReporting;
        List<String> filaSearchReporting = new ArrayList<>(List.of("Query Trends"));
        List<String> msSearchReporting = new ArrayList<>(); // List.of("Keyword Trends")
        List<String> connectSearchReporting;
        List<String> netNewSearchReporting = new ArrayList<>(List.of("Search Rank"
                //     , "SFR Click & Conversion Share"
        ));

        List<String> osDataAsService;
        List<String> filaDataAsService = new ArrayList<>(List.of("Data as a Service"));
        List<String> msDataAsService;
        List<String> connectDataAsService;
        List<String> netNewDataAsService;

        public List<String> getMsPaidMediaReporting() {
            Collections.sort(msPaidMediaReporting);
            return msPaidMediaReporting;
        }

        public List<String> getFilaRetailReportingForAllFilaUsers() {
            Collections.sort(filaRetailReportingForAllFilaUsers);
            return filaRetailReportingForAllFilaUsers;
        }

        public List<String> getNetNewProductHealth() {
            Collections.sort(netNewProductHealth);
            return netNewProductHealth;
        }

        public List<String> getNetNewSearchReporting() {
            Collections.sort(netNewSearchReporting);
            return netNewSearchReporting;
        }

        public List<String> getMsNetNewSearchReporting() {
            List<String> msNetNewSearchInsights = new ArrayList<>(netNewSearchReporting);
            msNetNewSearchInsights.addAll(msSearchReporting);
            Collections.sort(msNetNewSearchInsights);
            return msNetNewSearchInsights;
        }

        public List<String> getMsNetNewProductHealth() {
            List<String> msNetNewSearchInsights = new ArrayList<>(netNewProductHealth);
            msNetNewSearchInsights.addAll(msProductHealth);
            Collections.sort(msNetNewSearchInsights);
            return msNetNewSearchInsights;
        }
    }

    public class Execute {
        List<String> osMediaManagement;
        List<String> filaMediaManagement = new ArrayList<>(List.of("Advertised (Other) ASINs", "Air Traffic Control", "Alerts", "Budget Forecasting", "Budget Manager", "Campaign Segmentation", "DSP Control Panel", "Eligibility Tracker", "Ensemble", "Financial Protection", "FlightDeck", "Incrementality Recommendations", "Incrementality Reporting", "Intraday Multipliers", "Rule-Based Bidding", "Sponsored Products Genius Dashboard"));
        List<String> msMediaManagement;
        List<String> connectMediaManagement;
        List<String> netNewMediaManagement;

        List<String> osContentOptimization = new ArrayList<>(List.of("Configure Reports", "Content Analyzer", "Product Rank Tracking", "Report History", "Task History", "Task UI Mapping", "Tasks"));
        List<String> filaContentOptimization;
        List<String> msContentOptimization;
        List<String> connectContentOptimization;
        List<String> netNewContentOptimization;

        List<String> osIntegrations = new ArrayList<>(List.of("API Administration", "Destination Manager", "Destination Setup", "Publish Set Manager"));
        List<String> filaIntegrations;
        List<String> msIntegrations;
        List<String> connectIntegrations;
        List<String> netNewIntegrations;

        List<String> osProductManager = new ArrayList<>(List.of("Campaigns", "Imports", "Product History", "Product Lists", "Products", "Properties", "Retailer Requirements"));
        List<String> filaProductManager;
        List<String> msProductManager;
        List<String> connectProductManager;
        List<String> netNewProductManager;

        List<String> osRetailManagement;
        List<String> filaRetailManagement = new ArrayList<>(List.of("Case Management Creation", "PO Golden Data", "Product Details", "Retail: ASIN Segmentation"));
        List<String> msRetailManagement;
        List<String> connectRetailManagement;
        List<String> netNewRetailManagement;

        public List<String> getOsProductManager() {
            Collections.sort(osProductManager);
            return osProductManager;
        }
    }

    public class Manage {

        List<String> osDataManagement = new ArrayList<>(List.of("Category Associations", "Coverage Reporting", "Data Administration", "Keyword Segmentation"));
        List<String> filaDataManagement = new ArrayList<>(List.of("Budget Management", "Ensemble Management", "Financial Protection Configuration", "Manage Queries", "Query Approval"));
        List<String> msClientAdminDataManagement = new ArrayList<>(List.of("Catalog Download", "Saved Filters", "Catalog Classifier", "Toolbox API", "Toolbox UI")); //"Report Sharing"
        List<String> msReportsUserDataManagement = new ArrayList<>(List.of("Catalog Classifier", "Toolbox API", "Toolbox UI"));
        List<String> connectDataManagement = new ArrayList<>(List.of("Manage Exec Dash Data"));
        List<String> netNewDataManagement = new ArrayList<>(List.of("Search Term Management"));

        public List<String> getNetNewDataManagement() {
            Collections.sort(netNewDataManagement);
            return netNewDataManagement;
        }

        public List<String> getMsClientAdminNetNewDataManagement() {
            List<String> msClientAdminNetNewDataManagement = new ArrayList<>(netNewDataManagement);
            msClientAdminNetNewDataManagement.addAll(msClientAdminDataManagement);
            Collections.sort(msClientAdminNetNewDataManagement);
            return msClientAdminNetNewDataManagement;
        }

        public List<String> getMsReportsUserNetNewDataManagement() {
            List<String> msReportsUserNetNewDataManagement = new ArrayList<>(netNewDataManagement);
            msReportsUserNetNewDataManagement.addAll(msReportsUserDataManagement);
            Collections.sort(msReportsUserNetNewDataManagement);
            return msReportsUserNetNewDataManagement;
        }

        public List<String> getConnectNetNewDataManagement() {
            List<String> msConnectNetNewDataManagement = new ArrayList<>(netNewDataManagement);
            msConnectNetNewDataManagement.addAll(connectDataManagement);
            Collections.sort(msConnectNetNewDataManagement);
            return msConnectNetNewDataManagement;
        }
    }

    public class UserManagement {

        List<String> osSupportUserClientAdminPages = new ArrayList<>(List.of("Insights User Management"));
        List<String> osClientManagementPages = new ArrayList<>(List.of("API Credentials", "Notifications", "Profile Information", "Request Support"));

        List<String> filaAdminUserClientAdminPages = new ArrayList<>(List.of("DC App User Management", "DC App Role Management", "DC App Client Management"));
        List<String> filaClientManagementPages = new ArrayList<>(List.of("Notifications", "Profile Information", "Request Support"));

        List<String> connectClientManagementPages = new ArrayList<>(List.of("Profile Information", "Request Support"));


        public List<String> getFilaAdminUserClientAdminPages() {
            Collections.sort(filaAdminUserClientAdminPages);
            return filaAdminUserClientAdminPages;
        }

        public List<String> getFilaClientManagementPages() {
            Collections.sort(filaClientManagementPages);
            return filaClientManagementPages;
        }

        public List<String> getOsSupportUserClientAdminPages() {
            Collections.sort(osSupportUserClientAdminPages);
            return osSupportUserClientAdminPages;
        }

        public List<String> getOsClientManagementPages() {
            Collections.sort(osClientManagementPages);
            return osClientManagementPages;
        }

        public List<String> getConnectClientManagementPages() {
            Collections.sort(connectClientManagementPages);
            return connectClientManagementPages;
        }

        public List<String> getMsClientManagementPages() {
            return getOsClientManagementPages();
        }

    }


}