package com.DC.db.adc.advertising.media;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.DC.utilities.RedShiftUtility.connection;

public class FlightdeckQueries {

    public static ResultSet executeQuery(String queryName, String date) throws SQLException {
        String queryString;
        switch (queryName) {
            case "queryForAmazonCampaign":
                queryString = FlightdeckQueries.queryForAmazonCampaign;
                break;
            case "queryForAmazonKeywordsByCampaign":
                queryString = FlightdeckQueries.queryForAmazonKeywordsByCampaign;
                break;
            case "queryForAmazonKeywordsRolledUp":
                queryString = FlightdeckQueries.queryForAmazonKeywordsRolledUp;
                break;
            case "queryForAmazonCSQ":
                queryString = FlightdeckQueries.queryForAmazonCSQ;
                break;
            case "queryForAmazonASIN":
                queryString = FlightdeckQueries.queryForAmazonASIN;
                break;
            case "queryForWalmartCampaigns":
                queryString = FlightdeckQueries.queryForWalmartCampaigns;
                break;
            case "queryForWalmartKeywords":
                queryString = FlightdeckQueries.queryForWalmartKeywords;
                break;
            case "queryForWalmartCSQ":
                queryString = FlightdeckQueries.queryForWalmartCSQ;
                break;
            case "queryForWalmartItem":
                queryString = FlightdeckQueries.queryForWalmartItem;
                break;
            case "queryForCriteoItem":
                queryString = FlightdeckQueries.queryForCriteoItem;
                break;
            case "queryForCriteoProduct":
                queryString = FlightdeckQueries.queryForCriteoProduct;
                break;
            case "queryForCitrusAdCampaign":
                queryString = FlightdeckQueries.queryForCitrusAdCampaign;
                break;
            case "queryForInstacartAdGroup":
                queryString = FlightdeckQueries.queryForInstacartAdGroup;
                break;
            case "queryForInstacartCampaign":
                queryString = FlightdeckQueries.queryForInstacartCampaign;
                break;
            case "queryForInstacartKeywords":
                queryString = FlightdeckQueries.queryForInstacartKeywords;
                break;
            case "queryForInstacartKeywordsRolledUp":
                queryString = FlightdeckQueries.queryForInstacartKeywordsRolledUp;
                break;
            default:
                throw new IllegalArgumentException("Invalid query name");
        }
        PreparedStatement pstmt = connection.prepareStatement(queryString);
        pstmt.setString(1, date);

        return pstmt.executeQuery();
    }

    public static String queryForAmazonCampaign = "select spend, clicks, impressions, sales from daas.vw_media_amazon_cmp_daily " +
            "where formatted_date = ? and client_name = 'Hersheys' and country = 'US' order by spend desc limit 5";
    public static String queryForAmazonKeywordsByCampaign = "SELECT spend, clicks, impressions, sales from daas.vw_media_amazon_kw_daily " +
            "where datekey = ? and bu_id = '39' order by spend desc limit 5;";

    public static String queryForAmazonKeywordsRolledUp = "select sum(spend) as spend, sum(sales) as sales, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_amazon_kw_daily " +
            "where iosegment is not NULL and datekey = ? and bu_id = '39' GROUP BY datekey, bu_name, keyword ORDER BY spend DESC limit 5;";

    public static String queryForAmazonASIN = "select sum(spend) as spend, sum(sales) as sales, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_amazon_cmp_daily " +
            "where formatted_date = ? and businessunit_bk = '39' GROUP BY formatted_date, bu_name, platform_product_id ORDER BY spend DESC limit 5";

    public static String queryForAmazonCSQ = "select sum(cost) as spend, sum(attrsales14d) as sales, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_amazon_csq_daily " +
            "where io_segmentation is not NULL and formatted_date = ? and bu_id = 39 GROUP BY formatted_date, bu_id, query ORDER BY spend DESC limit 5;";

    public static String queryForWalmartCampaigns = "select spend, clicks, impressions from daas.vw_media_walmart_cmp_daily " +
            "where formatted_date = ? and bu_id = '39' order by spend desc limit 2";

    public static String queryForWalmartKeywords = "select spend, clicks, impressions from daas.vw_media_walmart_keyword_daily " +
            "where formatted_date = ? and bu_id = '39' order by spend desc limit 5";

    public static String queryForWalmartCSQ = "WITH CSEG AS (SELECT walmart_campaign_id, " +
            "business_unit_id, e.name AS business_unit_name, C_CNTRY.code AS country, " +
            "CLNT.name AS client_name, MAX(CASE WHEN segmentation_label = 'IO Segment' THEN segmentation_value ELSE NULL END) io_segmentation, " +
            "MAX(CASE WHEN segmentation_label = 'Item' THEN segmentation_value ELSE NULL END) item_segmentation " +
            "FROM walmart.t_walmart_campaign_segmentation_type a " +
            "LEFT JOIN walmart.t_walmart_campaign_segmentation_value b " +
            "ON b.campaign_segmentation_type_id = a.id AND a.is_deleted = 'N' AND " +
            "b.is_deleted = 'N' AND a.is_active = 'Y' AND b.is_active = 'Y' " +
            "LEFT JOIN walmart.t_walmart_campaign_segmentation_detail c " +
            "ON c.campaign_segmentation_value_id = b.id AND c.is_deleted = 'N' AND c.is_active = 'Y' " +
            "LEFT JOIN walmart.dim_walmart_campaign d ON d.id = c.walmart_campaign_id " +
            "LEFT JOIN ams.t_business_unit e ON e.id = a.business_unit_id AND e.is_deleted = 'N' " +
            "JOIN ams.t_client_country CC_CNTRY " +
            "ON e.client_country_id = CC_CNTRY.id AND CC_CNTRY.is_deleted = 'N' " +
            "JOIN ams.t_country C_CNTRY " +
            "ON C_CNTRY.id = CC_CNTRY.country_id AND C_CNTRY.is_deleted = 'N' AND C_CNTRY.is_active = 'Y' " +
            "JOIN ams.t_client CLNT ON CLNT.id = CC_CNTRY.client_id AND CLNT.is_deleted = 'N' AND CLNT.is_active = 'Y' " +
            "GROUP BY walmart_campaign_id, business_unit_id, e.name, C_CNTRY.code, CLNT.name), " +
            "MAIN AS (SELECT 'Walmart' AS retailer, campaignid, fct.advertiserid, d.api_campaign_id, formatted_date, campaign_name, kwmt.description keywordmatchtype, searchedkeyword, biddedkeyword, " +
            "ROUND(adspend, 2) AS adspend, numadsclicks::int, numadsshown::int, adv.name advertiser_name, ROUND(advertisedskusales14days, 2) " +
            "AS advertisedskusales14days, ROUND(otherskusales14days, 2) AS otherskusales14days " +
            "FROM walmart.searched_keyword_dly fct " +
            "JOIN walmart.keyword kw ON fct.keywordid = kw.id " +
            "JOIN walmart.dim_advertiser adv " +
            "ON fct.advertiserid = adv.advertiser_id AND adv.is_active = 'Y' AND adv.is_deleted = 'N' " +
            "JOIN walmart.dim_walmart_campaign d " +
            "ON fct.campaignid = d.id AND d.is_active = 'Y' AND d.is_deleted = 'N' " +
            "JOIN walmart.base_lookups ct " +
            "ON campaigntypeid = ct.ID AND ct.isdeleted = 'N' AND ct.lookupcatid = 610012 " +
            "JOIN walmart.base_lookups BS ON campaignstatusid = bs.id AND bs.lookupcatid = 610011 " +
            "JOIN walmart.base_lookups kwmt ON keywordmatchtypeid = kwmt.id AND kwmt.lookupcatid = 610015)" +
            "SELECT sum(adspend) as spend, " +
            "sum(advertisedskusales14days + otherskusales14days) as sales, " +
            "sum(numadsclicks) as clicks, " +
            "sum(numadsshown) as impressions " +
            "FROM MAIN ma LEFT JOIN CSEG ON ma.campaignid = cseg.walmart_campaign_id " +
            "LEFT JOIN walmart.dim_walmart_item dwi ON dwi.advertiser_id = ma.advertiserid " +
            "AND dwi.item_id = cseg.item_segmentation " +
            "AND dwi.api_campaign_id = ma.api_campaign_id " +
            "where io_segmentation is not NULL " +
            "and formatted_date = ? " +
            "and business_unit_id = '39' " +
            "GROUP BY formatted_date, business_unit_id, searchedkeyword " +
            "ORDER BY spend DESC limit 5;";

    public static String queryForWalmartItem = "select sum(spend) as spend, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_walmart_cmp_daily " +
            "where io_segmentation is not NULL and formatted_date = ? and bu_id = '39' GROUP BY formatted_date, business_unit, item_segmentation ORDER BY spend DESC limit 5;";

    public static String queryForCriteoItem = "SELECT spend, clicks, impressions FROM daas.vw_media_criteo_lineitem_daily " +
            "WHERE formatted_date = ? AND bu_id = '39' order by spend desc limit 5";

    public static String queryForCriteoProduct = "SELECT spend, clicks, impressions FROM daas.vw_media_criteo_products_daily " +
            "WHERE formatted_date = ? AND bu_id = '39' order by spend desc limit 5";

    public static String queryForCitrusAdCampaign = "SELECT sum(spend) as spend, sum(clicks) as clicks, sum(impressions) as impressions, sum(sales_revenue) as sales\n" +
            "           from daas.vw_media_citrusad_campagin_dly where formatted_date = ? and bu_id = '39';";

    public static String queryForInstacartAdGroup = "SELECT spend, clicks, impressions, sales from daas.vw_media_instacart_adgroupams_daily " +
            "where formatted_date = ? and bu_id = '39' order by spend desc limit 5";

    public static String queryForInstacartCampaign = "select sum(spend) as spend, sum(sales) as sales, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_instacart_adgroupams_daily " +
            "where io_segmentation is not NULL and formatted_date = ? and bu_id = '39' GROUP BY formatted_date, bu_name, campaign_name ORDER BY spend DESC limit 5;";

    public static String queryForInstacartKeywords = "SELECT spend, clicks, impressions, sales from daas.vw_media_instacart_keywordams_daily " +
            "where formatted_date = ? and bu_id = '39' order by spend desc limit 5;";

    public static String queryForInstacartKeywordsRolledUp = "select sum(spend) as spend, sum(sales) as sales, sum(clicks) as clicks, sum(impressions) as impressions from daas.vw_media_instacart_keywordams_daily " +
            "where io_segmentation is not NULL and formatted_date = ? and bu_id = 39 GROUP BY formatted_date, bu_name, keyword ORDER BY spend DESC limit 5;";

}