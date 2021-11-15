package org.springframework.samples.petclinic.model;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.petclinic.model.priceCalculators.SimplePriceCalculator;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimplePriceCalculatorTest {
	private static final double BASE_CHARGE = 20;
	private static final double BASE_PRICE_PER_PET = 20;
	private static final double DELTA = 0.001;
	private static final double BASE_RARE_COEF = 1.2;

	private static final SimplePriceCalculator simplePriceCalculator = new SimplePriceCalculator();

	private static final Pet rarePetMock = mock(Pet.class);
	private static final Pet commonPetMock = mock(Pet.class);
	private static final PetType rarePetTypeMock = mock(PetType.class);
	private static final PetType commonPetType = mock(PetType.class);

	private static List<Pet> rarePets;
	private static List<Pet> commonPets;
	private static List<Pet> allPets;

	@BeforeClass
	public static void setup() {
		rarePets = Arrays.asList(rarePetMock, rarePetMock);
		commonPets = Arrays.asList(commonPetMock, commonPetMock);
		allPets = Arrays.asList(rarePetMock, rarePetMock, commonPetMock, commonPetMock);

		when(rarePetMock.getType()).thenReturn(rarePetTypeMock);
		when(commonPetMock.getType()).thenReturn(commonPetType);
		when(rarePetTypeMock.getRare()).thenReturn(true);
		when(commonPetType.getRare()).thenReturn(false);
	}

	@Test
	public void calcPriceShouldApplyRareCoefForRarePets() {
		double actualPrice =
			simplePriceCalculator.calcPrice(rarePets, BASE_CHARGE, BASE_PRICE_PER_PET, UserType.GOLD);
		double expectedPrice = BASE_CHARGE + rarePets.size() * BASE_PRICE_PER_PET * BASE_RARE_COEF;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldNotApplyRareCoefForCommonPets() {
		double actualPrice =
			simplePriceCalculator.calcPrice(commonPets, BASE_CHARGE, BASE_PRICE_PER_PET, UserType.GOLD);
		double expectedPrice = BASE_CHARGE + commonPets.size() * BASE_PRICE_PER_PET;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldApplyNewUserDiscountAfterPetsPriceCalculation() {
		double actualPrice = simplePriceCalculator.calcPrice(allPets, BASE_CHARGE, BASE_PRICE_PER_PET, UserType.NEW);
		double expectedPrice = (
			BASE_CHARGE +
			commonPets.size() * BASE_PRICE_PER_PET +
			rarePets.size() * BASE_PRICE_PER_PET * BASE_RARE_COEF
		) * UserType.NEW.discountRate;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}

	@Test
	public void calcPriceShouldReturnBaseChargeForNonNewUserIfThereAreNoPets() {
		double actualPriceForSilverUser =
			simplePriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET, UserType.SILVER);
		assertEquals(BASE_CHARGE, actualPriceForSilverUser, DELTA);

		double actualPriceForGoldUser =
			simplePriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET, UserType.GOLD);
		assertEquals(BASE_CHARGE, actualPriceForGoldUser, DELTA);
	}

	@Test
	public void calcPriceShouldReturnDiscountedBaseChargeForNewUserThereAreNoPets() {
		double actualPrice =
			simplePriceCalculator.calcPrice(Collections.emptyList(), BASE_CHARGE, BASE_PRICE_PER_PET, UserType.NEW);
		double expectedPrice = BASE_CHARGE * UserType.NEW.discountRate;
		assertEquals(expectedPrice, actualPrice, DELTA);
	}
}
