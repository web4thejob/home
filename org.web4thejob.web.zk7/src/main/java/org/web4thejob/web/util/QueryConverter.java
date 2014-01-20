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

package org.web4thejob.web.util;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Query;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */
public class QueryConverter implements TypeConverter {
    @Override
    public Object coerceToUi(Object val, Component comp) {
        if (val != null) {
            return ContextUtil.getDRS().findById(Query.class, Long.valueOf((String) val));
        }
        return val;
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        if (val != null) {
            return Long.valueOf(((Query) val).getId()).toString();
        }
        return null;
    }
}
