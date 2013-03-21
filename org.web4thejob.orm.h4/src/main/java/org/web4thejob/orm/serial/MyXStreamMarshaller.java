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

package org.web4thejob.orm.serial;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.mapper.CachingMapper;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.util.ReflectionUtils;
import org.web4thejob.orm.Entity;

import java.lang.reflect.Field;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */


public class MyXStreamMarshaller extends XStreamMarshaller {

    @Autowired
    private SessionFactory sessionFactory;

    private void customizeMappers(XStream xstream) {
        // huge HACK since there seems to be no direct way of adding
        // HibernateMapper
        final Converter reflectionConverter = xstream.getConverterLookup().lookupConverterForType(Entity.class);

        if (!ReflectionConverter.class.isInstance(reflectionConverter))
            throw new IllegalStateException("expected " + ReflectionConverter.class.getName() + " but got " +
                    reflectionConverter.getClass().getName());

        final Field field = ReflectionUtils.findField(ReflectionConverter.class, "mapper");
        ReflectionUtils.makeAccessible(field);
        CachingMapper mapper = (CachingMapper) ReflectionUtils.getField(field, reflectionConverter);
        mapper = new CachingMapper(new MyHibernateMapper(mapper));
        ReflectionUtils.setField(field, reflectionConverter, mapper);
    }

    @Override
    protected void customizeXStream(XStream xstream) {

        customizeMappers(xstream);

        // register custom hibernate converter
        xstream.registerConverter(new MyHibernateProxyConverter());
        xstream.registerConverter(new DateFormulaConverter());
        xstream.registerConverter(new CurrentUserConverter());

        // configure xstream to ignore all properties except identifiers.
        for (final ClassMetadata meta : sessionFactory.getAllClassMetadata().values()) {
            for (final String propertyName : meta.getPropertyNames()) {
                xstream.omitField(meta.getMappedClass(), propertyName);
            }
        }
    }
}
