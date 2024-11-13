package com.DC.db.execute;

public class ATCQueries {

    public static String queryToFetchDatesAndValuesFromDB(int businessUnitId, String metric, String startDate, String endDate) {
        String startDateKey = startDate.replaceAll("-", "");
        String endDateKey = endDate.replaceAll("-", "");
        return "WITH _campaigns AS ( " +
                "    SELECT DISTINCT " +
                "        _amz_campaign.id AS campaign_id, " +
                "        _amz_campaign.targeting_type " +
                "    FROM ams.t_amazon_campaign _amz_campaign " +
                "    JOIN ams.vw_campaign_segmentation _segmentation " +
                "        ON _segmentation.business_unit_id = " + businessUnitId + " " +
                "            AND _amz_campaign.id = _segmentation.campaignid " +
                "    WHERE _amz_campaign.campaign_type = 610030 " +
                "), _base_table AS ( " +
                "    SELECT " +
                "        _dly.formatted_date AS date_key, " +
                "        SUM(" + metric + ") AS metric_numerator " +
                "    FROM ams.amscmp_dly _dly " +
                "    JOIN _campaigns _cmps " +
                "        ON _dly.date BETWEEN " + startDateKey + " AND " + endDateKey +
                "            AND _dly.campaignid = _cmps.campaign_id " +
                "    JOIN ams.dimdate _dim " +
                "        ON _dly.date = _dim.datekey " +
                "    GROUP BY 1 " +
                "), _automation_table AS ( " +
                "    SELECT " +
                "        _stream.time_window_start::date AS date_key, " +
                "        SUM(CASE WHEN COALESCE(_child.bidding_strategy_id, _parent.bidding_strategy_id) = 1 THEN " + metric + " ELSE 0 END) / NULLIF(SUM(" + metric + "), 0) AS ensemble_np, " +
                "        SUM(CASE WHEN COALESCE(_child.bidding_strategy_id, _parent.bidding_strategy_id) = 2 THEN " + metric + " ELSE 0 END) / NULLIF(SUM(" + metric + "), 0) AS fido_np, " +
                "        SUM(CASE WHEN COALESCE(_child.bidding_strategy_id, _parent.bidding_strategy_id) = 3 THEN " + metric + " ELSE 0 END) / NULLIF(SUM(" + metric + "), 0) AS rbb_np, " +
                "        SUM(CASE WHEN COALESCE(_child.bidding_strategy_id, _parent.bidding_strategy_id) = 4 THEN " + metric + " ELSE 0 END) / NULLIF(SUM(" + metric + "), 0) AS perpetua_np, " +
                "        (1 - ensemble_np - fido_np - rbb_np - perpetua_np) AS non_automated_np " +
                "    FROM ams.stream_agg _stream " +
                "    JOIN _campaigns " +
                "        ON _stream.campaign_id = _campaigns.campaign_id " +
                "            AND _stream.time_window_start BETWEEN " + startDate + " AND " + endDate +
                "    JOIN ams.dimdate _dim " +
                "        ON _dim.fulldate = _stream.time_window_start::date " +
                "    LEFT JOIN ams.t_amazon_bidding_strategy_child_tracking _child " +
                "        ON _stream.api_keyword_id = _child.api_unit_id " +
                "            AND (time_window_start >= _child.start_time " +
                "                OR ( " +
                "                DATE_TRUNC('hour', time_window_start) = DATE_TRUNC('hour', _child.start_time) " +
                "                AND EXTRACT(MINUTE FROM _child.start_time) <= 30 " +
                "                )) " +
                "            AND (_child.end_time IS NULL OR time_window_start < _child.end_time " +
                "                OR ( " +
                "                DATE_TRUNC('hour', time_window_start) = DATE_TRUNC('hour', _child.end_time) " +
                "                AND EXTRACT(MINUTE FROM _child.end_time) > 30 " +
                "                )) " +
                "    LEFT JOIN ams.t_amazon_bidding_strategy_parent_tracking _parent " +
                "        ON _child.bidding_strategy_id IS NULL " +
                "            AND _stream.campaign_id = _parent.amazon_campaign_id " +
                "            AND (time_window_start >= _parent.start_time " +
                "                OR ( " +
                "                DATE_TRUNC('hour', time_window_start) = DATE_TRUNC('hour', _parent.start_time) " +
                "                AND EXTRACT(MINUTE FROM _parent.start_time) <= 30 " +
                "                )) " +
                "            AND (_parent.end_time IS NULL OR time_window_start < _parent.end_time " +
                "                OR ( " +
                "                DATE_TRUNC('hour', time_window_start) = DATE_TRUNC('hour', _parent.end_time) " +
                "                AND EXTRACT(MINUTE FROM _parent.end_time) > 30 " +
                "                )) " +
                "    GROUP BY 1 " +
                ") " +
                "SELECT " +
                "    _base_table.date_key AS date_key, " +
                "    ROUND(metric_numerator * ensemble_np, 2) AS ensemble, " +
                "    ROUND(metric_numerator * fido_np, 2) AS fido, " +
                "    ROUND(metric_numerator * rbb_np, 2) AS rbb, " +
                "    ROUND(metric_numerator * perpetua_np, 2) AS perpetua, " +
                "    ROUND(metric_numerator * non_automated_np, 2) AS non_automated " +
                "FROM _automation_table " +
                "JOIN _base_table " +
                "    ON _automation_table.date_key = _base_table.date_key " +
                "ORDER BY 1; ";
    }
}
