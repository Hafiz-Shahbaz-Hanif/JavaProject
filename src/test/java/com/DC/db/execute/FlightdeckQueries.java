package com.DC.db.execute;

import com.DC.utilities.DateUtility;

public class FlightdeckQueries {

    public static String queryToFetchActiveCampaigns(int buId, int campaignToSelect) {
        return "SELECT DISTINCT\n" +
                "                   _campaign.ID parentId, _campaign.CAMPAIGN_NAME, _keyword.keyword,\n" +
                "                   COALESCE(_keyword.APIKeywordID, _target.targetid) itemApiUnitId,\n" +
                "                   IF(_keyword.ID IS NOT NULL, 'KEYWORD', 'TARGET') itemUnitType\n" +
                "                        FROM T_CAMPAIGN_SEGMENTATION_TYPE _segmentation_type\n" +
                "                                 JOIN T_CAMPAIGN_SEGMENTATION_VALUE _segmentation_value\n" +
                "                                    ON _segmentation_type.ID = _segmentation_value.CAMPAIGN_SEGMENTATION_ID\n" +
                "                                      AND _segmentation_value.IS_ACTIVE = 'Y'\n" +
                "                                      AND _segmentation_value.IS_DELETED = 'N'\n" +
                "                                 JOIN T_CAMPAIGN_SEGMENTATION_DETAIL _segmentation_detail\n" +
                "                                    ON _segmentation_value.ID = _segmentation_detail.CAMPAIGN_SEGMENTATION_VALUE_ID\n" +
                "                                      AND _segmentation_detail.IS_ACTIVE = 'Y'\n" +
                "                                      AND _segmentation_detail.IS_DELETED = 'N'\n" +
                "                                 JOIN T_AMAZON_CAMPAIGN _campaign\n" +
                "                                    ON _segmentation_detail.AMAZON_CAMPAIGN_ID = _campaign.ID\n" +
                "                                      AND _campaign.active = 'Y'\n" +
                "                                      AND _campaign.deleted = 'N'\n" +
                "                                      AND _campaign.CAMPAIGN_STATUS = 610215\n" +
                "                                      AND _campaign.ID IN (" + campaignToSelect + ")\n" +
                "                                 LEFT JOIN FW_APIKeywordDimension _keyword\n" +
                "                                    ON _keyword.AmzCampaignID = _campaign.ID\n" +
                "                                       AND _keyword.KeywordStatusID = 610042\n" +
                "                                       AND _keyword.IsDeleted = 0\n" +
                "                                 LEFT JOIN t_campaign_target _target\n" +
                "                                    ON _target.apicampaignid = _campaign.API_CAMPAIGN_ID\n" +
                "                                       AND _target.claimid = _campaign.CLAIM_ID\n" +
                "                                       AND _target.stateid = 611711\n" +
                "                                       AND _target.isdeleted = 'N'\n" +
                "                        WHERE _segmentation_type.BUSINESS_UNIT_ID = " + buId + "\n" +
                "                          AND _segmentation_type.IS_ACTIVE = 'Y'\n" +
                "                          AND _segmentation_type.IS_DELETED = 'N'\n" +
                "                          AND COALESCE(_keyword.APIKeywordID, _target.targetid) IS NOT NULL;";
    }

    public static String queryToGetMostRecentAvailableDate(String itemToSelect) {
        return "select rec.campaign_id, rec.unit_id, rec.bu_date, camp.campaign_name from fw.amazon_intra_day_bidding_recommendation rec\n" +
                "    join ams.t_amazon_campaign camp on rec.campaign_id = camp.api_campaign_id\n" +
                "    where unit_id in ('" + itemToSelect + "')\n" +
                "      and bu_date is not null\n" +
                "    order by bu_date desc;";
    }

    public static String queryToUpdateDownloadDate(String itemToSelect, String mostRecentDate) {
        String todaysDate = DateUtility.getTodayDate();
        return "UPDATE fw.amazon_intra_day_bidding_recommendation SET bu_date = '" + todaysDate + "'\n" +
                "    WHERE unit_id IN ('" + itemToSelect + "')\n" +
                "    and bu_date = '" + mostRecentDate + "';";
    }
}
