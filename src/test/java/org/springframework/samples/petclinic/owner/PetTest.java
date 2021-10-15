package org.springframework.samples.petclinic.owner;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class PetTest {
	private Pet pet;

	@DataPoints
	public static List<List<Visit>> visits = new ArrayList<>();

	private static List<Visit> generateVisits() {
		List<Visit> allVisits = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Visit visit = new Visit();
			visit.setDate(LocalDate.of(
				2021,
				new Random().nextInt(11)+1,
				new Random().nextInt(28)+1
				));
			allVisits.add(visit);
		}
		return allVisits;
	}

	@BeforeClass
	public static void generateTheories() {
		List<Visit> allVisits = generateVisits();

		visits.add(allVisits.subList(3, 6));
		visits.add(allVisits.subList(0, 4));
		visits.add(allVisits.subList(1, 7));
		visits.add(allVisits.subList(5, 9));
		visits.add(new ArrayList<>());
	}

	@Before
	public void setup() {
		pet = new Pet();
		pet.setId(0);
	}

	@Theory
	public void getVisits(List<Visit> visitList) {
		visitList.forEach(pet::addVisit);
		PropertyComparator.sort(
			visitList,
			new MutableSortDefinition("date", false, false));
		assertEquals(pet.getVisits(), visitList);
	}

	@After
	public void teardown() {
		pet = null;
		visits = null;
	}
}
