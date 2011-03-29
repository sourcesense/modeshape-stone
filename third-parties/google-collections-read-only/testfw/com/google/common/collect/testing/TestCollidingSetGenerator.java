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

package com.google.common.collect.testing;

import com.google.common.collect.testing.SampleElements.Colliders;

import java.util.List;

/**
 * A generator using sample elements whose hash codes all collide badly.
 *
 * @author Kevin Bourrillion
 */
public abstract class TestCollidingSetGenerator
    implements TestSetGenerator<Object> {
  public SampleElements<Object> samples() {
    return new Colliders();
  }

  public Object[] createArray(int length) {
    return new Object[length];
  }

  /** Returns the original element list, unchanged. */
  public List<Object> order(List<Object> insertionOrder) {
    return insertionOrder;
  }
}
