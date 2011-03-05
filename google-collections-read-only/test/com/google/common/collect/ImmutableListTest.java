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

import com.google.common.collect.ImmutableList.Builder;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.unmodifiableIterable;
import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.collect.Sets.newHashSet;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.MinimalIterable;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.TestUnhashableCollectionGenerator;
import com.google.common.collect.testing.UnhashableObject;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.testers.ListHashCodeTester;
import static com.google.common.testing.junit3.JUnitAsserts.assertNotEqual;
import com.google.common.testutils.NullPointerTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Unit test for {@link ImmutableList}.
 *
 * @author Kevin Bourrillion
 * @author George van den Driessche
 * @author Jared Levy
 */
public class ImmutableListTest extends TestCase {
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            return ImmutableList.of(elements);
          }
        })
        .named("ImmutableList")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            Builder<String> builder = ImmutableList.<String>builder();
            for (String element : elements) {
              builder.add(element);
            }
            return builder.build();
          }
        })
        .named("ImmutableList, built with Builder.add")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            return ImmutableList.<String>builder()
                .addAll(asList(elements))
                .build();
          }
        })
        .named("ImmutableList, built with Builder.addAll")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            return SerializableTester.reserialize(ImmutableList.of(elements));
          }
        })
        .named("ImmutableList, reserialized")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            String[] suffix = {"f", "g"};
            String[] all = concat(elements, suffix, String.class);
            return ImmutableList.of(all)
                .subList(0, elements.length);
          }
        })
        .named("ImmutableList, head subList")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            String[] prefix = {"f", "g"};
            String[] all = concat(prefix, elements, String.class);
            return ImmutableList.of(all)
                .subList(2, elements.length + 2);
          }
        })
        .named("ImmutableList, tail subList")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            String[] prefix = {"f", "g"};
            String[] suffix = {"h", "i"};
            String[] all = concat(prefix,
                concat(elements, suffix, String.class), String.class);
            return ImmutableList.of(all)
                .subList(2, elements.length + 2);
          }
        })
        .named("ImmutableList, middle subList")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    suite.addTest(ListTestSuiteBuilder.using(new TestUnhashableListGenerator() {
          @Override
          public List<UnhashableObject> create(UnhashableObject[] elements) {
            return ImmutableList.of(elements);
          }
        })
        .suppressing(ListHashCodeTester.getHashCodeMethod())
        .named("ImmutableList, unhashable values")
        .withFeatures(CollectionSize.ANY)
        .createTestSuite());
    return suite;
  }

  private abstract static class TestUnhashableListGenerator
      extends TestUnhashableCollectionGenerator<List<UnhashableObject>>
      implements TestListGenerator<UnhashableObject> {
  }

  public static class CreationTests extends TestCase {
    public void testCreation_noArgs() {
      List<String> list = ImmutableList.of();
      assertEquals(Collections.emptyList(), list);
    }

    public void testCreation_oneElement() {
      List<String> list = ImmutableList.of("a");
      assertEquals(Collections.singletonList("a"), list);
    }

    public void testCreation_twoElements() {
      List<String> list = ImmutableList.of("a", "b");
      assertEquals(Lists.newArrayList("a", "b"), list);
    }

    public void testCreation_threeElements() {
      List<String> list = ImmutableList.of("a", "b", "c");
      assertEquals(Lists.newArrayList("a", "b", "c"), list);
    }

    public void testCreation_fourElements() {
      List<String> list = ImmutableList.of("a", "b", "c", "d");
      assertEquals(Lists.newArrayList("a", "b", "c", "d"), list);
    }

    public void testCreation_fiveElements() {
      List<String> list = ImmutableList.of("a", "b", "c", "d", "e");
      assertEquals(Lists.newArrayList("a", "b", "c", "d", "e"), list);
    }

    public void testCreation_sixElements() {
      List<String> list = ImmutableList.of("a", "b", "c", "d", "e", "f");
      assertEquals(Lists.newArrayList("a", "b", "c", "d", "e", "f"), list);
    }

    public void testCreation_sevenElements() {
      List<String> list = ImmutableList.of("a", "b", "c", "d", "e", "f", "g");
      assertEquals(Lists.newArrayList("a", "b", "c", "d", "e", "f", "g"), list);
    }

    public void testCreation_eightElements() {
      List<String> list = ImmutableList.of(
          "a", "b", "c", "d", "e", "f", "g", "h");
      assertEquals(Lists.newArrayList(
          "a", "b", "c", "d", "e", "f", "g", "h"), list);
    }

    public void testCreation_nineElements() {
      List<String> list = ImmutableList.of(
          "a", "b", "c", "d", "e", "f", "g", "h", "i");
      assertEquals(Lists.newArrayList(
          "a", "b", "c", "d", "e", "f", "g", "h", "i"), list);
    }

    public void testCreation_tenElements() {
      List<String> list = ImmutableList.of(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
      assertEquals(Lists.newArrayList(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"), list);
    }

    public void testCreation_elevenElements() {
      List<String> list = ImmutableList.of(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
      assertEquals(Lists.newArrayList(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"), list);
    }

    public void testCreation_twelveElements() {
      // now we'll get the varargs overload
      List<String> list = ImmutableList.of(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l");
      assertEquals(Lists.newArrayList(
          "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"), list);
    }

    public void testCreation_singletonNull() {
      try {
        ImmutableList.of((String) null);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testCreation_withNull() {
      try {
        ImmutableList.of("a", null, "b");
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testCreation_generic() {
      List<String> a = ImmutableList.of("a");
      // only verify that there is no compile warning
      ImmutableList.of(a, a);
    }

    public void testCreation_emptyArray() {
      String[] array = new String[0];
      List<String> list = ImmutableList.of(array);
      assertEquals(Collections.emptyList(), list);
    }

    public void testCreation_arrayOfOneElement() {
      String[] array = new String[] { "a" };
      List<String> list = ImmutableList.of(array);
      assertEquals(Collections.singletonList("a"), list);
    }

    public void testCreation_arrayOfArray() {
      String[] array = new String[] { "a" };
      List<String[]> list = ImmutableList.<String[]>of(array);
      assertEquals(Collections.singletonList(array), list);
    }

    public void testCreation_arrayContainingOnlyNull() {
      String[] array = new String[] { null };
      try {
        ImmutableList.of(array);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testCopyOf_collection_empty() {
      // "<String>" is required to work around a javac 1.5 bug.
      Collection<String> c = MinimalCollection.<String>of();
      List<String> list = ImmutableList.copyOf(c);
      assertEquals(Collections.emptyList(), list);
    }

    public void testCopyOf_collection_oneElement() {
      Collection<String> c = MinimalCollection.of("a");
      List<String> list = ImmutableList.copyOf(c);
      assertEquals(Collections.singletonList("a"), list);
    }

    public void testCopyOf_collection_general() {
      Collection<String> c = MinimalCollection.of("a", "b", "a");
      List<String> list = ImmutableList.copyOf(c);
      assertEquals(asList("a", "b", "a"), list);
      List<String> mutableList = asList("a", "b");
      list = ImmutableList.copyOf(mutableList);
      mutableList.set(0, "c");
      assertEquals(asList("a", "b"), list);
    }

    public void testCopyOf_collectionContainingNull() {
      Collection<String> c = MinimalCollection.of("a", null, "b");
      try {
        ImmutableList.copyOf(c);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testCopyOf_iterator_empty() {
      Iterator<String> iterator = Iterators.emptyIterator();
      List<String> list = ImmutableList.copyOf(iterator);
      assertEquals(Collections.emptyList(), list);
    }

    public void testCopyOf_iterator_oneElement() {
      Iterator<String> iterator = Iterators.singletonIterator("a");
      List<String> list = ImmutableList.copyOf(iterator);
      assertEquals(Collections.singletonList("a"), list);
    }

    public void testCopyOf_iterator_general() {
      Iterator<String> iterator = asList("a", "b", "a").iterator();
      List<String> list = ImmutableList.copyOf(iterator);
      assertEquals(asList("a", "b", "a"), list);
    }

    public void testCopyOf_iteratorContainingNull() {
      Iterator<String> iterator = asList("a", null, "b").iterator();
      try {
        ImmutableList.copyOf(iterator);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    private static class CountingIterable implements Iterable<String> {
      int count = 0;
      public Iterator<String> iterator() {
        count++;
        return asList("a", "b", "a").iterator();
      }
    }

    public void testCopyOf_plainIterable() {
      CountingIterable iterable = new CountingIterable();
      List<String> list = ImmutableList.copyOf(iterable);
      assertEquals(asList("a", "b", "a"), list);
    }

    public void testCopyOf_plainIterable_iteratesOnce() {
      CountingIterable iterable = new CountingIterable();
      List<String> list = ImmutableList.copyOf(iterable);
      assertEquals(1, iterable.count);
    }

    public void testCopyOf_shortcut_empty() {
      Collection<String> c = ImmutableList.of();
      assertSame(c, ImmutableList.copyOf(c));
    }

    public void testCopyOf_shortcut_singleton() {
      Collection<String> c = ImmutableList.of("a");
      assertSame(c, ImmutableList.copyOf(c));
    }

    public void testCopyOf_shortcut_immutableList() {
      Collection<String> c = ImmutableList.of("a", "b", "c");
      assertSame(c, ImmutableList.copyOf(c));
    }

    enum WrapWithIterable { WRAP, NO_WRAP }

    private static void runConcurrentlyMutatedTest(
        Collection<Integer> initialContents,
        Iterable<ListFrobber> actionsToPerformConcurrently,
        WrapWithIterable wrap) {
      ConcurrentlyMutatedList<Integer> concurrentlyMutatedList =
          newConcurrentlyMutatedList(
              initialContents, actionsToPerformConcurrently);

      Iterable<Integer> iterableToCopy = wrap == WrapWithIterable.WRAP
          ? unmodifiableIterable(concurrentlyMutatedList)
          : concurrentlyMutatedList;

      ImmutableList<Integer> copyOfIterable =
          ImmutableList.copyOf(iterableToCopy);

      assertTrue(concurrentlyMutatedList.getAllStates()
          .contains(copyOfIterable));
      // Check that it's a RegularImmutableList iff it is nonempty:
      assertEquals(copyOfIterable.size() == 0, copyOfIterable.isEmpty());
    }

    private static void runConcurrentlyMutatedTest(WrapWithIterable wrap) {
      /*
       * TODO: Iterate over many array sizes and all possible operation lists,
       * performing adds and removes in different ways.
       */
      runConcurrentlyMutatedTest(
          elements(),
          ops(add(1), add(2)),
          wrap);

      runConcurrentlyMutatedTest(
          elements(),
          ops(add(1), nop()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(),
          ops(add(1), remove()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(),
          ops(nop(), add(1)),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1),
          ops(remove(), nop()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1),
          ops(remove(), add(2)),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1, 2),
          ops(remove(), remove()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1, 2),
          ops(remove(), nop()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1, 2),
          ops(remove(), add(3)),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1, 2),
          ops(nop(), remove()),
          wrap);

      runConcurrentlyMutatedTest(
          elements(1, 2, 3),
          ops(remove(), remove()),
          wrap);
    }

    private static ImmutableList<Integer> elements(Integer... elements) {
      return ImmutableList.of(elements);
    }

    private static ImmutableList<ListFrobber> ops(ListFrobber... elements) {
      return ImmutableList.of(elements);
    }

    public void testCopyOf_concurrentlyMutatedList() {
      runConcurrentlyMutatedTest(WrapWithIterable.NO_WRAP);
    }

    public void testCopyOf_concurrentlyMutatedIterable() {
      runConcurrentlyMutatedTest(WrapWithIterable.WRAP);
    }

    /** An operation to perform on a list. */
    interface ListFrobber {
      void perform(List<Integer> list);
    }

    static final ListFrobber add(final int element) {
      return new ListFrobber() {
        public void perform(List<Integer> list) {
          list.add(0, element);
        }
      };
    }

    static final ListFrobber remove() {
      return new ListFrobber() {
        public void perform(List<Integer> list) {
          list.remove(0);
        }
      };
    }

    static final ListFrobber nop() {
      return new ListFrobber() {
        public void perform(List<Integer> list) {
        }
      };
    }

    /**
     * A list that mutates itself after every call to each of its {@link List}
     * methods.
     */
    interface ConcurrentlyMutatedList<E> extends List<E> {
      /**
       * The elements of a {@link ConcurrentlyMutatedList} are added and removed
       * over time. This method returns every state that the list has passed
       * through at some point.
       */
      Set<List<E>> getAllStates();
    }

    /**
     * Returns a {@link ConcurrentlyMutatedList} that performs the given
     * operations as its concurrent modifications. The mutations occur in the
     * same thread as the triggering method call.
     */
    private static ConcurrentlyMutatedList<Integer> newConcurrentlyMutatedList(
        final Collection<Integer> initialContents,
        final Iterable<ListFrobber> actionsToPerformConcurrently) {
      InvocationHandler invocationHandler = new InvocationHandler() {
        final CopyOnWriteArrayList<Integer> delegate =
            new CopyOnWriteArrayList<Integer>(initialContents);

        final Method getAllStatesMethod = getOnlyElement(asList(
            ConcurrentlyMutatedList.class.getDeclaredMethods()));

        final Iterator<ListFrobber> remainingActions =
            actionsToPerformConcurrently.iterator();

        final Set<List<Integer>> allStates = newHashSet();

        public Object invoke(Object proxy, Method method,
            Object[] args) throws Throwable {
          return method.equals(getAllStatesMethod)
              ? getAllStates()
              : invokeListMethod(method, args);
        }

        private Set<List<Integer>> getAllStates() {
          return allStates;
        }

        private Object invokeListMethod(Method method, Object[] args)
            throws Throwable {
          try {
            Object returnValue = method.invoke(delegate, args);
            mutateDelegate();
            return returnValue;
          } catch (InvocationTargetException e) {
            throw e.getCause();
          } catch (IllegalAccessException e) {
            throw new AssertionError(e);
          }
        }

        private void mutateDelegate() {
          allStates.add(ImmutableList.copyOf(delegate));
          remainingActions.next().perform(delegate);
          allStates.add(ImmutableList.copyOf(delegate));
        }
      };

      @SuppressWarnings("unchecked")
      ConcurrentlyMutatedList<Integer> list =
          (ConcurrentlyMutatedList<Integer>) newProxyInstance(
              ImmutableListTest.CreationTests.class.getClassLoader(),
              new Class[] {ConcurrentlyMutatedList.class}, invocationHandler);
      return list;
    }

    public void testNullPointers() throws Exception {
      NullPointerTester tester = new NullPointerTester();
      tester.testAllPublicStaticMethods(ImmutableList.class);
      // TODO: get this re-enabled
      // tester.testAllPublicInstanceMethods(ImmutableList.of(1, 2, 3));
    }

    public void testSerialization_empty() {
      Collection<String> c = ImmutableList.of();
      assertSame(c, SerializableTester.reserialize(c));
    }

    public void testSerialization_singleton() {
      Collection<String> c = ImmutableList.of("a");
      ImmutableList<String> copy = (SingletonImmutableList<String>)
          SerializableTester.reserializeAndAssert(c);
    }

    public void testSerialization_multiple() {
      Collection<String> c = ImmutableList.of("a", "b", "c");
      SerializableTester.reserializeAndAssert(c);
    }

    public void testEquals_immutableList() {
      Collection<String> c = ImmutableList.of("a", "b", "c");
      assertEquals(c, ImmutableList.of("a", "b", "c"));
      assertNotEqual(c, ImmutableList.of("a", "c", "b"));
      assertNotEqual(c, ImmutableList.of("a", "b"));
      assertNotEqual(c, ImmutableList.of("a", "b", "c", "d"));
    }

    public void testBuilderAdd() {
      ImmutableList<String> list = new ImmutableList.Builder<String>()
          .add("a")
          .add("b")
          .add("a")
          .add("c")
          .build();
      assertEquals(asList("a", "b", "a", "c"), list);
    }

    public void testBuilderAdd_varargs() {
      ImmutableList<String> list = new ImmutableList.Builder<String>()
          .add("a", "b", "a", "c")
          .build();
      assertEquals(asList("a", "b", "a", "c"), list);
    }

    public void testBuilderAddAll_iterable() {
      List<String> a = asList("a", "b");
      List<String> b = asList("c", "d");
      ImmutableList<String> list = new ImmutableList.Builder<String>()
          .addAll(a)
          .addAll(b)
          .build();
      assertEquals(asList( "a", "b", "c", "d"), list);
      b.set(0, "f");
      assertEquals(asList( "a", "b", "c", "d"), list);
    }

    public void testBuilderAddAll_iterator() {
      List<String> a = asList("a", "b");
      List<String> b = asList("c", "d");
      ImmutableList<String> list = new ImmutableList.Builder<String>()
          .addAll(a.iterator())
          .addAll(b.iterator())
          .build();
      assertEquals(asList( "a", "b", "c", "d"), list);
      b.set(0, "f");
      assertEquals(asList( "a", "b", "c", "d"), list);
    }

    public void testComplexBuilder() {
      List<Integer> colorElem = asList(0x00, 0x33, 0x66, 0x99, 0xCC, 0xFF);
      ImmutableList.Builder<Integer> webSafeColorsBuilder
          = ImmutableList.builder();
      for (Integer red : colorElem) {
        for (Integer green : colorElem) {
          for (Integer blue : colorElem) {
            webSafeColorsBuilder.add((red << 16) + (green << 8) + blue);
          }
        }
      }
      ImmutableList<Integer> webSafeColors = webSafeColorsBuilder.build();
      assertEquals(216, webSafeColors.size());
      Integer[] webSafeColorArray =
          webSafeColors.toArray(new Integer[webSafeColors.size()]);
      assertEquals(0x000000, (int) webSafeColorArray[0]);
      assertEquals(0x000033, (int) webSafeColorArray[1]);
      assertEquals(0x000066, (int) webSafeColorArray[2]);
      assertEquals(0x003300, (int) webSafeColorArray[6]);
      assertEquals(0x330000, (int) webSafeColorArray[36]);
      assertEquals(0x000066, (int) webSafeColors.get(2));
      assertEquals(0x003300, (int) webSafeColors.get(6));
      ImmutableList<Integer> addedColor
          = webSafeColorsBuilder.add(0x00BFFF).build();
      assertEquals("Modifying the builder should not have changed any already"
          + " built sets", 216, webSafeColors.size());
      assertEquals("the new array should be one bigger than webSafeColors",
          217, addedColor.size());
      Integer[] appendColorArray =
          addedColor.toArray(new Integer[addedColor.size()]);
      assertEquals(0x00BFFF, (int) appendColorArray[216]);
    }

    public void testBuilderAddHandlesNullsCorrectly() {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      try {
        builder.add((String) null);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      try {
        builder.add((String[]) null);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      try {
        builder.add("a", null, "b");
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderAddAllHandlesNullsCorrectly() {
      ImmutableList.Builder<String> builder = ImmutableList.builder();
      try {
        builder.addAll((Iterable<String>) null);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      try {
        builder.addAll((Iterator<String>) null);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      builder = ImmutableList.builder();
      List<String> listWithNulls = asList("a", null, "b");
      try {
        builder.addAll(listWithNulls);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      builder = ImmutableList.builder();
      Iterator<String> iteratorWithNulls = asList("a", null, "b").iterator();
      try {
        builder.addAll(iteratorWithNulls);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }

      Iterable<String> iterableWithNulls = MinimalIterable.of("a", null, "b");
      try {
        builder.addAll(iterableWithNulls);
        fail("expected NullPointerException");
      } catch (NullPointerException expected) {
      }
    }
  }
}
