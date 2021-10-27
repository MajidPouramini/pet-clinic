package org.springframework.samples.petclinic.owner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.DummyPetStore;
import org.springframework.samples.petclinic.utility.DummyVisitFactory;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link PetManager}
 */
@RunWith(MockitoJUnitRunner.class)
public class PetManagerTestClassical {
	private static final Integer OWNER_ID_TEST = 0;
    private static final Integer PET_ID_TEST = 0;

    @Mock
	private PetTimedCache pets;

	@InjectMocks
	private PetManager petManager;
	private Owner testOwner;
    private static Pet testPet;

	@Before
	public void setup() {
		testOwner = new Owner();
		testOwner.setId(OWNER_ID_TEST);
        testPet = new Pet();
		testOwner.addPet(DummyPetStore.getNewPet());//pet1
        testOwner.addPet(DummyPetStore.getNewPet());//pet2
	}

	/*
     * Type: Classic
	 * Method: state
	 */
	@Test
	public void newPetTest() {
        //given
		Pet newPet = petManager.newPet(testOwner);

        //when

        //then
        assertTrue(testOwner.getPets().contains(newPet));
		assertEquals(newPet.getOwner(), testOwner);
	}

    /*
	 *  Double: mock, dummy
     *  Type: Classic
	 *  Method: behavior
	 */
	@Test
	public void getVisitsBetweenTest() {
        //given
        Visit DummyVisit1 = DummyVisitFactory.getDummyVisit();
        Visit DummyVisit2 = DummyVisitFactory.getDummyVisit();

		List<Visit> dummyVisits = Arrays.asList(DummyVisit1, DummyVisit2);
		LocalDate start = LocalDate.of(2020, java.time.Month.JANUARY, 1);
		LocalDate end = LocalDate.now();
		dummyVisits.forEach(testPet::addVisit);

        //when
		when(pets.get(PET_ID_TEST)).thenReturn(testPet);
		List<Visit> visits = petManager.getVisitsBetween(PET_ID_TEST, start, end);

        //then
        List<Visit> expectesVisits = testPet.getVisitsBetween(start, end);
		assertEquals(visits, expectesVisits);
	}
}