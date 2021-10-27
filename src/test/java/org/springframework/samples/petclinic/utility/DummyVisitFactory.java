package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.visit.Visit;

public class DummyVisitFactory {
	private static Integer visit_id = 0;
	private static final String TEST_DESC = "TEST_DESC";

	public static Visit getDummyVisit() {
		Visit newVisit = new Visit();
		newVisit.setDescription(TEST_DESC);
		newVisit.setId(visit_id);
		visit_id+=1;
		return newVisit;
	}
}