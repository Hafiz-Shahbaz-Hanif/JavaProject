package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.Iterator;
import java.util.List;

public class SegmentationTypesResponseBody {

	public List<Segmentations> segmentations; 

	public static class Segmentations {

		public String id;
		public int businessUnitId;
		public String segmentationLabel;
		public String segmentationName;
		public boolean active;
		public boolean deleteable;
		public boolean deleted;

		public String getId() {
			return id;
		}

		public String getSegmentationLabel() {
			return segmentationLabel;
		}

		public String getSegmentationName() {
			return segmentationName;
		}

	}

	public List<Segmentations> getSegmentations(){
		return segmentations;
	}

	public List<Segmentations> removeSelectedMpvFilterOptions(SegmentationTypesResponseBody rb, List<String> selectedOptions){
		List<Segmentations> segmentations = rb.getSegmentations();
		Iterator <Segmentations> itr = segmentations.iterator();

		while (itr.hasNext()) {
			Segmentations sg = (Segmentations) itr.next();
			if (selectedOptions.contains(sg.getId())) {
				itr.remove();
			}
		}

		return segmentations;

	}

}