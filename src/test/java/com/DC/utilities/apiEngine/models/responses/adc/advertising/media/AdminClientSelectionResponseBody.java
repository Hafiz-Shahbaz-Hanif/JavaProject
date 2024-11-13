package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.List;

public class AdminClientSelectionResponseBody {

	List<BusinessUnit> businessUnitUsers;

	public static class BusinessUnit {
		String businessUnitId;
		String businessUnitName;
		String clientName;
		String countryCode;
		String countryName;
		String logoUrl;
	}

	public List<BusinessUnit> getBusinessUnits() {
		return businessUnitUsers;
	}

}
