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

package com.google.common.collect.testing.google;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.testing.WrongType;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.SEVERAL;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * A generic JUnit test which tests multiset-specific read operations.
 * Can't be invoked directly; please see
 * {@link com.google.common.collect.testing.SetTestSuiteBuilder}.
 *
 * @author Jared Levy
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class MultisetReadsTester<E> extends AbstractMultisetTester<E> {
  public void testCount_0() {
    assertEquals("multiset.count(missing) didn't return 0",
        0, getMultiset().count(samples.e3));
  }

  @CollectionSize.Require(absent = ZERO)
  public void testCount_1() {
    assertEquals("multiset.count(present) didn't return 1",
        1, getMultiset().count(samples.e0));
  }

  @CollectionSize.Require(SEVERAL)
  public void testCount_3() {
    initThreeCopies();
    assertEquals("multiset.count(thriceContained) didn't return 3",
        3, getMultiset().count(samples.e0));
  }

  public void testCount_null() {
    assertEquals("multiset.count(null) didn't return 0",
        0, getMultiset().count(null));
  }

  public void testCount_wrongType() {
    assertEquals("multiset.count(wrongType) didn't return 0",
        0, getMultiset().count(WrongType.VALUE));
  }

  @CollectionSize.Require(absent = ZERO)
  public void testElementSet_contains() {
    assertTrue("multiset.elementSet().contains(present) returned false",
        getMultiset().elementSet().contains(samples.e0));
  }

  @CollectionSize.Require(absent = ZERO)
  public void testEntrySet_contains() {
    assertTrue("multiset.entrySet() didn't contain [present, 1]",
        getMultiset().entrySet().contains(
            Multisets.immutableEntry(samples.e0, 1)));
  }

  public void testEntrySet_contains_count0() {
    assertFalse("multiset.entrySet() contains [missing, 0]",
        getMultiset().entrySet().contains(
            Multisets.immutableEntry(samples.e3, 0)));
  }

  public void testEntrySet_contains_nonentry() {
    assertFalse("multiset.entrySet() contains a non-entry",
        getMultiset().entrySet().contains(samples.e0));
  }

  public void testEntrySet_twice() {
    assertEquals("calling multiset.entrySet() twice returned unequal sets",
        getMultiset().entrySet(), getMultiset().entrySet());
  }

  @CollectionSize.Require(ZERO)
  public void testEntrySet_hashCode_size0() {
    assertEquals("multiset.entrySet() has incorrect hash code",
        0, getMultiset().entrySet().hashCode());
  }

  @CollectionSize.Require(ONE)
  public void testEntrySet_hashCode_size1() {
    assertEquals("multiset.entrySet() has incorrect hash code",
        1 ^ samples.e0.hashCode(), getMultiset().entrySet().hashCode());
  }

  public void testEquals_yes() {
    assertTrue("multiset doesn't equal a multiset with the same elements",
        getMultiset().equals(HashMultiset.create(getSampleElements())));
  }

  public void testEquals_differentSize() {
    Multiset<E> other = HashMultiset.create(getSampleElements());
    other.add(samples.e0);
    assertFalse("multiset equals a multiset with a different size",
        getMultiset().equals(other));
  }

  @CollectionSize.Require(absent = ZERO)
  public void testEquals_differentElements() {
    Multiset<E> other = HashMultiset.create(getSampleElements());
    other.remove(samples.e0);
    other.add(samples.e3);
    assertFalse("multiset equals a multiset with different elements",
        getMultiset().equals(other));
  }

  @CollectionSize.Require(ZERO)
  public void testHashCode_size0() {
    assertEquals("multiset has incorrect hash code",
        0, getMultiset().hashCode());
  }

  @CollectionSize.Require(ONE)
  public void testHashCode_size1() {
    assertEquals("multiset has incorrect hash code",
        1 ^ samples.e0.hashCode(), getMultiset().hashCode());
  }

  /**
   * Returns {@link Method} instances for the read tests that assume multisets
   * support duplicates so that the test of {@code Multisets.forSet()} can
   * suppress them.
   */
  public static List<Method> getReadsDuplicateInitializingMethods() {
    try {
      return Arrays.asList(
          MultisetReadsTester.class.getMethod("testCount_3"));
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
