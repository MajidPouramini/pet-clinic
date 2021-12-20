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

import java.time.*;

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

	/**
	 * sample
	 * */
	@Test
	public void calcPriceReturnZeroOnNoPets() {
		double expectedPrice = 0;
		double calcedPrice = calcPrice(new ArrayList<>(), 0, 0 );
		assertEquals(expectedPrice, calcedPrice);
	}


	/**

	 each infant should increase (discountCounter + 2) test 
	
	 * */
	@Test
	public void calcPriceEachInfantShouldIncreaseCountBy2() {
		//given
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		int required_size=DISCOUNT_MIN_SCORE/2;
		//when
		for (int i = 0; i < required_size; ++i)
			pets.add(infantPet);

		final double calcedPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (required_size - 1) * SINGLE_INFANT_PET_PRICE;
		final double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_INFANT_PET_PRICE;
		//then
		assertEquals(expectedPrice, calcedPrice, DELTA);
	}

	/**

	  test price for new vists (ADULT_AGE) + has minimum discountCounter 
	  shoult calcualte totalPrice = (totalPrice * DISCOUNT_PRE_VISIT) + baseCharge;

	 * */
	@Test
	public void calcPriceWithDoscount calcPriceShouldApplyDiscountWhenMinScoreForNewVisits() {
		//given
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(newVisits);
		List<Pet> pets = new ArrayList<>();
		//when
		for (int i = 0; i < DISCOUNT_MIN_SCORE + 1; ++i)
			pets.add(pet);
		final double calcedPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (pets.size() - 1) * SINGLE_PET_PRICE;
		final double priceFirstDiscount = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double priceSecondDiscount = priceFirstDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double expectedPrice = priceSecondDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		//then
		assertEquals(expectedPrice, calcedPrice, DELTA);
	}

	/**

	  test price for old vists (INFANT_AGE) + has minimum discountCounter 
	  shoult calcualte totalPrice = (totalPrice + baseCharge) * (daysFromLastVisit / 100 + visits.size());

	 * */
	@Test
	public void calcPriceShouldApplyDiscountForOldVisitsInfants() {
		//given
		final int OLD_VISIT_DAYS = OLD_VISIT_THRESH + 1;
		LocalDate visited_date = LocalDate.now().minusDays(OLD_VISIT_DAYS);

		final Visit oldVisit = new Visit().setDate(visited_date);
		final List<Visit> visits = Arrays.asList(newVisit, newVisit, oldVisit, oldVisit);
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		int required_size=DISCOUNT_MIN_SCORE/2;
		//when
		for (int i = 0; i < required_size; ++i)
			pets.add(infantPet);
		final double calcedPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (required_size - 1) * SINGLE_INFANT_PET_PRICE;
		final int oldVisitDiscount = OLD_VISIT_DAYS/OLD_VISIT_THRESH + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_INFANT_PET_PRICE;
		//then
		assertEquals(expectedPrice, calcedPrice, DELTA);
	}

	/**

	  test price for old vists (ADULT_AGE) + has minimum discountCounter 
	  shoult calcualte totalPrice = (totalPrice + baseCharge) * (daysFromLastVisit / 100 + visits.size());

	 * */
	@Test
	public void calcPriceShouldApplyDiscountForOldVisitsAdult() {
		//given
		final int OLD_VISIT_DAYS = OLD_VISIT_THRESH + 1;
		LocalDate visited_date = LocalDate.now().minusDays(OLD_VISIT_DAYS);
		final Visit oldVisit = new Visit().setDate(visited_date);
		final List<Visit> visits = Collections.singletonList(oldVisit);
		when(pet.getVisitsUntilAge(ADULT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		//when
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(pet);
		final double calcedPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final int oldVisitDiscount = OLD_VISIT_DAYS/OLD_VISIT_THRESH + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_PET_PRICE;
		//then
		assertEquals(expectedPrice, calcedPrice, DELTA);
	}

}
