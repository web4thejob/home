/*
 * Copyright (c) 2012 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.web4thejob.orm;

import org.web4thejob.orm.parameter.Parameter;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;

/**
 * <p>Factory interface for building new instances of the internal entity types usually invoked through
 * {@link org.web4thejob.context.ContextUtil#getEntityFactory() ContextUtil.getEntityFactory()}.</p>
 * <p>Users should always use this interface for creating instances of the internal entity types since the framework
 * is agnostic of the orm implementation.</p>
 *
 * @author Veniamin Isaias
 * @see PanelDefinition
 * @see Query
 * @see Parameter
 * @see RenderScheme
 * @see UserIdentity
 * @see RoleIdentity
 * @see RoleMembers
 * @since 1.0.0
 */

public interface EntityFactory {

    public PanelDefinition buildPanelDefinition();

    public Query buildQuery(Class<? extends Entity> entityType);

    public Query buildQuery(String entityName);

    public <T extends Parameter> T buildParameter(Class<T> parameterType);

    public RenderScheme buildRenderScheme(Class<? extends Entity> entityType);

    public RenderScheme buildRenderScheme(String entityName);

    public UserIdentity buildUserIdentity();

    public RoleIdentity buildRoleIdentity();

    public RoleMembers buildRoleMembers();

    public Class<? extends Entity> toEntityType(String entityName);

    public AuthorizationPolicy buildAuthorizationPolicy();

    public RenderElement buildRenderElement(RenderScheme renderScheme);

    public RenderElement buildRenderElement(PathMetadata pathMetadata);
}
