// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.resources;

import io.github.alaugks.spring.messagesource.base.exception.BaseMessageSourceRuntimeException;
import io.github.alaugks.spring.messagesource.base.records.TransFileInterface;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocale;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcesLoaderBuilderTest {

	static final Locale LOCALE_EN = Locale.forLanguageTag("en");

	@Test
	void test_set_location_pattern_string() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, "translations/*")
			.fileExtensions(List.of("txt"))
			.build();

		assertEquals(5, resourcesLoader.getTranslationFiles().size());
	}

	@Test
	void test_set_location_patterns_list() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, List.of("translations_en/*", "translations_de/*"))
			.fileExtensions(List.of("txt"))
			.build();

		assertEquals(4, resourcesLoader.getTranslationFiles().size());
	}

	@Test
	void test_no_file_extension_filter() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, List.of("translations/*", "messages/*"))
			.build();

		assertEquals(10, resourcesLoader.getTranslationFiles().size());
	}

	@Test
	void test_custom_target_locale_resolver() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, List.of("translations/messages.txt"))
			.fileExtensions(List.of("txt"))
			.targetLocaleResolver(new DummyTargetLocaleResolver())
			.build();

		TransFileInterface translationFile = resourcesLoader.getTranslationFiles().get(0);

		assertEquals("de", translationFile.locale().toString());
	}

	static class DummyTargetLocaleResolver implements TargetLocaleResolverInterface {

		@Override
		public TransFileTargetLocale resolve(Resource resource) {
			return new TransFileTargetLocale("de", null);
		}
	}

	@Test
	void test_set_location_patterns_classpath_prefix() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, List.of(
				"classpath:/translations_en/*",
				"classpath:/translations_de/*"
			))
			.fileExtensions(List.of("txt"))
			.build();

		assertEquals(4, resourcesLoader.getTranslationFiles().size());
	}

	@Test
	void test_set_location_patterns_domain_messages() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
				.builder(LOCALE_EN, List.of("translations/messages*"))
				.fileExtensions(List.of("txt"))
				.build();

		assertEquals(3, resourcesLoader.getTranslationFiles().size());
	}


	@Test
	void test_set_location_patterns_language_de() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
				.builder(LOCALE_EN, List.of("translations/*_de*"))
				.fileExtensions(List.of("txt"))
				.build();

		assertEquals(2, resourcesLoader.getTranslationFiles().size());
	}

	@Test
	void test_record() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
				.builder(LOCALE_EN, List.of("translations_en_US/*"))
				.fileExtensions(List.of("txt"))
				.build();

		TransFileInterface translationFile = resourcesLoader.getTranslationFiles().get(0);

		assertEquals("en_US", translationFile.locale().toString());
		assertNotNull(translationFile.content());
	}

	@Test
	void test_parse_file_name_null() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
				.builder(LOCALE_EN, List.of("translations/.txt"))
				.fileExtensions(List.of("txt"))
				.build();

		assertTrue(resourcesLoader.getTranslationFiles().isEmpty());
	}

	@Test
	void test_file_extension_not_supported() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
				.builder(LOCALE_EN, List.of("translations/*"))
				.fileExtensions(List.of("json"))
				.build();

		assertTrue(resourcesLoader.getTranslationFiles().isEmpty());
	}

	@Test
	void test_exception() {
		ResourceLoaderBuilder resourcesLoader = ResourceLoaderBuilder
			.builder(LOCALE_EN, List.of("translations/not-exists.txt"))
			.fileExtensions(List.of("txt"))
			.build();

		Exception e = assertThrows(BaseMessageSourceRuntimeException.class, resourcesLoader::getTranslationFiles);
		assertInstanceOf(IOException.class, e.getCause());
	}
}
