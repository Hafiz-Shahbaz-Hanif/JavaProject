package com.DC.utilities.apiEngine.models.requests.adc.admin;

import java.util.List;
import org.apache.log4j.Logger;

public class AdminUserRequestBody {

	public boolean active;
	public int id;
	public String email;
	public String login;
	public String firstName;
	public String lastName;
	public String roleIds;
	public String businessUnitIds;
	public boolean externalUser;
	public List<Integer> organizations;
	public String retailerIds;

	public AdminUserRequestBody(
			boolean active,
			int id,
			String email,
			String login,
			String firstName,
			String lastName,
			String roleIds,
			String businessUnitIds,
			boolean externalUser,
			List<Integer> organizations,
			String retailerIds

			) 
	{
		this.active = active;
		this.id = id;
		this.email = email;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roleIds = roleIds;
		this.businessUnitIds = businessUnitIds;
		this.externalUser = externalUser;
		this.organizations = organizations;
		this.retailerIds = retailerIds;

		Logger.getLogger(AdminUserRequestBody.class).info("** Serializing request body for Admin User request body"); 
	}

}