/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect.testing.features;

import com.google.common.collect.testing.Helpers;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;

/**
 * Optional features of classes derived from {@code Collection}.
 *
 * @author George van den Driessche
 */
// Enum values use constructors with generic varargs.
@SuppressWarnings("unchecked")
public enum CollectionFeature implements Feature<Collection> {
  ALLOWS_NULL_VALUES,

  /**
   * Indicates that a collection disallows certain elements (other than
   * {@code null}, whose validity as an element is indicated by the presence
   * or absence of {@link #ALLOWS_NULL_VALUES}).
   * From the documentation for {@link Collection}:
   * <blockquote>"Some collection implementations have restrictions on the
   * elements that they may contain.  For example, some implementations
   * prohibit null elements, and some have restrictions on the types of their
   * elements."</blockquote>
   */
  RESTRICTS_ELEMENTS,

  /**
   * Indicates that a collection has a well-defined ordering of its elements.
   * The ordering may depend on the element values, such as a {@link SortedSet},
   * or on the insertion ordering, such as a {@link LinkedHashSet}. All list
   * tests automatically specify this feature.
   */
  KNOWN_ORDER,

  /**
   * Indicates that a collection has a different {@link Object#toString}
   * representation than most collections. If not specified, the collection
   * tests will examine the value returned by {@link Object#toString}.
   */
  NON_STANDARD_TOSTRING,

  SUPPORTS_ADD,
  SUPPORTS_REMOVE,
  SUPPORTS_ADD_ALL,
  SUPPORTS_REMOVE_ALL,
  SUPPORTS_RETAIN_ALL,
  SUPPORTS_CLEAR,

  /**
   * Features supported by general-purpose collections -
   * everything but {@link #RESTRICTS_ELEMENTS}.
   * @see java.util.Collection the definition of general-purpose collections.
   */
  GENERAL_PURPOSE(
      SUPPORTS_ADD,
      SUPPORTS_REMOVE,
      SUPPORTS_ADD_ALL,
      SUPPORTS_REMOVE_ALL,
      SUPPORTS_RETAIN_ALL,
      SUPPORTS_CLEAR),

  /** Features supported by collections where only removal is allowed. */
  REMOVE_OPERATIONS(
      SUPPORTS_REMOVE,
      SUPPORTS_REMOVE_ALL,
      SUPPORTS_RETAIN_ALL,
      SUPPORTS_CLEAR),

  /**
   * For documenting collections that support no optional features, such as
   * {@link java.util.Collections#emptySet}
   */
  NONE();

  private final Set<Feature<? super Collection>> implied;

  CollectionFeature(Feature<? super Collection> ... implied) {
    this.implied = Helpers.copyToSet(implied);
  }

  public Set<Feature<? super Collection>> getImpliedFeatures() {
    return implied;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @TesterAnnotation
  public @interface Require {
    CollectionFeature[] value() default {};
    CollectionFeature[] absent() default {};
  }
}
