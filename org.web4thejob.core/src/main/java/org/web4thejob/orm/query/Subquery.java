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

package org.web4thejob.orm.query;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.PathMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class for holding the EXISTS clause of a query.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class Subquery {
// ------------------------------ FIELDS ------------------------------

    public static final MasterIDPlaceholder MASTER_ID_PLACEHOLDER = new MasterIDPlaceholder();

    private final EntityMetadata target;
    private final SubqueryType subqueryType;

    public List<Criterion> getCriteria() {
        return criteria;
    }

    private final List<Criterion> criteria = new ArrayList<Criterion>();

// --------------------------- CONSTRUCTORS ---------------------------

    public Subquery(SubqueryType subqueryType, EntityMetadata target) {
        this.subqueryType = subqueryType;
        this.target = target;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public SubqueryType getSubqueryType() {
        return subqueryType;
    }

    public EntityMetadata getTarget() {
        return target;
    }

// -------------------------- OTHER METHODS --------------------------

    public void addCriterion(PathMetadata property, Condition condition, Object value) {
        Criterion criterion = ContextUtil.getMRS().newInstance(Criterion.class);
        criterion.setPropertyPath(property);
        criterion.setCondition(condition);
        criterion.setValue(value);
        criteria.add(criterion);
    }

// -------------------------- ENUMERATIONS --------------------------

    public enum SubqueryType {
        TYPE_EXISTS,
        TYPE_NOT_EXISTS
    }

// -------------------------- INNER CLASSES --------------------------

    private static class MasterIDPlaceholder {
        //placeholder class    
    }
}
