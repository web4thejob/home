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

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.web4thejob.SystemProtectedEntryException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.InsertTimeHolder;
import org.web4thejob.orm.annotation.UpdateTimeHolder;
import org.web4thejob.orm.annotation.UserIdHolder;
import org.web4thejob.util.CoreUtil;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class HibernateInterceptor extends EmptyInterceptor {
    private static final long serialVersionUID = 1L;
    private static final String INCORRECT_FIELD_INDEX = "incorrect field index in interceptor";

// --------------------- Interface Interceptor ---------------------

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {
        boolean engaged = false;

        MetaReaderService mrs = ContextUtil.getMRS();
        for (AnnotationMetadata<UpdateTimeHolder> metadata : mrs.getAnnotationMetadata(((Entity) entity)
                .getEntityType(),
                UpdateTimeHolder.class)) {
            if (!metadata.getName().equals(propertyNames[metadata.getIndex()])) {
                throw new IllegalStateException(INCORRECT_FIELD_INDEX);
            }
            currentState[metadata.getIndex()] = new Timestamp(System.currentTimeMillis());
            engaged = true;
        }
        return engaged;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        boolean engaged = false;

        MetaReaderService mrs = ContextUtil.getMRS();
        for (AnnotationMetadata<InsertTimeHolder> metadata : mrs.getAnnotationMetadata(((Entity) entity)
                .getEntityType(),
                InsertTimeHolder.class)) {
            if (!metadata.getName().equals(propertyNames[metadata.getIndex()])) {
                throw new IllegalStateException(INCORRECT_FIELD_INDEX);
            }
            state[metadata.getIndex()] = new Timestamp(System.currentTimeMillis());
            engaged = true;
        }
        for (AnnotationMetadata<UserIdHolder> metadata : mrs.getAnnotationMetadata(((Entity) entity).getEntityType(),
                UserIdHolder.class)) {
            if (!metadata.getName().equals(propertyNames[metadata.getIndex()])) {
                throw new IllegalStateException(INCORRECT_FIELD_INDEX);
            }
            if (state[metadata.getIndex()] == null) {
                state[metadata.getIndex()] = ContextUtil.getSessionContext().getSecurityContext()
                        .getUserIdentity();
                engaged = true;
            }
        }


        return engaged;
    }


    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (CoreUtil.isSystemLocked((Entity) entity)) {
            throw new SystemProtectedEntryException();
        }
    }

}
