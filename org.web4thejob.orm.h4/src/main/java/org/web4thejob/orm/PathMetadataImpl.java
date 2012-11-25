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
import org.web4thejob.context.ContextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/ class PathMetadataImpl implements PathMetadata {

    final private List<PropertyMetadata> steps;
    final private String path;

    public PathMetadataImpl(Class<? extends Entity> entityType, String property) {
        this(entityType, new String[]{property});
    }

    public PathMetadataImpl(Class<? extends Entity> entityType, String[] path) {
        this.path = StringUtils.arrayToDelimitedString(path, ".");
        steps = new ArrayList<PropertyMetadata>();

        EntityMetadata entityMetadata = ContextUtil.getMRS().getEntityMetadata(entityType);

        if (entityMetadata == null) {
            throw new IllegalArgumentException("Entity type " + entityType.getName() + " is unknown");
        }

        for (final String step : path) {
            final PropertyMetadata propertyMetadata = entityMetadata.getPropertyMetadata(step);

            if (propertyMetadata == null) {
                throw new RuntimeException("invalid attribute " + step + " for entity type " + entityMetadata.getName
                        ());
            }

            steps.add(propertyMetadata);
            entityMetadata = propertyMetadata.getAssociatedEntityMetadata();
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!PathMetadata.class.isInstance(obj)) {
            return false;
        } else {
            final PathMetadata other = (PathMetadata) obj;
            return getRoot().equals(other.getRoot()) && getPath().equals(other.getPath());
        }
    }

    @Override
    public PropertyMetadata getFirstStep() {
        return steps.get(0);
    }

    @Override
    public PropertyMetadata getLastStep() {
        return steps.get(steps.size() - 1);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public EntityMetadata getRoot() {
        return getFirstStep().getEntityMetadata();
    }

    @Override
    public List<PropertyMetadata> getSteps() {
        return steps;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, E extends Entity> T getValue(E rootEntity) {
        Object value = rootEntity;
        for (final PropertyMetadata step : steps) {
            value = step.getValue((E) value);
            if (value == null) {
                return null;
            }
        }
        return (T) value;
    }

    @Override
    public int hashCode() {
        if (getRoot() == null || getPath() == null) {
            return super.hashCode();
        }

        final int prime = 31;
        int result = getRoot().hashCode();
        result = prime * result + getPath().hashCode();
        return result;
    }

    @Override
    public boolean isMultiStep() {
        return steps.size() > 1;
    }

    @Override
    public boolean hasOneToManySteps() {
        for (final PropertyMetadata step : steps) {
            if (step.isOneToManyType()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFriendlyName() {
        StringBuilder sb = new StringBuilder();
        for (final PropertyMetadata step : steps) {
            if (sb.length() > 0) {
                sb.append(" > ");
            }
            sb.append(step.getFriendlyName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return path;
    }

}
