package org.springframework.samples.petclinic.owner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {
	Owner owner;

	final static String ADDRESS_TEST_TEHRAN = "Tehran, Iran";
	final static String ADDRESS_TEST_MASHHAD = "Mashhad, Iran";
	final static String CITY_TEST_TEHRAN = "Tehran";
	final static String CITY_TEST_MASHHAD = "Mashhad";
	final static String PHONE_TEST_1 = "09123456789";
	final static String PHONE_TEST_2 = "09129876543";

	Pet dog = new Pet();
	Pet cat = new Pet();
	Pet mouse = new Pet();

	@Before
	public void setup() {
		owner = new Owner();
		owner.setAddress(ADDRESS_TEST_TEHRAN);
		owner.setCity(CITY_TEST_TEHRAN);
		owner.setTelephone(PHONE_TEST_1);

		dog.setName("dog");
		cat.setName("cat");
		mouse.setName("mouse");

		owner.addPet(dog);
		owner.addPet(cat);
	}

	@After
	public void teardown() {
		owner = null;
		dog = null;
		cat = null;
		mouse = null;
	}

	@Test
	public void getAddressTest() {
		assertEquals(ADDRESS_TEST_TEHRAN, owner.getAddress());
	}

	@Test
	public void setAddressTest() {
		owner.setAddress(ADDRESS_TEST_MASHHAD);
		assertEquals(ADDRESS_TEST_MASHHAD, owner.getAddress());
	}

	@Test
	public void getCityTest() {
		assertEquals(CITY_TEST_TEHRAN, owner.getCity());
	}

	@Test
	public void setCityTest() {
		owner.setCity(CITY_TEST_MASHHAD);
		assertEquals(CITY_TEST_MASHHAD, owner.getCity());
	}

	@Test
	public void getTelephoneTest() {
		assertEquals(PHONE_TEST_1, owner.getTelephone());
	}

	@Test
	public void setTelephoneTest() {
		owner.setTelephone(PHONE_TEST_2);
		assertEquals(PHONE_TEST_2, owner.getTelephone());
	}

	@Test
	public void getPetsInternalTest() {
		owner.getPetsInternal();
		assertTrue(owner.getPetsInternal().contains(dog));
		assertTrue(owner.getPetsInternal().contains(cat));
	}

	@Test
	public void setPetsInternalTest() {
		Set<Pet> pets = new HashSet<>();
		pets.add(mouse);
		pets.add(cat);
		owner.setPetsInternal(pets);
		Set<Pet> collectedPets = owner.getPetsInternal();

		assertTrue(collectedPets.contains(cat));
		assertTrue(collectedPets.contains(mouse));
		assertEquals(2, collectedPets.size());
	}

	@Test
	public void getPetsTest() {
		Set<Pet> pets = new HashSet<>();
		pets.add(mouse);
		pets.add(cat);
		pets.add(dog);
		owner.setPetsInternal(pets);

		List<Pet> sortedPets = new ArrayList<>();
		sortedPets.add(cat);
		sortedPets.add(dog);
		sortedPets.add(mouse);

		assertEquals(owner.getPets(), sortedPets);
	}

	@Test
	public void addPetTest() {
		owner.setPetsInternal(new HashSet<>());
		owner.addPet(cat);
		assertEquals(owner.getPets().get(0), cat);
		assertEquals(1, owner.getPets().size());
		assertEquals(owner.getPets().get(0).getOwner(), owner);
	}

	@Test
	public void removePetTest() {
		owner.setPetsInternal(new HashSet<>());
		owner.addPet(cat);
		owner.addPet(dog);
		owner.removePet(cat);

		assertEquals(owner.getPet(dog.getName()), dog);
		assertNull(owner.getPet(cat.getName()));
	}

	@Test
	public void getPetTest() {
		owner.setPetsInternal(new HashSet<>());
		owner.addPet(cat);
		owner.addPet(mouse);
		mouse.setId(1);

		assertEquals(owner.getPet(mouse.getName()), mouse);
		assertEquals(owner.getPet(cat.getName()), cat);
	}

	@Test
	public void getPetWithIgnoreNewParam() {
		owner.setPetsInternal(new HashSet<>());
		owner.addPet(cat);
		owner.addPet(mouse);
		mouse.setId(1);

		assertEquals(owner.getPet(mouse.getName(), false), mouse);
		assertEquals(owner.getPet(mouse.getName(), true), mouse);

		assertEquals(owner.getPet(cat.getName(), false), cat);
		assertNull(owner.getPet(cat.getName(), true));
	}
}
