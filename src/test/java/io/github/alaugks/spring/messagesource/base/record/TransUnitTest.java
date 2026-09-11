// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.record;

import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransUnitTest {

	@Test
	void test_without_domain() {
		TransUnitInterface transUnit = new TransUnit(Locale.forLanguageTag("en"), "the-code", "the-value");

		assertEquals(Locale.forLanguageTag("en"), transUnit.locale());
		assertEquals("the-code", transUnit.code());
		assertEquals("the-value", transUnit.value());
	}

	@Test
	void test_with_domain() {
		TransUnitInterface transUnit = new TransUnit(Locale.forLanguageTag("en"), "the-code", "the-value");

		assertEquals(Locale.forLanguageTag("en"), transUnit.locale());
		assertEquals("the-code", transUnit.code());
		assertEquals("the-value", transUnit.value());
	}
}
