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
package org.modeshape.graph.request;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Randall Hauch
 */
public class DeleteBranchRequestTest extends AbstractRequestTest {

    private DeleteBranchRequest request;

    @Override
    @Before
    public void beforeEach() {
        super.beforeEach();
    }

    @Override
    protected Request createRequest() {
        return new DeleteBranchRequest(validPathLocation1, workspace1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullFromLocation() {
        new DeleteBranchRequest(null, workspace1);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullWorkspaceName() {
        new DeleteBranchRequest(validPathLocation1, null);
    }

    @Test
    public void shouldCreateValidRequestWithValidLocation() {
        request = new DeleteBranchRequest(validPathLocation1, workspace1);
        assertThat(request.at(), is(sameInstance(validPathLocation1)));
        assertThat(request.inWorkspace(), is(sameInstance(workspace1)));
        assertThat(request.hasError(), is(false));
        assertThat(request.getError(), is(nullValue()));
    }

    @Test
    public void shouldConsiderEqualTwoRequestsWithSameLocations() {
        request = new DeleteBranchRequest(validPathLocation1, workspace1);
        DeleteBranchRequest request2 = new DeleteBranchRequest(validPathLocation1, workspace1);
        assertThat(request, is(request2));
    }

    @Test
    public void shouldConsiderNotEqualTwoRequestsWithDifferentLocations() {
        request = new DeleteBranchRequest(validPathLocation1, workspace1);
        DeleteBranchRequest request2 = new DeleteBranchRequest(validPathLocation2, workspace1);
        assertThat(request.equals(request2), is(false));
    }

    @Test
    public void shouldConsiderNotEqualTwoRequestsWithDifferentWorkspaceNames() {
        request = new DeleteBranchRequest(validPathLocation1, workspace1);
        DeleteBranchRequest request2 = new DeleteBranchRequest(validPathLocation1, workspace2);
        assertThat(request.equals(request2), is(false));
    }
}
