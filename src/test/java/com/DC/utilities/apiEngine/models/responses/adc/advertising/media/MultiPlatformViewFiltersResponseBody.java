package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.ArrayList;
import java.util.List;

import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.MultiPlatformViewFiltersResponseBody.Links.LinkDetails;

public class MultiPlatformViewFiltersResponseBody {

	public List<String> platforms; 
	public List<Links> links;

	public static class Links {

		public int linkId;
		public String linkTitle;
		public List<LinkDetails> linkDetails;

		public static class LinkDetails {
			public String segmentationTypeId;
			public String platform;
			public String segmentationName;
			public String segmentationLabel;

			public String getSegmentationTypeId() {
				return segmentationTypeId;
			}
		}

		public List<LinkDetails> getLinkDetails(){
			return linkDetails;
		}

	}
	
	public MultiPlatformViewFiltersResponseBody () {
		
	}

	public List<Links> getLinks(){
		return links;
	}

	public List<String> getExistingMpvFilters(MultiPlatformViewFiltersResponseBody rb){
		List<String> segmentationTypeIds = new ArrayList<>();
		List<Links> links = rb.getLinks();
		for (Links link : links) {
			List<LinkDetails> LinkDetails = link.getLinkDetails();
			for (LinkDetails LinkDetail : LinkDetails) {
				segmentationTypeIds.add(LinkDetail.getSegmentationTypeId());
			}
		}
		return segmentationTypeIds;
	}



}