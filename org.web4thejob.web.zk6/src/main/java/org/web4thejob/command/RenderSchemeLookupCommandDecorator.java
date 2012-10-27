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

package org.web4thejob.command;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.RenderSchemePanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class RenderSchemeLookupCommandDecorator extends AbstractLookupCommandDecorator<RenderScheme> {
    // --------------------------- CONSTRUCTORS ---------------------------

    public RenderSchemeLookupCommandDecorator(Command command) {
        super(command);

        if (!(command.getOwner() instanceof RenderSchemePanel)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected RenderScheme newInstance() {
        RenderScheme renderScheme = ContextUtil.getEntityFactory().buildRenderScheme(((RenderSchemePanel) command
                .getOwner()).getTargetType());
        renderScheme.setSchemeType(((RenderSchemePanel) command.getOwner()).getSchemeType());
        renderScheme.setLocale(CoreUtil.getUserLocale());
        return renderScheme;
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected Query getRefreshQuery() {
        Query query = ContextUtil.getEntityFactory().buildQuery(RenderScheme.class);
        query.addCriterion(RenderScheme.FLD_FLAT_TARGET_TYPE, Condition.EQ, ((RenderSchemePanel) command.getOwner())
                .getTargetType().getCanonicalName());
        query.addCriterion(RenderScheme.FLD_SCHEME_TYPE, Condition.EQ, ((RenderSchemePanel) command.getOwner())
                .getSchemeType());
        query.addOrderBy(RenderScheme.FLD_NAME);
        query.setCached(true);
        return query;
    }
}
