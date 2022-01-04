package bdd;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import java.time.LocalDate;

public class PetServiceFeatureSteps {

	@Autowired
	PetService petService;

	@Autowired
	PetTimedCache petTimedCache;

	@Autowired
	PetRepository petRepository;

	@Autowired
	OwnerRepository ownerRepository;

	private Pet pet;
	private Pet foundPet;
	private Owner owner;
	private Owner foundOwner;
	private Logger logger = Mockito.mock(Logger.class);

	static final String PET_NAME = "Dogy";
	static final int PET_ID = 1;
	static final int OWNER_ID = 11;


	@Before("@pet_service_annotation")
	public void setup() {
		owner = new Owner();
		owner.setId(OWNER_ID);
		owner.setCity("Tehran");
		owner.setAddress("Gholhak");
		owner.setFirstName("Majid");
		owner.setLastName("Pouramini");
		owner.setTelephone("09123456789");

		PetType petType = new PetType();
		petType.setName("Dogy");
		petType.setId(1);
		pet = new Pet();
		pet.setId(PET_ID);
		pet.setType(petType);
		pet.setName(PET_NAME);
		pet.setBirthDate(LocalDate.parse("2022-01-01"));

		petTimedCache = new PetTimedCache(petRepository);
		petService = new PetService(petTimedCache, ownerRepository, logger);
	}

	@Given("There is an owner called {string}")
	public void thereIsAnOwnerCalled(String ownerName) {
		ownerRepository.save(owner);
	}

	@When("Save pet for owner")
	public void savePetForOwner() {
		petService.savePet(pet, owner);
	}

	@Then("Pet is saved successfully")
	public void petIsSavedSuccessfully(){
		Assertions.assertNotNull(owner.getPets());
		Assertions.assertNotNull(petService.findPet(PET_ID));
		Assertions.assertEquals(OWNER_ID, petService.findPet(PET_ID).getOwner().getId());
	}

	@When("Performing find owner")
	public void performingFindOwner(){
		foundOwner = petService.findOwner(OWNER_ID);
	}

	@Then("The owner is returned successfully")
	public void ownerReturnedSuccessfully(){
		Assertions.assertEquals(OWNER_ID, foundOwner.getId());
	}

	@Given("There is a pet called {string}")
	public void thereIsAPetCalled(String name) {
		owner.addPet(pet);
		petTimedCache.save(pet);
	}

	@When("Find pet")
	public void performingFindPet(){
		foundPet = petService.findPet(PET_ID);
	}

	@Then("The pet is returned successfully")
	public void petReturnedSuccessfully(){
		Assertions.assertEquals(pet.getId(), foundPet.getId());
		Assertions.assertEquals(pet.getName(), foundPet.getName());
	}

	@When("Performing new pet")
	public void performingNwePer(){
		foundPet = petService.newPet(owner);
	}

	@Then("An empty pet is added to the owner")
	public void emptyPetAddedToTheOwner(){
		Mockito.verify(logger).info("add pet for owner {}", owner.getId());
		Assertions.assertNotNull(owner.getPets());
	}

}
