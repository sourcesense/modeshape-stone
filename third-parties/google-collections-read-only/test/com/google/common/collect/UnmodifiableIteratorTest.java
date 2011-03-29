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

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests for {@link UnmodifiableIterator}.
 *
 * @author Jared Levy
 */
public class UnmodifiableIteratorTest extends TestCase {

  public void testRemove() {
    final String[] array = {"a", "b", "c"};

    Iterator<String> iterator = new UnmodifiableIterator<String>() {
      int i;
      public boolean hasNext() {
        return i < array.length;
      }
      public String next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return array[i++];
      }
    };

    assertTrue(iterator.hasNext());
    assertEquals("a", iterator.next());
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException expected) {}
  }
}
