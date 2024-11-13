package com.DC.utilities.hub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.DC.utilities.apiEngine.apiRequests.adc.admin.AdminApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.admin.AdminUserRequestBody;

public class FilaUser {

	public int getUserId(String authToken) throws Exception {
		return AdminApiRequests.getAdminUserInfoJson(authToken).getInt("userId");
	}

	public void updateFilaUser(String authToken, String roles, boolean userActive, boolean userExternal) throws Exception {
		updateFilaUser(authToken, null, null, roles, "26,39" , userActive, userExternal);
	}

	public void updateFilaUser(String authToken, String roles, String bus, boolean userActive, boolean userExternal) throws Exception {
		updateFilaUser(authToken, null, null, roles, bus, userActive, userExternal);
	}

	public void updateFilaUser(String authToken, String userFirst, String userLastName, String roles, String bus, boolean userActive, boolean userExternal) throws Exception {
		AdminUserRequestBody rs = getUserDetails (authToken, userFirst, userLastName, roles, bus, userActive, userExternal);
		AdminApiRequests.adminUser(rs, authToken);
	}

	public void updateNonAdminFilaUser(String authTokenForAdminUser, String authTokenForUserToUpdate, String roles, boolean userActive, boolean userExternal) throws Exception {
		updateNonAdminFilaUser(authTokenForAdminUser, authTokenForUserToUpdate, null, null, roles, "26,39" , userActive, userExternal);
	}

	public void updateNonAdminFilaUser(String authTokenForAdminUser, String authTokenForUserToUpdate, String userFirst, String userLastName, String roles, String bus, boolean userActive, boolean userExternal) throws Exception {
		AdminUserRequestBody rs = getUserDetails (authTokenForUserToUpdate, userFirst, userLastName, roles, bus, userActive, userExternal);
		AdminApiRequests.adminUser(rs, authTokenForAdminUser);
	}

	public AdminUserRequestBody getUserDetails (String authToken, String userFirst, String userLastName, String roles, String bus, boolean userActive, boolean userExternal) throws Exception {
		JSONObject userInfo = AdminApiRequests.getAdminUserInfoJson(authToken);
		String userEmail = userInfo.getString("userEmail");
		int userId = userInfo.getInt("userId");
		String[] userFirstLastName = userInfo.getString("userName").split(" ");
		String name = userFirst == null ? userFirstLastName[0] : userFirst;
		String lastName = userLastName == null ? userFirstLastName[1] : userLastName;
		AdminUserRequestBody rs = new AdminUserRequestBody(userActive, userId, userEmail, userEmail, name, lastName, roles, bus, userExternal, List.of(Integer.valueOf(1)), "1,3,8,2");
		return rs;
	}

	public String getFilaRoles(String authToken, String ... roleNames) throws Exception {
		List<String> roleList = Arrays.asList(roleNames);
		List<String> roleIdList = new ArrayList<>();

		JSONObject roles = AdminApiRequests.getFilaRolesJson(authToken);
		JSONArray items = roles.getJSONArray("items");

		for (String roleName : roleList) {
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				String moduleName = item.getString("name");

				if (moduleName.equalsIgnoreCase(roleName)) {
					int id = item.getInt("id");
					roleIdList.add(String.valueOf(id));
				}
			}
		}
		return String.join(",", roleIdList);
	}

	public String getFilaBus(String authToken, String ... buNames) throws Exception {
		List<String> buList = Arrays.asList(buNames);
		List<String> buIdList = new ArrayList<>();

		JSONArray bus = AdminApiRequests.getFilaBuJson(authToken);

		for (String roleName : buList) {
			for (int i = 0; i < bus.length(); i++) {
				JSONObject item = bus.getJSONObject(i);
				String businessUnitName = item.getString("businessUnitName");

				if (businessUnitName.equalsIgnoreCase(roleName)) {
					int id = item.getInt("businessUnitId");
					buIdList.add(String.valueOf(id));
				}
			}
		}
		return String.join(",", buIdList);
	}

}