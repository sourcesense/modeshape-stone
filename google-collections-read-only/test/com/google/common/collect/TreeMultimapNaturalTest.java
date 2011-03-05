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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import com.google.common.collect.SetsTest.Derived;
import com.google.common.collect.testing.Helpers;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.SerializableTester;

import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

/**
 * Unit tests for {@code TreeMultimap} with natural ordering.
 *
 * @author Jared Levy
 */
public class TreeMultimapNaturalTest<E> extends AbstractSetMultimapTest {
  @Override protected Multimap<String, Integer> create() {
    return TreeMultimap.create();
  }

  /* Null keys and values aren't supported. */
  @Override protected String nullKey() {
    return "null";
  }

  @Override protected Integer nullValue() {
    return 42;
  }

  /**
   * Create and populate a {@code TreeMultimap} with the natural ordering of
   * keys and values.
   */
  private TreeMultimap<String, Integer> createPopulate() {
    TreeMultimap<String, Integer> multimap = TreeMultimap.create();
    multimap.put("google", 2);
    multimap.put("google", 6);
    multimap.put("foo", 3);
    multimap.put("foo", 1);
    multimap.put("foo", 7);
    multimap.put("tree", 4);
    multimap.put("tree", 0);
    return multimap;
  }

  public void testToString() {
    assertEquals("{bar=[1, 2, 3], foo=[-1, 1, 2, 3, 4]}",
        createSample().toString());
  }

  public void testOrderedGet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.get("foo"), 1, 3, 7);
    assertContentsInOrder(multimap.get("google"), 2, 6);
    assertContentsInOrder(multimap.get("tree"), 0, 4);
  }

  public void testOrderedKeySet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(
        multimap.keySet(), "foo", "google", "tree");
  }

  public void testOrderedAsMapEntries() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    Iterator<Map.Entry<String, Collection<Integer>>> iterator =
        multimap.asMap().entrySet().iterator();
    Map.Entry<String, Collection<Integer>> entry = iterator.next();
    assertEquals("foo", entry.getKey());
    Helpers.assertContentsAnyOrder(entry.getValue(), 1, 3, 7);
    entry = iterator.next();
    assertEquals("google", entry.getKey());
    Helpers.assertContentsAnyOrder(entry.getValue(), 2, 6);
    entry = iterator.next();
    assertEquals("tree", entry.getKey());
    Helpers.assertContentsAnyOrder(entry.getValue(), 0, 4);
  }

  public void testOrderedEntries() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.entries(),
        Maps.immutableEntry("foo", 1),
        Maps.immutableEntry("foo", 3),
        Maps.immutableEntry("foo", 7),
        Maps.immutableEntry("google", 2),
        Maps.immutableEntry("google", 6),
        Maps.immutableEntry("tree", 0),
        Maps.immutableEntry("tree", 4));
  }

  public void testOrderedValues() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.values(),
        1, 3, 7, 2, 6, 0, 4);
  }

  public void testFirst() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertEquals(Integer.valueOf(1), multimap.get("foo").first());
    try {
      multimap.get("missing").first();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException expected) {}
  }

  public void testLast() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertEquals(Integer.valueOf(7), multimap.get("foo").last());
    try {
      multimap.get("missing").last();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException expected) {}
  }

  public void testHeadSet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    Set<Integer> fooSet = multimap.get("foo").headSet(4);
    assertEquals(Sets.newHashSet(1, 3), fooSet);
    Set<Integer> missingSet = multimap.get("missing").headSet(4);
    assertEquals(Sets.newHashSet(), missingSet);

    multimap.put("foo", 0);
    assertEquals(Sets.newHashSet(0, 1, 3), fooSet);

    missingSet.add(2);
    assertEquals(Sets.newHashSet(2), multimap.get("missing"));
  }

  public void testTailSet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    Set<Integer> fooSet = multimap.get("foo").tailSet(2);
    assertEquals(Sets.newHashSet(3, 7), fooSet);
    Set<Integer> missingSet = multimap.get("missing").tailSet(4);
    assertEquals(Sets.newHashSet(), missingSet);

    multimap.put("foo", 6);
    assertEquals(Sets.newHashSet(3, 6, 7), fooSet);

    missingSet.add(9);
    assertEquals(Sets.newHashSet(9), multimap.get("missing"));
  }

  public void testSubSet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    Set<Integer> fooSet = multimap.get("foo").subSet(2, 6);
    assertEquals(Sets.newHashSet(3), fooSet);

    multimap.put("foo", 5);
    assertEquals(Sets.newHashSet(3, 5), fooSet);

    fooSet.add(4);
    assertEquals(Sets.newHashSet(1, 3, 4, 5, 7), multimap.get("foo"));
  }

  /**
   * Test that creating one TreeMultimap from a non-TreeMultimap
   * results in natural ordering.
   */
  public void testCreateFromHashMultimap() {
    Multimap<Double, Double> hash = HashMultimap.create();
    hash.put(1.0, 2.0);
    hash.put(2.0, 3.0);
    hash.put(3.0, 4.0);
    hash.put(4.0, 5.0);

    TreeMultimap<Double, Double> copyFromHash = TreeMultimap.create(hash);
    assertEquals(hash, copyFromHash);
    assertEquals(Ordering.natural(), copyFromHash.keyComparator());
    assertEquals(Ordering.natural(), copyFromHash.valueComparator());
  }

  public void testComparators() {
    TreeMultimap<String, Integer> multimap = TreeMultimap.create();
    assertEquals(Ordering.natural(), multimap.keyComparator());
    assertEquals(Ordering.natural(), multimap.valueComparator());
  }

  public void testSortedKeySet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    SortedSet<String> keySet = multimap.keySet();

    assertEquals("foo", keySet.first());
    assertEquals("tree", keySet.last());
    assertEquals(Ordering.natural(), keySet.comparator());
    assertEquals(ImmutableSet.of("foo", "google"), keySet.headSet("hi"));
    assertEquals(ImmutableSet.of("tree"), keySet.tailSet("hi"));
    assertEquals(ImmutableSet.of("google"), keySet.subSet("gap", "hi"));
  }

  public void testKeySetSubSet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    SortedSet<String> keySet = multimap.keySet();
    SortedSet<String> subSet = keySet.subSet("gap", "hi");

    assertEquals(1, subSet.size());
    assertTrue(subSet.contains("google"));
    assertFalse(subSet.contains("foo"));
    assertTrue(subSet.containsAll(Collections.singleton("google")));
    assertFalse(subSet.containsAll(Collections.singleton("foo")));

    Iterator<String> iterator = subSet.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("google", iterator.next());
    assertFalse(iterator.hasNext());

    assertFalse(subSet.remove("foo"));
    assertTrue(multimap.containsKey("foo"));
    assertEquals(7, multimap.size());
    assertTrue(subSet.remove("google"));
    assertFalse(multimap.containsKey("google"));
    assertEquals(5, multimap.size());
  }

  public void testGetIteration() throws Exception {
    new IteratorTester<Integer>(6, MODIFIABLE,
        Sets.newTreeSet(asList(2, 3, 4, 7, 8)),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Integer> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(3, 8, 4));
        multimap.putAll("bar", asList(5, 6));
        multimap.putAll("foo", asList(7, 2));
        return multimap.get("foo").iterator();
      }

      @Override protected void verify(List<Integer> elements) {
        assertEquals(newHashSet(elements), multimap.get("foo"));
      }
    }.test();
  }

  @SuppressWarnings("unchecked")
  public void testEntriesIteration() throws Exception {
    Set<Entry<String, Integer>> set = Sets.newLinkedHashSet(asList(
        Helpers.mapEntry("bar", 4),
        Helpers.mapEntry("bar", 5),
        Helpers.mapEntry("foo", 2),
        Helpers.mapEntry("foo", 3),
        Helpers.mapEntry("foo", 6)));
    new IteratorTester<Entry<String, Integer>>(6, MODIFIABLE, set,
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Entry<String, Integer>> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(6, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(2));
        return multimap.entries().iterator();
      }

      @Override protected void verify(List<Entry<String, Integer>> elements) {
        assertEquals(newHashSet(elements), multimap.entries());
      }
    }.test();
  }

  public void testKeysIteration() throws Exception {
    new IteratorTester<String>(6, MODIFIABLE, Lists.newArrayList("bar", "bar",
        "foo", "foo", "foo"), IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<String> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        return multimap.keys().iterator();
      }

      @Override protected void verify(List<String> elements) {
        assertEquals(elements, Lists.newArrayList(multimap.keys()));
      }
    }.test();
  }

  public void testValuesIteration() throws Exception {
    new IteratorTester<Integer>(6, MODIFIABLE, newArrayList(4, 5, 2, 3, 6),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Integer> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        return multimap.values().iterator();
      }

      @Override protected void verify(List<Integer> elements) {
        assertEquals(elements, Lists.newArrayList(multimap.values()));
      }
    }.test();
  }

  public void testKeySetIteration() throws Exception {
    new IteratorTester<String>(6, MODIFIABLE,
        Sets.newTreeSet(asList("bar", "baz", "cat", "dog", "foo")),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<String> newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        multimap.putAll("baz", asList(7, 8));
        multimap.putAll("dog", asList(9));
        multimap.putAll("bar", asList(10, 11));
        multimap.putAll("cat", asList(12, 13, 14));
        return multimap.keySet().iterator();
      }

      @Override protected void verify(List<String> elements) {
        assertEquals(newHashSet(elements), multimap.keySet());
      }
    }.test();
  }

  @SuppressWarnings("unchecked")
  public void testAsSetIteration() throws Exception {
    Set<Entry<String, Collection<Integer>>> set = Sets.newTreeSet(
        new Comparator<Entry<String, ?>>() {
          public int compare(Entry<String, ?> o1, Entry<String, ?> o2) {
            return o1.getKey().compareTo(o2.getKey());
          }
        });
    Collections.addAll(set,
        Helpers.mapEntry("bar",
            (Collection<Integer>) Sets.newHashSet(4, 5, 10, 11)),
        Helpers.mapEntry("baz",
            (Collection<Integer>) Sets.newHashSet(7, 8)),
        Helpers.mapEntry("cat",
            (Collection<Integer>) Sets.newHashSet(12, 13, 14)),
        Helpers.mapEntry("dog",
            (Collection<Integer>) Sets.newHashSet(9)),
        Helpers.mapEntry("foo",
            (Collection<Integer>) Sets.newHashSet(2, 3, 6))
    );

    new IteratorTester<Entry<String, Collection<Integer>>>(6, MODIFIABLE, set,
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Multimap<String, Integer> multimap;

      @Override protected Iterator<Entry<String, Collection<Integer>>>
          newTargetIterator() {
        multimap = create();
        multimap.putAll("foo", asList(2, 3));
        multimap.putAll("bar", asList(4, 5));
        multimap.putAll("foo", asList(6));
        multimap.putAll("baz", asList(7, 8));
        multimap.putAll("dog", asList(9));
        multimap.putAll("bar", asList(10, 11));
        multimap.putAll("cat", asList(12, 13, 14));
        return multimap.asMap().entrySet().iterator();
      }

      @Override protected void verify(
          List<Entry<String, Collection<Integer>>> elements) {
        assertEquals(newHashSet(elements), multimap.asMap().entrySet());
      }
    }.test();
  }

  public void testExplicitComparatorSerialization() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    TreeMultimap<String, Integer> copy
        = SerializableTester.reserializeAndAssert(multimap);
    assertContentsInOrder(
        copy.values(), 1, 3, 7, 2, 6, 0, 4);
    assertContentsInOrder(
        copy.keySet(), "foo", "google", "tree");
    assertEquals(multimap.keyComparator(), copy.keyComparator());
    assertEquals(multimap.valueComparator(), copy.valueComparator());
  }

  public void testTreeMultimapDerived() {
    TreeMultimap<Derived, Derived> multimap = TreeMultimap.create();
    assertEquals(ImmutableMultimap.of(), multimap);
    multimap.put(new Derived("foo"), new Derived("f"));
    multimap.put(new Derived("foo"), new Derived("o"));
    multimap.put(new Derived("foo"), new Derived("o"));
    multimap.put(new Derived("bar"), new Derived("b"));
    multimap.put(new Derived("bar"), new Derived("a"));
    multimap.put(new Derived("bar"), new Derived("r"));
    assertContentsInOrder(multimap.keySet(),
        new Derived("bar"), new Derived("foo"));
    assertContentsInOrder(multimap.values(),
        new Derived("a"), new Derived("b"), new Derived("r"),
        new Derived("f"), new Derived("o"));
    assertEquals(Ordering.natural(), multimap.keyComparator());
    assertEquals(Ordering.natural(), multimap.valueComparator());
    SerializableTester.reserializeAndAssert(multimap);
  }

  public void testTreeMultimapNonGeneric() {
    TreeMultimap<LegacyComparable, LegacyComparable> multimap
        = TreeMultimap.create();
    assertEquals(ImmutableMultimap.of(), multimap);
    multimap.put(new LegacyComparable("foo"), new LegacyComparable("f"));
    multimap.put(new LegacyComparable("foo"), new LegacyComparable("o"));
    multimap.put(new LegacyComparable("foo"), new LegacyComparable("o"));
    multimap.put(new LegacyComparable("bar"), new LegacyComparable("b"));
    multimap.put(new LegacyComparable("bar"), new LegacyComparable("a"));
    multimap.put(new LegacyComparable("bar"), new LegacyComparable("r"));
    assertContentsInOrder(multimap.keySet(),
        new LegacyComparable("bar"), new LegacyComparable("foo"));
    assertContentsInOrder(multimap.values(),
        new LegacyComparable("a"),
        new LegacyComparable("b"),
        new LegacyComparable("r"),
        new LegacyComparable("f"),
        new LegacyComparable("o"));
    assertEquals(Ordering.natural(), multimap.keyComparator());
    assertEquals(Ordering.natural(), multimap.valueComparator());
    SerializableTester.reserializeAndAssert(multimap);
  }

  public void testTreeMultimapAsMapSorted() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    SortedMap<String, Collection<Integer>> asMap = multimap.asMap();
    assertEquals(Ordering.natural(), asMap.comparator());
    assertEquals("foo", asMap.firstKey());
    assertEquals("tree", asMap.lastKey());
    Set<Integer> fooValues = ImmutableSet.of(1, 3, 7);
    Set<Integer> googleValues = ImmutableSet.of(2, 6);
    Set<Integer> treeValues = ImmutableSet.of(4, 0);
    assertEquals(ImmutableMap.of("google", googleValues, "tree", treeValues),
        asMap.tailMap("g"));
    assertEquals(ImmutableMap.of("google", googleValues, "foo", fooValues),
        asMap.headMap("h"));
    assertEquals(ImmutableMap.of("google", googleValues),
        asMap.subMap("g", "h"));
  }

  public void testTailSetClear() {
    TreeMultimap<String, Integer> multimap = TreeMultimap.create();
    multimap.put("a", 1);
    multimap.put("a", 11);
    multimap.put("b", 2);
    multimap.put("c", 3);
    multimap.put("d", 4);
    multimap.put("e", 5);
    multimap.put("e", 55);

    multimap.keySet().tailSet("d").clear();
    assertEquals(ImmutableSet.of("a", "b", "c"), multimap.keySet());
    assertEquals(4, multimap.size());
    assertEquals(4, multimap.values().size());
    assertEquals(4, multimap.keys().size());
  }
}
