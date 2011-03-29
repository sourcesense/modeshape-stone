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

import com.google.common.collect.testing.AbstractCollectionTester;
import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.features.CollectionFeature;
import static com.google.common.collect.testing.features.CollectionFeature.KNOWN_ORDER;
import static com.google.common.collect.testing.features.CollectionFeature.NON_STANDARD_TOSTRING;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ONE;
import static com.google.common.collect.testing.features.CollectionSize.SEVERAL;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;

/**
 * A generic JUnit test which tests {@code toString()} operations on a
 * collection. Can't be invoked directly; please see
 * {@link com.google.common.collect.testing.CollectionTestSuiteBuilder}.
 *
 * @author Kevin Bourrillion
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class CollectionToStringTester<E> extends AbstractCollectionTester<E> {
  public void testToString_minimal() {
    assertNotNull("toString() should not return null",
        collection.toString());
  }

  @CollectionSize.Require(ZERO)
  @CollectionFeature.Require(absent = NON_STANDARD_TOSTRING)
  public void testToString_size0() {
    assertEquals("emptyCollection.toString should return []", "[]",
        collection.toString());
  }

  @CollectionSize.Require(ONE)
  @CollectionFeature.Require(absent = NON_STANDARD_TOSTRING)
  public void testToString_size1() {
    assertEquals("size1Collection.toString should return [{element}]",
        "[" + samples.e0 + "]", collection.toString());
  }

  @CollectionSize.Require(SEVERAL)
  @CollectionFeature.Require(
      value = KNOWN_ORDER, absent = NON_STANDARD_TOSTRING)
  public void testToString_sizeSeveral() {
    String expected = Helpers.copyToList(getOrderedElements()).toString();
    assertEquals("collection.toString() incorrect",
        expected, collection.toString());
  }
}
