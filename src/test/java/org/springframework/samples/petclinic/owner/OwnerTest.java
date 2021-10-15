package org.springframework.samples.petclinic.owner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {
	Owner owner;

	String address1 = "Tehran, Iran";
	String address2 = "Mashhad, Iran";
	String city1 = "Tehran";
	String city2 = "Mashhad";
	String phone1 = "09123456789";
	String phone2 = "09129876543";

	Pet dog = new Pet();
	Pet cat = new Pet();
	Pet mouse = new Pet();

	@Before
	public void setup() {
		owner = new Owner();
		owner.setAddress(address1);
		owner.setCity(city1);
		owner.setTelephone(phone1);

		dog.setName("Dog");
		cat.setName("Cat");
		mouse.setName("Mouse");

		owner.addPet(dog);
		owner.addPet(cat);

	}

	@Test
	public void getAddressTest() {
		 assertEquals(address1, owner.getAddress());
	}

	@Test
	public void setAddress() {
		owner.setAddress(address2);
		assertEquals(address2, owner.getAddress());
	}

	@Test
	public void getCityTest() {
		assertEquals(city1, owner.getCity());
	}

	@Test
	public void setCityTest() {
		owner.setCity(city2);
		assertEquals(city2, owner.getCity());
	}

	@Test
	public void getTelephoneTest() {
		assertEquals(phone1, owner.getTelephone());
	}

	@Test
	public void setTelephoneTest() {
		owner.setTelephone(phone2);
		assertEquals(phone2, owner.getTelephone());
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

	@After
	public void teardown() {
		owner = null;
		dog = null;
		cat = null;
		mouse = null;
	}
}
