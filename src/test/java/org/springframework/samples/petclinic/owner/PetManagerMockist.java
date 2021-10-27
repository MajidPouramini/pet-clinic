package org.springframework.samples.petclinic.owner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
	@Mock
	private OwnerRepository owners;
	@InjectMocks
	private PetManager petManager;
	private static Pet dummyPet;
	private Owner testOwner;


	@Before
	public void setup() {
		testOwner = new Owner();
		testOwner.setId(OWNER_ID_TEST);
        dummyPet = DummyPetStore.getDummyPet();
		testOwner.addPet(DummyPetStore.getNewPet());//pet1
		testOwner.addPet(DummyPetStore.getNewPet());//pet2
	}

	/*
	 * Double: mock
     * Type: mockist
	 * Method: behavior
	 */
	@Test
	public void findOwnerTest() {
        //given 
		when(owners.findById(OWNER_ID_TEST)).thenReturn(testOwner);

        //when
        Owner owner = petManager.findOwner(OWNER_ID_TEST);

        //then
		assertEquals(owner, testOwner);
	}

	/*
	 * Double: mock, dummy
     * Type: mockist
	 * Method: behavior
	 */
	@Test
	public void findPetTest() {
        //given
		when(pets.get(PET_ID_TEST)).thenReturn(dummyPet);

        //when
		Pet pet = petManager.findPet(PET_ID_TEST);

        //then
		assertEquals(pet, dummyPet);
	}

	/*
	 * Double: mock, dummy
     * Type: mockist
	 * Method: state
	 */
	@Test
	public void savePetTest() {
        //given
		petManager.savePet(dummyPet, testOwner);

        //when
		verify(pets).save(dummyPet);

        //then
		assertEquals(dummyPet.getOwner(), testOwner);
	}

	/*
	 * Double: mock
     * Type: mockist
	 * Method: behavior
	 */
	@Test
	public void getOwnerPetsTest() {
        //given
		when(owners.findById(OWNER_ID_TEST)).thenReturn(testOwner);

        //when
		List<Pet> pets = petManager.getOwnerPets(OWNER_ID_TEST);

        //then
		assertEquals(pets, testOwner.getPets());
	}

	/*
	 * Double: mock
     * Type: mockist
	 * Method: behavior
	 */
	@Test
	public void getOwnerPetTypes() {
        //given
		when(owners.findById(OWNER_ID_TEST)).thenReturn(testOwner);

        //when
		Set<PetType> petTypes = petManager.getOwnerPetTypes(OWNER_ID_TEST);

        //then
        Set<PetType> petTypesExpected = testOwner.getPets().stream().map(Pet::getType).collect(Collectors.toSet());
		assertEquals(petTypes, petTypesExpected);
	}
}