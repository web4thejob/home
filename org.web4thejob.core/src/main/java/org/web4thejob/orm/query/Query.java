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

package org.web4thejob.orm.query;

import org.web4thejob.orm.Entity;
import org.web4thejob.security.Identity;

import java.util.List;

/**
 * <p>Internal entity type for managing query definitions. Queries always return instances of a single entity type.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface Query extends Entity {
    public static final String FLD_ID = "id";
    public static final String FLD_NAME = "name";
    public static final String FLD_FLAT_TARGET_TYPE = "flatTargetType";
    public static final String FLD_ORDERINGS = "orderings";

    public Criterion addCriterion(String property, Condition condition);

    public Criterion addCriterion(String property, Condition condition, Object value);

    public Criterion addCriterion(String property, Condition condition, Object value, boolean isFixed);

    public Criterion addCriterion(String property, Condition condition, Object value, boolean isFixed,
                                  boolean isMaster);

    public Criterion addCriterion(Criterion criterion);

    public OrderBy addOrderBy(String property);

    public OrderBy addOrderBy(String property, boolean descending);

    public OrderBy addOrderBy(String property, boolean descending, boolean isFixed);

    public List<Criterion> getCriteria();

    public long getId();

    public String getName();

    public List<OrderBy> getOrderings();

    public Class<? extends Entity> getTargetType();

    public void setName(String name);

    public OrderBy addOrderBy(OrderBy orderBy);

    //required for zk binding
    public String getFlatTargetType();

    //not persistable
    public List<Subquery> getSubqueries();

    public void setSubqueries(List<Subquery> subqueries);

    public boolean hasMasterCriterion();

    public boolean isCached();

    public void setCached(boolean cached);

    public void setCacheRegion(String cacheRegion);

    public String getCacheRegion();

    public Identity getOwner();

    public void setOwner(Identity owner);
}
