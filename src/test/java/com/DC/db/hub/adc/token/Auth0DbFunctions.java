package com.DC.db.hub.adc.token;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.DC.utilities.SQLUtility;

public class Auth0DbFunctions {

    private static ResultSet rs;
    public static Logger logger = Logger.getLogger(Auth0DbFunctions.class);

    public String getAuth0Id(String userEmail) throws SQLException {
        String id = null;
        SQLUtility.connectToServer();
        rs = SQLUtility.executeQuery("select AUTH0_ID from T_USER where EMAIL = '" + userEmail + "'");
		while (rs.next()) {
			id = rs.getString("AUTH0_ID");
		}
        SQLUtility.closeConnections();
        return id;
    }

}
