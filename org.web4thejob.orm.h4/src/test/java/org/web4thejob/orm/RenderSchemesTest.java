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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.mapping.Master1;
import org.web4thejob.orm.mapping.Reference1;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.security.SecurityService;

import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class RenderSchemesTest extends AbstractHibernateDependentTest {

    @Autowired
    private MetaReaderService metaReaderService;

    @Autowired
    private DataWriterService dataWriterService;

    @Autowired
    private DataReaderService dataReaderService;

    @Autowired
    private EntityFactory entityFactory;

    @Test
    public void indexTest() {
        RenderScheme renderScheme1 = entityFactory.buildRenderScheme(Master1.class);
        Assert.assertNotNull(renderScheme1);
        renderScheme1.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());

        renderScheme1.setName("my test render scheme");
        renderScheme1.setFriendlyName(renderScheme1.getName());
        renderScheme1.setLocale(Locale.CHINA);
        renderScheme1.setSchemeType(SchemeType.ENTITY_SCHEME);
        renderScheme1.setColSpan(2);

        RenderElement element1 = renderScheme1.addElement(metaReaderService.getPropertyPath(Master1.class,
                new Path().append(Master1.FLD_REFERENCE1).append(Reference1.FLD_NAME)));
        dataWriterService.save(renderScheme1);

        RenderElement element2 = renderScheme1.addElement(metaReaderService.getPropertyPath(Master1.class,
                new Path(Master1.FLD_DETAILS)));
        dataWriterService.save(renderScheme1);

        renderScheme1 = dataReaderService.refresh(renderScheme1);
        Assert.assertEquals(element1, renderScheme1.getElements().get(0));
        element1 = renderScheme1.getElements().get(0);
        Assert.assertEquals(element2, renderScheme1.getElements().get(1));
        element2 = renderScheme1.getElements().get(1);

        Assert.assertEquals(0, element1.getIndex());
        Assert.assertEquals(1, element2.getIndex());

        renderScheme1.getElements().set(0, element2);
        renderScheme1.getElements().set(1, element1);
        dataWriterService.save(renderScheme1);

        renderScheme1 = dataReaderService.refresh(renderScheme1);
        Assert.assertEquals(element2, renderScheme1.getElements().get(0));
        element2 = renderScheme1.getElements().get(0);
        Assert.assertEquals(element1, renderScheme1.getElements().get(1));
        element1 = renderScheme1.getElements().get(1);

        Assert.assertEquals(0, element2.getIndex());
        Assert.assertEquals(1, element1.getIndex());

    }

    @Test
    public void persistenceTest() {
        final RenderScheme renderScheme1 = entityFactory.buildRenderScheme(Master1.class);
        Assert.assertNotNull(renderScheme1);
        renderScheme1.setOwner(ContextUtil.getBean(SecurityService.class).getAdministratorIdentity());

        renderScheme1.setName("my test render scheme");
        renderScheme1.setFriendlyName(renderScheme1.getName());
        renderScheme1.setLocale(Locale.US);
        renderScheme1.setSchemeType(SchemeType.ENTITY_SCHEME);
        renderScheme1.setColSpan(2);

        final RenderElement element = renderScheme1.addElement(metaReaderService.getPropertyPath(Master1.class,
                new Path(Master1.FLD_REFERENCE1).append(Reference1.FLD_NAME)));
        element.setFormat("123");
        dataWriterService.save(renderScheme1);

        final RenderScheme renderScheme2 = dataReaderService.get(RenderScheme.class, renderScheme1.getId());
        Assert.assertNotNull(renderScheme2);

        Assert.assertEquals(renderScheme1.getName(), renderScheme2.getName());
        Assert.assertEquals(renderScheme1.getLocale(), renderScheme2.getLocale());
        Assert.assertEquals(renderScheme1.getSchemeType(), renderScheme2.getSchemeType());
        Assert.assertEquals(renderScheme1.getElements().get(0).getPropertyPath(), renderScheme2.getElements().get(0)
                .getPropertyPath());
    }
}
