package com.DC.db.analyze;

import com.DC.utilities.DateUtility;

import java.time.LocalDate;

public class CaseManagementReportingQueries {

    public static String queryToFetchCurrentAndPreviousTotalAutomatedCases(String bu, String startDate, String endDate) {
        LocalDate startDateConverted = DateUtility.convertStringToDate(startDate);
        LocalDate endDateNewConverted = DateUtility.convertStringToDate(endDate);
        String previousStartDate = DateUtility.getPreviousPeriodStartDate(startDateConverted, endDateNewConverted);
        String previousEndDate = DateUtility.getPreviousPeriodEndDate(startDateConverted, endDateNewConverted);
        return "Select distinct sum(totalAutomatedCases) as totalAutomatedCases, sum(totalAutomatedPreviousCases) as totalAutomatedPreviousCases from (\n" +
                "              select count(tclda.id) as totalAutomatedCases, 0 totalAutomatedPreviousCases\n" +
                "              from ams.t_case_language_detail_asin tclda\n" +
                "              join ams.t_case_language_detail tcld on tclda.t_case_language_detail_id = tcld.id\n" +
                "              join ams.t_automated_cases tac on tclda.asin_id = tac.asin_id and tcld.case_type_id = tac.case_type_id\n" +
                "              JOIN fw.populate_asin_segmentation_daily pasd\n" +
                "              on pasd.asin_id = tclda.asin_id and tcld.business_unit_id = pasd.business_unit_id\n" +
                "              where tcld.business_unit_id = " + bu + "\n" +
                "              and tclda.status IN ('Submitted', 'Submitted for WFL')\n" +
                "              and CAST(tclda.created_on as Date) between '" + startDate + "' and '" + endDate + "'  AND pasd.fwcustomasinmetadata7 IS NULL\n" +
                "    UNION ALL\n" +
                "              select distinct 0 totalAutomatedCases, count(tclda.id) as totalAutomatedPreviousCases\n" +
                "              from ams.t_case_language_detail_asin tclda\n" +
                "              join ams.t_case_language_detail tcld on tclda.t_case_language_detail_id = tcld.id\n" +
                "              join ams.t_automated_cases tac on tclda.asin_id = tac.asin_id and tcld.case_type_id = tac.case_type_id\n" +
                "              JOIN fw.populate_asin_segmentation_daily pasd\n" +
                "              on pasd.asin_id = tclda.asin_id and tcld.business_unit_id = pasd.business_unit_id\n" +
                "              where tcld.business_unit_id = " + bu + "\n" +
                "              and tclda.status IN ('Submitted', 'Submitted for WFL')\n" +
                "              and CAST(tclda.created_on as Date) between '" + previousStartDate + "' and '" + previousEndDate + "'  AND pasd.fwcustomasinmetadata7 IS NULL\n" +
                ") data;";
    }

    public static String queryToFetchConversionRate(String bu, String startDate, String endDate) {
        LocalDate startDateConverted = DateUtility.convertStringToDate(startDate);
        LocalDate endDateNewConverted = DateUtility.convertStringToDate(endDate);
        String previousStartDate = DateUtility.getPreviousPeriodStartDate(startDateConverted, endDateNewConverted);
        String previousEndDate = DateUtility.getPreviousPeriodEndDate(startDateConverted, endDateNewConverted);
        return "WITH previousperiod AS\n" +
                "     (\n" +
                "            SELECT\n" +
                "                   dbu.businessunit_bk\n" +
                "                 , un.date_sk\n" +
                "                 , ta.id asin_id\n" +
                "                 , un.orderedunits\n" +
                "                 , traffic.glanceviews glanceviews\n" +
                "            FROM\n" +
                "                   vc.fact_arap_api_salesdiagnostic_orderedrevenue_daily un\n" +
                "                   JOIN\n" +
                "                          vc.fact_arap_api_trafficdiagnostic_daily traffic\n" +
                "                          ON\n" +
                "                                 traffic.link_clientaccount_asin_sk = un.link_clientaccount_asin_sk\n" +
                "                                 AND un.date_sk                     = traffic.date_sk\n" +
                "                                 AND un.sellingprogramname = traffic.sellingprogramname\n" +
                "                   JOIN\n" +
                "                          vc.link_clientaccount_asin lca\n" +
                "                          ON\n" +
                "                                 lca.link_clientaccount_asin_sk = un.link_clientaccount_asin_sk\n" +
                "                   JOIN\n" +
                "                          vc.link_businessunit_asin lba\n" +
                "                          ON\n" +
                "                                 lba.link_clientaccount_asin_sk = lca.link_clientaccount_asin_sk\n" +
                "                   JOIN\n" +
                "                          vc.dim_businessunit dbu\n" +
                "                          ON\n" +
                "                                 dbu.businessunit_sk = lba.businessunit_sk\n" +
                "                   JOIN\n" +
                "                          vc.dim_asin da\n" +
                "                          ON\n" +
                "                                 da.asin_sk = lba.asin_sk\n" +
                "                   JOIN\n" +
                "                          vc.dim_clientaccount dca\n" +
                "                          ON\n" +
                "                                 dca.clientaccount_sk = lca.clientaccount_sk\n" +
                "                   JOIN\n" +
                "                          ams.t_asin ta\n" +
                "                          ON\n" +
                "                                 ta.client_account_id = dca.clientaccount_bk\n" +
                "                                 AND ta.asin          = da.asin_bk\n" +
                "            WHERE\n" +
                "                   dbu.businessunit_bk            = " + bu + "\n" +
                "                   AND un.sellingprogramname      = 'AMAZON_RETAIL'\n" +
                "                   AND traffic.sellingprogramname = 'AMAZON_RETAIL'\n" +
                "                   AND un.date_sk                >= " + DateUtility.convertDateToInt(previousStartDate) + "\n" +
                "                   AND un.date_sk                <= " + DateUtility.convertDateToInt(endDate) + "\n" +
                "                   AND dbu.isactive               = true\n" +
                "                   AND lba.isactive               = true\n" +
                "                   AND lca.isactive               = true\n" +
                "                   AND un.iscurrent               = true\n" +
                "                   AND traffic.iscurrent          = true\n" +
                "                   AND da.isactive                = true\n" +
                "                   AND dca.isactive               = true\n" +
                "                   AND ta.is_active               = 'Y'\n" +
                "                   AND ta.is_deleted              = 'N'\n" +
                "                   AND\n" +
                "                   (\n" +
                "                          orderedunits   > 0\n" +
                "                          OR glanceviews > 0\n" +
                "                   )\n" +
                "     )\n" +
                "   , cte AS\n" +
                "     (\n" +
                "                              SELECT\n" +
                "                                     businessunit_bk\n" +
                "                                   , date_sk\n" +
                "                                   , asin_id\n" +
                "                                   , orderedunits\n" +
                "                                   , glanceviews\n" +
                "                                   , 0 previousperiod_orderedunits\n" +
                "                                   , 0 previousperiod_glanceviews\n" +
                "                              FROM\n" +
                "                                     previousperiod\n" +
                "                              WHERE\n" +
                "                                     date_sk     >= " + DateUtility.convertDateToInt(startDate) + "\n" +
                "                                     AND date_sk <= " + DateUtility.convertDateToInt(endDate) + "\n" +
                "                              UNION ALL\n" +
                "                              SELECT\n" +
                "                                     businessunit_bk\n" +
                "                                   , date_sk\n" +
                "                                   , asin_id\n" +
                "                                   , 0            orderedunits\n" +
                "                                   , 0            glanceviews\n" +
                "                                   , orderedunits previousperiod_orderedunits\n" +
                "                                   , glanceviews  previousperiod_glanceviews\n" +
                "                              FROM\n" +
                "                                     previousperiod pp\n" +
                "                              WHERE\n" +
                "                                     date_sk     >= " + DateUtility.convertDateToInt(previousStartDate) + "\n" +
                "                                     AND date_sk <= " + DateUtility.convertDateToInt(previousEndDate) + "\n" +
                "     )\n" +
                "   , automatedCases AS (Select distinct\n" +
                "                     tclda.asin_id\n" +
                "              from\n" +
                "                     ams.t_case_language_detail_asin tclda\n" +
                "                     join\n" +
                "                            ams.t_case_language_detail tcld\n" +
                "                            on\n" +
                "                                   tclda.t_case_language_detail_id = tcld.id\n" +
                "                     join\n" +
                "                            ams.t_automated_cases tac\n" +
                "                            on\n" +
                "                                   tclda.asin_id         = tac.asin_id\n" +
                "                                   and tcld.case_type_id = tac.case_type_id\n" +
                "                     JOIN\n" +
                "                            fw.populate_asin_segmentation_daily pasd\n" +
                "                            on\n" +
                "                                   pasd.asin_id              = tclda.asin_id\n" +
                "                                   and tcld.business_unit_id = pasd.business_unit_id\n" +
                "\n" +
                "       JOIN\n" +
                "              cte c\n" +
                "              ON\n" +
                "                     c.businessunit_bk                                 = tcld.business_unit_id\n" +
                "                     AND c.asin_id                                     = tclda.asin_id\n" +
                "WHERE\n" +
                "       tcld.business_unit_id             = " + bu + "\n" +
                "       AND tclda.status                  IN ('Submitted', 'Submitted for WFL')\n" +
                "       AND CAST(tclda.created_on as Date) >= '" + previousStartDate + "'\n" +
                "       AND CAST(tclda.created_on as Date) <= '" + endDate + "'\n" +
                "  AND pasd.fwcustomasinmetadata7 IS NULL\n" +
                ")\n" +
                "SELECT\n" +
                "       ROUND((SUM(c.orderedunits)                /NULLIF(SUM(c.glanceviews), 0) ::numeric(18,2) * 100),0)                conversionrate\n" +
                "       , ROUND((SUM(c.previousperiod_orderedunits) /NULLIF(SUM(c.previousperiod_glanceviews), 0) ::numeric(18,2) * 100),0) previousweek_conversionrate\n" +
                "FROM\n" +
                "       automatedCases a\n" +
                "join cte c on a.asin_id = c.asin_id;";
    }
}
