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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Reference1;
import org.web4thejob.orm.mapping.Reference2;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class PropertyPathTest extends AbstractHibernateDependentTest {

    @Autowired
    private MetaReaderService metaReaderService;

    @Autowired
    private DataReaderService dataReaderService;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void getValue() {
        final String[] path = new String[]{Master1.FLD_REFERENCE1, Reference1.FLD_REFERENCE2};

        final PathMetadata pathMetadata = metaReaderService.getPropertyPath(Master1.class, path);
        Assert.assertNotNull(pathMetadata);
        Assert.assertEquals(pathMetadata.getSteps().size(), 2);
        Assert.assertEquals(pathMetadata.getPath(), StringUtils.arrayToDelimitedString(path, Path.DELIMITER));
        Assert.assertTrue(pathMetadata.getFirstStep().getAssociatedEntityMetadata().equals(metaReaderService
                .getEntityMetadata(Reference1.class)));
        Assert.assertTrue(pathMetadata.getLastStep().getAssociatedEntityMetadata().equals(metaReaderService
                .getEntityMetadata(Reference2.class)));

        final Master1 master1 = dataReaderService.getOne(Master1.class);

        final Query query = entityFactory.buildQuery(Reference2.class);
        query.addCriterion(new Path(Reference2.FLD_REFERENCES1).append(Reference1.FLD_MASTERS1).append(Master1.FLD_ID),
                Condition.EQ, master1.getId());

        final Reference2 reference2 = dataReaderService.findFirstByQuery(query);
        Assert.assertNotNull(reference2);
        Assert.assertEquals(pathMetadata.getValue(master1), reference2);

    }
}
