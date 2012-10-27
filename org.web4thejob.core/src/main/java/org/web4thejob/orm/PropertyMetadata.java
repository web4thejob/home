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

/**
 * <p>This interface provides information about the mapping between a property of a pojo in the java world and its
 * footprint in the relational world.</p>
 * <p>In its simplest form it could be a mapping between a get/set method and a single database field as is the case
 * with most properties of the internal framework entity types pojos (e.g. {@link PanelDefinition#getBeanId()} and
 * {@link PanelDefinition#setBeanId(String)}).</p>
 * <p>However more complex mappings may exist like composite database fields or formula fields.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface PropertyMetadata {
// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object obj);

// -------------------------- OTHER METHODS --------------------------

    public <E extends Entity> void deproxyValue(E entity);

    public String getAlign();

    public <A extends Annotation> A getAnnotation(Class<A> annotationType);

    public EntityMetadata getAssociatedEntityMetadata();

    public EntityMetadata getEntityMetadata();

    public String getFormat();

    public String getFriendlyName();

    public int getIndex();

    public Class<?> getJavaType();

    public int getMaxLength();

    public String getName();

    public String getStyle();

    public <T, E extends Entity> T getValue(E entity);

    public boolean hasColumn(String name);

    public boolean hasColumns();

    public <A extends Annotation> boolean isAnnotatedWith(Class<A> annotationType);

    public boolean isAssociatedWith(PropertyMetadata propertyMetadata);

    public boolean isAssociationType();

    public boolean isBlobType();

    public boolean isClobType();

    public boolean isCompositeType();

    public boolean isIdentifier();

    public boolean isIdentityIdentifier();

    public boolean isInsertable();

    public boolean isManyToOneType();

    public boolean isMemberOfUniqueKey();

    public boolean isOfJavaType(Class<?> type);

    public boolean isOneToManyType();

    public boolean isOneToOneType();

    public boolean isOptional();

    public boolean isTimestampType();

    public <E extends Entity> boolean isProxyValue(E entity);

    public boolean isUniqueKeyWith(String propertyName);

    public boolean isUpdateable();

    public <E extends Entity> void setValue(E entity, Object value);

    public String getWidth();

    public boolean isNumericType();

    public String getHeight();

    public boolean isTextType();
}
