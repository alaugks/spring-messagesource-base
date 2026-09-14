// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.record;

import io.github.alaugks.spring.messagesource.base.records.TransFile;
import io.github.alaugks.spring.messagesource.base.records.TransFileInterface;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransFileTest {

	@ParameterizedTest()
	@MethodSource("provider_transunits")
	void test_record(Locale locale, byte[] content, Resource resource) {

		TransFileInterface translationFile = new TransFile(
			locale,
				content,
				resource
		);

		assertEquals(locale, translationFile.locale());
		assertEquals(content, translationFile.content());
		assertEquals(resource, translationFile.resource());
	}

	private static Stream<Arguments> provider_transunits() throws IOException {
		byte[] content;
		try (InputStream inputStream = TransFileTest.class.getClassLoader()
			.getResourceAsStream("translations_en_US/messages_en_US.txt")) {
			assertNotNull(inputStream);
			content = inputStream.readAllBytes();
		}
		Resource resource = new ClassPathResource("translations_en_US/messages_en_US.txt");

		return Stream.of(
			Arguments.of(Locale.forLanguageTag("en-US"), content, resource),
			Arguments.of(Locale.forLanguageTag("en-US"), content, null),
			Arguments.of(Locale.forLanguageTag("en-US"), null, null)
		);
	}


	@Test
	void test_equals() {
		TransFileInterface a = new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {1, 2, 3}
		);
		TransFile b = new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {1, 2, 3}
		);

		assertEquals(a, b);
		assertEquals(a, a);
	}

	@Test
	void test_not_equals() {
		TransFileInterface a = new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {1, 2, 3}
		);

		assertNotEquals(null, a);
		assertNotEquals(a, new TransFile(
			Locale.forLanguageTag("de-DE"),
			new byte[] {1, 2, 3}
		));
		assertNotEquals(a, new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {4, 5, 6}
		));
	}

	@Test
	void test_hash_code() {
		TransFileInterface a = new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {1, 2, 3}
		);
		TransFile b = new TransFile(
		Locale.forLanguageTag("en-US"),
		new byte[] {1, 2, 3}
		);

		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	void test_to_string() {
		TransFileInterface translationFile = new TransFile(
			Locale.forLanguageTag("en-US"),
			new byte[] {1, 2, 3}
		);

        assertEquals("TranslationFile[locale=en_US, content=3 bytes]", translationFile.toString());
	}

	@Test
	void test_to_string_null_content() {
		TransFileInterface translationFile = new TransFile(
			Locale.forLanguageTag("en-US"),
			null
		);

		assertEquals("TranslationFile[locale=en_US, content=null]", translationFile.toString());
	}
}
