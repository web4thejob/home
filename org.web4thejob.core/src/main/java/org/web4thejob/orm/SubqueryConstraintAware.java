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

import org.web4thejob.orm.query.Subquery;

import java.util.List;

/**
 * <p>Interface for recieving a list of subqueries as supplementary constraints to the current
 * {@link org.web4thejob.orm.query.Query Query} instance.</p>
 * <p>Used by the framework in order to enforce {@link UniqueKeyConstraint} constraints usually constructed via a
 * call to {@link ORMUtil#buildUniqueKeyCriteria(org.web4thejob.web.panel.Panel,
 * org.web4thejob.orm.scheme.RenderElement)
 * ORMUtil.buildUniqueKeyCriteria()}.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface SubqueryConstraintAware {

    public void setSubqueryConstraints(List<Subquery> subqueries);

}
