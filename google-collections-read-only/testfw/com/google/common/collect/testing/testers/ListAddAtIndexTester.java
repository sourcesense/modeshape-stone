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

import com.google.common.collect.testing.features.CollectionFeature;
import static com.google.common.collect.testing.features.CollectionFeature.ALLOWS_NULL_VALUES;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import com.google.common.collect.testing.features.ListFeature;
import static com.google.common.collect.testing.features.ListFeature.SUPPORTS_ADD_WITH_INDEX;

import java.lang.reflect.Method;

/**
 * A generic JUnit test which tests {@code add(int, Object)} operations on a
 * list. Can't be invoked directly; please see
 * {@link com.google.common.collect.testing.ListTestSuiteBuilder}.
 *
 * @author Chris Povirk
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class ListAddAtIndexTester<E> extends AbstractListTester<E> {
  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  @CollectionSize.Require(absent = ZERO)
  public void testAddAtIndex_supportedPresent() {
    getList().add(0, samples.e0);
    expectAdded(0, samples.e0);
  }

  @ListFeature.Require(absent = SUPPORTS_ADD_WITH_INDEX)
  @CollectionSize.Require(absent = ZERO)
  /*
   * absent = ZERO isn't required, since unmodList.add() must
   * throw regardless, but it keeps the method name accurate.
   */
  public void testAddAtIndex_unsupportedPresent() {
    try {
      getList().add(0, samples.e0);
      fail("add(n, present) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  public void testAddAtIndex_supportedNotPresent() {
    getList().add(0, samples.e3);
    expectAdded(0, samples.e3);
  }

  @ListFeature.Require(absent = SUPPORTS_ADD_WITH_INDEX)
  public void testAddAtIndex_unsupportedNotPresent() {
    try {
      getList().add(0, samples.e3);
      fail("add(n, notPresent) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
    expectMissing(samples.e3);
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  @CollectionSize.Require(absent = {ZERO, ONE})
  public void testAddAtIndex_middle() {
    getList().add(getNumElements() / 2, samples.e3);
    expectAdded(getNumElements() / 2, samples.e3);
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  @CollectionSize.Require(absent = ZERO)
  public void testAddAtIndex_end() {
    getList().add(getNumElements(), samples.e3);
    expectAdded(getNumElements(), samples.e3);
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  @CollectionFeature.Require(ALLOWS_NULL_VALUES)
  public void testAddAtIndex_nullSupported() {
    getList().add(0, null);
    expectAdded(0, (E) null);
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  @CollectionFeature.Require(absent = ALLOWS_NULL_VALUES)
  public void testAddAtIndex_nullUnsupported() {
    try {
      getList().add(0, null);
      fail("add(n, null) should throw");
    } catch (NullPointerException expected) {
    }
    expectUnchanged();
    expectNullMissingWhenNullUnsupported(
        "Should not contain null after unsupported add(n, null)");
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  public void testAddAtIndex_negative() {
    try {
      getList().add(-1, samples.e3);
      fail("add(-1, e) should throw");
    } catch (IndexOutOfBoundsException expected) {
    }
    expectUnchanged();
    expectMissing(samples.e3);
  }

  @ListFeature.Require(SUPPORTS_ADD_WITH_INDEX)
  public void testAddAtIndex_tooLarge() {
    try {
      getList().add(getNumElements() + 1, samples.e3);
      fail("add(size + 1, e) should throw");
    } catch (IndexOutOfBoundsException expected) {
    }
    expectUnchanged();
    expectMissing(samples.e3);
  }

  /**
   * Returns the {@link Method} instance for
   * {@link #testAddAtIndex_nullSupported()} so that tests can suppress it. See
   * {@link CollectionAddTester#getAddNullSupportedMethod()} for details.
   */
  public static Method getAddNullSupportedMethod() {
    try {
      return
          ListAddAtIndexTester.class.getMethod("testAddAtIndex_nullSupported");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
