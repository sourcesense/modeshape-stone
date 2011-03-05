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

package com.google.common.collect.testing.testers;

import com.google.common.collect.testing.AbstractMapTester;
import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import com.google.common.collect.testing.features.MapFeature;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;
import static com.google.common.collect.testing.features.MapFeature.SUPPORTS_REMOVE;

/**
 * A generic JUnit test which tests {@code remove} operations on a map. Can't be
 * invoked directly; please see
 * {@link com.google.common.collect.testing.MapTestSuiteBuilder}.
 *
 * @author George van den Driessche
 * @author Chris Povirk
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class MapRemoveTester<K, V> extends AbstractMapTester<K, V> {
  @MapFeature.Require(SUPPORTS_REMOVE)
  @CollectionSize.Require(absent = ZERO)
  public void testRemove_present() {
    int initialSize = getMap().size();
    assertEquals("remove(present) should return the associated value",
        samples.e0.getValue(), getMap().remove(samples.e0.getKey()));
    assertEquals("remove(present) should decrease a map's size by one.",
        initialSize - 1, getMap().size());
    expectMissing(samples.e0);
  }

  @MapFeature.Require(SUPPORTS_REMOVE)
  public void testRemove_notPresent() {
    assertNull("remove(notPresent) should return null",
        getMap().remove(samples.e3.getKey()));
    expectUnchanged();
  }

  @MapFeature.Require({SUPPORTS_REMOVE, ALLOWS_NULL_KEYS})
  @CollectionSize.Require(absent = ZERO)
  public void testRemove_nullPresent() {
    initMapWithNullKey();

    int initialSize = getMap().size();
    assertEquals("remove(null) should return the associated value",
        getValueForNullKey(), getMap().remove(null));
    assertEquals("remove(present) should decrease a map's size by one.",
        initialSize - 1, getMap().size());
    expectMissing(entry(null, getValueForNullKey()));
  }

  @MapFeature.Require(absent = SUPPORTS_REMOVE)
  @CollectionSize.Require(absent = ZERO)
  public void testRemove_unsupported() {
    try {
      getMap().remove(samples.e0.getKey());
      fail("remove(present) should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
    assertEquals("remove(present) should not remove the element",
        samples.e0.getValue(), get(samples.e0.getKey()));
  }

  @MapFeature.Require(absent = SUPPORTS_REMOVE)
  public void testRemove_unsupportedNotPresent() {
    try {
      assertNull("remove(notPresent) should return null or throw "
          + "UnsupportedOperationException",
          getMap().remove(samples.e3.getKey()));
    } catch (UnsupportedOperationException tolerated) {
    }
    expectUnchanged();
    expectMissing(samples.e3);
  }

  @MapFeature.Require(
      value = SUPPORTS_REMOVE,
      absent = ALLOWS_NULL_KEYS)
  public void testRemove_nullNotSupported() {
    try {
      assertNull("remove(null) should return null or throw "
          + "NullPointerException",
          getMap().remove(null));
    } catch (NullPointerException tolerated) {
    }
    expectUnchanged();
  }

  @MapFeature.Require({SUPPORTS_REMOVE, ALLOWS_NULL_KEYS})
  public void testRemove_nullSupportedMissing() {
    assertNull("remove(null) should return null", getMap().remove(null));
    expectUnchanged();
  }

  @MapFeature.Require(SUPPORTS_REMOVE)
  public void testRemove_wrongType() {
    try {
      assertNull(getMap().remove(WrongType.VALUE));
    } catch (ClassCastException tolerated) {
    }
    expectUnchanged();
  }
}
