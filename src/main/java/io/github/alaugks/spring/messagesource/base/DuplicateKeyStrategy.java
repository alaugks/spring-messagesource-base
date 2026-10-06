// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base;

/**
 * Defines how a duplicate translation is handled, i.e. a trans unit whose locale and code
 * are already present, regardless of its value.
 */
public enum DuplicateKeyStrategy {

	/** Fails with a {@link io.github.alaugks.spring.messagesource.base.exception.BaseMessageSourceRuntimeException}. */
	FAIL,

	/** Keeps the first value and ignores later ones; a warning is logged. */
	KEEP_FIRST,

	/** Replaces the existing value with the later one; a warning is logged. */
	KEEP_LAST
}
