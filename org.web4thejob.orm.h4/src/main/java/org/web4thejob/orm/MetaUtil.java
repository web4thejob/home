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

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.web4thejob.context.ContextUtil;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

class MetaUtil {

    public static int getHibernatePropertyIndex(String entityType, String propertyName) {

        final SessionFactory sessionFactory = ContextUtil.getBean(SessionFactory.class);
        final ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityType);
        for (int i = 0; i < classMetadata.getPropertyNames().length; i++) {
            if (classMetadata.getPropertyNames()[i].equals(propertyName)) {
                return i;
            }
        }

        return -1;
    }

    public static String getMetaAttribute(Property property, String name) {
        return property.getMetaAttribute(name).getValue();
    }

    public static String getMetaAttribute(PersistentClass persistentClass, String name) {
        return persistentClass.getMetaAttribute(name).getValue();
    }

    public static boolean hasMetaAttribute(Property property, String name) {
        final MetaAttribute metaAttribute = property.getMetaAttribute(name);
        if (metaAttribute != null) {
            try {
                return metaAttribute.getValue() != null;
            } catch (final Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean hasMetaAttribute(PersistentClass persistentClass, String name) {
        final MetaAttribute metaAttribute = persistentClass.getMetaAttribute(name);
        if (metaAttribute != null) {
            try {
                return metaAttribute.getValue() != null;
            } catch (final Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isIdentityKey(KeyValue key) {
        return key.isIdentityColumn(ContextUtil.getBean(HibernateConfiguration.class).getConfiguration()
                .getIdentifierGeneratorFactory(), ContextUtil.getBean(SessionFactoryImplementor.class).getDialect());
    }

}
