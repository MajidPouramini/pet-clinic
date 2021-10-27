package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.time.LocalDate;

public class DummyPetStore {
	private static Integer name_id = 0;
    private static Integer type_id = 0;

	private static final String PET_NAME_PREFIX = "PET-";
    private static final String PET_TYPE_PREFIX = "TYPE-";

	public static Pet getDummyPet() {
		Pet newPet = new Pet();
		newPet.setId(name_id);
		newPet.setName(PET_NAME_PREFIX + name_id);
        name_id += 1;
		PetType type = new PetType();
		type.setName(PET_TYPE_PREFIX+type_id);
		newPet.setType(type_id);
		newPet.setBirthDate(LocalDate.now());
		type_id += 1;
		return newPet;
	}
}