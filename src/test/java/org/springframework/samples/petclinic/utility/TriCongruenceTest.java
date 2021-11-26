package org.springframework.samples.petclinic.utility;

import com.github.mryf323.tractatus.*;
import com.github.mryf323.tractatus.experimental.extensions.ReportingExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ReportingExtension.class)
@ClauseDefinition(clause = 'a', def = "t1arr[0] != t2arr[0]")
@ClauseDefinition(clause = 'b', def = "t1arr[1] != t2arr[1]")
@ClauseDefinition(clause = 'c', def = "t1arr[2] != t2arr[2]")
@ClauseDefinition(clause = 'p', def = "t1arr[0] < 0")
@ClauseDefinition(clause = 'q', def = "t1arr[0] + t1arr[1] < t1arr[2]")
class TriCongruenceTest {

	private static final Logger log = LoggerFactory.getLogger(TriCongruenceTest.class);

	/**
	 * this function is a predicate that if two triangles have the same sides
	 * @param t1arr sides of second triangle
	 * @param t2arr sides of second triangle
	 * @return true if triangles have the same sides
	 */
	public boolean predicateForSameSides(double[] t1arr, double[] t2arr) {
		// a + b + c
		return t1arr[0] != t2arr[0] || t1arr[1] != t2arr[1] || t1arr[2] != t2arr[2];
	}

	/**
	 * this function is a predicate that if you can make a triangle with these sides
	 * @param t1arr sides of first triangle
	 * @return true if you can make triangle
	 */
	public boolean predicateForMakeTriangle(double[] t1arr) {
		// p + q
		return t1arr[0] < 0 || t1arr[0] + t1arr[1] < t1arr[2];
	}

	/**
	 * Line 14 tests using corresponding unique true points and near false point coverage
	 */
	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "a",
		valuations = {
			@Valuation(clause = 'a', valuation = true),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test1 () {
		assertTrue(predicateForSameSides(new double[] {1, 2, 3}, new double[] {2, 2, 3}));
	}

	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "b",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = true),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test2 () {
		assertTrue(predicateForSameSides(new double[] {1, 2, 3}, new double[] {2, 2, 3}));
	}

	@UniqueTruePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "c",
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = true)
		}
	)
	@Test
	public void test3 () {
		assertTrue(predicateForSameSides(new double[] {1, 2, 3}, new double[] {2, 2, 3}));
	}

	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "a",
		clause = 'a',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "b",
		clause = 'b',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@NearFalsePoint(
		predicate = "a + b + c",
		dnf = "a + b + c",
		implicant = "c",
		clause = 'c',
		valuations = {
			@Valuation(clause = 'a', valuation = false),
			@Valuation(clause = 'b', valuation = false),
			@Valuation(clause = 'c', valuation = false)
		}
	)
	@Test
	public void test4() {
		assertFalse(predicateForSameSides(new double[] {1, 2, 3}, new double[] {1, 2, 3}));
	}


	/**
	 * TODO
	 * explain your answer here
	 */
	private static boolean questionTwo(boolean a, boolean b, boolean c, boolean d, boolean e) {
		boolean predicate = false;
//		predicate = a predicate with any number of clauses
		return predicate;
	}

	@Test
	public void sampleTest() {
		Triangle t1 = new Triangle(2, 3, 7);
		Triangle t2 = new Triangle(7, 2, 3);
		boolean areCongruent = TriCongruence.areCongruent(t1, t2);
		log.debug("Triangles identified as '{}'.", areCongruent ? "Congruent" : "Not Congruent");
		Assertions.assertFalse(areCongruent);
	}
}
