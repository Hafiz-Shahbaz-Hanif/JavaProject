package com.DC.db.analyze;

public class RoundUpQueries {

    public static String realTimeSalesHourlyData = "select hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and hour = ?\n" +
            "group by hour, dayofyear, year, hourly_rsrc";

    public static String realTimeSalesHourlyDataDay = "select hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "group by hour, dayofyear, year, hourly_rsrc";

    public static String realTimeSalesDaily = "select\n" +
            "distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_daily_rsrc daily_source, sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin                    \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where orm.date_sk = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and dca.clientaccount_bk in (28)\n" +
            "group by bu.businessunit_bk, orm.date_sk, daily_source\n" +
            "order by orm.date_sk";

    public static String dateSpApiDailyAvailable = "select distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_daily_rsrc daily_source, sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca \n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk \n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm \n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and daily_source = ?\n" +
            "--and dca.clientaccount_bk in (28)\n" +
            "group by bu.businessunit_bk, orm.date_sk, daily_source\n" +
            "order by orm.date_sk desc\n" +
            "limit 1";

    public static String dateSpApiWeeklyAvailable = "select distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_weekly_rsrc weekly_source, sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca \n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_weekly orm on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and weekly_source = ?\n" +
            "group by bu.businessunit_bk, orm.date_sk, weekly_source\n" +
            "order by orm.date_sk desc\n" +
            "limit 1";

    public static String dateSpApiMonthlyAvailable = "select distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_monthly_rsrc monthly_rsrc,\n" +
            "sum(orm.orderedunits)   as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_monthly orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where --orm.date_sk between 20230601 and 20231031\n" +
            "asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and monthly_rsrc = ?\n" +
            "group by bu.businessunit_bk, orm.date_sk, monthly_rsrc\n" +
            "order by orm.date_sk desc\n" +
            "limit 1";

    public static String asinIdForProductGrid = "select distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_daily_rsrc daily_rsrc, \n" +
            "asd.asin_id,\n" +
            "sum(orm.orderedunits)   as Ordered_Units,\n" +
            "sum(orm.orderedrevenue) as ordered_revenue\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where orm.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "--and asd.asin_id = 285200\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and daily_rsrc = 'SP API Ingestion'\n" +
            "group by bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_daily_rsrc, asd.asin_id\n" +
            "order by Ordered_Units desc \n" +
            "limit 1";

    public static String realTimeSalesDailyOrderedRevenue = "select \n" +
            "--distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_daily_rsrc daily_rsrc, \n" +
            "--asd.asin_id, asd.asin,\n" +
            "--sum(orm.orderedunits)   as Ordered_Units,\n" +
            "--sum(orm.orderedrevenue) as ordered_revenue, \n" +
            "round(sum(orm.orderedrevenue), 2) as ordered_revenue\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where orm.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.asin_id = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and orm.arap_orderedrevenue_daily_rsrc = ?";

    public static String realTimeSalesDataWeekly = "select distinct bu.businessunit_bk, orm.date_sk, \n" +
            "orm.arap_orderedrevenue_weekly_rsrc weekly_source, \n" +
            "sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca \n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk \n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_weekly orm \n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and weekly_source = 'REAL TIME INGESTION'\n" +
            "group by bu.businessunit_bk, orm.date_sk, weekly_source\n" +
            "order by orm.date_sk desc";

    public static String realTimeSalesDataMonthly = "select distinct bu.businessunit_bk, orm.date_sk, orm.arap_orderedrevenue_monthly_rsrc monthly_rsrc,\n" +
            "sum(orm.orderedunits)   as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_monthly orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and monthly_rsrc = 'REAL TIME INGESTION'\n" +
            "group by bu.businessunit_bk, orm.date_sk, monthly_rsrc\n" +
            "order by orm.date_sk desc";

    public static String realTimeSalesHourlyForSegment = "select \n" +
            "--hour, dayofyear, year,\n" +
            "--hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as ordered_units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate \n" +
            "on hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.fwcustomasinmetadata3 = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?";

    public static String latestDayForFullHourlyData = "select hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk <= ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and hour = 23\n" +
            "group by hour, dayofyear, year, hourly_rsrc\n" +
            "order by year desc, dayofyear desc \n" +
            "limit 1";

    public static String sumOfHourlyData = "select \n" +
            "--hour, dayofyear, year,\n" +
            "--hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as ordered_revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as ordered_units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as average_selling_price\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?";

    public static String totalHourlyRecords = "select count(*)\n" +
            "from (\n" +
            "select asd.asin,\n" +
            "hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "--and asd.asin = 'B0005XN9KK'\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and hour = 23\n" +
            "group by asd.asin, hour, dayofyear, year, hourly_rsrc)";

    public static String totalMultiBuHourlyRecords = "select count(*)\n" +
            "from (\n" +
            "select asd.asin,\n" +
            "hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "--and asd.asin = 'B0005XN9KK'\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk in (?, ?)\n" +
            "--and hour = 23\n" +
            "group by asd.asin, hour, dayofyear, year, hourly_rsrc)";

    public static String realTimeSalesHourlyDataByAsin = "select hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and hour = ?\n" +
            "group by hour, dayofyear, year, hourly_rsrc";

    public static String realTimeSalesMultiBuHourlyDataByAsin = "select hour, dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunitname = ?\n" +
            "and hour = ?\n" +
            "group by hour, dayofyear, year, hourly_rsrc";

    public static String totalHourlyRecordsByAsin = "select count(*)\n" +
            "from (\n" +
            "select \n" +
            "asd.asin,\n" +
            "--hour, dayofyear, year,\n" +
            "--hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "--and asd.asin = 'B00B4IF2HI'\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and hour = 7\n" +
            "group by asd.asin\n" +
            "--, hour, dayofyear, year, hourly_rsrc\n" +
            ")";

    public static String realTimeSalesDailyHourlyDataByAsin = "select \n" +
            "asd.asin,\n" +
            "--hour, dayofyear, year,\n" +
            "--hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and hour = 7\n" +
            "group by asd.asin";

    public static String totalMultiBuHourlyRecordsByAsin = "select count(*)\n" +
            "from (\n" +
            "select \n" +
            "asd.asin,\n" +
            "--hour, dayofyear, year,\n" +
            "--hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "--and asd.asin = 'B00B4IF2HI'\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk in (?, ?)\n" +
            "--and hour = 7\n" +
            "group by asd.asin\n" +
            "--, hour, dayofyear, year, hourly_rsrc\n" +
            ")";

    public static String realTimeSalesMultiBuFullDayHourlyDataByAsin = "select dayofyear, year,\n" +
            "hourlySales.ara_salesdiagnostic_hourly_rsrc hourly_rsrc, \n" +
            "ROUND(SUM(hourlySales.orderedrevenue), 2) as revenue,\n" +
            "ROUND(SUM(hourlySales.orderedunits), 2) as units,\n" +
            "ROUND(SUM(hourlySales.orderedrevenue) / nullif(SUM(hourlySales.orderedunits), 0),\n" +
            "2) as averagesellingprice\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_ara_api_salesdiagnostic_hourly hourlySales\n" +
            "on hourlySales.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "join vc.dim_date as dimDate on\n" +
            "hourlySales.date_sk = dimDate.date_sk\n" +
            "where hourlySales.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and hourlySales.iscurrent = true\n" +
            "and hourlySales.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunitname = ?\n" +
            "group by dayofyear, year, hourly_rsrc";

    public static String totalDailyRecords = "select count(*)\n" +
            "from (\n" +
            "select bu.businessunit_bk, orm.date_sk, asd.asin, orm.arap_orderedrevenue_daily_rsrc daily_source, sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin                    \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where orm.date_sk between ? and ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and daily_source = ?\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and dca.clientaccount_bk in (28)\n" +
            "group by bu.businessunit_bk, orm.date_sk, daily_source, asd.asin)";

    public static String realTimeSalesDailyDataByAsin = "select bu.businessunit_bk, orm.date_sk, asd.asin, orm.arap_orderedrevenue_daily_rsrc daily_source, sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin                    \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where orm.date_sk = ?\n" +
            "and asd.asin = ?\n" +
            "and asd.fwcustomasinmetadata7 is null\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and daily_source = ?\n" +
            "--and dca.clientaccount_bk in (28)\n" +
            "group by bu.businessunit_bk, orm.date_sk, daily_source, asd.asin";

    public static String totalWeeklyRecords = "select count(*)\n" +
            "from (\n" +
            "select bu.businessunit_bk, orm.date_sk, asd.asin,\n" +
            "orm.arap_orderedrevenue_weekly_rsrc weekly_source, \n" +
            "sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca \n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk \n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_weekly orm \n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where  asd.fwcustomasinmetadata7 is null\n" +
            "and orm.date_sk between ? and ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and weekly_source = 'REAL TIME INGESTION'\n" +
            "group by bu.businessunit_bk, orm.date_sk, weekly_source, asd.asin)";

    public static String realTimeSalesWeeklyDataByAsin = "select bu.businessunit_bk, orm.date_sk, asd.asin,\n" +
            "orm.arap_orderedrevenue_weekly_rsrc weekly_source, \n" +
            "sum(orm.orderedunits) as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu \n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca \n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin \n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca \n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk \n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_weekly orm \n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where  asd.fwcustomasinmetadata7 is null\n" +
            "and orm.date_sk = ?\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and weekly_source = ?\n" +
            "group by bu.businessunit_bk, orm.date_sk, weekly_source, asd.asin";

    public static String totalMonthlyRecords = "select count(*)\n" +
            "from (\n" +
            "select bu.businessunit_bk, orm.date_sk, asd.asin,\n" +
            "orm.arap_orderedrevenue_monthly_rsrc monthly_rsrc,\n" +
            "sum(orm.orderedunits)   as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_monthly orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and orm.date_sk between ? and ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "--and monthly_rsrc = ?\n" +
            "group by bu.businessunit_bk, orm.date_sk, monthly_rsrc, asd.asin)";

    public static String realTimeSalesMonthlyDataByAsin = "select bu.businessunit_bk, orm.date_sk, asd.asin,\n" +
            "orm.arap_orderedrevenue_monthly_rsrc monthly_rsrc,\n" +
            "sum(orm.orderedunits)   as Ordered_Units\n" +
            "from fw.populate_asin_segmentation_daily asd\n" +
            "join vc.dim_businessunit bu\n" +
            "on bu.businessunit_bk = asd.business_unit_id\n" +
            "join vc.dim_clientaccount dca\n" +
            "on dca.clientaccount_bk = asd.client_account_id\n" +
            "join vc.dim_asin dasin\n" +
            "on dasin.asin_bk = asd.asin\n" +
            "join vc.link_clientaccount_asin lca\n" +
            "on dca.clientaccount_sk = lca.clientaccount_sk\n" +
            "and dasin.asin_sk = lca.asin_sk\n" +
            "join vc.fact_arap_api_salesdiagnostic_orderedrevenue_monthly orm\n" +
            "on orm.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
            "where asd.fwcustomasinmetadata7 is null\n" +
            "and orm.date_sk = ?\n" +
            "and asd.asin = ?\n" +
            "and dasin.isactive = true\n" +
            "and dca.isactive = true\n" +
            "and lca.isactive = true\n" +
            "and bu.isactive = true\n" +
            "and orm.iscurrent = true\n" +
            "and orm.sellingprogramname = 'AMAZON_RETAIL'\n" +
            "and bu.businessunit_bk = ?\n" +
            "and monthly_rsrc = ?\n" +
            "group by bu.businessunit_bk, orm.date_sk, monthly_rsrc, asd.asin";


}