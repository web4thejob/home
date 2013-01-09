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

package org.web4thejob.orm.query;

import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;

/**
 * <p>Internal entity type for managing the WHERE clause of queries.</p>
 *
 * @author Veniamin Isaias
 * @see Query
 * @since 1.0.0
 */

public interface Criterion extends Entity {
    public static final String FLD_ID = "id";
    public static final String FLD_QUERY = "query";
    public static final String FLD_FLAT_PROPERTY = "flatPropertyPath";
    public static final String FLD_PROPERTY = "propertyPath";
    public static final String FLD_FLAT_CONDITION = "flatCondition";
    public static final String FLD_CONDITION = "condition";
    public static final String FLD_FLAT_VALUE = "flatValue";
    public static final String FLD_VALUE = "value";
    public static final String FLD_INDEX = "index";
    public static final String FLD_FIXED = "fixed";

    public long getId();

    public Query getQuery();

    public void setPropertyPath(PathMetadata path);

    public PathMetadata getPropertyPath();

    public Condition getCondition();

    public void setCondition(Condition condition);

    public void setValue(Object value);

    public Object getValue();

    public int getIndex();

    public boolean isLocal();

    public boolean isFixed();

    public void setFixed(boolean fixed);

    public boolean isMaster();

    //required for zk binding
    public String getFlatPropertyPath();

    //required for zk binding
    public String getFlatValue();

    //required for zk binding
    public String getFlatCondition();

    //required for zk binding
    public void setFlatCondition(String flatCondition);

    //required for zk binding
    public void setFlatPropertyPath(String flatPropertyPath);

    //required for zk binding
    public void setFlatValue(String flatValue);
}
