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

package org.web4thejob.orm;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;
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


    @Override
    public String getCacheRegion() {
        return cacheRegion;
    }

    @Override
    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    private String cacheRegion;

    // --------------------------- CONSTRUCTORS ---------------------------

    public QueryImpl() {
        super();
    }

    public QueryImpl(Class<? extends Entity> targetType) {
        this.targetType = targetType;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    @Override
    public String getFlatTargetType() {
        if (flatTargetType == null && targetType != null) {
            flatTargetType = targetType.getName();
        }
        return flatTargetType;
    }

    @Override
    public List<Subquery> getSubqueries() {
        if (subqueries == null) {
            return Collections.emptyList();
        }
        return subqueries;
    }

    @Override
    public void setSubqueries(List<Subquery> subqueries) {
        this.subqueries = subqueries;
    }

    @Override
    public boolean hasMasterCriterion() {
        for (Criterion criterion : criteria) {
            if (criterion.isMaster()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCached() {
        return cached;
    }

    @Override
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<OrderBy> getOrderings() {
        return orderings;
    }

    public void setOrderings(List<OrderBy> orderings) {
        this.orderings = orderings;
    }

    @Override
    public Class<? extends Entity> getTargetType() {
        if (targetType == null && flatTargetType != null) {
            targetType = ContextUtil.getBean(EntityFactory.class).toEntityType(flatTargetType);
        }
        return targetType;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return name;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface Entity ---------------------

    @Override
    public Serializable getIdentifierValue() {
        return id;
    }

    @Override
    public void setAsNew() {
        id = 0;
    }

    // --------------------- Interface Query ---------------------

    @Override
    public Criterion addCriterion(String property, Condition condition) {
        return addCriterion(property, condition, null, false, false);
    }

    @Override
    public Criterion addCriterion(String property, Condition condition, Object value) {
        return addCriterion(property, condition, value, false, false);
    }

    @Override
    public Criterion addCriterion(String property, Condition condition, Object value, boolean isFixed) {
        return addCriterion(property, condition, value, isFixed, false);
    }

    @Override
    public Criterion addCriterion(String property, Condition condition, Object value, boolean isFixed,
                                  boolean isMaster) {
        final CriterionImpl criterion = new CriterionImpl(isMaster);
        criterion.setQuery(this);
        criterion.setPropertyPath(ContextUtil.getMRS().getPropertyPath(getTargetType(),
                StringUtils.tokenizeToStringArray(property, ".")));
        criterion.setCondition(condition);
        criterion.setValue(value);
        criterion.setFixed(isFixed);
        criteria.add(criterion);
        return criterion;
    }

    @Override
    public Criterion addCriterion(Criterion criterion) {
        final CriterionImpl criterionImpl = (CriterionImpl) criterion.clone();
        criterionImpl.setQuery(this);
        criteria.add(criterionImpl);
        return criterionImpl;
    }

    @Override
    public OrderBy addOrderBy(String property) {
        return addOrderBy(property, false, false);
    }

    @Override
    public OrderBy addOrderBy(String property, boolean descending) {
        return addOrderBy(property, descending, false);
    }

    @Override
    public OrderBy addOrderBy(String property, boolean descending, boolean isFixed) {
        final OrderByImpl orderBy = new OrderByImpl();
        orderBy.setQuery(this);
        orderBy.setProperty(property);
        orderBy.setDescending(descending);
        orderBy.setFixed(isFixed);
        orderings.add(orderBy);
        return orderBy;
    }

    @Override
    public OrderBy addOrderBy(OrderBy orderBy) {
        final OrderByImpl orderByImpl = (OrderByImpl) orderBy.clone();
        orderByImpl.setQuery(this);
        orderings.add(orderByImpl);
        return orderByImpl;
    }

    // -------------------------- OTHER METHODS --------------------------

    public void setFlatTargetType(String flatTargetType) {
        this.flatTargetType = flatTargetType;
        this.targetType = null;
    }

    public void setTargetType(Class<? extends Entity> targetType) {
        this.targetType = targetType;
        this.flatTargetType = null;
    }

    @Override
    public Identity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Identity owner) {
        this.owner = owner;
    }
}
