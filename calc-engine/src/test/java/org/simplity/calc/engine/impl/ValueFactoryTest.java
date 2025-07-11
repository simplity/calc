package org.simplity.calc.engine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.DataType;

@RunWith(Parameterized.class)
public class ValueFactoryTest {

	private final IValue valueToTest;
	private final DataType expectedType;
	private final Object valueToMatch;
	private final IValue duplicate;

	// JUnit 4 uses a constructor to inject parameters for each run
	public ValueFactoryTest(IValue valueToTest, DataType expectedType, Object valueToMatch, IValue duplicate) {
		this.valueToTest = valueToTest;
		this.expectedType = expectedType;
		this.valueToMatch = valueToMatch;
		this.duplicate = duplicate;

	}

	// This static method provides the data for the test runs
	@Parameters(name = "{index}: Type={1}")
	public static Collection<Object[]> provideTestCases() {
		return Arrays.asList(new Object[][] {
				{ Values.newValue(new BigDecimal("123.45")), DataType.NUMBER, new BigDecimal("123.45"),
						Values.newValue(new BigDecimal("123.45")) },
				{ Values.newValue(new BigDecimal("0")), DataType.NUMBER, new BigDecimal("0"),
						Values.newValue(new BigDecimal("0")) },
				{ Values.newValue(new BigDecimal("-12")), DataType.NUMBER, new BigDecimal("-12"),
						Values.newValue(new BigDecimal("-12")) },
				{ Values.newValue("hello"), DataType.STRING, "hello", Values.newValue("hello") },
				{ Values.newValue(""), DataType.STRING, "", Values.newValue("") },
				{ Values.newValue("!@#$"), DataType.STRING, "!@#$", Values.newValue("!@#$") },
				{ Values.newValue(true), DataType.BOOLEAN, true, Values.newValue(true) },
				{ Values.newValue(false), DataType.BOOLEAN, false, Values.newValue(false) },
				{ Values.newValue(LocalDate.of(2025, 1, 1)), DataType.DATE, LocalDate.of(2025, 1, 1),
						Values.newValue(LocalDate.of(2025, 1, 1)) } });
	}

	@Test
	public void testValueCreationAndAccess() {
		// 1. Assert that the type is correct
		assertEquals(this.expectedType, this.valueToTest.getValueType());

		// 2. assert that getValue() matches
		assertEquals(this.valueToMatch, this.valueToTest.getValue());

		// 3. Assert that the correct getter works (and the others fail)
		if (this.expectedType == DataType.NUMBER) {
			assertEquals(this.valueToMatch, this.valueToTest.getNumberValue());
		} else {
			try {
				this.valueToTest.getNumberValue();
				fail("Expected IllegalStateException for getNumberValue()");
			} catch (IllegalStateException e) {
				// This is the expected outcome for non-number types
			}
		}

		if (this.expectedType == DataType.BOOLEAN) {
			assertEquals(this.valueToMatch, this.valueToTest.getBooleanValue());
		} else {
			try {
				this.valueToTest.getBooleanValue();
				fail("Expected IllegalStateException for getBooleanValue()");
			} catch (IllegalStateException e) {
				// This is the expected outcome for non-boolean types
			}
		}

		if (this.expectedType == DataType.STRING) {
			assertEquals(this.valueToMatch, this.valueToTest.getStringValue());
		} else {
			try {
				this.valueToTest.getStringValue();
				fail("Expected IllegalStateException for getStringValue()");
			} catch (IllegalStateException e) {
				// This is the expected outcome for non-string types
			}
		}

		if (this.expectedType == DataType.DATE) {
			assertEquals(this.valueToMatch, this.valueToTest.getDateValue());
		} else {
			try {
				this.valueToTest.getDateValue();
				fail("Expected IllegalStateException for getDateValue()");
			} catch (IllegalStateException e) {
				// This is the expected outcome for non-date types
			}
		}

		// 4. toString()
		assertEquals(this.valueToMatch.toString(), this.valueToTest.toString());

		// 5 hash code
		assertEquals(this.valueToTest.hashCode(), Objects.hash(this.valueToMatch));

		// 6 equals
		assertEquals(this.valueToTest.equals(this.valueToTest), true);
		assertEquals(this.valueToTest.equals(this.duplicate), true);
		assertEquals(this.valueToTest.equals(this.valueToMatch), false);

	}

	@Test
	public void testDefaultValues() {
		assertEquals(Values.newDefaultValue(DataType.BOOLEAN), Values.newValue(false));
		assertEquals(Values.newDefaultValue(DataType.NUMBER), Values.newValue(new BigDecimal("0")));
		assertEquals(Values.newDefaultValue(DataType.STRING), Values.newValue(""));
		assertEquals(Values.newDefaultValue(DataType.DATE), Values.newValue(LocalDate.now()));
	}
}