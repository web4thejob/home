/*
 * Copyright (c) 2012-2014 Veniamin Isaias.
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

import org.springframework.stereotype.Service;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.parameter.Parameter;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.security.UserIdentity;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Service
/* package */class EntityFactoryImpl implements EntityFactory {

    @Override
    public PanelDefinition buildPanelDefinition() {
        return new PanelDefinitionImpl();
    }

    @Override
    public Query buildQuery(Class<? extends Entity> entityType) {
        return new QueryImpl(entityType);
    }

    @Override
    public Query buildQuery(String entityName) {
        return new QueryImpl(toEntityType(entityName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameter> T buildParameter(Class<T> parameterType) {
        try {
            return (T) ContextUtil.getBean(CustomSessionFactoryBean.class).getConfiguration().getClassMapping
                    (parameterType.getCanonicalName()).getMappedClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenderScheme buildRenderScheme(Class<? extends Entity> entityType) {
        return new RenderSchemeImpl(entityType);
    }

    @Override
    public RenderScheme buildRenderScheme(String entityName) {
        return new RenderSchemeImpl(toEntityType(entityName));
    }

    @Override
    public RenderElement buildRenderElement(RenderScheme renderScheme) {
        RenderElementImpl renderElement = new RenderElementImpl();
        renderElement.setRenderScheme(renderScheme);
        return renderElement;
    }

    @Override
    public RenderElement buildRenderElement(PathMetadata pathMetadata) {
        RenderElementImpl renderElement = new RenderElementImpl();
        renderElement.setPropertyPath(pathMetadata);
        return renderElement;
    }


    @Override
    public UserIdentity buildUserIdentity() {
        return new UserIdentityImpl();
    }

    @Override
    public RoleIdentity buildRoleIdentity() {
        return new RoleIdentityImpl();
    }

    @Override
    public RoleMembers buildRoleMembers() {
        return new RoleMembersImpl();
    }

    @Override
    public Class<? extends Entity> toEntityType(String entityName) {
        EntityMetadata entityMetadata = ContextUtil.getMRS().getEntityMetadata(entityName);
        if (entityMetadata != null) {
            return entityMetadata.getEntityType();
        }
        return null;
    }

    @Override
    public AuthorizationPolicy buildAuthorizationPolicy() {
        return new AuthorizationPolicyImpl();
    }

}
