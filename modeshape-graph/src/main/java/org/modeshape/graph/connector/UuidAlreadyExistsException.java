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
package org.modeshape.graph.connector;

import java.util.UUID;
import net.jcip.annotations.Immutable;
import org.modeshape.graph.GraphI18n;
import org.modeshape.graph.request.CloneBranchRequest;

/**
 * Exception that indicates that a copy request failed because one of the UUIDs in the source branch already exists in the target
 * workspace and the {@link CloneBranchRequest#removeExisting() UUID conflict behavior} is set to throw an exception instead of
 * removing the existing nodes.
 */
@Immutable
public class UuidAlreadyExistsException extends RepositorySourceException {

    private static final long serialVersionUID = 1L;

    public UuidAlreadyExistsException( String repositorySourceName,
                                       UUID uuid,
                                       String pathAsString,
                                       String workspaceName ) {
        super(repositorySourceName, GraphI18n.nodeAlreadyExistsWithUuid.text(uuid, pathAsString, workspaceName));
    }

}
