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
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.Detail;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Master2;
import org.web4thejob.orm.mapping.Reference1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class MappingTest extends AbstractHibernateDependentTest {

    @Autowired
    private DataWriterService dataWriterService;

    @Autowired
    private DataReaderService dataReaderService;

    @Autowired
    private MetaReaderService metaReaderService;

    @Test
    public void testHashCodes() {
        List<Master1> list = dataReaderService.getAll(Master1.class);
        Assert.assertEquals(iterations, list.size());

        Set<Master1> set = new HashSet<Master1>(dataReaderService.getAll(Master1.class));
        Assert.assertEquals(iterations, set.size());

        // all should be contained
        for (int i = 0; i < iterations; i++) {
            Master1 master1 = list.get(i);
            Assert.assertTrue(set.contains(master1));
        }

        // this should NOT be contained
        Master1 master1 = new Master1();
        master1.setName(master1.getEntityType().getName());
        Reference1 reference1 = ContextUtil.getDRS().getOne(Reference1.class);
        Assert.assertNotNull(reference1);

        master1.setReference1(reference1);
        dataWriterService.save(master1);
        Assert.assertTrue(!set.contains(master1));
    }

    public void testIdentifierNames() {
        EntityMetadata entityMetadata;

        entityMetadata = metaReaderService.getEntityMetadata(Master1.class);
        Assert.assertSame(Master1.FLD_ID, entityMetadata.getIdentifierName());

        entityMetadata = metaReaderService.getEntityMetadata(Master2.class);
        Assert.assertSame(Master2.FLD_KEY, entityMetadata.getIdentifierName());

        entityMetadata = metaReaderService.getEntityMetadata(Detail.class);
        Assert.assertSame(Detail.FLD_ID, entityMetadata.getIdentifierName());

    }
}
