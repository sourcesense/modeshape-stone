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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering.IncomparableValueException;
import com.google.common.collect.testing.Helpers;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.EqualsTester;
import com.google.common.testutils.NullPointerTester;
import static com.google.common.testutils.SerializableTester.reserialize;
import static com.google.common.testutils.SerializableTester.reserializeAndAssert;

import junit.framework.TestCase;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

/**
 * Unit tests for {@code Ordering}.
 *
 * @author Jesse Wilson
 */
public class OrderingTest extends TestCase {

  private final Ordering<Number> numberOrdering = new NumberOrdering();

  public void testNatural() {
    Ordering<Integer> comparator = Ordering.natural();
    Helpers.testComparator(comparator,
        Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
    try {
      comparator.compare(1, null);
      fail();
    } catch (NullPointerException expected) {}
    try {
      comparator.compare(null, 2);
      fail();
    } catch (NullPointerException expected) {}
    try {
      comparator.compare(null, null);
      fail();
    } catch (NullPointerException expected) {}
    assertSame(comparator, reserialize(comparator));
    assertEquals("Ordering.natural()", comparator.toString());
  }

  public void testFrom() {
    Ordering<String> caseInsensitiveOrdering
        = Ordering.from(String.CASE_INSENSITIVE_ORDER);
    assertEquals(0, caseInsensitiveOrdering.compare("A", "a"));
    assertTrue(caseInsensitiveOrdering.compare("a", "B") < 0);
    assertTrue(caseInsensitiveOrdering.compare("B", "a") > 0);

    @SuppressWarnings("deprecation") // test of deprecated method
    Ordering<String> orderingFromOrdering =
        Ordering.from(Ordering.<String>natural());
    new EqualsTester(caseInsensitiveOrdering)
        .addEqualObject(Ordering.from(String.CASE_INSENSITIVE_ORDER))
        .addNotEqualObject(orderingFromOrdering)
        .addNotEqualObject(Ordering.natural())
        .testEquals();
  }

  public void testExplicit_none() {
    Comparator<Integer> c
        = Ordering.explicit(Collections.<Integer>emptyList());
    try {
      c.compare(0, 0);
      fail();
    } catch (IncomparableValueException expected) {
      assertEquals(0, expected.value);
    }
    reserializeAndAssert(c);
  }

  public void testExplicit_one() {
    Comparator<Integer> c = Ordering.explicit(0);
    assertEquals(0, c.compare(0, 0));
    try {
      c.compare(0, 1);
      fail();
    } catch (IncomparableValueException expected) {
      assertEquals(1, expected.value);
    }
    reserializeAndAssert(c);
    assertEquals("Ordering.explicit([0])", c.toString());
  }

  public void testExplicit_two() {
    Comparator<Integer> c = Ordering.explicit(42, 5);
    assertEquals(0, c.compare(5, 5));
    assertTrue(c.compare(5, 42) > 0);
    assertTrue(c.compare(42, 5) < 0);
    try {
      c.compare(5, 666);
      fail();
    } catch (IncomparableValueException expected) {
      assertEquals(666, expected.value);
    }
    new EqualsTester(c)
        .addEqualObject(Ordering.explicit(42, 5))
        .addNotEqualObject(Ordering.explicit(5, 42))
        .addNotEqualObject(Ordering.explicit(42))
        .testEquals();
    reserializeAndAssert(c);
  }

  public void testExplicit_sortingExample() {
    Comparator<Integer> c
        = Ordering.explicit(2, 8, 6, 1, 7, 5, 3, 4, 0, 9);
    List<Integer> list = Arrays.asList(0, 3, 5, 6, 7, 8, 9);
    Collections.sort(list, c);
    assertContentsInOrder(list, 8, 6, 7, 5, 3, 0, 9);
    reserializeAndAssert(c);
  }

  public void testExplicit_withDuplicates() {
    try {
      Ordering.explicit(1, 2, 3, 4, 2);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testUsingToString() {
    Ordering<Object> ordering = Ordering.usingToString();
    Helpers.testComparator(ordering, 1, 12, 124, 2);
    assertEquals("Ordering.usingToString()", ordering.toString());
    assertSame(ordering, reserialize(ordering));
  }

  // use an enum to get easy serializability
  private enum CharAtFunction implements Function<String, Character> {
    AT0(0),
    AT1(1),
    AT2(2),
    AT3(3),
    AT4(4),
    AT5(5),
    ;

    final int index;
    CharAtFunction(int index) {
      this.index = index;
    }
    public Character apply(String string) {
      return string.charAt(index);
    }
  }

  private static Ordering<String> byCharAt(int index) {
    return Ordering.natural().onResultOf(CharAtFunction.values()[index]);
  }

  public void testCompound_static() {
    Comparator<String> comparator = Ordering.compound(
        Iterables.unmodifiableIterable(ImmutableList.of(
            byCharAt(0), byCharAt(1), byCharAt(2),
            byCharAt(3), byCharAt(4), byCharAt(5))));
    Helpers.testComparator(comparator, ImmutableList.of(
        "applesauce",
        "apricot",
        "artichoke",
        "banality",
        "banana",
        "banquet",
        "tangelo",
        "tangerine"));
    reserializeAndAssert(comparator);
  }

  public void testCompound_instance() {
    Comparator<String> comparator = byCharAt(1).compound(byCharAt(0));
    Helpers.testComparator(comparator, ImmutableList.of(
        "red",
        "yellow",
        "violet",
        "blue",
        "indigo",
        "green",
        "orange"));
  }

  public void testCompound_instance_generics() {
    Ordering<Object> objects = Ordering.explicit((Object) 1);
    Ordering<Number> numbers = Ordering.explicit((Number) 1);
    Ordering<Integer> integers = Ordering.explicit(1);

    // Like by like equals like
    Ordering<Number> a = numbers.compound(numbers);

    // The compound takes the more specific type of the two, regardless of order

    Ordering<Number> b = numbers.compound(objects);
    Ordering<Number> c = objects.compound(numbers);

    Ordering<Integer> d = numbers.compound(integers);
    Ordering<Integer> e = integers.compound(numbers);

    // This works with three levels too (IDEA falsely reports errors as noted
    // below. Both javac and eclipse handle these cases correctly.)

    Ordering<Number> f = numbers.compound(objects).compound(objects); //bad IDEA
    Ordering<Number> g = objects.compound(numbers).compound(objects);
    Ordering<Number> h = objects.compound(objects).compound(numbers);

    Ordering<Number> i = numbers.compound(objects.compound(objects));
    Ordering<Number> j = objects.compound(numbers.compound(objects)); //bad IDEA
    Ordering<Number> k = objects.compound(objects.compound(numbers));

    // You can also arbitrarily assign a more restricted type - not an intended
    // feature, exactly, but unavoidable (I think) and harmless
    Ordering<Integer> l = objects.compound(numbers);

    // This correctly doesn't work:
    // Ordering<Object> m = numbers.compound(objects);

    // Sadly, the following works in javac 1.6, but at least it fails for
    // eclipse, and is *correctly* highlighted red in IDEA.
    // Ordering<Object> n = objects.compound(numbers);
  }

  public void testReverse() {
    Ordering<Number> reverseOrder = numberOrdering.reverse();
    Helpers.testComparator(reverseOrder,
        Integer.MAX_VALUE, 1, 0, -1, Integer.MIN_VALUE);

    new EqualsTester(reverseOrder)
        .addEqualObject(numberOrdering.reverse())
        .addNotEqualObject(Ordering.natural().reverse())
        .addNotEqualObject(Collections.reverseOrder())
        .testEquals();
  }

  public void testReverseOfReverseSameAsForward() {
    // Not guaranteed by spec, but it works, and saves us from testing
    // exhaustively
    assertSame(numberOrdering, numberOrdering.reverse().reverse());
  }

  private enum StringLengthFunction implements Function<String, Integer> {
    StringLength;

    public Integer apply(String string) {
      return string.length();
    }
  }
  
  private static final Ordering<Integer> DECREASING_INTEGER
      = Ordering.natural().reverse();
  
  public void testOnResultOf_natural() {
    Comparator<String> comparator
        = Ordering.natural().onResultOf(StringLengthFunction.StringLength);
    assertTrue(comparator.compare("to", "be") == 0);
    assertTrue(comparator.compare("or", "not") < 0);
    assertTrue(comparator.compare("that", "to") > 0);

    new EqualsTester(comparator)
        .addEqualObject(
            Ordering.natural().onResultOf(StringLengthFunction.StringLength))
        .addNotEqualObject(DECREASING_INTEGER)
        .testEquals();
    reserializeAndAssert(comparator);
    assertEquals("Ordering.natural().onResultOf(StringLength)",
        comparator.toString());
  }

  public void testOnResultOf_chained() {
    Comparator<String> comparator = DECREASING_INTEGER.onResultOf(
        StringLengthFunction.StringLength);
    assertTrue(comparator.compare("to", "be") == 0);
    assertTrue(comparator.compare("not", "or") < 0);
    assertTrue(comparator.compare("to", "that") > 0);

    new EqualsTester(comparator)
        .addEqualObject(
            DECREASING_INTEGER.onResultOf(StringLengthFunction.StringLength))
        .addNotEqualObject(
            DECREASING_INTEGER.onResultOf(Functions.constant(1)))
        .addNotEqualObject(Ordering.natural())
        .testEquals();
    reserializeAndAssert(comparator);
    assertEquals("Ordering.natural().reverse().onResultOf(StringLength)",
        comparator.toString());
  }

  public void testNullsFirst() {
    Ordering<Integer> ordering = Ordering.natural().nullsFirst();
    Helpers.testComparator(ordering, null, Integer.MIN_VALUE, 0, 1);
  }

  public void testNullsLast() {
    Ordering<Integer> ordering = Ordering.natural().nullsLast();
    Helpers.testComparator(ordering, 0, 1, Integer.MAX_VALUE, null);
  }

  public void testBinarySearch() {
    List<Integer> ints = Lists.newArrayList(0, 2, 3, 5, 7, 9);
    assertEquals(4, numberOrdering.binarySearch(ints, 7));
  }

  public void testSortedCopy() {
    List<Integer> unsortedInts = Lists.newArrayList(5, 3, 0, 9);
    List<Integer> sortedInts = numberOrdering.sortedCopy(unsortedInts);
    assertEquals(Lists.newArrayList(0, 3, 5, 9), sortedInts);
    assertEquals(Lists.newArrayList(5, 3, 0, 9), unsortedInts);
  }

  public void testIsOrdered() {
    assertFalse(numberOrdering.isOrdered(asList(5, 3, 0, 9)));
    assertFalse(numberOrdering.isOrdered(asList(0, 5, 3, 9)));
    assertTrue(numberOrdering.isOrdered(asList(0, 3, 5, 9)));
    assertTrue(numberOrdering.isOrdered(asList(0, 0, 3, 3)));
    assertTrue(numberOrdering.isOrdered(asList(0, 3)));
    assertTrue(numberOrdering.isOrdered(Collections.singleton(1)));
    assertTrue(numberOrdering.isOrdered(Collections.<Integer>emptyList()));
  }

  public void testIsStrictlyOrdered() {
    assertFalse(numberOrdering.isStrictlyOrdered(asList(5, 3, 0, 9)));
    assertFalse(numberOrdering.isStrictlyOrdered(asList(0, 5, 3, 9)));
    assertTrue(numberOrdering.isStrictlyOrdered(asList(0, 3, 5, 9)));
    assertFalse(numberOrdering.isStrictlyOrdered(asList(0, 0, 3, 3)));
    assertTrue(numberOrdering.isStrictlyOrdered(asList(0, 3)));
    assertTrue(numberOrdering.isStrictlyOrdered(Collections.singleton(1)));
    assertTrue(numberOrdering.isStrictlyOrdered(
        Collections.<Integer>emptyList()));
  }

  public void testIterableMinAndMax() {
    List<Integer> ints = Lists.newArrayList(5, 3, 0, 9);
    assertEquals(9, (int) numberOrdering.max(ints));
    assertEquals(0, (int) numberOrdering.min(ints));

    // when the values are the same, the first argument should be returned
    Integer a = new Integer(4);
    Integer b = new Integer(4);
    ints = Lists.newArrayList(a, b, b);
    assertSame(a, numberOrdering.max(ints));
    assertSame(a, numberOrdering.min(ints));
  }

  public void testVarargsMinAndMax() {
    // try the min and max values in all positions, since some values are proper
    // parameters and others are from the varargs array
    assertEquals(9, (int) numberOrdering.max(9, 3, 0, 5, 8));
    assertEquals(9, (int) numberOrdering.max(5, 9, 0, 3, 8));
    assertEquals(9, (int) numberOrdering.max(5, 3, 9, 0, 8));
    assertEquals(9, (int) numberOrdering.max(5, 3, 0, 9, 8));
    assertEquals(9, (int) numberOrdering.max(5, 3, 0, 8, 9));
    assertEquals(0, (int) numberOrdering.min(0, 3, 5, 9, 8));
    assertEquals(0, (int) numberOrdering.min(5, 0, 3, 9, 8));
    assertEquals(0, (int) numberOrdering.min(5, 3, 0, 9, 8));
    assertEquals(0, (int) numberOrdering.min(5, 3, 9, 0, 8));
    assertEquals(0, (int) numberOrdering.min(5, 3, 0, 9, 0));

    // when the values are the same, the first argument should be returned
    Integer a = new Integer(4);
    Integer b = new Integer(4);
    assertSame(a, numberOrdering.max(a, b, b));
    assertSame(a, numberOrdering.min(a, b, b));
  }

  public void testParameterMinAndMax() {
    assertEquals(5, (int) numberOrdering.max(3, 5));
    assertEquals(5, (int) numberOrdering.max(5, 3));
    assertEquals(3, (int) numberOrdering.min(3, 5));
    assertEquals(3, (int) numberOrdering.min(5, 3));

    // when the values are the same, the first argument should be returned
    Integer a = new Integer(4);
    Integer b = new Integer(4);
    assertSame(a, numberOrdering.max(a, b));
    assertSame(a, numberOrdering.min(a, b));
  }

  private static class NumberOrdering extends Ordering<Number> {
    public int compare(Number a, Number b) {
      return ((Double) a.doubleValue()).compareTo(b.doubleValue());
    }
    @Override public int hashCode() {
      return NumberOrdering.class.hashCode();
    }
    @Override public boolean equals(Object other) {
      return other instanceof NumberOrdering;
    }
    private static final long serialVersionUID = 0;
  }

  /*
   * Now we have monster tests that create hundreds of Orderings using different
   * combinations of methods, then checks compare(), binarySearch() and so
   * forth on each one.
   */

  // should periodically try increasing this, but it makes the test run long
  private static final int RECURSE_DEPTH = 2;

  public void testCombinationsExhaustively_startingFromNatural() {
    testExhaustively(Ordering.<String>natural(), Arrays.asList("a", "b"));
  }

  public void testCombinationsExhaustively_startingFromExplicit() {
    testExhaustively(Ordering.explicit("a", "b", "c", "d"),
        Arrays.asList("b", "d"));
  }

  public void testCombinationsExhaustively_startingFromUsingToString() {
    testExhaustively(Ordering.usingToString(), Arrays.asList(1, 12, 2));
  }

  private static <T> void testExhaustively(
      Ordering<? super T> ordering, List<T> list) {
    // shoot me, but I didn't want to deal with wildcards through the whole test
    @SuppressWarnings("unchecked")
    Scenario<T> starter = new Scenario<T>((Ordering) ordering, list);
    verifyScenario(starter, 0);
  }

  private static <T> void verifyScenario(Scenario<T> scenario, int level) {
    scenario.testCompareTo();
    scenario.testIsOrdered();
    scenario.testMinAndMax();
    scenario.testBinarySearch();

    if (level < RECURSE_DEPTH) {
      for (OrderingMutation alteration : OrderingMutation.values()) {
        verifyScenario(alteration.mutate(scenario), level + 1);
      }
    }
  }

  /**
   * An aggregation of an ordering with a list (of size > 1) that should prove
   * to be in strictly increasing order according to that ordering.
   */
  private static class Scenario<T> {
    final Ordering<T> ordering;
    final List<T> strictlyOrderedList;

    Scenario(Ordering<T> ordering, List<T> strictlyOrderedList) {
      this.ordering = ordering;
      this.strictlyOrderedList = strictlyOrderedList;
    }

    void testCompareTo() {
      Helpers.testComparator(ordering, strictlyOrderedList);
    }

    void testIsOrdered() {
      assertTrue(ordering.isOrdered(strictlyOrderedList));
      assertTrue(ordering.isStrictlyOrdered(strictlyOrderedList));
    }

    void testMinAndMax() {
      List<T> shuffledList = Lists.newArrayList(strictlyOrderedList);
      Collections.shuffle(shuffledList, new Random(5));

      assertEquals(strictlyOrderedList.get(0), ordering.min(shuffledList));
      assertEquals(strictlyOrderedList.get(strictlyOrderedList.size() - 1),
          ordering.max(shuffledList));
    }

    void testBinarySearch() {
      for (int i = 0; i < strictlyOrderedList.size(); i++) {
        assertEquals(i, ordering.binarySearch(
            strictlyOrderedList, strictlyOrderedList.get(i)));
      }
      List<T> newList = Lists.newArrayList(strictlyOrderedList);
      T valueNotInList = newList.remove(1);
      assertEquals(-2, ordering.binarySearch(newList, valueNotInList));
    }
  }

  /**
   * A means for changing an Ordering into another Ordering. Each instance is
   * responsible for creating the alternate Ordering, and providing a List that
   * is known to be ordered, based on an input List known to be ordered
   * according to the input Ordering.
   */
  private enum OrderingMutation {
    REVERSE {
      <T> Scenario<?> mutate(Scenario<T> scenario) {
        List<T> newList = Lists.newArrayList(scenario.strictlyOrderedList);
        Collections.reverse(newList);
        return new Scenario<T>(scenario.ordering.reverse(), newList);
      }
    },
    NULLS_FIRST {
      <T> Scenario<?> mutate(Scenario<T> scenario) {
        @SuppressWarnings("unchecked")
        List<T> newList = Lists.newArrayList((T) null);
        for (T t : scenario.strictlyOrderedList) {
          if (t != null) {
            newList.add(t);
          }
        }
        return new Scenario<T>(scenario.ordering.nullsFirst(), newList);
      }
    },
    NULLS_LAST {
      <T> Scenario<?> mutate(Scenario<T> scenario) {
        List<T> newList = Lists.newArrayList();
        for (T t : scenario.strictlyOrderedList) {
          if (t != null) {
            newList.add(t);
          }
        }
        newList.add(null);
        return new Scenario<T>(scenario.ordering.nullsLast(), newList);
      }
    },
    ON_RESULT_OF {
      <T> Scenario<?> mutate(final Scenario<T> scenario) {
        Ordering<Integer> ordering = scenario.ordering.onResultOf(
            new Function<Integer, T>() {
              public T apply(@Nullable Integer from) {
                return scenario.strictlyOrderedList.get(from);
              }
            });
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < scenario.strictlyOrderedList.size(); i++) {
          list.add(i);
        }
        return new Scenario<Integer>(ordering, list);
      }
    },
    COMPOUND_THIS_WITH_NATURAL {
      <T> Scenario<?> mutate(Scenario<T> scenario) {
        List<Composite<T>> composites = Lists.newArrayList();
        for (T t : scenario.strictlyOrderedList) {
          composites.add(new Composite<T>(t, 1));
          composites.add(new Composite<T>(t, 2));
        }
        Ordering<Composite<T>> ordering =
            scenario.ordering.onResultOf(Composite.<T>getValueFunction())
                .compound(Ordering.natural());
        return new Scenario<Composite<T>>(ordering, composites);
      }
    },
    COMPOUND_NATURAL_WITH_THIS {
      <T> Scenario<?> mutate(Scenario<T> scenario) {
        List<Composite<T>> composites = Lists.newArrayList();
        for (T t : scenario.strictlyOrderedList) {
          composites.add(new Composite<T>(t, 1));
        }
        for (T t : scenario.strictlyOrderedList) {
          composites.add(new Composite<T>(t, 2));
        }
        Ordering<Composite<T>> ordering = Ordering.natural().compound(
            scenario.ordering.onResultOf(Composite.<T>getValueFunction()));
        return new Scenario<Composite<T>>(ordering, composites);
      }
    },
    ;

    abstract <T> Scenario<?> mutate(Scenario<T> scenario);
  }

  /**
   * A dummy object we create so that we can have something meaningful to have
   * a compound ordering over.
   */
  private static class Composite<T> implements Comparable<Composite<T>> {
    final T value;
    final int rank;

    Composite(T value, int rank) {
      this.value = value;
      this.rank = rank;
    }

    // natural order is by rank only; the test will compound() this with the
    // order of 't'.
    public int compareTo(Composite<T> that) {
      return rank < that.rank ? -1 : rank > that.rank ? 1 : 0; 
    }

    static <T> Function<Composite<T>, T> getValueFunction() {
      return new Function<Composite<T>, T>() {
        public T apply(Composite<T> from) {
          return from.value;
        }
      };
    }
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Ordering.class);
    
    // any Ordering<Object> instance should be good enough
    tester.testAllPublicInstanceMethods(Ordering.usingToString());
  }
}
