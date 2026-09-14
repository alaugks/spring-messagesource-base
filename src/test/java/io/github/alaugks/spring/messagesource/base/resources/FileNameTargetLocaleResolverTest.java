// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.resources;

import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocaleInterface;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileNameTargetLocaleResolverTest {

	@ParameterizedTest
	@MethodSource("provider_filenames")
	void resolve(String filename, String language, String region, String locale) {
		TransFileTargetLocaleInterface result = new FileNameTargetLocaleResolver().resolve(resource(filename));
		Locale resultLocale = result.locale();

		assertEquals(language, result.language());
		assertEquals(region, result.region());
		assertEquals(locale, resultLocale != null ? resultLocale.toString() : null);
	}

	private static Stream<Arguments> provider_filenames() {
		return Stream.of(
				// filename, domain, language, region, locale
				Arguments.of("message.ext", null, null, null),
				Arguments.of("message_en.ext", "en", null, "en"),
				Arguments.of("message.en.ext", "en", null, "en"),
				Arguments.of("message-en.ext", "en", null, "en"),
				Arguments.of("message_en_GB.ext", "en", "GB", "en_GB"),
				Arguments.of("message.en_GB.ext", "en", "GB", "en_GB"),
				Arguments.of("message-en-GB.ext", "en", "GB", "en_GB"),
				Arguments.of("Message-En-Gb.ext", "En", "Gb", "en_GB")
		);
	}

	@Test
	void test_not() {
		assertNull(new FileNameTargetLocaleResolver().resolve(resource(".ext")));
	}

	private static Resource resource(String filename) {
		return new ByteArrayResource(new byte[0]) {

			@Override
			public String getFilename() {
				return filename;
			}
		};
	}
}
