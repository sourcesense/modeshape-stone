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

package com.google.common.collect;

import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.NullPointerTester;

import junit.framework.TestCase;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for {@code ObjectArrays}.
 *
 * @author Kevin Bourrillion
 */
public class ObjectArraysTest extends TestCase {

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(ObjectArrays.class);
  }

  public void testNewArray_fromClass_Empty() {
    String[] empty = ObjectArrays.newArray(String.class, 0);
    assertEquals(String[].class, empty.getClass());
    assertEquals(0, empty.length);
  }

  public void testNewArray_fromClass_Nonempty() {
    String[] array = ObjectArrays.newArray(String.class, 2);
    assertEquals(String[].class, array.getClass());
    assertEquals(2, array.length);
    assertNull(array[0]);
  }

  public void testNewArray_fromClass_OfArray() {
    String[][] array = ObjectArrays.newArray(String[].class, 1);
    assertEquals(String[][].class, array.getClass());
    assertEquals(1, array.length);
    assertNull(array[0]);
  }

  public void testNewArray_fromArray_Empty() {
    String[] in = new String[0];
    String[] empty = ObjectArrays.newArray(in, 0);
    assertEquals(0, empty.length);
  }

  public void testNewArray_fromArray_Nonempty() {
    String[] array = ObjectArrays.newArray(new String[0], 2);
    assertEquals(String[].class, array.getClass());
    assertEquals(2, array.length);
    assertNull(array[0]);
  }

  public void testNewArray_fromArray_OfArray() {
    String[][] array = ObjectArrays.newArray(new String[0][0], 1);
    assertEquals(String[][].class, array.getClass());
    assertEquals(1, array.length);
    assertNull(array[0]);
  }

  public void testConcatEmptyEmpty() {
    String[] result
        = ObjectArrays.concat(new String[0], new String[0], String.class);
    assertEquals(String[].class, result.getClass());
    assertEquals(0, result.length);
  }

  public void testConcatEmptyNonempty() {
    String[] result = ObjectArrays.concat(
        new String[0], new String[] { "a", "b" }, String.class);
    assertEquals(String[].class, result.getClass());
    assertContentsInOrder(Arrays.asList(result), "a", "b");
  }

  public void testConcatNonemptyEmpty() {
    String[] result = ObjectArrays.concat(
        new String[] { "a", "b" }, new String[0], String.class);
    assertEquals(String[].class, result.getClass());
    assertContentsInOrder(Arrays.asList(result), "a", "b");
  }

  public void testConcatBasic() {
    String[] result = ObjectArrays.concat(
        new String[] { "a", "b" }, new String[] { "c", "d" }, String.class);
    assertEquals(String[].class, result.getClass());
    assertContentsInOrder(Arrays.asList(result), "a", "b", "c", "d");
  }

  public void testConcatWithMoreGeneralType() {
    Serializable[] result
        = ObjectArrays.concat(new String[0], new String[0], Serializable.class);
    assertEquals(Serializable[].class, result.getClass());
  }

  public void testToArrayImpl1() {
    doTestToArrayImpl1(Lists.<Integer>newArrayList());
    doTestToArrayImpl1(Lists.newArrayList(1));
    doTestToArrayImpl1(Lists.newArrayList(1, null, 3));
  }

  private void doTestToArrayImpl1(List<Integer> list) {
    Object[] reference = list.toArray();
    Object[] target = ObjectArrays.toArrayImpl(list);
    assertEquals(reference.getClass(), target.getClass());
    assertTrue(Arrays.equals(reference, target));
  }

  public void testToArrayImpl2() {
    doTestToArrayImpl2(Lists.<Integer>newArrayList(), new Integer[0], false);
    doTestToArrayImpl2(Lists.<Integer>newArrayList(), new Integer[1], true);

    doTestToArrayImpl2(Lists.newArrayList(1), new Integer[0], false);
    doTestToArrayImpl2(Lists.newArrayList(1), new Integer[1], true);
    doTestToArrayImpl2(Lists.newArrayList(1), new Integer[] { 2, 3 }, true);

    doTestToArrayImpl2(Lists.newArrayList(1, null, 3), new Integer[0], false);
    doTestToArrayImpl2(Lists.newArrayList(1, null, 3), new Integer[2], false);
    doTestToArrayImpl2(Lists.newArrayList(1, null, 3), new Integer[3], true);
  }

  private void doTestToArrayImpl2(List<Integer> list, Integer[] array1,
      boolean expectModify) {
    Integer[] starting = array1.clone();
    Integer[] array2 = array1.clone();
    Object[] reference = list.toArray(array1);

    Object[] target = ObjectArrays.toArrayImpl(list, array2);

    assertEquals(reference.getClass(), target.getClass());
    assertTrue(Arrays.equals(reference, target));
    assertTrue(Arrays.equals(reference, target));

    Object[] expectedArray1 = expectModify ? reference : starting;
    Object[] expectedArray2 = expectModify ? target : starting;
    assertTrue(Arrays.equals(expectedArray1, array1));
    assertTrue(Arrays.equals(expectedArray2, array2));
  }

  public void testPrependZeroElements() {
    String[] result = ObjectArrays.concat("foo", new String[] {});
    assertContentsInOrder(Arrays.asList(result), "foo");
  }

  public void testPrependOneElement() {
    String[] result = ObjectArrays.concat("foo", new String[]{ "bar" });
    assertContentsInOrder(Arrays.asList(result), "foo", "bar");
  }

  public void testPrependTwoElements() {
    String[] result = ObjectArrays.concat("foo", new String[]{ "bar", "baz" });
    assertContentsInOrder(Arrays.asList(result), "foo", "bar", "baz");
  }

  public void testAppendZeroElements() {
    String[] result = ObjectArrays.concat(new String[] {}, "foo");
    assertContentsInOrder(Arrays.asList(result), "foo");
  }

  public void testAppendOneElement() {
    String[] result = ObjectArrays.concat(new String[]{ "foo" }, "bar");
    assertContentsInOrder(Arrays.asList(result), "foo", "bar");
  }

  public void testAppendTwoElements() {
    String[] result = ObjectArrays.concat(new String[]{ "foo", "bar" }, "baz");
    assertContentsInOrder(Arrays.asList(result), "foo", "bar", "baz");
  }
}
