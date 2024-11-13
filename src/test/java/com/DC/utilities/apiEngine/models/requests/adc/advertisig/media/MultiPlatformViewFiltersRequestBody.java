package com.DC.utilities.apiEngine.models.requests.adc.advertisig.media;

import java.util.List;
import org.apache.log4j.Logger;

public class MultiPlatformViewFiltersRequestBody {

	public String linkId;
	public String linkTitle;
	public List<LinkDetails> linkDetails;

	public MultiPlatformViewFiltersRequestBody(String linkId, String linkTitle, List<LinkDetails> linkDetails){
		this.linkId = linkId;
		this.linkTitle = linkTitle;
		this.linkDetails = linkDetails;
		Logger.getLogger(ReportingDashboardRequestBody.class).info("** Serializing request body for Flight Deck request body"); 
	}


	public static class LinkDetails {

		public String platform;
		public String segmentationTypeId;
		public String segmentationName;
		public String segmentationLabel;

		public LinkDetails(String platform, String segmentationTypeId, String segmentationName, String segmentationLabel) {
			this.platform = platform;
			this.segmentationTypeId = segmentationTypeId;
			this.segmentationName = segmentationName;
			this.segmentationLabel = segmentationLabel;
		}
	}
}
