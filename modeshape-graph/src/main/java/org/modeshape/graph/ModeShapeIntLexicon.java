/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * Unless otherwise indicated, all code in ModeShape is licensed
 * to you under the terms of the GNU Lesser General Public License as
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
package org.modeshape.graph;

import net.jcip.annotations.Immutable;
import org.modeshape.graph.property.Name;
import org.modeshape.graph.property.basic.BasicName;

/**
 * A lexicon of internal and implementation-specific information
 */
@Immutable
public class ModeShapeIntLexicon {

    public static class Namespace {
        public static final String URI = "http://www.modeshape.org/internal/1.0";
        public static final String PREFIX = "modeint";
    }

    public static final Name NODE_DEFINITON = new BasicName(Namespace.URI, "nodeDefinition");
    public static final Name MULTI_VALUED_PROPERTIES = new BasicName(Namespace.URI, "multiValuedProperties");

}
