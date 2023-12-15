package ru.alexnv.apps.wallet.in.dto.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.alexnv.apps.wallet.domain.dto.AbstractDto;
import ru.alexnv.apps.wallet.in.servlets.ServletsUtility;

class DtoValidatorTest {

	private DtoAnnotationsInner dtoAnnotations;
	private ServletsUtility util;

	class DtoAnnotationsInner extends AbstractDto {

		@NotNull(message = "Не может быть пустым или null.")
		@Size(min = 3, max = 20, message = "Длина должна быть от 3 до 20 символов.")
		private String testString;

		@Positive
		private Long testIdLong;

		@Null
		private Object testNull;
		
		@NotBlank
		@Digits(integer = 2, fraction = 1)
		private String decimalString;

		public String getTestString() {
			return testString;
		}

		public void setTestString(String testString) {
			this.testString = testString;
		}

		public Long getTestIdLong() {
			return testIdLong;
		}

		public void setTestIdLong(Long testIdLong) {
			this.testIdLong = testIdLong;
		}

		public Object getTestNull() {
			return testNull;
		}

		public void setTestNull(Object testNull) {
			this.testNull = testNull;
		}

		/**
		 * @return the decimalString
		 */
		public String getDecimalString() {
			return decimalString;
		}

		/**
		 * @param decimalString the decimalString to set
		 */
		public void setDecimalString(String decimalString) {
			this.decimalString = decimalString;
		}
	}

	@BeforeEach
	final void setup() {
		dtoAnnotations = new DtoAnnotationsInner();
		util = new ServletsUtility();
		validDefaults();
	}

	@Test
	final void should_returnTrue_whenNoViolations() throws IllegalArgumentException, IllegalAccessException {
		// defaults valid
		
		List<String> violations = util.validateDto(dtoAnnotations);

		assertTrue(violations.isEmpty());
	}
	
	static Stream<Arguments> testStringCases() {
		return Stream.of(
				Arguments.of(Named.of("Should return false when field is empty", "")),
				Arguments.of(Named.of("Should return false when field is null", null)),
				Arguments.of(Named.of("Should return false when field size is too big", "123456789012345678901234567890"))
		);
	}

	@ParameterizedTest
	@MethodSource("testStringCases")
	final void should_returnFalse_whenFieldEmpty(String testString) throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setTestString(testString);

		List<String> violations = util.validateDto(dtoAnnotations);

		assertFalse(violations.isEmpty());
	}

	@Test
	final void should_returnTrue_whenFieldNull() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setTestNull(null);

		List<String> violations = util.validateDto(dtoAnnotations);

		assertTrue(violations.isEmpty());
	}

	@Test
	final void should_returnFalse_whenFieldNotNull() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setTestNull(new Object());

		List<String> violations = util.validateDto(dtoAnnotations);

		assertFalse(violations.isEmpty());
	}
	
	@Test
	final void should_returnFalse_whenFieldNotPositive() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setTestIdLong(-1L);

		List<String> violations = util.validateDto(dtoAnnotations);

		assertFalse(violations.isEmpty());
	}
	
	@Test
	final void should_returnTrue_whenFieldNotBlank() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setDecimalString("2.1");

		List<String> violations = util.validateDto(dtoAnnotations);

		assertTrue(violations.isEmpty());
	}
	
	@Test
	final void should_returnFalse_whenFieldBlank() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setDecimalString("");

		List<String> violations = util.validateDto(dtoAnnotations);

		assertFalse(violations.isEmpty());
	}
	
	@Test
	final void should_returnTrue_whenFieldDigitsInRange() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setDecimalString("2.1");

		List<String> violations = util.validateDto(dtoAnnotations);

		assertTrue(violations.isEmpty());
	}
	
	@Test
	final void should_returnFalse_whenFieldDigitsNotInRange() throws IllegalArgumentException, IllegalAccessException {
		dtoAnnotations.setDecimalString("22222.1");

		List<String> violations = util.validateDto(dtoAnnotations);

		assertFalse(violations.isEmpty());
	}
	
	final private void validDefaults() {
		dtoAnnotations.setDecimalString("2.1");
		dtoAnnotations.setTestIdLong(1L);
		dtoAnnotations.setTestString("normal");
		dtoAnnotations.setTestNull(null);
	}
	
}
