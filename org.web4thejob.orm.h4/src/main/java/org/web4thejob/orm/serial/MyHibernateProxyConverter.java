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

package org.web4thejob.orm.serial;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.hibernate.proxy.HibernateProxy;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class MyHibernateProxyConverter implements Converter {
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return HibernateProxy.class.isAssignableFrom(clazz);
    }

    public void marshal(Object object, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Entity entity = ContextUtil.getMRS().deproxyEntity((Entity) object);
        context.convertAnother(entity);
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate proxy");
    }

}
