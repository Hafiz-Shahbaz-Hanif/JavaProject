package com.DC.db.searchDBFunctions;

import com.DC.utilities.RedShiftUtility;
import com.DC.utilities.SQLUtility;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SearchDBFunctions {

    private static ResultSet rs;
    public static Logger logger = Logger.getLogger(SearchDBFunctions.class);

    public List<String> getNoOneFrequencyClicksValues(String interval, String department, String startDate, String endDate) throws SQLException {
        String noOneClikedAsin = null;
        String noOneClickShare = null;
        String noOneProductTitle = null;
        RedShiftUtility.connectToServer();
        rs = RedShiftUtility.executeQuery("SELECT * FROM vc.fact_search_terms_weekly\n" +
                "where search_term = 'chocolate'\n" +
                "and interval = '" + interval + "'\n" +
                "AND department = '" + department + "'\n" +
                "AND region = 'US'\n" +
                "and start_date between '\n" + startDate + "\n' and '\n" + endDate + "\n'\n" +
                "ORDER BY start_date desc, search_term");
        while (rs.next()) {
            noOneClikedAsin = rs.getString("no_one_clicked_asin");
            noOneClickShare = rs.getString("no_one_click_share");
            noOneProductTitle = rs.getString("no_one_product_title");
        }
        RedShiftUtility.closeConnections();
        return Arrays.asList(noOneClikedAsin, noOneClickShare, noOneProductTitle);
    }

    public List<String> getSearchTermsForTermInTitleSearchDb(String term, String sfrLw, String startDate) throws SQLException {
        List<String> searchTerms = new ArrayList<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select * from vc.fact_search_terms_weekly\r\n"
                + "where department = 'Amazon.com' \r\n"
                + "and interval = 'Weekly'\r\n"
                + "and start_date = '\n" + startDate + "\n'\n"
                + "and (\r\n"
                + " lower(no_one_product_title) like '%" + term + "%' \r\n"
                + " or lower(no_two_product_title) like '%" + term + "%' \r\n"
                + " or lower(no_three_product_title) like '%" + term + "%' \r\n"
                + ")\r\n"
                + "and search_frequency_rank <= " + sfrLw);

        while (rs.next()) {
            searchTerms.add(rs.getString("search_term"));
        }

        RedShiftUtility.closeConnections();
        return searchTerms;

    }

    public List<String> getSearchTermsDb(String searchTerm, String sfrLw, String startDate) throws SQLException {
        List<String> searchTerms = new ArrayList<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select * from vc.fact_search_terms_weekly\r\n"
                + "where department = 'Amazon.com' \r\n"
                + "and interval = 'Weekly'\r\n"
                + "and start_date = '\n" + startDate + "\n'\n"
                + "and (\r\n"
                + " lower(no_one_product_title) like '%" + searchTerm + "%' \r\n"
                + " or lower(no_two_product_title) like '%" + searchTerm + "%' \r\n"
                + " or lower(no_three_product_title) like '%" + searchTerm + "%' \r\n"
                + ")\r\n"
                + "and lower(search_term) like '%" + searchTerm + "%' \r\n"
                + "and search_frequency_rank <= " + sfrLw);

        while (rs.next()) {
            searchTerms.add(rs.getString("search_term"));
        }

        RedShiftUtility.closeConnections();
        return searchTerms;
    }

    public List<String> getSfrLwDb(String searchTerm, String sfrLw, String startDate) throws SQLException {
        List<String> searchTerms = new ArrayList<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select * from vc.fact_search_terms_weekly\r\n"
                + "where department = 'Amazon.com' \r\n"
                + "and interval = 'Weekly'\r\n"
                + "and start_date = '\n" + startDate + "\n'\n"
                + "and lower(search_term) like '%" + searchTerm + "%' \r\n"
                + "and search_frequency_rank <= " + sfrLw);

        while (rs.next()) {
            searchTerms.add(rs.getString("search_frequency_rank"));
        }

        RedShiftUtility.closeConnections();
        return searchTerms;
    }

    public String getSfrLwForSearchTerm(String searchTerm, String startDate) throws SQLException {
        RedShiftUtility.connectToServer();
        String sfr = "";

        rs = RedShiftUtility.executeQuery("select * from vc.fact_search_terms_weekly\n"
                + "where department = 'Amazon.com'\n"
                + "and interval = 'Weekly'\r\n"
                + "and start_date = '" + startDate + "'\n"
                + "and search_term = '" + searchTerm + "'\n");

        while (rs.next()) {
            sfr = rs.getString("search_frequency_rank");
        }

        RedShiftUtility.closeConnections();
        return sfr;
    }

    public List<String> getAvgRatingReviewPriceForSearchTerm(String keyword, String firstDayOfweek, String lastDayOfWeek) throws SQLException {
        List<String> avgsList = new ArrayList<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select keyword, avg(average_rating) as avg_rating, \r\n"
                + "avg(total_reviews) as avg_review, avg(price) as avg_price from fw.sov\r\n"
                + "where keyword = '" + keyword + "' and utctime >= '" + firstDayOfweek + "' and utctime <= '" + lastDayOfWeek + "'\r\n"
                + "and placement = 'Organic' and zonename = '.Com' and rank < 11\r\n"
                + "and average_rating != 0 and total_reviews != 0 and price != 0\r\n"
                + "group by keyword;");

        while (rs.next()) {
            String avg_rating = new BigDecimal(rs.getString("avg_rating")).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            avgsList.add(avg_rating);
            avgsList.add(rs.getString("avg_review"));
            avgsList.add(rs.getString("avg_price"));
        }

        RedShiftUtility.closeConnections();
        return avgsList;
    }

    public Map<String, String> getSearchTermAndSfrWeeklyFromDb(String searchTerm, String sfrLw, String startDate) throws SQLException {
        Map<String, String> searchTermsAndRanks = new LinkedHashMap<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select * from vc.fact_search_terms_weekly\r\n"
                + "where department = 'Amazon.com' \r\n"
                + "and interval = 'Weekly'\r\n"
                + "and start_date = '\n" + startDate + "\n'\n"
                + "and lower(search_term) like '%" + searchTerm + "%' \r\n"
                + "and search_frequency_rank <= " + sfrLw);

        while (rs.next()) {
            searchTermsAndRanks.put(rs.getString("search_term"), rs.getString("search_frequency_rank"));
        }

        RedShiftUtility.closeConnections();
        return searchTermsAndRanks;

    }

    public List<String> getStartDayForSearchTermsWeeklyFromDb() throws SQLException {
        List<String> startDate = new ArrayList<>();
        RedShiftUtility.connectToServer();

        rs = RedShiftUtility.executeQuery("select distinct(start_date) from vc.fact_search_terms_weekly\r\n"
                + "where department = 'Amazon.com' \r\n"
                + "and interval = 'Weekly'\r\n"
                + "order by start_date desc\r\n"
                + "limit 5");

        while (rs.next()) {
            startDate.add(rs.getString("start_date"));
        }

        RedShiftUtility.closeConnections();
        return startDate;
    }

    public List<String> getNoTwoFrequencyClicksValues(String interval, String startDate, String endDate) throws SQLException {
        String noTwoClikedAsin = null;
        String noTwoClickShare = null;
        String noTwoProductTitle = null;
        RedShiftUtility.connectToServer();
        rs = RedShiftUtility.executeQuery("SELECT * FROM vc.fact_search_terms_weekly\n" +
                "where search_term = 'chocolate'\n" +
                "and interval = '" + interval + "'\n" +
                "AND department = 'Amazon.com'\n" +
                "AND region = 'US'\n" +
                "and start_date between '\n" + startDate + "\n' and '\n" + endDate + "\n'\n" +
                "ORDER BY start_date desc, search_term");
        while (rs.next()) {
            noTwoClikedAsin = rs.getString("no_two_clicked_asin");
            noTwoClickShare = rs.getString("no_two_click_share");
            noTwoProductTitle = rs.getString("no_two_product_title");
        }
        RedShiftUtility.closeConnections();
        return Arrays.asList(noTwoClikedAsin, noTwoClickShare, noTwoProductTitle);
    }

    public List<String> getNoThreeFrequencyClicksValues(String interval, String startDate, String endDate) throws SQLException {
        String noThreeClikedAsin = null;
        String noThreeClickShare = null;
        String noThreeProductTitle = null;
        RedShiftUtility.connectToServer();
        rs = RedShiftUtility.executeQuery("SELECT * FROM vc.fact_search_terms_weekly\n" +
                "where search_term = 'chocolate'\n" +
                "and interval = '" + interval + "'\n" +
                "AND department = 'Amazon.com'\n" +
                "AND region = 'US'\n" +
                "and start_date between '\n" + startDate + "\n' and '\n" + endDate + "\n'\n" +
                "ORDER BY start_date desc, search_term");
        while (rs.next()) {
            noThreeClikedAsin = rs.getString("no_three_clicked_asin");
            noThreeClickShare = rs.getString("no_three_click_share");
            noThreeProductTitle = rs.getString("no_three_product_title");
        }
        RedShiftUtility.closeConnections();
        return Arrays.asList(noThreeClikedAsin, noThreeClickShare, noThreeProductTitle);
    }

    public String getSearchTermFrequencyValue(String interval, String startDate, String endDate) throws SQLException {
        String searchTerm = null;
        RedShiftUtility.connectToServer();
        rs = RedShiftUtility.executeQuery("SELECT * FROM vc.fact_search_terms_weekly\n" +
                "where search_term = 'chocolate'\n" +
                "and interval = '" + interval + "'\n" +
                "AND department = 'Amazon.com'\n" +
                "AND region = 'US'\n" +
                "and start_date between '\n" + startDate + "\n' and '\n" + endDate + "\n'\n" +
                "ORDER BY start_date desc, search_term");
        while (rs.next()) {
            searchTerm = rs.getString("search_term");
        }
        RedShiftUtility.closeConnections();
        return searchTerm;
    }

    public String getBusinessUnitsWithNoClientAccountId(String businessUnit) throws SQLException {
        String name = null;
        SQLUtility.connectToServer();
        rs = SQLUtility.executeQuery("SELECT A.ID,A.NAME,B.CLIENT_ACCOUNT_ID\n" +
                "FROM T_BUSINESS_UNIT A\n" +
                "LEFT JOIN T_BUSINESS_UNIT_CLIENT_ACCOUNT B on A.ID = B.BUSINESS_UNIT_ID\n" +
                "where B.CLIENT_ACCOUNT_ID is null\n" +
                "and A.IS_DELETED = 'N' ");
        while (rs.next()) {
            name = rs.getString("NAME");
            if (name.equalsIgnoreCase(businessUnit)) {
                logger.info("Business Unit is " + name);
                break;
            }
        }
        SQLUtility.closeConnections();
        return name;
    }

}