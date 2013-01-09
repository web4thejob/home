/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
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

import org.hibernate.EntityNameResolver;
import org.web4thejob.orm.parameter.*;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.security.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class EntityNameResolverImpl implements EntityNameResolver {
    public static final EntityNameResolverImpl INSTANCE = new EntityNameResolverImpl();

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String resolveEntityName(Object entity) {

        final Class<? extends Entity> entityType = resolveEntityType((Entity) entity);
        if (entityType != null) return entityType.getName();

        return null;
    }

    public <E extends Entity> Class<? extends Entity> resolveEntityType(E entity) {
        if (entity instanceof Query) return Query.class;
        else if (entity instanceof Criterion) return Criterion.class;
        else if (entity instanceof OrderBy) return OrderBy.class;
        else if (entity instanceof RenderScheme) return RenderScheme.class;
        else if (entity instanceof RenderElement) return RenderElement.class;
        else if (entity instanceof PanelDefinition) return PanelDefinition.class;
        else if (entity instanceof RoleMembers) return RoleMembers.class;
        else if (entity instanceof AuthorizationPolicy) return AuthorizationPolicy.class;

        else if (entity instanceof EntityTypeEntityViewParameter) return EntityTypeEntityViewParameter.class;
        else if (entity instanceof EntityTypeListViewParameter) return EntityTypeListViewParameter.class;
        else if (entity instanceof EntityTypeQueryParameter) return EntityTypeQueryParameter.class;
        else if (entity instanceof PrinterCharsetParameter) return PrinterCharsetParameter.class;
        else if (entity instanceof LocationImagesRepoParameter) return LocationImagesRepoParameter.class;
        else if (entity instanceof LocationParameter) return LocationParameter.class;
        else if (entity instanceof Parameter) return Parameter.class;

        else if (entity instanceof UserIdentity) return UserIdentity.class;
        else if (entity instanceof RoleIdentity) return RoleIdentity.class;
        else if (entity instanceof Identity) return Identity.class; //always after UserIdentity & RoleIdentity

        return null;
    }
}
