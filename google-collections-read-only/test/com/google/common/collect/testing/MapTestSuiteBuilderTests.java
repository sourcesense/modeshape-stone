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

package com.google.common.collect.testing;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.MapFeature;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_VALUES;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tests {@link MapTestSuiteBuilder} by using it against maps that have various
 * negative behaviors.
 *
 * @author George van den Driessche
 */
public final class MapTestSuiteBuilderTests extends TestCase {
  private MapTestSuiteBuilderTests() {}

  public static Test suite() {
    TestSuite suite = new TestSuite(
        MapTestSuiteBuilderTests.class.getSimpleName());
    suite.addTest(testsForHashMapNullKeysForbidden());
    suite.addTest(testsForHashMapNullValuesForbidden());
    return suite;
  }

  private abstract static class WrappedHashMapGenerator
      extends TestStringMapGenerator {
    @Override protected final Map<String, String> create(
        Map.Entry<String, String>[] entries) {
      HashMap<String, String> map = Maps.newHashMap();
      for (Map.Entry<String, String> entry : entries) {
        map.put(entry.getKey(), entry.getValue());
      }
      return wrap(map);
    }

    abstract Map<String, String> wrap(HashMap<String, String> map);
  }

  private static TestSuite wrappedHashMapTests(
      WrappedHashMapGenerator generator, String name, Feature<?>... features) {
    return MapTestSuiteBuilder.using(generator)
        .named(name)
        .withFeatures(Lists.asList(
            MapFeature.GENERAL_PURPOSE, CollectionSize.ANY, features))
        .createTestSuite();
  }

  // TODO(Chris Povirk): consider being null-hostile in these tests

  private static Test testsForHashMapNullKeysForbidden() {
    return wrappedHashMapTests(new WrappedHashMapGenerator() {
      @Override Map<String, String> wrap(final HashMap<String, String> map) {
        for (String s :map.keySet()) {
          checkNotNull(s);
        }
        return new AbstractMap<String, String>() {
          @Override public Set<Map.Entry<String, String>> entrySet() {
            return map.entrySet();
          }
          @Override public String put(String key, String value) {
            checkNotNull(key);
            return map.put(key, value);
          }
        };
      }
    }, "HashMap w/out null keys", ALLOWS_NULL_VALUES);
  }

  private static Test testsForHashMapNullValuesForbidden() {
    return wrappedHashMapTests(new WrappedHashMapGenerator() {
      @Override Map<String, String> wrap(final HashMap<String, String> map) {
        for (String s :map.values()) {
          checkNotNull(s);
        }
        return new AbstractMap<String, String>() {
          @Override public Set<Map.Entry<String, String>> entrySet() {
            return map.entrySet();
          }
          @Override public String put(String key, String value) {
            checkNotNull(value);
            return map.put(key, value);
          }
        };
      }
    }, "HashMap w/out null values", ALLOWS_NULL_KEYS);
  }
}
