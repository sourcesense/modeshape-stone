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
package org.modeshape.graph.query.model;

import net.jcip.annotations.Immutable;
import org.modeshape.common.util.CheckArg;

/**
 * A constraint requiring that the selected node is a descendant of the node reachable by the supplied absolute path
 */
@Immutable
public class DescendantNode implements Constraint {
    private static final long serialVersionUID = 1L;

    private final SelectorName selectorName;
    private final String ancestorPath;

    /**
     * Create a constraint requiring that the node identified by the selector is a descendant of the node reachable by the
     * supplied absolute path.
     * 
     * @param selectorName the name of the selector
     * @param ancestorPath the absolute path to the ancestor
     */
    public DescendantNode( SelectorName selectorName,
                           String ancestorPath ) {
        CheckArg.isNotNull(selectorName, "selectorName");
        CheckArg.isNotNull(ancestorPath, "ancestorPath");
        this.selectorName = selectorName;
        this.ancestorPath = ancestorPath;
    }

    /**
     * Get the name of the selector for the node.
     * 
     * @return the selector name; never null
     */
    public final SelectorName selectorName() {
        return selectorName;
    }

    /**
     * Get the path of the node that is to be the ancestor of the target node.
     * 
     * @return the path of the ancestor node; never null
     */
    public final String ancestorPath() {
        return ancestorPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Visitors.readable(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return selectorName().hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == this) return true;
        if (obj instanceof DescendantNode) {
            DescendantNode that = (DescendantNode)obj;
            if (!this.selectorName.equals(that.selectorName)) return false;
            if (!this.ancestorPath.equals(that.ancestorPath)) return false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.modeshape.graph.query.model.Visitable#accept(org.modeshape.graph.query.model.Visitor)
     */
    public void accept( Visitor visitor ) {
        visitor.visit(this);
    }
}
