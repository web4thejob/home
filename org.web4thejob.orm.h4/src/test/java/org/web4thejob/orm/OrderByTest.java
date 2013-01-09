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

import org.junit.Test;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Reference1;
import org.web4thejob.orm.mapping.Reference2;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class OrderByTest extends AbstractHibernateDependentTest {

    @Test
    public void orderOnReferencedEntityTest() {
        Path path = new Path(Master1.FLD_REFERENCE1).append(Reference1.FLD_REFERENCE2).append(Reference2.FLD_NAME);

        Query query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query.addCriterion(path, Condition.SW, "A");
        query.addOrderBy(path, false);
        ContextUtil.getDRS().findFirstByQuery(query);

        query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query.addOrderBy(path, false);
        ContextUtil.getDRS().findFirstByQuery(query);


        query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query.addOrderBy(new Path(Master1.FLD_REFERENCE1), false);
        ContextUtil.getDRS().findFirstByQuery(query);

    }

}
