package org.springframework.samples.petclinic.utility;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.visit.Visit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.samples.petclinic.utility.PriceCalculator.calcPrice;

public class PriceCalculatorTest {

	private static final double RARE_INFANCY_COEF = 1.4;
	private static final double BASE_RARE_COEF = 1.2;
	private static final int DISCOUNT_MIN_SCORE = 10;
	private static final int DISCOUNT_PRE_VISIT = 2;
	private static final int BASE_CHARGE = 15000;
	private static final int BASE_PRICE_PER_PET = 20000;
	private static final double DELTA = 0.01;
	private static final int ADULT_AGE = 3;
	private static final int INFANT_AGE = 1;
	private static final int OLD_VISIT_THRESH = 100;
	private static final double SINGLE_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF;
	private static final double SINGLE_INFANT_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF * RARE_INFANCY_COEF;

	private static final Pet pet = mock(Pet.class);
	private static final Pet infantPet = mock(Pet.class);
	private static final Visit newVisit = new Visit();
	private static final List<Visit> newVisits = Arrays.asList(newVisit, newVisit, newVisit);

	@BeforeAll
	public static void setup() {
		newVisit.setDate(LocalDate.now().minusDays(OLD_VISIT_THRESH - 1));

		final LocalDate adultPetBirthDate = LocalDate.now().minusYears(ADULT_AGE);
		final LocalDate infantPetBirthDate = LocalDate.now().minusYears(INFANT_AGE);

		when(pet.getBirthDate()).thenReturn(adultPetBirthDate);
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(Collections.emptyList());

		when(infantPet.getBirthDate()).thenReturn(infantPetBirthDate);
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(Collections.emptyList());
	}

	/** This test is for situation that no pet is provided for method */
	@Test
	public void calcPriceReturnZeroOnNoPets() {
		double expectedPrice = 0;
		double actualPrice = calcPrice(new ArrayList<>(), BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}


	@Test
	public void calcPriceReturnOnePetPriceEvenIfItHasVisits() {
		ArrayList<Visit> visits = new ArrayList<>();
		visits.add(newVisit);
		when(pet.getVisits()).thenReturn(visits);

		ArrayList<Pet> pets = new ArrayList<>();
		pets.add(pet);

		double expectedPrice = BASE_PRICE_PER_PET * BASE_RARE_COEF;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	/**
	 * This test is for when there is only one none-infant pet is provided
	 * so only base rare coef should be applied
	 * */
	@Test
	public void calcPriceReturnOnePetPriceForSingleNoneInfantPet() {
		ArrayList<Pet> pets = new ArrayList<>();
		pets.add(pet);

		double expectedPrice = BASE_PRICE_PER_PET * BASE_RARE_COEF;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	/**
	 * This test is for when only an infant pet provided and therefore rare
	 * infancy coef also should be applied
	 * */
	@Test
	public void calcPriceReturnPriceMultipleRareInfancyCoefForSingleInfantPet() {
		ArrayList<Pet> pets = new ArrayList<>();
		pets.add(infantPet);
		double expectedPrice = BASE_PRICE_PER_PET * BASE_RARE_COEF * RARE_INFANCY_COEF;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	@Test
	public void calcPriceShouldNotApplyDiscountIfDiscountMinScoreIsNotReached() {
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE - 1; ++i)
			pets.add(pet);
		double expectedPrice = pets.size() * SINGLE_PET_PRICE;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	@Test
	public void calcPriceShouldApplyDiscountPreVisitAfterDiscountMinScoreIsReachedForNewVisits() {
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(pet);
		double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}
}
