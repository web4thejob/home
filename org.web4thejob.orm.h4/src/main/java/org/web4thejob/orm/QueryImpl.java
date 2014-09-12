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
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.UserIdHolder;
import org.web4thejob.orm.query.*;
import org.web4thejob.security.Identity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

/*package*/class QueryImpl extends AbstractHibernateEntity implements Query {
    // ------------------------------ FIELDS ------------------------------

    public QueryImpl() {
        super();
    }

    public QueryImpl(Class<? extends Entity> targetType) {
        this.targetType = targetType;
    }

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String flatTargetType;
    @SuppressWarnings("unused")
    private int version;
    private Class<? extends Entity> targetType;
    private List<Criterion> criteria = new ArrayList<Criterion>(0);
    private List<OrderBy> orderings = new ArrayList<OrderBy>(0);
    private List<Subquery> subqueries;
    private boolean cached;
    @UserIdHolder
    private Identity owner;
    private String cacheRegion;

    // --------------------------- CONSTRUCTORS ---------------------------

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public String getFlatTargetType() {
        if (flatTargetType == null && targetType != null) {
            flatTargetType = targetType.getName();
        }
        return flatTargetType;
    }

    public void setFlatTargetType(String flatTargetType) {
        this.flatTargetType = flatTargetType;
        this.targetType = null;
    }

    public List<Subquery> getSubqueries() {
        if (subqueries == null) {
            return Collections.emptyList();
        }
        return subqueries;
    }

    public void setSubqueries(List<Subquery> subqueries) {
        this.subqueries = subqueries;
    }

    public boolean hasMasterCriterion() {
        for (Criterion criterion : criteria) {
            if (criterion.isMaster()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderBy> getOrderings() {
        return orderings;
    }

    public void setOrderings(List<OrderBy> orderings) {
        this.orderings = orderings;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    public Class<? extends Entity> getTargetType() {
        if (targetType == null && flatTargetType != null) {
            targetType = ContextUtil.getBean(EntityFactory.class).toEntityType(flatTargetType);
        }
        return targetType;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Entity ---------------------

    public void setTargetType(Class<? extends Entity> targetType) {
        this.targetType = targetType;
        this.flatTargetType = null;
    }

    @Override
    public String toString() {
        return name;
    }

    // --------------------- Interface Query ---------------------

    public Serializable getIdentifierValue() {
        return id;
    }

    public void setAsNew() {
        id = 0;
    }

    public Criterion addCriterion(Path property, Condition condition) {
        return addCriterion(property, condition, null, false, false);
    }

    public Criterion addCriterion(Path property, Condition condition, Object value) {
        return addCriterion(property, condition, value, false, false);
    }

    public Criterion addCriterion(Path property, Condition condition, Object value, boolean isFixed) {
        return addCriterion(property, condition, value, isFixed, false);
    }

    public Criterion addCriterion(Path property, Condition condition, Object value, boolean isFixed,
                                  boolean isMaster) {
        final CriterionImpl criterion = new CriterionImpl(isMaster);
        criterion.setQuery(this);
        criterion.setPropertyPath(ContextUtil.getMRS().getPropertyPath(getTargetType(),
                property));
        criterion.setCondition(condition);
        criterion.setValue(value);
        criterion.setFixed(isFixed);
        criteria.add(criterion);
        return criterion;
    }

    public Criterion addCriterion(Criterion criterion) {
        final CriterionImpl criterionImpl = (CriterionImpl) criterion.clone();
        criterionImpl.setQuery(this);
        criteria.add(criterionImpl);
        return criterionImpl;
    }

    public OrderBy addOrderBy(Path property) {
        return addOrderBy(property, false, false);
    }

    public OrderBy addOrderBy(Path property, boolean descending) {
        return addOrderBy(property, descending, false);
    }

    // -------------------------- OTHER METHODS --------------------------

    public OrderBy addOrderBy(Path property, boolean descending, boolean isFixed) {
        final OrderByImpl orderBy = new OrderByImpl();
        orderBy.setQuery(this);
        orderBy.setProperty(property.toString());
        orderBy.setDescending(descending);
        orderBy.setFixed(isFixed);
        orderings.add(orderBy);
        return orderBy;
    }

    public OrderBy addOrderBy(OrderBy orderBy) {
        final OrderByImpl orderByImpl = (OrderByImpl) orderBy.clone();
        orderByImpl.setQuery(this);
        orderings.add(orderByImpl);
        return orderByImpl;
    }

    public Identity getOwner() {
        return owner;
    }

    public void setOwner(Identity owner) {
        this.owner = owner;
    }
}
