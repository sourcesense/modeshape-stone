/*
 * Copyright (C) 2007 Google Inc.
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

package com.google.common.collect.testing.testers;

import com.google.common.collect.testing.AbstractMapTester;
import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import com.google.common.collect.testing.features.MapFeature;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;

/**
 * A generic JUnit test which tests {@code get} operations on a map. Can't be
 * invoked directly; please see
 * {@link com.google.common.collect.testing.MapTestSuiteBuilder}.
 *
 * @author Kevin Bourrillion
 * @author Chris Povirk
 */
public class MapGetTester<K, V> extends AbstractMapTester<K, V> {
  @CollectionSize.Require(absent = ZERO)
  public void testGet_yes() {
    assertEquals("get(present) should return the associated value",
        samples.e0.getValue(), get(samples.e0.getKey()));
  }

  public void testGet_no() {
    assertNull("get(notPresent) should return null", get(samples.e3.getKey()));
  }

  @MapFeature.Require(ALLOWS_NULL_KEYS)
  public void testGet_nullNotContainedButSupported() {
    assertNull("get(null) should return null", get(null));
  }

  @MapFeature.Require(absent = ALLOWS_NULL_KEYS)
  public void testGet_nullNotContainedAndUnsupported() {
    try {
      assertNull("get(null) should return null or throw", get(null));
    } catch (NullPointerException tolerated) {
    }
  }

  @MapFeature.Require(ALLOWS_NULL_KEYS)
  @CollectionSize.Require(absent = ZERO)
  public void testGet_nonNullWhenNullContained() {
    initMapWithNullKey();
    assertNull("get(notPresent) should return null", get(samples.e3.getKey()));
  }

  @MapFeature.Require(ALLOWS_NULL_KEYS)
  @CollectionSize.Require(absent = ZERO)
  public void testGet_nullContained() {
    initMapWithNullKey();
    assertEquals("get(null) should return the associated value",
        getValueForNullKey(), get(null));
  }

  public void testGet_wrongType() {
    try {
      assertNull("get(wrongType) should return null or throw",
          getMap().get(WrongType.VALUE));
    } catch (ClassCastException tolerated) {
    }
  }
}
