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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.util.ReflectHelper;
import org.springframework.util.ReflectionUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.validation.ValidatingGroup;
import org.web4thejob.web.panel.DirtyListener;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractHibernateEntity implements Entity {
    private static final EntityNameResolverImpl entityNameResolver = EntityNameResolverImpl.INSTANCE;
    private DirtyListener dirtyListener;
    private Map<String, Object> attributes;

    protected void setDirty() {
        calculate();
        if (dirtyListener != null) {
            dirtyListener.onDirty(true);
        }
    }

    public void addDirtyListener(DirtyListener dirtyListener) {
        this.dirtyListener = dirtyListener;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Entity clone() {
        try {
            final Entity clone = (Entity) ReflectHelper.getDefaultConstructor(getClass()).newInstance((Object[]) null);
            clone.addDirtyListener(dirtyListener);

            ReflectionUtils.doWithFields(getClass(), new ReflectionUtils.FieldCallback() {

                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    ReflectionUtils.makeAccessible(field);
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        // don't clone one-to-many fields
                        field.set(clone, field.get(AbstractHibernateEntity.this));
                    }
                }
            }, ReflectionUtils.COPYABLE_FIELDS);

            return clone;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Clone failed for " + toString(), e);
        }
    }


    public void merge(final Entity source) {
        ReflectionUtils.doWithFields(getClass(), new ReflectionUtils.FieldCallback() {

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                if (!Collection.class.isAssignableFrom(field.getType())) {
                    // don't merge one-to-many fields
                    field.set(AbstractHibernateEntity.this, field.get(source));
                }
            }
        }, ReflectionUtils.COPYABLE_FIELDS);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Entity> getEntityType() {
        final Class<? extends Entity> entityType = entityNameResolver.resolveEntityType(this);
        if (entityType != null) {
            return entityType;
        } else {
            if (this instanceof HibernateProxy) {
                return ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass();
            } else {
                return getClass();
            }
        }
    }

    public boolean isNewInstance() {
        final Serializable id = getIdentifierValue();
        return id == null || id.toString().equals("0") || id.toString().equals("");
    }

    public String toRichString() {
        return toString();
    }

    public Set<ConstraintViolation<Entity>> validate() {
        calculate();

        final Validator validator = ContextUtil.getBean(Validator.class);

        // do default validations
        final Set<ConstraintViolation<Entity>> violations = validator.validate((Entity) this);

        // do other group validations (if any)
        if (this instanceof ValidatingGroup) {
            final Class<?>[] groups = ((ValidatingGroup) this).getGroupNames();
            if (groups != null) {
                violations.addAll(validator.validate((Entity) this, groups));
            }
        }

        return violations;
    }

    public <T> void setAttribute(String key, T value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(1);
            attributes.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        if (attributes != null) {
            return (T) attributes.get(key);
        }
        return null;
    }

    public void removeAttribute(String key) {
        if (attributes != null) {
            attributes.remove(key);
        }
    }

    public void calculate() {
        //override
    }

    public boolean hasAttribute(String key) {
        return attributes != null && attributes.containsKey(key);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getEntityType()).append(getIdentifierValue()).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (this == o) return true;
        else if (!Entity.class.isInstance(o)) return false;

        return new EqualsBuilder().append(getEntityType(), ((Entity) o).getEntityType()).append(getIdentifierValue(),
                ((Entity) o).getIdentifierValue()).isEquals();

    }
}
