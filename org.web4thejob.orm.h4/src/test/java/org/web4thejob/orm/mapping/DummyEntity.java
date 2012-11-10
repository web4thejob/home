/*
 * Copyright (c) 2012 Veniamin Isaias.
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

package org.web4thejob.orm.mapping;

import org.web4thejob.orm.AbstractHibernateEntity;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.validation.AdhocConstraintViolation;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 3.2.1
 */
public class DummyEntity extends AbstractHibernateEntity {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    @Override
    public Set<ConstraintViolation<Entity>> validate() {
        Set<ConstraintViolation<Entity>> violations = super.validate();

        if (id == 0) {
            violations.add(new AdhocConstraintViolation("id cannot be zero", "id", this, 0));
        }

        return violations;
    }
}
