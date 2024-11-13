package com.DC.utilities.apiEngine.models.requests.hub.marketshare.authservice;

public class MarketShareUserUpdateRequestBody {

	public String first_name;
	public String last_name;
	public String email;
	public int permission_level;


	public MarketShareUserUpdateRequestBody(String first_name, String last_name, String email, int permission_level) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
		this.permission_level = permission_level;

	}

}