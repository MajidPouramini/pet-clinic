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

	private static final int OLD_VISIT_THRESHOLD = 100;

	private static final double SINGLE_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF;
	private static final double SINGLE_INFANT_PET_PRICE = BASE_PRICE_PER_PET * BASE_RARE_COEF * RARE_INFANCY_COEF;

	private static final Pet adultPet = mock(Pet.class);
	private static final Pet infantPet = mock(Pet.class);

	private static final Visit visit = new Visit();
	private static final List<Visit> visitList = Arrays.asList(visit, visit, visit);

	@BeforeAll
	public static void setup() {
		visit.setDate(LocalDate.now().minusDays(OLD_VISIT_THRESHOLD - 1));
		when(adultPet.getBirthDate()).thenReturn(LocalDate.now().minusYears(ADULT_AGE));
		when(adultPet.getVisitsUntilAge(ADULT_AGE)).thenReturn(Collections.emptyList());
		when(infantPet.getBirthDate()).thenReturn(LocalDate.now().minusYears(INFANT_AGE));
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(Collections.emptyList());
	}

	/** This test is for situation that no pet is provided for method */
	@Test
	public void calcPriceReturnZeroOnNoPets() {
		double expectedPrice = 0;
		double actualPrice = calcPrice(new ArrayList<>(), BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	/** This test is for the time that pet has visits but discount did not reach min score*/
	@Test
	public void calcPriceReturnOnePetPriceEvenIfItHasVisits() {
		ArrayList<Visit> visits = new ArrayList<>();
		visits.add(visit);
		when(adultPet.getVisits()).thenReturn(visits);

		ArrayList<Pet> pets = new ArrayList<>();
		pets.add(adultPet);

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
		pets.add(adultPet);

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
			pets.add(adultPet);
		double expectedPrice = pets.size() * SINGLE_PET_PRICE;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	@Test
	public void calcPriceShouldApplyDiscountPreVisitAfterDiscountMinScoreIsReachedForNewVisits() {
		when(adultPet.getVisitsUntilAge(ADULT_AGE)).thenReturn(visitList);
		List<Pet> pets = new ArrayList<>();
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(adultPet);
		double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		assertEquals(expectedPrice, actualPrice);
	}

	/**
	 each infant should increase (discountCounter + 2) test
	 * */
	@Test
	public void calcPriceEachInfantShouldIncreaseCountBy2() {
		//given
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(visitList);
		List<Pet> pets = new ArrayList<>();
		int required_size=DISCOUNT_MIN_SCORE/2;
		//when
		for (int i = 0; i < required_size; ++i)
			pets.add(infantPet);

		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (required_size - 1) * SINGLE_INFANT_PET_PRICE;
		final double expectedPrice = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_INFANT_PET_PRICE;
		//then
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	/**

	 test price for new visits (ADULT_AGE) + has minimum discountCounter
	 should calculate totalPrice = (totalPrice * DISCOUNT_PRE_VISIT) + baseCharge;

	 * */
	@Test
	public void calcPriceShouldApplyDiscountWhenMinScoreForNewVisits() {
		//given
		when(adultPet.getVisitsUntilAge(ADULT_AGE)).thenReturn(visitList);
		List<Pet> pets = new ArrayList<>();
		//when
		for (int i = 0; i < DISCOUNT_MIN_SCORE + 2; ++i)
			pets.add(adultPet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final double priceAfterFirstDiscount = priceBeforeMinScore * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double priceAfterSecondDiscount = priceAfterFirstDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		final double expectedPrice = priceAfterSecondDiscount * DISCOUNT_PRE_VISIT + BASE_CHARGE + SINGLE_PET_PRICE;
		//then
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	/**
	 * test price for old visits (INFANT_AGE) + has minimum discountCounter
	 * should calculate totalPrice = (totalPrice + baseCharge) * (daysFromLastVisit / 100 + visits.size());
	 */
	@Test
	public void calcPriceShouldApplyDiscountForOldVisitsInfants() {
		//given
		final int OLD_VISIT_DAYS = OLD_VISIT_THRESHOLD + 1;
		LocalDate visited_date = LocalDate.now().minusDays(OLD_VISIT_DAYS);

		final Visit oldVisit = new Visit().setDate(visited_date);
		final List<Visit> visits = Arrays.asList(visit, visit, oldVisit, oldVisit);
		when(infantPet.getVisitsUntilAge(INFANT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		int required_size=DISCOUNT_MIN_SCORE/2;
		//when
		for (int i = 0; i < required_size; ++i)
			pets.add(infantPet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (required_size - 1) * SINGLE_INFANT_PET_PRICE;
		final int oldVisitDiscount = OLD_VISIT_DAYS/ OLD_VISIT_THRESHOLD + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_INFANT_PET_PRICE;
		//then
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	/**
	 test price for old visits (ADULT_AGE) + has minimum discountCounter
	 should calculate totalPrice = (totalPrice + baseCharge) * (daysFromLastVisit / 100 + visits.size());
	 * */
	@Test
	public void calcPriceShouldApplyDiscountForOldVisitsAdult() {
		//given
		final int OLD_VISIT_DAYS = OLD_VISIT_THRESHOLD + 1;
		LocalDate visited_date = LocalDate.now().minusDays(OLD_VISIT_DAYS);
		final Visit oldVisit = new Visit().setDate(visited_date);
		final List<Visit> visits = Collections.singletonList(oldVisit);
		when(adultPet.getVisitsUntilAge(ADULT_AGE)).thenReturn(visits);
		List<Pet> pets = new ArrayList<>();
		//when
		for (int i = 0; i < DISCOUNT_MIN_SCORE; ++i)
			pets.add(adultPet);
		final double actualPrice = calcPrice(pets, BASE_CHARGE, BASE_PRICE_PER_PET);
		final double priceBeforeMinScore = (DISCOUNT_MIN_SCORE - 1) * SINGLE_PET_PRICE;
		final int oldVisitDiscount = OLD_VISIT_DAYS/ OLD_VISIT_THRESHOLD + visits.size();
		final double expectedPrice = (priceBeforeMinScore + BASE_CHARGE) * oldVisitDiscount + SINGLE_PET_PRICE;
		//then
		assertEquals(expectedPrice, actualPrice, DELTA);
	}
}
