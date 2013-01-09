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

import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/class OrderByImpl extends AbstractHibernateEntity implements OrderBy {
    private long id;
    @NotNull
    private Query query;
    @NotBlank
    private String property;
    private boolean descending = false;
    private int index;
    private boolean fixed;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public boolean isDescending() {
        return descending;
    }

    @Override
    public boolean isFixed() {
        return fixed;
    }

    @Override
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    @Override
    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIndex(int ordering) {
        this.index = ordering;
    }

    @Override
    public void setProperty(String property) {
        this.property = property;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }
}
