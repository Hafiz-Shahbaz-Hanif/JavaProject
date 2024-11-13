package com.DC.db.hubDbFunctions;

import com.DC.utilities.PostgreSqlUtility;

import java.sql.*;
import java.util.*;

import com.DC.utilities.SQLUtility;
import com.DC.utilities.hub.HubCommonMethods;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;

public class HubDbFunctions {

	private PostgreSqlUtility pu;
	private Logger logger;

	public HubDbFunctions() {
		pu = new PostgreSqlUtility();
		logger = Logger.getLogger(HubDbFunctions.class);
		PropertyConfigurator.configure("log4j.properties");
	}

	public List<String> getUserRoles(String email) throws SQLException {
		List<String> userRoles = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userRole);
			stmt.setString(1, email);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				userRoles.add(resultSet.getString("serialized"));
			}
		} catch (Exception e) {
			logger.info("Failed to execute query for User Roles. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return userRoles;
	}

	public List<String> getRetailerPlatforms() throws SQLException {
		List<String> platforms = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.retailerPlatforms);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				platforms.add(resultSet.getString("id"));
			}
		} catch (Exception e) {
			logger.info("Failed to execute query for Retailer Platforms. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return platforms;
	}

	public String getUserRolesForInsights(String email) throws SQLException {
		List<String> userRoles = getUserRoles(email);
		String insightsRole = null;
		for(String role : userRoles){
			if(role.contains("\"data\": \"")){
				insightsRole = role;
			}
		}
		JSONObject jsonObj = new JSONObject(insightsRole);
		return	jsonObj.getString("data");
	}

	public List<String> getOrganizationsForUser(String auth0Id) throws SQLException {
		List<String> organizations = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.organizationsForUser);
			stmt.setString(1, auth0Id);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				organizations.add(resultSet.getString("orgId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Organizations. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(organizations);
		return organizations;
	}

	public List<String> getBusinessUnitsForUser(String auth0Id, String orgId) throws SQLException {
		List<String> bus = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.businessUnitsForUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, orgId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				bus.add(resultSet.getString("businessUnitId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User BUs. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(bus);
		return bus;
	}

	public List<String> getBusinessUnitsForAuthorizeUser(String auth0Id) throws SQLException {
		List<String> bus = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.businessUnitsForAuthorizeUser);
			stmt.setString(1, auth0Id);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				bus.add(resultSet.getString("businessUnitId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User BUs. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(bus);
		return bus;
	}

	public List<String> getRetailerPlatformsForUser(String auth0Id, String buId) throws SQLException {
		List<String> bus = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.retailerPlatformsForUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, buId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				bus.add(resultSet.getString("platformid"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Retailer Platforms. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(bus);
		return bus;
	}

	public List<String> getBuRetailersForUser(String auth0Id, String orgId, String buId) throws SQLException {
		List<String> retailers = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buRetailersForUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, orgId);
			stmt.setObject(3, buId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				retailers.add(resultSet.getString("retailerId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for BU Retailers. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(retailers);
		return retailers;
	}

	public List<String> getBuRetailersForAuthorizeUser(String auth0Id, String buId) throws SQLException {
		List<String> retailers = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buRetailersForAuthorizeUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, buId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				retailers.add(resultSet.getString("retailerId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for BU Retailers. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(retailers);
		return retailers;
	}

	public List<String> getBuRetailerPlatformsForUser(String auth0Id, String orgId, String buId, String retailerId) throws SQLException {
		List<String> retailerPlatforms = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buRetailerPlatformsForUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, orgId);
			stmt.setObject(3, buId);
			stmt.setObject(4, retailerId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				retailerPlatforms.add(resultSet.getString("platformId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for BU Retailer Platforms. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(retailerPlatforms);
		return retailerPlatforms;
	}

	public List<String> getBuRetailerPlatformsForAuthorizeUser(String auth0Id, String buId, String retailerId) throws SQLException {
		List<String> retailerPlatforms = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buRetailerPlatformsForAuthorizeUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, buId);
			stmt.setObject(3, retailerId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				retailerPlatforms.add(resultSet.getString("platformId"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User BU Retailer Platforms. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(retailerPlatforms);
		return retailerPlatforms;
	}

	public List<String> getModuleIdsForAuthorizeUser(String auth0Id, String buId) throws SQLException {
		List<String> moduleIds = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.moduleIdsForAuthorizeUser);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, buId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				moduleIds.add(resultSet.getString("module_id"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(moduleIds);
		return moduleIds;
	}

	public void updateUserBuModulePermissions (boolean canCreate, boolean canRead, boolean canUpdate, boolean canDelete, String userId, String buModuleId) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userBuModuleAuthorization);
			stmt.setBoolean(1, canCreate);
			stmt.setBoolean(2, canRead);
			stmt.setBoolean(3, canUpdate);
			stmt.setBoolean(4, canDelete);
			stmt.setObject(5, userId);
			stmt.setObject(6, buModuleId);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query to Update User BU Module Permissions. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public Map<String, String> getUserBuModuleIds(String auth0Id, String buName, String moduleName) throws SQLException {
		Map<String, String> userBuModuleIds = new LinkedHashMap<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userBuModuleIds);
			stmt.setString(1, auth0Id);
			stmt.setString(2, buName);
			stmt.setString(3, moduleName);
			ResultSet resultSet = stmt.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();

			while (resultSet.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					userBuModuleIds.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
				}
			}
		} catch (Exception e) {
			logger.info("Failed to execute query for User Bu Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return userBuModuleIds;
	}

	public List<String> getBuAgnosticModuleIdsForUser(String auth0Id) throws SQLException {
		List<String> moduleIds = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buAgnosticModuleUser);
			stmt.setString(1, auth0Id);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				moduleIds.add(resultSet.getString("module_id"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for Bu Agnostic Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(moduleIds);
		return moduleIds;
	}

	public List<String> getLegacyPlatformIds() throws SQLException {
		List<String> platformIds = new ArrayList<>();
		Connection con = null;

		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.legacyPlatforms);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				platformIds.add(resultSet.getString("id"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for Bu Agnostic Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(platformIds);
		return platformIds;
	}

	public List<String> getAggregationTypeId(String aggregationTypeId) throws SQLException {
		List<String> platformIds = new ArrayList<>();
		Connection con = null;

		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.aggregationType);
			stmt.setObject(1, aggregationTypeId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				platformIds.add(resultSet.getString("id"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for Bu Agnostic Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(platformIds);
		return platformIds;
	}

	public void createOrganization(String orgId, String organizationName) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.createOrganization);
			stmt.setObject(1, orgId);
			stmt.setString(2, organizationName);
			stmt.setBoolean(3, false);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query for Bu Agnostic Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public List<String> getAggregationId(String aggregationId) throws SQLException {
		List<String> aggregationIds = new ArrayList<>();
		Connection con = null;

		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.organizationAggregation);
			stmt.setObject(1, aggregationId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				aggregationIds.add(resultSet.getString("id"));
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for Bu Agnostic Module Ids. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		Collections.sort(aggregationIds);
		return aggregationIds;
	}

	public void createUser(String userEmail, String userName, String userLastName, String auth0Id) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.createUser);
			stmt.setString(1, userEmail);
			stmt.setString(2, userName);
			stmt.setString(3, userLastName);
			stmt.setString(4, auth0Id);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query for create user. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public String getUserId(String userEmail) throws SQLException {
		Connection con = null;
		String userId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userId);
			stmt.setString(1, userEmail);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				userId = resultSet.getString("id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for user id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return userId;
	}

	public String getUserAuth0Id(String userEmail) throws SQLException {
		Connection con = null;
		String userId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userId);
			stmt.setString(1, userEmail);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				userId = resultSet.getString("auth0_id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for user id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return userId;
	}

	public String getUserIdByAuth0Token(String auth0Token) throws SQLException {
		Connection con = null;
		String userId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userIdByAuthId);
			stmt.setString(1, new HubCommonMethods().getAuth0IdFromToken(auth0Token));
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				userId = resultSet.getString("id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for user id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return userId;
	}

	public void createlegacyPlatformForUser(String userEmail, String legacyPlatformName) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.createLegacyPlatformForUser);
			stmt.setString(1, userEmail);
			stmt.setString(2, legacyPlatformName);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query for create legacy platform for user. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public void deletelegacyPlatformForUser(String userId) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.deleteLegacyPlatformForUser);
			stmt.setObject(1, userId);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query for delete legacy platform for user. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public void deleteUser(String userId) throws SQLException {
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.deleteUser);
			stmt.setObject(1, userId);
			stmt.executeUpdate();
		} catch (Exception e) {
			logger.info("Failed to execute query for delete legacy platform for user. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
	}

	public String getAggregationId(String aggrType, String userId, String legacyPlatform) throws SQLException {
		Connection con = null;
		String aggregationId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.aggregation);
			stmt.setString(1, aggrType);
			stmt.setObject(2, userId);
			stmt.setString(3, legacyPlatform);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				aggregationId = resultSet.getString("aggregation_id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for user id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}

		pu.closeConnection(con);
		return aggregationId;
	}

	public List<UserOrganization> getUserOrganizations(String auth0Id) throws SQLException {
		List<UserOrganization> organizations = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userOrganizations);
			stmt.setString(1, auth0Id);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				Object column1Value = resultSet.getObject("organizationId");
				Object column2Value = resultSet.getObject("businessUnitId");
				Object column3Value = resultSet.getObject("retailerPlatformId");

				UserOrganization rowData = new UserOrganization(column1Value, column2Value, column3Value);
				organizations.add(rowData);			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Organizations. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return organizations;
	}

	public List<UserBuAggregation> getUserBuAggregations(String auth0Id, String orgId, String buId, String retailerPlatformId) throws SQLException {
		List<UserBuAggregation> buAggregations = new ArrayList<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userBuAggregations);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, orgId);
			stmt.setObject(3, buId);
			stmt.setObject(4, retailerPlatformId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				String platform_name = resultSet.getString("platform_name");
				Object serialized_value = resultSet.getObject("serialized_value");
				String currencyCode = resultSet.getString("currency_code");
				String region = resultSet.getString("code");
				String currencySymbol = resultSet.getString("currency_symbol");
				boolean mediaOffsiteEnabled = resultSet.getBoolean("offsite_media_enabled");
				String domain = resultSet.getString("retailerplatformdomain");
				String retailPlatformId = resultSet.getString("retailerplatformid");
				boolean mediaOnsiteEnabled = resultSet.getBoolean("onsite_media_enabled");
				boolean retailEnabled = resultSet.getBoolean("retail_enabled");

				UserBuAggregation rowData = new UserBuAggregation(platform_name, serialized_value, currencyCode, region, currencySymbol, mediaOffsiteEnabled, domain, retailPlatformId, mediaOnsiteEnabled, retailEnabled);
				buAggregations.add(rowData);
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Organizations. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return buAggregations;
	}

	public UserBuModule getUserBuModule(String auth0Id, String buId, String moduleId) throws SQLException {
		Connection con = null;
		UserBuModule module = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.userBuModule);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, buId);
			stmt.setObject(3, moduleId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				String user_id = resultSet.getString("user_id");
				String business_unit_id = resultSet.getString("business_unit_id");
				String module_id = resultSet.getString("module_id");
				String name = resultSet.getString("name");
				boolean can_create = resultSet.getBoolean("can_create");
				boolean can_read = resultSet.getBoolean("can_read");
				boolean can_update = resultSet.getBoolean("can_update");
				boolean can_delete = resultSet.getBoolean("can_delete");
				module = new UserBuModule(user_id, business_unit_id, module_id, name, can_create, can_read, can_update, can_delete);
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User BU Module. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return module;
	}

	public UserModule getUserModule(String auth0Id, String moduleId) throws SQLException {
		Connection con = null;
		UserModule module = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buAgnosticModule);
			stmt.setString(1, auth0Id);
			stmt.setObject(2, moduleId);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				String user_id = resultSet.getString("user_id");
				String module_id = resultSet.getString("module_id");
				String name = resultSet.getString("name");
				boolean can_create = resultSet.getBoolean("can_create");
				boolean can_read = resultSet.getBoolean("can_read");
				boolean can_update = resultSet.getBoolean("can_update");
				boolean can_delete = resultSet.getBoolean("can_delete");
				module = new UserModule(user_id, module_id, name, can_create, can_read, can_update, can_delete);
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for User Module. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return module;
	}

	public void updateConnectUserScreenPermissions (int roleId, int userId) throws SQLException {
		SQLUtility.connectToServer();
		try {
			SQLUtility.executeUpdate(HubQueries.connectUserScreenPermissions(roleId, userId));
		} catch (Exception e) {
			logger.info("Failed to execute query to Update Connect user screen permissions. Exception: " + e.getMessage());
			SQLUtility.closeConnections();
			throw e;
		}
		SQLUtility.closeConnections();
	}

	public String getBuId(String buName) throws SQLException {
		Connection con = null;
		String buId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.buId);
			stmt.setString(1, buName);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				buId = resultSet.getString("id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for bu id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return buId;
	}

	public String getRetailerId(String country, String retailer, String retailerPlatform) throws SQLException {
		Connection con = null;
		String retailerId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.retailerId);
			stmt.setString(1, country);
			stmt.setString(2, retailer);
			stmt.setString(3, retailerPlatform);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				retailerId = resultSet.getString("retailer_platform_id");
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for retailer platform id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return retailerId;
	}

	public String getGoalMetricId(String metric) throws SQLException {
		Connection con = null;
		String metricId = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.goalMetricId);
			stmt.setObject(1, metric, Types.OTHER);
			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				metricId = resultSet.getObject("id").toString();
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for goal metric id. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return metricId;
	}

	public Map<String, String> getPivotId(String buName, String retailer, String retailerPlatform, String pivotType) throws SQLException {
		Map<String, String> metricGoal = new LinkedHashMap<>();
		Connection con = null;
		try {
			con = pu.getConnection();
			PreparedStatement stmt = con.prepareStatement(HubQueries.goalPivotDetails);
			stmt.setString(1, buName);
			stmt.setString(2, retailer);
			stmt.setString(3, retailerPlatform);
			stmt.setObject(4, pivotType, Types.OTHER);
			ResultSet resultSet = stmt.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();

			while (resultSet.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					metricGoal.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
				}
			}

		} catch (Exception e) {
			logger.info("Failed to execute query for pivot details. Exception: " + e.getMessage());
			pu.closeConnection(con);
			throw e;
		}
		pu.closeConnection(con);
		return metricGoal;
	}

}