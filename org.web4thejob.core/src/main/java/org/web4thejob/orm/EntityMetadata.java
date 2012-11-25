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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.SortedSet;

/**
 * <p>This interface provides information about the mapping between an entity type of the java world and its
 * footprint in the relational world.</p>
 * <p>In its simplest form it could be a mapping between an entity type and a single database table as is the case
 * with most internal framework entity types (e.g. {@link PanelDefinition}).</p>
 * <p>However more complex mappings may exist that rely on class inheritance (e.g. {@link
 * org.web4thejob.security.Identity
 * Identity} -> {@link org.web4thejob.security.UserIdentity UserIdentity}). </p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface EntityMetadata {

    public String getSchema();

    public String getName();

    public String getFriendlyName();

    public String getIdentifierName();

    public <E extends Entity> Integer getVersion(E entity);

    public SortedSet<PropertyMetadata> getPropertiesMetadata();

    public PropertyMetadata getPropertyMetadata(String property);

    public Class<? extends Entity> getEntityType();

    public Class<? extends Entity> getMappedClass();

    public boolean isVersioned();

    public boolean isReadOnly();

    public <A extends Annotation> PropertyMetadata getAnnotatedProperty(Class<A> annotation);

    public List<UniqueKeyConstraint> getUniqueConstraints();

    @Override
    public boolean equals(Object obj);

    public boolean isCached();

    public boolean isTableSubset();

    public boolean isAbstract();

    public boolean isDenyAddNew();

    public List<Class<? extends Entity>> getSubclasses();
}
