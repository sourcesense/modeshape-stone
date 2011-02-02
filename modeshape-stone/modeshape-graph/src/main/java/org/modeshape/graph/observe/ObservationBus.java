/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.graph.observe;

import net.jcip.annotations.ThreadSafe;

/**
 * A simple {@link Observer} that is itself {@link Observable}. This interface essentially multiplexes the events from a single
 * Observable to disseminate each event to multiple Observers.
 */
@ThreadSafe
public interface ObservationBus extends Observable, Observer {

    /**
     * Prepare this bus for operation by starting any resources.
     */
    public void start();

    /**
     * Determine whether this particular bus currently has any observers.
     * 
     * @return true if there is at least one observer, or false otherwise
     */
    public boolean hasObservers();

    /**
     * Unregister all registered observers, and mark this as no longer accepting new registered observers.
     */
    public void shutdown();
}
