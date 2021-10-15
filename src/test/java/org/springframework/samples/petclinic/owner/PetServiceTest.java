package org.springframework.samples.petclinic.owner;

import org.junit.After;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PetServiceTest {

	private final Integer petId;
	private PetService petService;

	public PetServiceTest(Integer petId) {
		this.petId = petId;
	}

	private static class MockPetTimedCache extends PetTimedCache {
		private final Map<Integer, Pet> pets = new HashMap<>();

		public MockPetTimedCache() {
			super(null);
			for (int i = 0; i < 10; i++) {
				Pet pet = new Pet();
				pet.setName("pet num: " + i);
				pet.setId(i);
				pets.put(i, pet);
			}
		}

		@Override
		public Pet get(Integer petId) {
			return pets.get(petId);
		}
	}

	@Before
	public void setup() {
		petService = new PetService(
			new MockPetTimedCache(),
			null,
			Mockito.mock(Logger.class)
		);
	}

	@Parameters
	public static List<Integer> petIds() {
		return Arrays.asList(1, 3, 5 ,9);
	}

	@Test
	public void findPetTest() {
		Pet pet = petService.findPet(petId);
		assertEquals(pet.getId(), petId);
	}

	@After
	public void teardown() {
		petService = null;
	}
}
