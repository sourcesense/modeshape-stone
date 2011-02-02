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
package org.modeshape.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import org.modeshape.common.util.CheckArg;

/**
 * A concrete {@link NodeIterator} that returns the first node and then delegates to the supplied node iterator.
 */
class JcrNodeIterator implements NodeIterator {

    private final long size;
    private final AbstractJcrNode firstNode;
    private final NodeIterator nextNodes;
    private boolean visitedFirst = false;

    protected JcrNodeIterator( AbstractJcrNode firstNode,
                               NodeIterator nextNodes ) {
        assert firstNode != null;
        assert nextNodes != null;
        this.firstNode = firstNode;
        this.nextNodes = nextNodes;
        this.size = 1 + nextNodes.getSize();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.NodeIterator#nextNode()
     */
    public Node nextNode() {
        if (!visitedFirst) {
            visitedFirst = true;
            return firstNode;
        }
        return nextNodes.nextNode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.RangeIterator#getPosition()
     */
    public long getPosition() {
        return visitedFirst ? nextNodes.getPosition() + 1 : 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.RangeIterator#getSize()
     */
    public long getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.RangeIterator#skip(long)
     */
    public void skip( long skipNum ) {
        CheckArg.isNonNegative(skipNum, "skipNum");
        if (skipNum == 0L) return;
        if (visitedFirst) {
            nextNodes.skip(skipNum);
        }
        visitedFirst = true;
        nextNodes.skip(skipNum - 1);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return visitedFirst ? nextNodes.hasNext() : true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return nextNode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
