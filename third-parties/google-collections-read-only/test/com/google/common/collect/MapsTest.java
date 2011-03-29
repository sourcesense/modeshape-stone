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

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.SetsTest.Derived;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import static com.google.common.collect.testing.testers.CollectionIteratorTester.getIteratorUnknownOrderRemoveSupportedMethod;
import com.google.common.testing.junit3.JUnitAsserts;
import com.google.common.testutils.EqualsTester;
import com.google.common.testutils.NullPointerTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Unit test for {@code Maps}.
 *
 * @author Kevin Bourrillion
 * @author Mike Bostock
 * @author Jared Levy
 */
public class MapsTest extends TestCase {

  private static final Comparator<Integer> SOME_COMPARATOR =
      Collections.reverseOrder();

  public void testHashMap() {
    HashMap<Integer, Integer> map = Maps.newHashMap();
    assertEquals(Collections.emptyMap(), map);
  }

  public void testHashMapWithInitialMap() {
    Map<String, Integer> original = new TreeMap<String, Integer>();
    original.put("a", 1);
    original.put("b", 2);
    original.put("c", 3);
    HashMap<String, Integer> map = Maps.newHashMap(original);
    assertEquals(original, map);
  }

  public void testHashMapGeneralizesTypes() {
    Map<String, Integer> original = new TreeMap<String, Integer>();
    original.put("a", 1);
    original.put("b", 2);
    original.put("c", 3);
    HashMap<Object, Object> map =
        Maps.newHashMap((Map<? extends Object, ? extends Object>) original);
    assertEquals(original, map);
  }

  public void testCapacityForNegativeSizeFails() {
    try {
      Maps.capacity(-1);
      fail("Negative expected size must result in IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
    }
  }

  public void testCapacityForNonNegativeSizeIsAtLeast16() {
    int[] nonNegativeExpectedSizes = new int[] {0, 1, 15, 16, 32};
    for (int expectedSize : nonNegativeExpectedSizes) {
      int capacity = Maps.capacity(expectedSize);
      assertTrue(capacity >= 16);
      assertTrue(capacity >= expectedSize * 2);
    }
  }

  public void testLinkedHashMap() {
    LinkedHashMap<Integer, Integer> map = Maps.newLinkedHashMap();
    assertEquals(Collections.emptyMap(), map);
  }

  @SuppressWarnings("serial")
  public void testLinkedHashMapWithInitialMap() {
    Map<String, String> map = new LinkedHashMap<String, String>() {{
      put("Hello", "World");
      put("first", "second");
      put("polygene", "lubricants");
      put("alpha", "betical");
    }};

    LinkedHashMap<String, String> copy = Maps.newLinkedHashMap(map);

    Iterator<Entry<String, String>> iter = copy.entrySet().iterator();
    assertTrue(iter.hasNext());
    Entry<String, String> entry = iter.next();
    assertEquals("Hello", entry.getKey());
    assertEquals("World", entry.getValue());
    assertTrue(iter.hasNext());

    entry = iter.next();
    assertEquals("first", entry.getKey());
    assertEquals("second", entry.getValue());
    assertTrue(iter.hasNext());

    entry = iter.next();
    assertEquals("polygene", entry.getKey());
    assertEquals("lubricants", entry.getValue());
    assertTrue(iter.hasNext());

    entry = iter.next();
    assertEquals("alpha", entry.getKey());
    assertEquals("betical", entry.getValue());
    assertFalse(iter.hasNext());
  }

  public void testLinkedHashMapGeneralizesTypes() {
    Map<String, Integer> original = new LinkedHashMap<String, Integer>();
    original.put("a", 1);
    original.put("b", 2);
    original.put("c", 3);
    HashMap<Object, Object> map
        = Maps.<Object, Object>newLinkedHashMap(original);
    assertEquals(original, map);
  }

  public void testIdentityHashMap() {
    IdentityHashMap<Integer, Integer> map = Maps.newIdentityHashMap();
    assertEquals(Collections.emptyMap(), map);
  }

  public void testTreeMap() {
    TreeMap<Integer, Integer> map = Maps.newTreeMap();
    assertEquals(Collections.emptyMap(), map);
    assertNull(map.comparator());
  }

  public void testTreeMapDerived() {
    TreeMap<Derived, Integer> map = Maps.newTreeMap();
    assertEquals(Collections.emptyMap(), map);
    map.put(new Derived("foo"), 1);
    map.put(new Derived("bar"), 2);
    JUnitAsserts.assertContentsInOrder(map.keySet(),
        new Derived("bar"), new Derived("foo"));
    JUnitAsserts.assertContentsInOrder(map.values(), 2, 1);
    assertNull(map.comparator());
  }

  public void testTreeMapNonGeneric() {
    TreeMap<LegacyComparable, Integer> map = Maps.newTreeMap();
    assertEquals(Collections.emptyMap(), map);
    map.put(new LegacyComparable("foo"), 1);
    map.put(new LegacyComparable("bar"), 2);
    JUnitAsserts.assertContentsInOrder(map.keySet(),
        new LegacyComparable("bar"), new LegacyComparable("foo"));
    JUnitAsserts.assertContentsInOrder(map.values(), 2, 1);
    assertNull(map.comparator());
  }

  public void testTreeMapWithComparator() {
    TreeMap<Integer, Integer> map = Maps.newTreeMap(SOME_COMPARATOR);
    assertEquals(Collections.emptyMap(), map);
    assertSame(SOME_COMPARATOR, map.comparator());
  }

  public void testTreeMapWithInitialMap() {
    SortedMap<Integer, Integer> map = Maps.newTreeMap();
    map.put(5, 10);
    map.put(3, 20);
    map.put(1, 30);
    TreeMap<Integer, Integer> copy = Maps.newTreeMap(map);
    assertEquals(copy, map);
    assertSame(copy.comparator(), map.comparator());
  }

  public enum SomeEnum { SOME_INSTANCE }

  public void testEnumMap() {
    EnumMap<SomeEnum, Integer> map = Maps.newEnumMap(SomeEnum.class);
    assertEquals(Collections.emptyMap(), map);
    map.put(SomeEnum.SOME_INSTANCE, 0);
    assertEquals(Collections.singletonMap(SomeEnum.SOME_INSTANCE, 0), map);
  }

  public void testEnumMapNullClass() {
    try {
      Maps.<SomeEnum, Long>newEnumMap((Class<MapsTest.SomeEnum>) null);
      fail("no exception thrown");
    } catch (NullPointerException expected) {
    }
  }

  public void testEnumMapWithInitialEnumMap() {
    EnumMap<SomeEnum, Integer> original = Maps.newEnumMap(SomeEnum.class);
    original.put(SomeEnum.SOME_INSTANCE, 0);
    EnumMap<SomeEnum, Integer> copy = Maps.newEnumMap(original);
    assertEquals(original, copy);
  }

  public void testEnumMapWithInitialEmptyEnumMap() {
    EnumMap<SomeEnum, Integer> original = Maps.newEnumMap(SomeEnum.class);
    EnumMap<SomeEnum, Integer> copy = Maps.newEnumMap(original);
    assertEquals(original, copy);
    assertNotSame(original, copy);
  }

  public void testEnumMapWithInitialMap() {
    HashMap<SomeEnum, Integer> original = Maps.newHashMap();
    original.put(SomeEnum.SOME_INSTANCE, 0);
    EnumMap<SomeEnum, Integer> copy = Maps.newEnumMap(original);
    assertEquals(original, copy);
  }

  public void testEnumMapWithInitialEmptyMap() {
    Map<SomeEnum, Integer> original = Maps.newHashMap();
    try {
      Maps.newEnumMap(original);
      fail("Empty map must result in an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {}
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.setDefault(BiMap.class, ImmutableBiMap.of());
    tester.testAllPublicStaticMethods(Maps.class);
  }

  private static final Map<Integer,Integer> EMPTY
      = Collections.emptyMap();
  private static final Map<Integer,Integer> SINGLETON
      = Collections.singletonMap(1, 2);

  public void testMapDifferenceEmptyEmpty() {
    MapDifference<Integer,Integer> diff = Maps.difference(EMPTY, EMPTY);
    assertTrue(diff.areEqual());
    assertEquals(EMPTY, diff.entriesOnlyOnLeft());
    assertEquals(EMPTY, diff.entriesOnlyOnRight());
    assertEquals(EMPTY, diff.entriesInCommon());
    assertEquals(EMPTY, diff.entriesDiffering());
    assertEquals("equal", diff.toString());
  }

  public void testMapDifferenceEmptySingleton() {
    MapDifference<Integer,Integer> diff = Maps.difference(EMPTY, SINGLETON);
    assertFalse(diff.areEqual());
    assertEquals(EMPTY, diff.entriesOnlyOnLeft());
    assertEquals(SINGLETON, diff.entriesOnlyOnRight());
    assertEquals(EMPTY, diff.entriesInCommon());
    assertEquals(EMPTY, diff.entriesDiffering());
    assertEquals("not equal: only on right={1=2}", diff.toString());
  }

  public void testMapDifferenceSingletonEmpty() {
    MapDifference<Integer,Integer> diff = Maps.difference(SINGLETON, EMPTY);
    assertFalse(diff.areEqual());
    assertEquals(SINGLETON, diff.entriesOnlyOnLeft());
    assertEquals(EMPTY, diff.entriesOnlyOnRight());
    assertEquals(EMPTY, diff.entriesInCommon());
    assertEquals(EMPTY, diff.entriesDiffering());
    assertEquals("not equal: only on left={1=2}", diff.toString());
  }

  public void testMapDifferenceTypical() {
    Map<Integer, String> left = ImmutableMap.of(
        1, "a", 2, "b", 3, "c", 4, "d", 5, "e");
    Map<Integer, String> right = ImmutableMap.of(
        1, "a", 3, "f", 5, "g", 6, "z");

    MapDifference<Integer, String> diff1 = Maps.difference(left, right);
    assertFalse(diff1.areEqual());
    assertEquals(ImmutableMap.of(2, "b", 4, "d"), diff1.entriesOnlyOnLeft());
    assertEquals(ImmutableMap.of(6, "z"), diff1.entriesOnlyOnRight());
    assertEquals(ImmutableMap.of(1, "a"), diff1.entriesInCommon());
    assertEquals(ImmutableMap.of(3,
        new Maps.ValueDifferenceImpl<String>("c", "f"), 5,
        new Maps.ValueDifferenceImpl<String>("e", "g")),
        diff1.entriesDiffering());
    assertEquals("not equal: only on left={2=b, 4=d}: only on right={6=z}: "
        + "value differences={3=(c, f), 5=(e, g)}", diff1.toString());

    MapDifference<Integer, String> diff2 = Maps.difference(right, left);
    assertFalse(diff2.areEqual());
    assertEquals(ImmutableMap.of(6, "z"), diff2.entriesOnlyOnLeft());
    assertEquals(ImmutableMap.of(2, "b", 4, "d"), diff2.entriesOnlyOnRight());
    assertEquals(ImmutableMap.of(1, "a"), diff2.entriesInCommon());
    assertEquals(ImmutableMap.of(3,
        new Maps.ValueDifferenceImpl<String>("f", "c"), 5,
        new Maps.ValueDifferenceImpl<String>("g", "e")),
        diff2.entriesDiffering());
    assertEquals("not equal: only on left={6=z}: only on right={2=b, 4=d}: "
        + "value differences={3=(f, c), 5=(g, e)}", diff2.toString());
  }

  public void testMapDifferenceEquals() {
    Map<Integer, String> left = ImmutableMap.of(
        1, "a", 2, "b", 3, "c", 4, "d", 5, "e");
    Map<Integer, String> right = ImmutableMap.of(
        1, "a", 3, "f", 5, "g", 6, "z");
    Map<Integer, String> right2 = ImmutableMap.of(
        1, "a", 3, "h", 5, "g", 6, "z");
    MapDifference<Integer, String> original = Maps.difference(left, right);
    MapDifference<Integer, String> same = Maps.difference(left, right);
    MapDifference<Integer, String> reverse = Maps.difference(right, left);
    MapDifference<Integer, String> diff2 = Maps.difference(left, right2);

    new EqualsTester(original)
        .addEqualObject(same)
        .addNotEqualObject(reverse, diff2)
        .testEquals();
  }

  private static final BiMap<Integer, String> INT_TO_STRING_MAP =
      new ImmutableBiMap.Builder<Integer, String>()
          .put(1, "one")
          .put(2, "two")
          .put(3, "three")
          .build();

  public void testUniqueIndexCollection() {
    ImmutableMap<Integer, String> outputMap =
        Maps.uniqueIndex(INT_TO_STRING_MAP.values(),
            Functions.forMap(INT_TO_STRING_MAP.inverse()));
    assertEquals(INT_TO_STRING_MAP, outputMap);
  }

  public void testUniqueIndexIterable() {
    ImmutableMap<Integer, String> outputMap =
        Maps.uniqueIndex(new Iterable<String>() {
          public Iterator<String> iterator() {
            return INT_TO_STRING_MAP.values().iterator();
          }
        },
        Functions.forMap(INT_TO_STRING_MAP.inverse()));
    assertEquals(INT_TO_STRING_MAP, outputMap);
  }

  public void testUniqueIndexIterableWithCollection() {
    ImmutableMap<Integer, String> outputMap =
        Maps.uniqueIndex(INT_TO_STRING_MAP.values(),
            Functions.forMap(INT_TO_STRING_MAP.inverse()));
    assertEquals(INT_TO_STRING_MAP, outputMap);
  }

  /** Can't create the map if more than one value maps to the same key. */
  public void testUniqueIndexDuplicates() {
    try {
      Maps.uniqueIndex(ImmutableSet.of("one", "uno"), Functions.constant(1));
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  /** Null values are not allowed. */
  public void testUniqueIndexNullValue() {
    List<String> listWithNull = Lists.newArrayList((String) null);
    try {
      Maps.uniqueIndex(listWithNull, Functions.constant(1));
      fail();
    } catch (NullPointerException expected) {
    }
  }

  /** Null keys aren't allowed either. */
  public void testUniqueIndexNullKey() {
    List<String> oneStringList = Lists.newArrayList("foo");
    try {
      Maps.uniqueIndex(oneStringList, Functions.constant(null));
      fail();
    } catch (NullPointerException expected) {
    }
  }

  @SuppressWarnings("deprecation") // StringBufferInputStream
  public void testFromProperties() throws IOException {
    Properties testProp = new Properties();

    Map<String, String> result = Maps.fromProperties(testProp);
    assertTrue(result.isEmpty());
    testProp.setProperty("first", "true");

    result = Maps.fromProperties(testProp);
    assertEquals("true", result.get("first"));
    assertEquals(1, result.size());
    testProp.setProperty("second", "null");

    result = Maps.fromProperties(testProp);
    assertEquals("true", result.get("first"));
    assertEquals("null", result.get("second"));
    assertEquals(2, result.size());

    // Now test values loaded from a stream.
    String props = "test\n second = 2\n Third item :   a short  phrase   ";

    // TODO: change to StringReader in Java 1.6
    testProp.load(new java.io.StringBufferInputStream(props));

    result = Maps.fromProperties(testProp);
    assertEquals(4, result.size());
    assertEquals("true", result.get("first"));
    assertEquals("", result.get("test"));
    assertEquals("2", result.get("second"));
    assertEquals("item :   a short  phrase   ", result.get("Third"));
    assertFalse(result.containsKey("not here"));

    // Test loading system properties
    result = Maps.fromProperties(System.getProperties());
    assertTrue(result.containsKey("java.version"));

    // Test that defaults work, too.
    testProp = new Properties(System.getProperties());
    String override = "test\njava.version : hidden";

    // TODO: change to StringReader in Java 1.6
    testProp.load(new java.io.StringBufferInputStream(override));

    result = Maps.fromProperties(testProp);
    assertTrue(result.size() > 2);
    assertEquals("", result.get("test"));
    assertEquals("hidden", result.get("java.version"));
    assertNotSame(System.getProperty("java.version"),
                  result.get("java.version"));
  }

  @SuppressWarnings("serial") // never serialized
  public void testFromPropertiesNullKey() {
    Properties properties = new Properties() {
      @Override public Enumeration<?> propertyNames() {
        return Iterators.asEnumeration(
            Arrays.asList(null, "first", "second").iterator());
      }
    };
    properties.setProperty("first", "true");
    properties.setProperty("second", "null");

    try {
      Maps.fromProperties(properties);
      fail();
    } catch (NullPointerException expected) {}
  }

  @SuppressWarnings("serial") // never serialized
  public void testFromPropertiesNonStringKeys() {
    Properties properties = new Properties() {
      @Override public Enumeration<?> propertyNames() {
        return Iterators.asEnumeration(
            Arrays.<Object>asList(Integer.valueOf(123), "first").iterator());
      }
    };

    try {
      Maps.fromProperties(properties);
      fail();
    } catch (ClassCastException expected) {}
  }

  /**
   * Constructs a "nefarious" map entry with the specified key and value,
   * meaning an entry that is suitable for testing that map entries cannot be
   * modified via a nefarious implementation of equals. This is used for testing
   * unmodifiable collections of map entries; for example, it should not be
   * possible to access the raw (modifiable) map entry via a nefarious equals
   * method.
   */
  public static <K, V> Map.Entry<K, V> nefariousEntry(
      final K key, final V value) {
    return new AbstractMapEntry<K, V>() {
        @Override public K getKey() {
          return key;
        }
        @Override public V getValue() {
          return value;
        }
        @Override public V setValue(@SuppressWarnings("hiding") V value) {
          throw new UnsupportedOperationException();
        }
        @SuppressWarnings("unchecked")
        @Override public boolean equals(Object o) {
          if (o instanceof Map.Entry<?, ?>) {
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            e.setValue(value); // muhahaha!
          }
          return super.equals(o);
        }
      };
  }

  public void testUnmodifiableBiMap() {
    BiMap<Integer, String> mod = HashBiMap.create();
    mod.put(1, "one");
    mod.put(2, "two");
    mod.put(3, "three");

    BiMap<Number, String> unmod = Maps.<Number, String>unmodifiableBiMap(mod);

    /* No aliasing on inverse operations. */
    assertSame(unmod.inverse(), unmod.inverse());
    assertSame(unmod, unmod.inverse().inverse());

    /* Unmodifiable is a view. */
    mod.put(4, "four");
    assertEquals(true, unmod.get(4).equals("four"));
    assertEquals(true, unmod.inverse().get("four").equals(4));

    /* UnsupportedOperationException on direct modifications. */
    try {
      unmod.put(4, "four");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      unmod.forcePut(4, "four");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      unmod.putAll(Collections.singletonMap(4, "four"));
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}

    /* UnsupportedOperationException on indirect modifications. */
    BiMap<String, Number> inverse = unmod.inverse();
    try {
      inverse.put("four", 4);
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      inverse.forcePut("four", 4);
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      inverse.putAll(Collections.singletonMap("four", 4));
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    Set<String> values = unmod.values();
    try {
      values.remove("four");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    Set<Map.Entry<Number, String>> entries = unmod.entrySet();
    Map.Entry<Number, String> entry = entries.iterator().next();
    try {
      entry.setValue("four");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    @SuppressWarnings("unchecked")
    Map.Entry<Integer, String> entry2
        = (Map.Entry<Integer, String>) entries.toArray()[0];
    try {
      entry2.setValue("four");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
  }

  public void testBiMapEntrySetIteratorRemove() {
    BiMap<Integer, String> map = HashBiMap.create();
    map.put(1, "one");
    Set<Map.Entry<Integer, String>> entries = map.entrySet();
    Iterator<Map.Entry<Integer, String>> iterator = entries.iterator();
    Map.Entry<Integer, String> entry = iterator.next();
    entry.setValue("two"); // changes the iterator's current entry value
    assertEquals("two", map.get(1));
    iterator.remove(); // removes the updated entry
    assertTrue(map.isEmpty());
  }

  public void testImmutableEntry() {
    Map.Entry<String, Integer> e = Maps.immutableEntry("foo", 1);
    assertEquals("foo", e.getKey());
    assertEquals(1, (int) e.getValue());
    try {
      e.setValue(2);
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    assertEquals("foo=1", e.toString());
    assertEquals(101575, e.hashCode());
  }

  public void testImmutableEntryNull() {
    Map.Entry<String, Integer> e
        = Maps.immutableEntry((String) null, (Integer) null);
    assertNull(e.getKey());
    assertNull(e.getValue());
    try {
      e.setValue(null);
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    assertEquals("null=null", e.toString());
    assertEquals(0, e.hashCode());
  }

  /** See {@link SynchronizedBiMapTest} for more tests. */
  public void testSynchronizedBiMap() {
    BiMap<String, Integer> bimap = HashBiMap.create();
    bimap.put("one", 1);
    BiMap<String, Integer> sync = Maps.synchronizedBiMap(bimap);
    bimap.put("two", 2);
    sync.put("three", 3);
    assertEquals(ImmutableSet.of(1, 2, 3), bimap.inverse().keySet());
    assertEquals(ImmutableSet.of(1, 2, 3), sync.inverse().keySet());
  }

  private static final Predicate<String> NOT_LENGTH_3
      = new Predicate<String>() {
        public boolean apply(String input) {
          return input == null || input.length() != 3;
        }
      };

  private static final Predicate<Integer> EVEN
      = new Predicate<Integer>() {
        public boolean apply(Integer input) {
          return input == null || input % 2 == 0;
        }
      };

  private static final Predicate<Entry<String, Integer>> CORRECT_LENGTH
      = new Predicate<Entry<String, Integer>>() {
        public boolean apply(Entry<String, Integer> input) {
          return input.getKey().length() == input.getValue();
        }
      };

  public void testFilteredKeysIllegalPut() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    Map<String, Integer> filtered = Maps.filterKeys(unfiltered, NOT_LENGTH_3);
    filtered.put("a", 1);
    filtered.put("b", 2);
    assertEquals(ImmutableMap.of("a", 1, "b", 2), filtered);

    try {
      filtered.put("yyy", 3);
      fail();
    } catch (IllegalArgumentException expected) {}

    try {
      filtered.putAll(ImmutableMap.of("c", 3, "zzz", 4, "b", 5));
      fail();
    } catch (IllegalArgumentException expected) {}

    assertEquals(ImmutableMap.of("a", 1, "b", 2), filtered);
  }

  public void testFilteredKeysChangeFiltered() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    Map<String, Integer> filtered = Maps.filterKeys(unfiltered, NOT_LENGTH_3);
    unfiltered.put("two", 2);
    unfiltered.put("three", 3);
    unfiltered.put("four", 4);
    assertEquals(ImmutableMap.of("two", 2, "three", 3, "four", 4), unfiltered);
    assertEquals(ImmutableMap.of("three", 3, "four", 4), filtered);

    unfiltered.remove("three");
    assertEquals(ImmutableMap.of("two", 2, "four", 4), unfiltered);
    assertEquals(ImmutableMap.of("four", 4), filtered);

    unfiltered.clear();
    assertEquals(ImmutableMap.of(), unfiltered);
    assertEquals(ImmutableMap.of(), filtered);
  }

  public void testFilteredKeysChangeUnfiltered() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    Map<String, Integer> filtered = Maps.filterKeys(unfiltered, NOT_LENGTH_3);
    unfiltered.put("two", 2);
    unfiltered.put("three", 3);
    unfiltered.put("four", 4);
    assertEquals(ImmutableMap.of("two", 2, "three", 3, "four", 4), unfiltered);
    assertEquals(ImmutableMap.of("three", 3, "four", 4), filtered);

    filtered.remove("three");
    assertEquals(ImmutableMap.of("two", 2, "four", 4), unfiltered);
    assertEquals(ImmutableMap.of("four", 4), filtered);

    filtered.clear();
    assertEquals(ImmutableMap.of("two", 2), unfiltered);
    assertEquals(ImmutableMap.of(), filtered);
  }

  public void testFilteredValuesIllegalPut() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    Map<String, Integer> filtered = Maps.filterValues(unfiltered, EVEN);
    filtered.put("a", 2);
    unfiltered.put("b", 4);
    unfiltered.put("c", 5);
    assertEquals(ImmutableMap.of("a", 2, "b", 4), filtered);

    try {
      filtered.put("yyy", 3);
      fail();
    } catch (IllegalArgumentException expected) {}

    try {
      filtered.putAll(ImmutableMap.of("c", 4, "zzz", 5, "b", 6));
      fail();
    } catch (IllegalArgumentException expected) {}

    assertEquals(ImmutableMap.of("a", 2, "b", 4), filtered);
  }

  public void testFilteredValuesIllegalSetValue() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    Map<String, Integer> filtered = Maps.filterValues(unfiltered, EVEN);
    filtered.put("a", 2);
    filtered.put("b", 4);
    assertEquals(ImmutableMap.of("a", 2, "b", 4), filtered);

    Entry<String, Integer> entry = filtered.entrySet().iterator().next();
    try {
      entry.setValue(5);
      fail();
    } catch (IllegalArgumentException expected) {}

    assertEquals(ImmutableMap.of("a", 2, "b", 4), filtered);
  }

  public void testFilteredValuesClear() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    unfiltered.put("one", 1);
    unfiltered.put("two", 2);
    unfiltered.put("three", 3);
    unfiltered.put("four", 4);
    Map<String, Integer> filtered = Maps.filterValues(unfiltered, EVEN);
    assertEquals(ImmutableMap.of("one", 1, "two", 2, "three", 3, "four", 4),
        unfiltered);
    assertEquals(ImmutableMap.of("two", 2, "four", 4), filtered);

    filtered.clear();
    assertEquals(ImmutableMap.of("one", 1, "three", 3), unfiltered);
    assertTrue(filtered.isEmpty());
  }

  public void testFilteredEntriesIllegalPut() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    unfiltered.put("cat", 3);
    unfiltered.put("dog", 2);
    unfiltered.put("horse", 5);
    Map<String, Integer> filtered
        = Maps.filterEntries(unfiltered, CORRECT_LENGTH);
    assertEquals(ImmutableMap.of("cat", 3, "horse", 5), filtered);

    filtered.put("chicken", 7);
    assertEquals(ImmutableMap.of("cat", 3, "horse", 5, "chicken", 7), filtered);

    try {
      filtered.put("cow", 7);
      fail();
    } catch (IllegalArgumentException expected) {}
    assertEquals(ImmutableMap.of("cat", 3, "horse", 5, "chicken", 7), filtered);

    try {
      filtered.putAll(ImmutableMap.of("sheep", 5, "cow", 7));
      fail();
    } catch (IllegalArgumentException expected) {}
    assertEquals(ImmutableMap.of("cat", 3, "horse", 5, "chicken", 7), filtered);
  }

  public void testFilteredEntriesObjectPredicate() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    unfiltered.put("cat", 3);
    unfiltered.put("dog", 2);
    unfiltered.put("horse", 5);
    Predicate<Object> predicate = Predicates.alwaysFalse();
    Map<String, Integer> filtered
        = Maps.filterEntries(unfiltered, predicate);
    assertTrue(filtered.isEmpty());
  }

  public void testFilteredEntriesWildCardEntryPredicate() {
    Map<String, Integer> unfiltered = Maps.newHashMap();
    unfiltered.put("cat", 3);
    unfiltered.put("dog", 2);
    unfiltered.put("horse", 5);
    Predicate<Entry<?, ?>> predicate = new Predicate<Entry<?, ?>>() {
      public boolean apply(Entry<?, ?> input) {
        return "cat".equals(input.getKey())
            || Integer.valueOf(2) == input.getValue();
      }
    };
    Map<String, Integer> filtered
        = Maps.filterEntries(unfiltered, predicate);
    assertEquals(ImmutableMap.of("cat", 3, "dog", 2), filtered);
  }

  // TestStringMapGenerator uses entries of the form "one=January" and so forth.
  // To test the filtered collections, we'll create a map containing the entries
  // they ask for, plus some bogus numeric entries. Then our predicates will
  // simply filter numeric entries back out.

  private static ImmutableMap<String, String> ENTRIES_TO_FILTER_OUT =
      new ImmutableMap.Builder<String, String>()
          .put("0", "0")
          .put("1", "1")
          .put("2", "2")
          .build();

  public static class FilteredMapTests extends TestCase {
    public static Test suite() {
      TestSuite suite = new TestSuite();

      suite.addTest(MapTestSuiteBuilder.using(
          new TestStringMapGenerator() {
            @Override protected Map<String, String> create(
                Entry<String, String>[] entries) {
              Map<String, String> map = Maps.newHashMap();
              for (Entry<String, String> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
              }
              map.putAll(ENTRIES_TO_FILTER_OUT);
              return Maps.filterKeys(map, new Predicate<String>() {
                public boolean apply(String input) {
                  return input == null
                      || (input.charAt(0) >= 'a' && input.charAt(0) <= 'z');
                }
              });
            }
          })
          .named("Maps.filterKeys")
          .withFeatures(
              CollectionSize.ANY,
              MapFeature.ALLOWS_NULL_KEYS,
              MapFeature.ALLOWS_NULL_VALUES,
              MapFeature.GENERAL_PURPOSE)
          .suppressing(getIteratorUnknownOrderRemoveSupportedMethod())
          .createTestSuite());

      suite.addTest(MapTestSuiteBuilder.using(
          new TestStringMapGenerator() {
            @Override protected Map<String, String> create(
                Entry<String, String>[] entries) {
              Map<String, String> map = Maps.newHashMap();
              for (Entry<String, String> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
              }
              map.putAll(ENTRIES_TO_FILTER_OUT);
              return Maps.filterValues(map, new Predicate<String>() {
                public boolean apply(String input) {
                  return input == null
                      || (input.charAt(0) >= 'A' && input.charAt(0) <= 'Z');
                }
              });
            }
          })
          .named("Maps.filterValues")
          .withFeatures(
              CollectionSize.ANY,
              MapFeature.ALLOWS_NULL_KEYS,
              MapFeature.ALLOWS_NULL_VALUES,
              MapFeature.GENERAL_PURPOSE)
          .suppressing(getIteratorUnknownOrderRemoveSupportedMethod())
          .createTestSuite());

      suite.addTest(MapTestSuiteBuilder.using(
          new TestStringMapGenerator() {
            @Override protected Map<String, String> create(
                Entry<String, String>[] entries) {
              Map<String, String> map = Maps.newHashMap();
              for (Entry<String, String> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
              }
              map.putAll(ENTRIES_TO_FILTER_OUT);
              return Maps.filterEntries(map,
                  new Predicate<Entry<String, String>>() {
                    public boolean apply(Entry<String, String> entry) {
                      String input = entry.getKey();
                      return input == null
                          || (input.charAt(0) >= 'a' && input.charAt(0) <= 'z');
                    }
                  });
            }
          })
          .named("Maps.filterEntries")
          .withFeatures(
              CollectionSize.ANY,
              MapFeature.ALLOWS_NULL_KEYS,
              MapFeature.ALLOWS_NULL_VALUES,
              MapFeature.GENERAL_PURPOSE)
          .suppressing(getIteratorUnknownOrderRemoveSupportedMethod())
          .createTestSuite());

      suite.addTest(MapTestSuiteBuilder.using(
          new TestStringMapGenerator() {
            @Override protected Map<String, String> create(
                Entry<String, String>[] entries) {
              Map<String, String> map = Maps.newHashMap();
              for (Entry<String, String> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
              }
              map.putAll(ENTRIES_TO_FILTER_OUT);
              map.put("", "weird");
              Map<String, String> withoutEmptyKey = Maps.filterKeys(map,
                  new Predicate<String>() {
                    public boolean apply(String input) {
                      return input == null || input.length() != 0;
                    }
                  });
              return Maps.filterKeys(withoutEmptyKey, new Predicate<String>() {
                public boolean apply(String input) {
                  return input == null
                      || (input.charAt(0) >= 'a' && input.charAt(0) <= 'z');
                }
              });
              // note: these filters were deliberately chosen so that an
              // element somehow getting around the first filter would cause
              // an exception in the second
            }
          })
          .named("Maps.filterKeys, chained")
          .withFeatures(
              CollectionSize.ANY,
              MapFeature.ALLOWS_NULL_KEYS,
              MapFeature.ALLOWS_NULL_VALUES,
              MapFeature.GENERAL_PURPOSE)
          .suppressing(getIteratorUnknownOrderRemoveSupportedMethod())
          .createTestSuite());

      return suite;
    }
  }
}
