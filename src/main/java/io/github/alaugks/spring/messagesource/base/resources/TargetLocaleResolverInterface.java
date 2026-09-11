// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.resources;

import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocale;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocaleInterface;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;

/**
 * Resolves the target locale of a translation resource into a {@link TransFileTargetLocale} record.
 *
 * <p>Functional interface: a custom resolver can be passed to {@link ResourceLoaderBuilder} as a
 * class implementation or as a lambda. The locale may be derived from the resource's file name,
 * its content, or both.
 */
@FunctionalInterface
public interface TargetLocaleResolverInterface {

	/**
	 * Resolves the target locale of the given resource.
	 *
	 * @param resource the resource whose target locale is resolved
	 * @return the resolved {@link TransFileTargetLocale}, or {@code null} if the resource does not match the expected pattern
	 */
	@Nullable TransFileTargetLocaleInterface resolve(Resource resource);
}
