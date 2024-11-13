package com.DC.utilities.apiEngine.models.responses.hub.insights.authservice;


public class InsightsAuthServiceResponseBody {

	public Jwt jwt;
	public boolean acceptedTerms; 

	public static class Jwt {
		public String token;

		public String getToken() {
			return token;
		}
	}

	public Jwt getJwt() {
		return jwt;
	}

}