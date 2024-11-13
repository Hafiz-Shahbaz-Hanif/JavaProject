package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.List;

public class MultiPlatformViewFilterCreationResponseBody {

	public String linkId;
	public String linkTitle;
	public List<LinkDetails> linkDetails;

	public String getlinkId() {
		return linkId;
	}

	public static class LinkDetails {

		public String platform;
		public String segmentationTypeId;
		public String segmentationName;
		public String segmentationLabel;

	}

}
