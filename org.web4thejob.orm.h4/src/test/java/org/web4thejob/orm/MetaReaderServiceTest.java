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

import junit.framework.Assert;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.annotation.InsertTimeHolder;
import org.web4thejob.orm.annotation.StatusHolder;
import org.web4thejob.orm.annotation.UpdateTimeHolder;
import org.web4thejob.orm.mapping.Detail;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Reference1;
import org.web4thejob.orm.mapping.Reference2;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class MetaReaderServiceTest extends AbstractHibernateDependentTest {

    @Autowired
    MetaReaderService metaReaderService;

    @Autowired
    private DataReaderService dataReaderService;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void getValue() {
        final Detail detail = ContextUtil.getDRS().findFirstByQuery(entityFactory.buildQuery(Detail.class));
        Assert.assertNotNull(detail);
        Assert.assertTrue(detail.getId().getMaster1() instanceof HibernateProxy);

    }

    @Test
    public void newInstance() {
        final Master1 master1 = metaReaderService.newInstance(Master1.class);
        Assert.assertTrue(master1.isNewInstance());

        final Query query = metaReaderService.newInstance(Query.class);
        Assert.assertTrue(query.isNewInstance());

        final Criterion criterion = metaReaderService.newInstance(Criterion.class);
        Assert.assertTrue(criterion.isNewInstance());

        final OrderBy orderBy = metaReaderService.newInstance(OrderBy.class);
        Assert.assertTrue(orderBy.isNewInstance());

    }

    @Test
    public void testAnnotationCache() {
        final Reference1 reference1 = new Reference1();
        Assert.assertNull(reference1.getCreateTime());
        Assert.assertNull(reference1.getUpdateTime());
        reference1.setName("aaaaa");
        reference1.setReference2(dataReaderService.getOne(Reference2.class));
        ContextUtil.getDWS().save(reference1);
        Assert.assertNotNull(reference1.getCreateTime());
        Assert.assertNull(reference1.getUpdateTime());

        ContextUtil.getDWS().save(reference1);
        Assert.assertNotNull(reference1.getUpdateTime());

        PropertyMetadata propertyMetadata = metaReaderService.getPropertyMetadata(Reference1.class,
                Reference1.FLD_CREATE_TIME);
        Assert.assertTrue(propertyMetadata.isAnnotatedWith(InsertTimeHolder.class));
        Assert.assertNotNull(propertyMetadata.getAnnotation(InsertTimeHolder.class));

        propertyMetadata = metaReaderService.getPropertyMetadata(Reference1.class, Reference1.FLD_UPDATE_TIME);
        Assert.assertTrue(propertyMetadata.isAnnotatedWith(UpdateTimeHolder.class));
        Assert.assertNotNull(propertyMetadata.getAnnotation(UpdateTimeHolder.class));

        propertyMetadata = metaReaderService.getPropertyMetadata(Reference2.class, Reference2.FLD_NAME);
        Assert.assertFalse(propertyMetadata.isAnnotatedWith(UpdateTimeHolder.class));
        Assert.assertNull(propertyMetadata.getAnnotation(UpdateTimeHolder.class));

        propertyMetadata = metaReaderService.getPropertyMetadata(Reference2.class, Reference2.FLD_STATUS1);
        Assert.assertTrue(propertyMetadata.isAnnotatedWith(StatusHolder.class));
        StatusHolder holder = propertyMetadata.getAnnotation(StatusHolder.class);
        Assert.assertNotNull(holder);
        Assert.assertTrue(holder.InactiveWhen());

        propertyMetadata = metaReaderService.getPropertyMetadata(Reference2.class, Reference2.FLD_STATUS2);
        Assert.assertTrue(propertyMetadata.isAnnotatedWith(StatusHolder.class));
        holder = propertyMetadata.getAnnotation(StatusHolder.class);
        Assert.assertNotNull(holder);
        Assert.assertFalse(holder.InactiveWhen());

    }

}
