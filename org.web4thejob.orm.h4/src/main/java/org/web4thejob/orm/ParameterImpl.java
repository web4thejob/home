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

import org.hibernate.validator.constraints.NotBlank;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Parameter;
import org.web4thejob.security.Identity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.5
 */
class ParameterImpl extends AbstractHibernateEntity implements Parameter {
    private long id;
    @NotNull
    private Identity owner;
    @NotNull
    private Category category;
    @NotBlank
    private String key;
    @NotBlank
    private String value;
    private int version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Identity getOwner() {
        return owner;
    }

    public void setOwner(Identity owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }


    public void setCategory(Category category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Serializable getIdentifierValue() {
        return id;
    }

    public void setAsNew() {
        id = 0;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (category != null) {
            sb.append(category.name());
            sb.append(" > ");
        }
        if (key != null) {
            sb.append(key);
            sb.append(" > ");
        }
        if (value != null) {
            sb.append(value);
        }

        return sb.toString();
    }
}
