package org.springframework.samples.petclinic.owner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.samples.petclinic.utility.DummyPetStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OwnerTest {
	private static Owner owner;

	@Before
	public void setup() {
		owner = new Owner();
	}

    // State Verification test for Owner
	@Test
	public void addPetTestStateVerification() {
        //given
		Pet newPet = DummyPetStore.getNewPet();

        //when
		owner.addPet(newPet);

        //then
		assertEquals(newPet.getOwner(), owner);
		assertTrue(owner.getPets().contains(newPet));
	}

    // Behavior Verification test for Owner
    @Test
	public void addPetTestBehaviorVerification() {
        //given
		Pet mockPet = mock(Pet.class);

        //when
		when(mockPet.isNew()).thenReturn(true);
		owner.addPet(mockPet);

        //then
		verify(mockPet).setOwner(owner);
		assertTrue(owner.getPets().contains(mockPet));
	}    
}