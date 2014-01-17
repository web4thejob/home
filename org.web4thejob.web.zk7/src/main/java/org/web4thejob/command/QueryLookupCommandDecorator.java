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

package org.web4thejob.command;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.web.panel.QueryPanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class QueryLookupCommandDecorator extends AbstractLookupCommandDecorator<Query> {
    // --------------------------- CONSTRUCTORS ---------------------------

    public QueryLookupCommandDecorator(Command command) {
        super(command);

        if (!(command.getOwner() instanceof QueryPanel)) {
            throw new IllegalArgumentException();
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected Query getRefreshQuery() {
        Query query = ContextUtil.getEntityFactory().buildQuery(Query.class);
        query.addCriterion(new Path(Query.FLD_FLAT_TARGET_TYPE), Condition.EQ, ((QueryPanel) command.getOwner())
                .getTargetType().getCanonicalName());
        query.addOrderBy(new Path(Query.FLD_NAME));
        query.setCached(true);
        return query;
    }

    @Override
    protected Query newInstance() {
        return ContextUtil.getEntityFactory().buildQuery(((QueryPanel) command.getOwner()).getTargetType());
    }
}
