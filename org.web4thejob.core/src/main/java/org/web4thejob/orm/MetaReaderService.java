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
import java.util.Collection;
import java.util.List;

/**
 * <p>Service for retrieving informational objects ({@link EntityMetadata} and {@link PropertyMetadata}) that provide
 * useful meta data of the framework's data model.</p>
 * <p>Additionally it provides convinient methods for constructing {@link PathMetadata} instances that are used
 * extensively throughout the framework for traversing data graphs.</p>
 * <p>Usually invoked through
 * {@link org.web4thejob.context.ContextUtil#getMRS() ContextUtil.getMRS()}.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface MetaReaderService {

    public <E extends Entity> E deproxyEntity(E entity);

    public <E extends Entity, A extends Annotation> Collection<AnnotationMetadata<A>>
    getAnnotationMetadata(Class<E> entityType, Class<A> annotationType);

    public EntityMetadata getEntityMetadata(Class<? extends Entity> entityType);

    public EntityMetadata getEntityMetadata(String entityType);

    public PropertyMetadata getPropertyMetadata(Class<? extends Entity> entityType, String property);

    public PropertyMetadata getPropertyMetadata(String entityType, String property);

    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, Path path);

    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, String[] path);

    public PathMetadata getPropertyPath(Class<? extends Entity> entityType, List<PropertyMetadata> path);

    public PathMetadata getPropertyPath(PropertyMetadata propertyMetadata);


    public <E extends Entity> E newInstance(Class<? extends Entity> entityType);

    public void refreshMetaCache();

    public Collection<EntityMetadata> getEntityMetadatas();


}
