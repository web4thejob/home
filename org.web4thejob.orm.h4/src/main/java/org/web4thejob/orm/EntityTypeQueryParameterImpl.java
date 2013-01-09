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

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.ControllerHolder;
import org.web4thejob.orm.annotation.EntityTypeHolder;
import org.web4thejob.orm.annotation.QueryHolder;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.EntityTypeQueryParameter;
import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 3.3.0
 */

public class EntityTypeQueryParameterImpl extends ParameterImpl implements EntityTypeQueryParameter {

    @EntityTypeHolder
    private String key;

    @QueryHolder
    @ControllerHolder
    private String value;

    public EntityTypeQueryParameterImpl() {
        setCategory(Category.ENTITY_TYPE_QUERY_PARAM);
    }

    @Override
    public String toString() {
        if (getOwner() != null && getKey() != null && getValue() != null) {

            StringBuilder sb = new StringBuilder().append(getOwner().toString()).append(" > ").append(ContextUtil
                    .getMRS()
                    .getEntityMetadata(getKey()).getFullFriendlyName()).append(" > ");

            Query query = ContextUtil.getDRS().findById(Query.class, Long.valueOf(getValue()));
            if (query != null) {
                sb.append(query.getName());
            } else {
                sb.append(getValue());
            }

            return sb.toString();
        }

        return super.toString();
    }

}
