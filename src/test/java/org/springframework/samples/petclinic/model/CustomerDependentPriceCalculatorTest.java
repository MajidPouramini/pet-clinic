package org.springframework.samples.petclinic.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.petclinic.model.priceCalculators.CustomerDependentPriceCalculator;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerDependentPriceCalculatorTest {
	private static final double BASE_CHARGE = 20000;
	private static final double BASE_PRICE_PER_PET = 10000;
	private static final double DELTA = 0.001;
	private static final double RARE_INFANCY_COEF = 1.4;
	private static final double COMMON_INFANCY_COEF = 1.2;
	private static final double BASE_RARE_COEF = 1.2;
	private static final UserType NEW_USER = UserType.NEW;
	private static final UserType GOLD_USER = UserType.GOLD;

	private static final CustomerDependentPriceCalculator customerDependentPriceCalculator
		= new CustomerDependentPriceCalculator();

	private static final PetType rarePetType = mock(PetType.class);
	private static final PetType commonPetType = mock(PetType.class);
	private static final Pet rarePet = mock(Pet.class);
	private static final Pet commonPet = mock(Pet.class);
	private static final Pet rareInfantPet = mock(Pet.class);
	private static final Pet commonInfantPet = mock(Pet.class);

	private static final List<Pet> manyCommonPets = Arrays.asList(commonPet, commonPet, commonPet, commonPet, commonPet,
		commonPet, commonPet, commonPet, commonPet, commonPet, commonPet, commonPet);

	@BeforeClass
	public static void setup() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2000);
		final Date adultPetBirthDate = calendar.getTime();
		final Date infantPetBirthDate = new Date();

		when(rarePetType.getRare()).thenReturn(true);
		when(commonPetType.getRare()).thenReturn(false);

		when(rarePet.getType()).thenReturn(rarePetType);
		when(rarePet.getBirthDate()).thenReturn(adultPetBirthDate);

		when(commonPet.getType()).thenReturn(commonPetType);
		when(commonPet.getBirthDate()).thenReturn(adultPetBirthDate);

		when(rareInfantPet.getType()).thenReturn(rarePetType);
		when(rareInfantPet.getBirthDate()).thenReturn(infantPetBirthDate);

		when(commonInfantPet.getType()).thenReturn(commonPetType);
		when(commonInfantPet.getBirthDate()).thenReturn(infantPetBirthDate);
	}

	@Test
	public void calcPriceShouldReturnBaseChargeIfThereAreNoPetsForNonGoldUser() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE,
			BASE_PRICE_PER_PET, NEW_USER);
		assertEquals(BASE_CHARGE, price, DELTA);
	}

	@Test
	public void calcPriceShouldReturnBaseChargeIfThereAreNoPetsForGoldUser() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE,
			BASE_PRICE_PER_PET, GOLD_USER);
		assertEquals(BASE_CHARGE, price, DELTA);
	}

	@Test
	public void calcPriceShouldNotApplyAnyDiscountForNonGoldUsersWhenDiscountMinScoreIsNotReached() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.singletonList(commonPet), BASE_CHARGE,
			BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE + BASE_PRICE_PER_PET;
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountForGoldUsersEvenWhenDiscountMinScoreIsNotReached() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.singletonList(commonPet), BASE_CHARGE,
			BASE_PRICE_PER_PET, GOLD_USER);
		double expectedPrice = BASE_CHARGE + BASE_PRICE_PER_PET * GOLD_USER.discountRate;
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldNotApplyDiscountOnBaseChargeForNewUsersWhenDiscountMinScoreIsReached() {
		double price = customerDependentPriceCalculator.calcPrice(manyCommonPets, BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE + (BASE_PRICE_PER_PET * manyCommonPets.size() * NEW_USER.discountRate);
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldApplyDiscountOnEveryThingForPremiumUsersWhenDiscountMinScoreIsReached() {
		double price = customerDependentPriceCalculator.calcPrice(manyCommonPets, BASE_CHARGE, BASE_PRICE_PER_PET, GOLD_USER);
		double expectedPrice = (BASE_CHARGE + BASE_PRICE_PER_PET * manyCommonPets.size()) * GOLD_USER.discountRate;
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldApplyCommonInfancyCoefForCommonInfantPets() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.singletonList(commonInfantPet),
			BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE + BASE_PRICE_PER_PET * COMMON_INFANCY_COEF;
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldApplyRareCoefForRarePets() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.singletonList(rarePet),
			BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE + BASE_PRICE_PER_PET * BASE_RARE_COEF;
		assertEquals(expectedPrice, price, DELTA);
	}

	@Test
	public void calcPriceShouldApplyRareCoefAndRareInfancyCoefForRareInfantPets() {
		double price = customerDependentPriceCalculator.calcPrice(Collections.singletonList(rareInfantPet),
			BASE_CHARGE, BASE_PRICE_PER_PET, NEW_USER);
		double expectedPrice = BASE_CHARGE + BASE_PRICE_PER_PET * BASE_RARE_COEF * RARE_INFANCY_COEF;
		assertEquals(expectedPrice, price, DELTA);
	}
}
