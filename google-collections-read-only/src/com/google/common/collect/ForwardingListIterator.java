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

import com.google.common.annotations.GwtCompatible;

import java.util.ListIterator;

/**
 * A list iterator which forwards all its method calls to another list
 * iterator. Subclasses should override one or more methods to modify the
 * behavior of the backing iterator as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * @see ForwardingObject
 * @author Mike Bostock
 */
@GwtCompatible
public abstract class ForwardingListIterator<E> extends ForwardingIterator<E>
    implements ListIterator<E> {

  @Override protected abstract ListIterator<E> delegate();

  public void add(E element) {
    delegate().add(element);
  }

  public boolean hasPrevious() {
    return delegate().hasPrevious();
  }

  public int nextIndex() {
    return delegate().nextIndex();
  }

  public E previous() {
    return delegate().previous();
  }

  public int previousIndex() {
    return delegate().previousIndex();
  }

  public void set(E element) {
    delegate().set(element);
  }
}
