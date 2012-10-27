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
import java.lang.reflect.Field;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*default*/class AnnotationMetadataImpl<A extends Annotation> implements AnnotationMetadata<A> {

    private final Class<? extends Entity> entityType;
    private final int hibernateIndex;
    private final Field field;
    private final A annotation;

    public AnnotationMetadataImpl(Class<? extends Entity> entityType, Field field, A annotation) {
        this.entityType = entityType;
        this.field = field;
        this.annotation = annotation;
        this.hibernateIndex = MetaUtil.getHibernatePropertyIndex(entityType.getName(), field.getName());
    }

    public Class<? extends Entity> getEntityType() {
        return entityType;
    }

    public int getIndex() {
        return hibernateIndex;
    }

    public Field getField() {
        return field;
    }

    public A getAnnotation() {
        return annotation;
    }

    public String getName() {
        return field.getName();
    }
}
