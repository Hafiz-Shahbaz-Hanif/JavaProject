package com.DC.utilities.apiEngine.routes.hub.authservice;

import com.DC.utilities.ReadConfig;

public class AuthServiceRoutes {

	public static final String AUTH_SERVICE_BASE_URI = ReadConfig.getInstance().getaAuthServiceBaseUri();
	public static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();
	public static final String LIVE_HEALTH_CHECK = "/health/live";
	public static final String READY_HEALTH_CHECK = "/health/ready";
	public static final String AUTHORIZATION = "/authorization/auth0";
	public static final String LEGACY_USER_INFO_SYNC = "/authorization/legacy-userinfo-sync";
// 	public static final String USER_AUTH_INFO = "/authorization-service/authorization/userinfo"; //?useAggregation=true //STAGING
//	public static final String USER_AUTH ="/authorization-service/authorize/user?verbose=true"; //STAGING
	public static final String USER_AUTH_INFO = "/authorization/userinfo"; //?useAggregation=true //QA
	public static final String USER_AUTH ="/authorization/authorize/user?verbose=true"; //QA
	public static final String USER_LOGOUT ="/authorization/logout/user"; //QA

	public static String getAuthServiceLiveHealthCheckRoutePath() {
		return AUTH_SERVICE_BASE_URI + LIVE_HEALTH_CHECK;
	}

	public static String getAuthServiceReadyHealthCheckRoutePath() {
		return AUTH_SERVICE_BASE_URI + READY_HEALTH_CHECK;
	}

	public static String getAuthorizationRoutePath() {
		return AUTH_SERVICE_BASE_URI + AUTHORIZATION;
	}

	public static String getLegacyUserInfoSyncRoutePath() {
		return AUTH_SERVICE_BASE_URI + LEGACY_USER_INFO_SYNC;
	}

	public static String getUserAuthInfoRoutePath() {
		return HUB_EXTERNAL_GATEWAY + USER_AUTH_INFO;
	}

	public static String getUserAuthRoutePath() {
		return HUB_EXTERNAL_GATEWAY + USER_AUTH;
	}

	public static String getlogOutUserAuthRoutePath() {
		return HUB_EXTERNAL_GATEWAY + USER_LOGOUT;
	}

}