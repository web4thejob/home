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

import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;

/**
 * @author Veniamin Isaias
 * @since 3.2.1
 */
/*package*/ class SubclassPropertyMetadataImpl implements PropertyMetadata, Comparable<PropertyMetadata> {
    private final EntityMetadataImpl entityMetadata;
    private final EntityMetadataImpl subclassMetadata;


    public SubclassPropertyMetadataImpl(EntityMetadataImpl entityMetadata, EntityMetadataImpl subclassMetadata) {
        this.entityMetadata = entityMetadata;
        this.subclassMetadata = subclassMetadata;
    }

    @Override
    public <E extends Entity> void deproxyValue(E entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAlign() {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public EntityMetadata getAssociatedEntityMetadata() {
        return subclassMetadata;
    }

    @Override
    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String getFriendlyName() {
        return subclassMetadata.getFriendlyName();
    }

    @Override
    public int getIndex() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Class<?> getJavaType() {
        return subclassMetadata.getMappedClass();
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public String getName() {
        return StringUtils.uncapitalize(subclassMetadata.getEntityType().getSimpleName());
    }

    @Override
    public String getStyle() {
        return null;
    }

    @Override
    public <T, E extends Entity> T getValue(E entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasColumn(String name) {
        return false;
    }

    @Override
    public boolean hasColumns() {
        return false;
    }

    @Override
    public <A extends Annotation> boolean isAnnotatedWith(Class<A> annotationType) {
        return false;
    }

    @Override
    public boolean isAssociatedWith(PropertyMetadata propertyMetadata) {
        return false;
    }

    @Override
    public boolean isAssociationType() {
        return true;
    }

    @Override
    public boolean isSubclassType() {
        return true;
    }

    @Override
    public boolean isBlobType() {
        return false;
    }

    @Override
    public boolean isClobType() {
        return false;
    }

    @Override
    public boolean isCompositeType() {
        return false;
    }

    @Override
    public boolean isIdentifier() {
        return false;
    }

    @Override
    public boolean isIdentityIdentifier() {
        return false;
    }

    @Override
    public boolean isInsertable() {
        return false;
    }

    @Override
    public boolean isManyToOneType() {
        return false;
    }

    @Override
    public boolean isMemberOfUniqueKey() {
        return false;
    }

    @Override
    public boolean isOfJavaType(Class<?> type) {
        return getJavaType().isAssignableFrom(type);
    }

    @Override
    public boolean isOneToManyType() {
        return false;
    }

    @Override
    public boolean isOneToOneType() {
        return false;
    }

    @Override
    public boolean isOptional() {
        return true;
    }

    @Override
    public boolean isTimestampType() {
        return false;
    }

    @Override
    public <E extends Entity> boolean isProxyValue(E entity) {
        return false;
    }

    @Override
    public boolean isUniqueKeyWith(String propertyName) {
        return false;
    }

    @Override
    public boolean isUpdateable() {
        return false;
    }

    @Override
    public <E extends Entity> void setValue(E entity, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWidth() {
        return null;
    }

    @Override
    public boolean isNumericType() {
        return false;
    }

    @Override
    public String getHeight() {
        return null;
    }

    @Override
    public boolean isTextType() {
        return false;
    }

    @Override
    public int compareTo(PropertyMetadata other) {
        final Integer i1 = getIndex();
        final Integer i2 = other.getIndex();

        final int res = i1.compareTo(i2);
        if (res != 0) {
            return res;
        } else {
            return Integer.valueOf(hashCode()).compareTo(other.hashCode());
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(entityMetadata.getName()).append("|").append(getName()).append(" [subclass]");
        return sb.toString();
    }

}
