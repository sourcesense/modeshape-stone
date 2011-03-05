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

package com.google.common.collect;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SampleElements.Unhashables;
import com.google.common.collect.testing.UnhashableObject;

import junit.framework.TestCase;

/**
 * Tests for {@link ImmutableMultimap}.
 *
 * @author Jared Levy
 */
public class ImmutableMultimapTest extends TestCase {

  // TODO: test ImmutableMultimap builder and factory methods

  public void testCopyOf() {
    ImmutableSetMultimap<String, String> setMultimap
        = ImmutableSetMultimap.of("k1", "v1");
    ImmutableMultimap<String, String> setMultimapCopy
        = ImmutableMultimap.copyOf(setMultimap);
    assertSame("copyOf(ImmutableSetMultimap) should not create a new instance",
        setMultimap, setMultimapCopy);

    ImmutableListMultimap<String, String> listMultimap
        = ImmutableListMultimap.of("k1", "v1");
    ImmutableMultimap<String, String> listMultimapCopy
        = ImmutableMultimap.copyOf(listMultimap);
    assertSame("copyOf(ImmutableListMultimap) should not create a new instance",
        listMultimap, listMultimapCopy);
  }

  public void testUnhashableSingletonValue() {
    SampleElements<UnhashableObject> unhashables = new Unhashables();
    Multimap<Integer, UnhashableObject> multimap = ImmutableMultimap.of(
        0, unhashables.e0);
    assertEquals(1, multimap.get(0).size());
    assertTrue(multimap.get(0).contains(unhashables.e0));
  }

  public void testUnhashableMixedValues() {
    SampleElements<UnhashableObject> unhashables = new Unhashables();
    Multimap<Integer, Object> multimap = ImmutableMultimap.<Integer, Object>of(
        0, unhashables.e0, 2, "hey you", 0, unhashables.e1);
    assertEquals(2, multimap.get(0).size());
    assertTrue(multimap.get(0).contains(unhashables.e0));
    assertTrue(multimap.get(0).contains(unhashables.e1));
    assertTrue(multimap.get(2).contains("hey you"));
  }
}
