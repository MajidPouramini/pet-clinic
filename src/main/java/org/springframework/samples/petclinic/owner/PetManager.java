package org.springframework.samples.petclinic.owner;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PetManager {

	private final PetTimedCache pets;

	private final OwnerRepository owners;

	private final Logger log;

	@Autowired
	public PetManager(
		PetTimedCache pets,
		OwnerRepository owners,
		Logger criticalLogger) {
		this.pets = pets;
		this.owners = owners;
		this.log = criticalLogger;
	}

	public Owner findOwner(int ownerId) {
		log.info("find owner {}", ownerId);
		return this.owners.findById(ownerId);
	}

	public Pet newPet(Owner owner) {
		log.info("add pet for owner {}", owner.getId());
		Pet pet = new Pet();
		owner.addPet(pet);
		return pet;
	}

	public Pet findPet(int petId) {
		log.info("find pet by id {}", petId);
		return this.pets.get(petId);
	}

	public void savePet(Pet pet, Owner owner) {
		log.info("save pet {}", pet.getId());
		owner.addPet(pet);
		this.pets.save(pet);
	}

	public List<Pet> getOwnerPets(int ownerId) {
		log.info("finding the owner's pets by id {}", ownerId);
		Owner owner = findOwner(ownerId);
		List<Pet> pets = owner.getPets();
		return pets;
	}

	public Set<PetType> getOwnerPetTypes(int ownerId) {
		log.info("finding the owner's petTypes by id {}", ownerId);
		Owner owner = findOwner(ownerId);
		Set<PetType> petTypes = new HashSet<>();
		for (Pet pet: owner.getPets()) {
			petTypes.add(pet.getType());
		}
		return petTypes;
	}

	public List<Visit> getVisitsBetween(int petId, LocalDate startDate, LocalDate endDate) {
		log.info("get visits for pet {} from {} since {}", petId, startDate, endDate);
		Pet pet = pets.get(petId);
		List<Visit> petVisits = pet.getVisitsBetween(startDate, endDate);
		return petVisits;
	}

}
