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

import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.hibernate.collection.internal.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

/**
 * Mapper for Hibernate types. It will map the class names of the Hibernate
 * collections with equivalents of the JDK at serialization time. It will also
 * map the names of the proxy types to the names of the proxies element's type.
 *
 * @author Konstantin Pribluda
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
@SuppressWarnings("rawtypes")
public class MyHibernateMapper extends MapperWrapper {

    final private Map collectionMap = new HashMap();

    @SuppressWarnings("unchecked")
    public MyHibernateMapper(final MapperWrapper mapper) {
        super(mapper);
        collectionMap.put(PersistentBag.class, ArrayList.class);
        collectionMap.put(PersistentList.class, ArrayList.class);
        collectionMap.put(PersistentMap.class, HashMap.class);
        collectionMap.put(PersistentSet.class, HashSet.class);
        collectionMap.put(PersistentSortedMap.class, TreeMap.class);
        collectionMap.put(PersistentSortedSet.class, TreeSet.class);
    }

    @Override
    public Class defaultImplementationOf(final Class clazz) {
        if (collectionMap.containsKey(clazz)) return super.defaultImplementationOf((Class) collectionMap.get(clazz));

        return super.defaultImplementationOf(clazz);
    }

    @Override
    public String serializedClass(final Class clazz) {
        // check whether we are Hibernate proxy and substitute real name
        if (HibernateProxy.class.isAssignableFrom(clazz)) return super.serializedClass(clazz.getSuperclass());

        if (collectionMap.containsKey(clazz))
            // Pretend this is the underlying collection class and map that
            // instead
            return super.serializedClass((Class) collectionMap.get(clazz));

        return super.serializedClass(clazz);
    }
}
