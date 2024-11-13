package com.DC.utilities.apiEngine.models.responses.hub.authservice;


public class UserAuthorization {

	public Data data;
	public boolean success;

	public static class Data {
		public String sub;
		public String nickname;
		public String name;
		public String picture;
		public String updated_at;
		public String email;
		public boolean email_verified;

	}

	public Data getData() {
		return data;
	}

}
