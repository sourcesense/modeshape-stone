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
public class CopyBranchRequestTest extends AbstractRequestTest {

    private CopyBranchRequest request;

    @Override
    @Before
    public void beforeEach() {
        super.beforeEach();
    }

    @Override
    protected Request createRequest() {
        return new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullFromLocation() {
        new CopyBranchRequest(null, workspace1, validPathLocation2, workspace2);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullToLocation() {
        new CopyBranchRequest(validPathLocation1, workspace1, null, workspace2);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullFromWorkspace() {
        new CopyBranchRequest(validPathLocation1, null, validPathLocation2, workspace2);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreatingRequestWithNullToWorkspace() {
        new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, null);
    }

    @Test
    public void shouldConsiderRequestsToBeInSameWorkspaceIfFromAndToWorkspaceNamesMatch() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, new String(workspace1));
        assertThat(request.isSameWorkspace(), is(true));
    }

    @Test
    public void shouldConsiderRequestsToNotBeInSameWorkspaceIfFromAndToWorkspaceNamesDoNotMatch() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        assertThat(request.isSameWorkspace(), is(false));
    }

    @Test
    public void shouldCreateValidRequestWithValidFromLocationAndValidToLocation() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        assertThat(request.from(), is(sameInstance(validPathLocation1)));
        assertThat(request.into(), is(sameInstance(validPathLocation2)));
        assertThat(request.fromWorkspace(), is(sameInstance(workspace1)));
        assertThat(request.intoWorkspace(), is(sameInstance(workspace2)));
        assertThat(request.hasError(), is(false));
        assertThat(request.getError(), is(nullValue()));
    }

    @Test
    public void shouldConsiderEqualTwoRequestsWithSameLocations() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        CopyBranchRequest request2 = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        assertThat(request, is(request2));
    }

    @Test
    public void shouldConsiderNotEqualTwoRequestsWithDifferentLocations() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        CopyBranchRequest request2 = new CopyBranchRequest(validPathLocation2, workspace1, validPathLocation1, workspace2);
        assertThat(request.equals(request2), is(false));
    }

    @Test
    public void shouldConsiderNotEqualTwoRequestsWithDifferentFromWorkspaceName() {
        request = new CopyBranchRequest(validPathLocation1, workspace1 + "a", validPathLocation2, workspace2);
        CopyBranchRequest request2 = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        assertThat(request.equals(request2), is(false));
    }

    @Test
    public void shouldConsiderNotEqualTwoRequestsWithDifferentToWorkspaceName() {
        request = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2 + "a");
        CopyBranchRequest request2 = new CopyBranchRequest(validPathLocation1, workspace1, validPathLocation2, workspace2);
        assertThat(request.equals(request2), is(false));
    }
}
