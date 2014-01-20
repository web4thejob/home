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

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.UniqueKey;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class UniqueKeyConstraintImpl implements UniqueKeyConstraint {
    private final EntityMetadata entityMetadata;
    private final Set<PropertyMetadata> propertyMetadatas;

    public UniqueKeyConstraintImpl(EntityMetadata entityMetadata, UniqueKey uniqueKey) {
        this.entityMetadata = entityMetadata;

        Set<PropertyMetadata> temp = new HashSet<PropertyMetadata>();
        for (Iterator<?> iterCols = uniqueKey.getColumnIterator(); iterCols.hasNext(); ) {
            Column column = (Column) iterCols.next();
            PropertyMetadata propertyMetadata = getPropertyForColumn(column);
            if (propertyMetadata != null) {
                temp.add(propertyMetadata);
            }
        }

        propertyMetadatas = Collections.unmodifiableSet(temp);
    }

    @Override
    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    public Set<PropertyMetadata> getPropertyMetadatas() {
        return propertyMetadatas;
    }

    @Override
    public String getFriendlyName() {
        StringBuilder stringBuffer = new StringBuilder();
        for (PropertyMetadata propertyMetadata : propertyMetadatas) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(propertyMetadata.getFriendlyName());
        }

        return stringBuffer.toString();
    }

    @Override
    public Query getValidationQuery(Entity entity) {
        Query query = ContextUtil.getEntityFactory().buildQuery(entityMetadata.getEntityType());
        for (PropertyMetadata propertyMetadata : getPropertyMetadatas()) {
            query.addCriterion(new Path(propertyMetadata), Condition.EQ, propertyMetadata.getValue(entity));
        }
        return query;
    }

    @Override
    public boolean isViolated(Entity entity) {
        boolean notNull = true;
        for (PropertyMetadata propertyMetadata : getPropertyMetadatas()) {
            Object val = propertyMetadata.getValue(entity);
            notNull &= val != null && StringUtils.hasText(val.toString());
        }

        if (notNull) {
            return !ContextUtil.getDRS().findByQuery(getValidationQuery(entity)).isEmpty();
        } else {
            return false;
        }

    }

    public PropertyMetadata getPropertyForColumn(Column column) {
        PersistentClass pc = ContextUtil.getBean(HibernateConfiguration.class).getConfiguration().getClassMapping
                (entityMetadata.getName());

        while (pc != null) {
            for (Iterator<?> iterProps = pc.getPropertyIterator(); iterProps.hasNext(); ) {
                Property property = (Property) iterProps.next();
                for (Iterator<?> iterCols = property.getColumnIterator(); iterCols.hasNext(); ) {
                    Column col = (Column) iterCols.next();
                    if (col.equals(column)) {
                        return entityMetadata.getPropertyMetadata(property.getName());
                    }
                }
            }

            pc = pc.getSuperclass();
        }

        return null;
    }


}
