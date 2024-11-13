package com.DC.db.execute;

public class MediaManagementQueries {

    public static String getEligibleCampaigns(int businessUnitId, String campaignStatus) {
        return "SELECT * " +
                "FROM (SELECT CLAIM.PROFILE_ID claimId, " +
                "CAMPAIGN.ID campaignId, " +
                "CAMPAIGN.API_CAMPAIGN_ID apiCampaignId, " +
                "CAMPAIGN.CAMPAIGN_NAME campaignName, " +
                "SEGMENTATION.ASIN asin, " +
                "RBB.*, " +
                "CASE " +
                "WHEN RBB.active = 'Y' " +
                "THEN 'Enabled' " +
                "WHEN RBB.rbbId IS NULL AND ELIGIBLE_CAMPAIGN.ID IS NOT NULL " +
                "THEN 'Eligible' " +
                "WHEN RBB.active = 'N' AND ELIGIBLE_CAMPAIGN.ID IS NOT NULL " +
                "THEN 'Disabled - Eligible' " +
                "WHEN RBB.active = 'N' AND ELIGIBLE_CAMPAIGN.ID IS NULL " +
                "THEN 'Disabled - Ineligible' " +
                "END status " +
                "FROM T_AMAZON_CAMPAIGN CAMPAIGN " +
                "INNER JOIN (SELECT AMAZON_CAMPAIGN_ID CAMPAIGN_ID, " +
                "V.SEGMENTATION_VALUE ASIN " +
                "FROM T_CAMPAIGN_SEGMENTATION_DETAIL D " +
                "INNER JOIN T_CAMPAIGN_SEGMENTATION_VALUE V " +
                "ON D.CAMPAIGN_SEGMENTATION_VALUE_ID = V.ID " +
                "AND V.IS_ACTIVE = 'Y' " +
                "AND V.IS_DELETED = 'N' " +
                "INNER JOIN T_CAMPAIGN_SEGMENTATION_TYPE T " +
                "ON V.CAMPAIGN_SEGMENTATION_ID = T.ID " +
                "AND T.IS_ACTIVE = 'Y' " +
                "AND T.IS_DELETED = 'N' " +
                "AND T.SEGMENTATION_NAME = 'DEFAULT ASIN SEGMENT' " +
                "AND T.BUSINESS_UNIT_ID = " + businessUnitId + " " +
                "WHERE D.IS_ACTIVE = 'Y' " +
                "AND D.IS_DELETED = 'N' " +
                ") SEGMENTATION " +
                "ON CAMPAIGN.ID = SEGMENTATION.CAMPAIGN_ID " +
                "INNER JOIN T_CLAIM CLAIM " +
                "ON CLAIM.ID = CAMPAIGN.CLAIM_ID " +
                "AND CLAIM.IS_ACTIVE = 'Y' " +
                "AND CLAIM.IS_DELETED = 'N' " +
                "LEFT JOIN (SELECT RBB.ID rbbId, " +
                "RBB.RECURRENCE recurrence, " +
                "RBB.RULE_ACTION ruleAction, " +
                "RBB.RULE_TYPE ruleType, " +
                "RBB.API_CAMPAIGN_OPTIMIZATION_ID apiCampaignOptimizationId, " +
                "RBB.IS_ACTIVE active, " +
                "RBB.RULE_NAME ruleName, " +
                "RBB.API_CAMPAIGN_OPTIMIZATION_ID campaignOptimizationId, " +
                "RULE_DETAIL.COMPARISON_OPERATOR ruleComparison, " +
                "RULE_DETAIL.THRESHOLD ruleThreshold, " +
                "RULE_CONSTRAINT.METRIC_NAME constraintType, " +
                "RULE_CONSTRAINT.COMPARISON_OPERATOR constraintComparison, " +
                "RULE_CONSTRAINT.THRESHOLD constraintThreshold " +
                "FROM T_AMAZON_CAMPAIGN_RULE_BASE_BIDDING RBB " +
                "INNER JOIN T_AMAZON_CAMPAIGN_RULE_BASE_BIDDING_DETAIL RULE_DETAIL " +
                "ON RBB.ID = RULE_DETAIL.AMAZON_CAMPAIGN_RULE_BASE_BIDDING_ID " +
                "LEFT JOIN T_AMAZON_CAMPAIGN_RULE_BASE_BIDDING_CONSTRAINT RULE_CONSTRAINT " +
                "ON RBB.ID = RULE_CONSTRAINT.AMAZON_CAMPAIGN_RULE_BASE_BIDDING_ID " +
                ") RBB " +
                "ON RBB.rbbId = CAMPAIGN.AMAZON_CAMPAIGN_RULE_BASE_BIDDING_ID " +
                "LEFT JOIN FW_dim_amazon_eligibility ELIGIBLE_CAMPAIGN " +
                "ON CAMPAIGN.ID = ELIGIBLE_CAMPAIGN.CAMPAIGNID " +
                "WHERE CAMPAIGN.active = 'Y' " +
                "AND CAMPAIGN.deleted = 'N' " +
                ") A " +
                "WHERE A.status IS NOT NULL " +
                "and status in ('Eligible', 'Disabled - Eligible');";
    }

    public static String getKeywordsAndAsinsEligibleForCreation(int businessUnitId) {
        return "SELECT _keyword_dim.keyword, _segmentation_value.SEGMENTATION_VALUE ASIN " +
                "FROM FW_APIKeywordDimension _keyword_dim " +
                "JOIN T_AMAZON_CAMPAIGN _amazon_campaign ON _keyword_dim.APICampaignId = _amazon_campaign.API_CAMPAIGN_ID " +
                "AND _amazon_campaign.active = 'Y' AND _amazon_campaign.deleted = 'N' " +
                "JOIN T_CAMPAIGN_SEGMENTATION_DETAIL _segmentation_detail ON _amazon_campaign.ID = _segmentation_detail.AMAZON_CAMPAIGN_ID " +
                "AND _segmentation_detail.IS_ACTIVE = 'Y' AND _segmentation_detail.IS_DELETED = 'N' " +
                "JOIN T_CAMPAIGN_SEGMENTATION_VALUE _segmentation_value ON _segmentation_detail.CAMPAIGN_SEGMENTATION_VALUE_ID = _segmentation_value.ID " +
                "AND _segmentation_detail.IS_ACTIVE = 'Y' AND _segmentation_detail.IS_DELETED = 'N' " +
                "JOIN T_CAMPAIGN_SEGMENTATION_TYPE _segmentation_type ON _segmentation_value.CAMPAIGN_SEGMENTATION_ID = _segmentation_type.ID " +
                "AND _segmentation_type.IS_ACTIVE = 'Y' AND _segmentation_type.IS_DELETED = 'N' AND _segmentation_type.SEGMENTATION_LABEL = 'ASIN' " +
                "WHERE _segmentation_type.BUSINESS_UNIT_ID = '" + businessUnitId + "' " +
                "AND _amazon_campaign.CAMPAIGN_TYPE = 610030 AND _keyword_dim.KWMatchTypeId = 610044 " +
                "AND _amazon_campaign.CAMPAIGN_STATUS = 610215 AND _keyword_dim.KeywordStatusID = 610042 AND _keyword_dim.IsDeleted = 0 AND NOT EXISTS(SELECT 1 " +
                "FROM T_SLOT_TARGETING_CONFIGURATION config " +
                "where config.QUERY_TEXT = _keyword_dim.keyword AND config.CAMPAIGN_SEGMENTATION_VALUE_ID = _segmentation_value.ID AND config.STATUS = 'ENABLED' AND config.IS_DELETED = 'N') " +
                "AND NOT EXISTS(SELECT 1 FROM T_AUTOMATED_BIDDING_SCOPE SCOPE " +
                "JOIN T_AUTOMATED_BIDDING_STRATEGY TABS ON SCOPE.ID = TABS.AUTOMATED_BIDDING_SCOPE_ID " +
                "JOIN T_AMAZON_AUTOMATED_BIDDING TAAB ON TABS.ID = TAAB.AUTOMATED_BIDDING_STRATEGY_ID " +
                "WHERE SCOPE.IS_DELETED = 'N' AND SCOPE.SCOPE_TYPE = 'PARENT' AND TABS.IS_DELETED = 'N' AND TAAB.IS_ACTIVE = 'Y' AND TAAB.AMAZON_CAMPAIGN_ID = _amazon_campaign.ID) " +
                "AND NOT EXISTS(SELECT 1 FROM T_AUTOMATED_BIDDING_SCOPE SCOPE " +
                "JOIN T_AUTOMATED_BIDDING_STRATEGY TABS ON SCOPE.ID = TABS.AUTOMATED_BIDDING_SCOPE_ID " +
                "JOIN T_AMAZON_AUTOMATED_BIDDING TAAB ON TABS.ID = TAAB.AUTOMATED_BIDDING_STRATEGY_ID " +
                "JOIN T_AMAZON_AUTOMATED_BIDDING_UNIT TAABU ON TAAB.ID = TAABU.AMAZON_AUTOMATED_BIDDING_ID WHERE TABS.CODE <> 'ENSEMBLE' AND SCOPE.IS_DELETED = 'N' " +
                "AND TABS.IS_DELETED = 'N' AND TAAB.IS_ACTIVE = 'Y' AND TAABU.IS_ACTIVE = 'Y' AND TAABU.UNIT_TYPE = 'KEYWORD' AND TAAB.AMAZON_CAMPAIGN_ID = _amazon_campaign.ID " +
                "AND TAABU.API_UNIT_ID = _keyword_dim.APIKeywordID) ORDER BY CAMPAIGN_SEGMENTATION_VALUE_ID;";
    }

    public static String getCreatedConfig(String queryText, String bidFloor, String bidCeiling) {
        return "Select * from T_SLOT_TARGETING_CONFIGURATION " +
                "         where QUERY_TEXT = '" + queryText + "' " +
                "           and MIN_CPC = '" + bidFloor + "' " +
                "           and BID_CEILING = '" + bidCeiling + "';";
    }
}
