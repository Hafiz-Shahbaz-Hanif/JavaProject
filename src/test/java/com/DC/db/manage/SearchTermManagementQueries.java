package com.DC.db.manage;

public class SearchTermManagementQueries {
    public static String getQueryToFetchRetailers(String BU, String... retailerNames) {
        StringBuilder retailerNamesString = new StringBuilder();
        if (retailerNames.length > 0) {
            retailerNamesString.append("'").append(retailerNames[0]).append("'");
            for (int i = 1; i < retailerNames.length; i++) {
                retailerNamesString.append(",'").append(retailerNames[i]).append("'");
            }
        }
        return "SELECT tst.search_term, tr.name AS Retailer " +
                "FROM search.t_search_term tst " +
                "JOIN search.t_business_unit_search_term tbust ON tst.id = tbust.search_term_id " +
                "LEFT JOIN search.t_search_term_group_assignment tstga ON tbust.id = tstga.business_unit_search_term_id " +
                "LEFT JOIN search.t_search_term_group tstg ON tstga.search_term_group_id = tstg.id " +
                "JOIN search.t_search_term_assignment tsta ON tbust.id = tsta.business_unit_search_term_id " +
                "JOIN global.t_business_unit_provision tbup ON tsta.business_unit_provision_id = tbup.id " +
                "JOIN global.t_business_unit tbu ON tbup.business_unit_id = tbu.id " +
                "JOIN global.t_retailer_platform_scope trps ON tbup.retailer_platform_scope_id = trps.id " +
                "JOIN global.t_retailer_platform trp ON trps.retailer_platform_id = trp.id " +
                "JOIN global.t_retailer tr ON trp.retailer_id = tr.id " +
                "JOIN global.t_country tc ON trp.country_id = tc.id " +
                "WHERE tr.name IN (" + retailerNamesString + ") " +
                "and tbu.name ilike '%" + BU + "%' " +
                "GROUP BY tst.search_term, tbust.priority_term, tr.name, tbust.scrape_frequency, tstg.name;";
    }

    public static String getQueryToFetchSearchTerms(String searchTermName) {
        return "SELECT abc.BU_Name, abc.Name, abc.Priority, string_agg(abc.Retailer,',') As A, abc.scrape_frequency, abc.Search_Term_Group " +
                "FROM (SELECT tst.search_term AS Name, tbust.priority_term AS Priority, tr.name As Retailer, tbust.scrape_frequency, string_agg(tstg.name,',' order by tstg.name) AS Search_Term_Group, tbu.name As BU_Name, tbu.id " +
                "FROM search.t_search_term tst " +
                "JOIN search.t_business_unit_search_term tbust ON tst.id = tbust.search_term_id " +
                "LEFT JOIN search.t_search_term_group_assignment tstga ON tbust.id = tstga.business_unit_search_term_id " +
                "LEFT JOIN search.t_search_term_group tstg ON tstga.search_term_group_id = tstg.id " +
                "JOIN search.t_search_term_assignment tsta ON tbust.id = tsta.business_unit_search_term_id " +
                "JOIN global.t_business_unit_provision tbup ON tsta.business_unit_provision_id = tbup.id " +
                "JOIN global.t_business_unit tbu ON tbup.business_unit_id = tbu.id " +
                "JOIN global.t_retailer_platform_scope trps ON tbup.retailer_platform_scope_id = trps.id " +
                "JOIN global.t_retailer_platform trp ON trps.retailer_platform_id = trp.id " +
                "JOIN global.t_retailer tr ON trp.retailer_id = tr.id " +
                "JOIN global.t_country tc ON trp.country_id = tc.id " +
                "WHERE tbu.name like '%SuperFizz%' " +
                "And tst.search_term ilike '%" + searchTermName + "%'" +
                "GROUP BY tst.search_term, tbust.priority_term, tr.name, tbust.scrape_frequency, tbu.name, tbu.id) abc " +
                "GROUP BY abc.Name, abc.Priority, abc.scrape_frequency, abc.Search_Term_Group, abc.BU_Name, abc.id;";
    }

    public static String getQueryToFetchRetailersAssignedToBU(String BU) {
        return "SELECT tr.name As Retailers " +
                "From global.t_business_unit tbu " +
                "Join global.t_business_unit_provision tbup ON tbup.business_unit_id = tbu.id " +
                "JOIN global.t_retailer_platform_scope trps ON tbup.retailer_platform_scope_id = trps.id " +
                "JOIN global.t_retailer_platform trp ON trps.retailer_platform_id = trp.id AND trp.deleted = 'N' AND trp.deleted = 'N' " +
                "JOIN global.t_retailer tr ON trp.retailer_id = tr.id " +
                "JOIN global.t_country tc ON trp.country_id = tc.id " +
                "WHERE tbu.name LIKE '%" + BU + "%'" +
                "AND tbup.retail_enabled = 'true' " +
                "AND tc.code = 'us';";
    }

    public static String fetchNumberOfRecords(String BU, String... retailerNames) {
        StringBuilder retailerNamesString = new StringBuilder();
        if (retailerNames.length > 0) {
            retailerNamesString.append("'").append(retailerNames[0]).append("'");
            for (int i = 1; i < retailerNames.length; i++) {
                retailerNamesString.append(",'").append(retailerNames[i]).append("'");
            }
        }
        return "SELECT COUNT(distinct search_term) AS record_count " +
                "FROM search.t_search_term tst " +
                "JOIN search.t_business_unit_search_term tbust ON tst.id = tbust.search_term_id " +
                "LEFT JOIN search.t_search_term_group_assignment tstga ON tbust.id = tstga.business_unit_search_term_id " +
                "LEFT JOIN search.t_search_term_group tstg ON tstga.search_term_group_id = tstg.id " +
                "JOIN search.t_search_term_assignment tsta ON tbust.id = tsta.business_unit_search_term_id " +
                "JOIN global.t_business_unit_provision tbup ON tsta.business_unit_provision_id = tbup.id JOIN global.t_business_unit tbu ON tbup.business_unit_id = tbu.id " +
                "JOIN global.t_retailer_platform_scope trps ON tbup.retailer_platform_scope_id = trps.id " +
                "JOIN global.t_retailer_platform trp ON trps.retailer_platform_id = trp.id " +
                "JOIN global.t_retailer tr ON trp.retailer_id = tr.id " +
                "JOIN global.t_country tc ON trp.country_id = tc.id " +
                "WHERE tr.name IN (" + retailerNamesString + ") " +
                "and tbu.name ilike '%" + BU + "%';";
    }
}
