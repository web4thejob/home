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

/**
 * <p>Internal entity type for managing the ORDER BY clause of queries.</p>
 *
 * @author Veniamin Isaias
 * @see Query
 * @since 1.0.0
 */

public interface OrderBy extends Entity {
    public static final String FLD_ID = "id";
    public static final String FLD_QUERY = "query";
    public static final String FLD_PROPERTY = "property";
    public static final String FLD_DESCENDING = "descending";
    public static final String FLD_INDEX = "index";
    public static final String FLD_FIXED = "fixed";

    public long getId();

    public Query getQuery();

    public void setProperty(String property);

    public String getProperty();

    public int getIndex();

    public void setDescending(boolean descending);

    public boolean isDescending();

    public boolean isFixed();

    public void setFixed(boolean fixed);


}
