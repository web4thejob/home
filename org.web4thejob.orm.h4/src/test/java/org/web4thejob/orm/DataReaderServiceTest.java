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

import junit.framework.Assert;
import my.joblet.Detail;
import my.joblet.Master1;
import my.joblet.Master2;
import my.joblet.Reference1;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.test.AbstractHibernateDependentTest;
import org.web4thejob.security.SecurityService;

import java.util.UUID;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DataReaderServiceTest extends AbstractHibernateDependentTest {

    @Test
    public void findFirstByQuery() {
        final Master1 master1 = ContextUtil.getDRS().get(Master1.class, Long.valueOf(iterations));
        Assert.assertNotNull(master1);

        final Query query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query.addOrderBy(new Path(ContextUtil.getMRS().getEntityMetadata(Master1.class).getIdentifierName()), true);

        Assert.assertEquals(master1, ContextUtil.getDRS().findFirstByQuery(query));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findUniqueByQuery() {
        final Query query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        Assert.assertEquals(iterations, ContextUtil.getDRS().getAll(Master1.class).size());
        ContextUtil.getDRS().findUniqueByQuery(query);
    }

    @Test
    public void queryWithLocalCriteria() {
        final Reference1 reference1 = ContextUtil.getDRS().getOne(Reference1.class);
        Assert.assertNotNull(reference1);

        final Query query = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query.addCriterion(new Path(Master1.FLD_REFERENCE1), Condition.EQ, reference1);
        query.addCriterion(new Path(Master1.FLD_NAME), Condition.EQ, Master1.class.getName());
        Assert.assertEquals(2, query.getCriteria().size());
        final Master1 master1 = ContextUtil.getDRS().findFirstByQuery(query);
        Assert.assertNotNull(master1);
    }

    @Test
    public void saveQuery() {
        Query query = null;
        for (int i = 1; i <= 50; i++) {
            query = ContextUtil.getEntityFactory().buildQuery(Master2.class);
            query.setName(UUID.randomUUID().toString());
            query.addCriterion(new Path(Master2.FLD_KEY), Condition.EQ, "123");
            query.addCriterion(new Path(Master2.FLD_DETAILS).append(Detail.FLD_FCLASS), Condition.EQ, Master2.class);
            query.addOrderBy(new Path(Master2.FLD_KEY), true);
            query.addOrderBy(new Path(Master2.FLD_NAME), false);
            query.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());
            ContextUtil.getDWS().save(query);
        }

        query = ContextUtil.getDRS().get(Query.class, query.getId());
        Assert.assertEquals(2, query.getCriteria().size());

        int i = 0;
        for (final Criterion criterion : query.getCriteria()) {
            Assert.assertEquals(i, criterion.getIndex());
            if (i == 0) {
                Assert.assertEquals(Master2.FLD_KEY, criterion.getPropertyPath().getPath());
            } else {
                Assert.assertEquals(new Path(Master2.FLD_DETAILS).append(Detail.FLD_FCLASS).toString(),
                        criterion.getPropertyPath().getPath());
            }
            i++;
        }

        i = 0;
        for (final OrderBy orderBy : query.getOrderings()) {
            Assert.assertEquals(i, orderBy.getIndex());
            if (i == 0) {
                Assert.assertEquals(Master2.FLD_KEY, orderBy.getProperty());
            } else {
                Assert.assertEquals(Master2.FLD_NAME, orderBy.getProperty());
            }
            i++;
        }

        query.getCriteria().remove(1);
        query.getOrderings().remove(0);
        query.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());
        ContextUtil.getDWS().save(query);

        query = ContextUtil.getDRS().get(Query.class, query.getId());
        Assert.assertEquals(1, query.getCriteria().size());
        Assert.assertEquals(0, query.getCriteria().get(0).getIndex());
        Assert.assertEquals(1, query.getOrderings().size());
        Assert.assertEquals(0, query.getOrderings().get(0).getIndex());

    }

    @Test(expected = HibernateOptimisticLockingFailureException.class)
    public void testQueryOptimisticLock() {
        final Reference1 reference1 = ContextUtil.getDRS().getOne(Reference1.class);

        final Query query1 = ContextUtil.getEntityFactory().buildQuery(Master1.class);
        query1.setName(UUID.randomUUID().toString());
        query1.addCriterion(new Path(Master1.FLD_ID), Condition.EQ, 123);
        query1.addCriterion(new Path(Master1.FLD_DETAILS).append(Detail.FLD_FCLASS), Condition.EQ, Master2.class);
        Criterion criterion = query1.addCriterion(new Path(Master1.FLD_REFERENCE1), Condition.EQ, reference1);
        query1.addOrderBy(new Path(Master1.FLD_ID), true);
        query1.addOrderBy(new Path(Master1.FLD_NAME), false);
        query1.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());
        ContextUtil.getDWS().save(query1);

        criterion = ContextUtil.getDRS().refresh(criterion);
        Assert.assertEquals(reference1, criterion.getValue());

        final Query query2 = ContextUtil.getDRS().get(Query.class, query1.getId());

        query1.setName("aaaaaaaaaaaaaaaaaa");
        ContextUtil.getDWS().save(query1);

        query2.setName("aaaaaaaaaaaaaaaaaa____");
        ContextUtil.getDWS().save(query2);

    }

    @Test
    public void testVersion() {
        final Master1 master1a = ContextUtil.getDRS().getOne(Master1.class);
        Assert.assertNotNull(master1a);

        final Master1 master1b = ContextUtil.getDRS().getOne(Master1.class);
        Assert.assertNotNull(master1b);

        Assert.assertEquals(master1a, master1b);
        Assert.assertEquals(master1a.hashCode(), master1b.hashCode());

        ContextUtil.getDWS().save(master1b);
        Assert.assertTrue(master1a.equals(master1b));
        Assert.assertTrue(master1a.hashCode() == master1b.hashCode());
        Assert.assertTrue(master1a.getVersion() != master1b.getVersion());

    }

}
