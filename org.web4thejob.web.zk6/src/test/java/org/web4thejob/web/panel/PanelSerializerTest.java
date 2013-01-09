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

package org.web4thejob.web.panel;

import junit.framework.Assert;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.context.SessionContext;
import org.web4thejob.orm.DataWriterService;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.test.AbstractWebApplicationContextTest;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.XMLUtil;

import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class PanelSerializerTest extends AbstractWebApplicationContextTest {

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private DataWriterService dataWriterService;

    @Test
    public void serilizationTest() throws ValidityException, ParsingException, IOException {

        LayoutPanel parent1 = ContextUtil.getBean("dummyLayoutPanel", LayoutPanel.class);
        parent1.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        parent1.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        Assert.assertEquals(2, parent1.getSubpanels().size());

        Class<? extends Entity> targetType = ((TargetTypeAware) parent1.getSubpanels().first()).getTargetType();
        Assert.assertNotNull(targetType);

        final LayoutPanel parent2 = ContextUtil.getBean("dummyLayoutPanel", LayoutPanel.class);
        parent2.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        parent2.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        Assert.assertEquals(2, parent2.getSubpanels().size());
        parent2.setParent(parent1);

        final LayoutPanel parent3 = ContextUtil.getBean("dummyLayoutPanel", LayoutPanel.class);
        parent3.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        parent3.getSubpanels().add(ContextUtil.getBean("dummyContentPanel", ContentPanel.class));
        Assert.assertEquals(2, parent3.getSubpanels().size());
        Assert.assertNull(parent3.getParent());
        parent3.setParent(parent2);

        String xml = parent1.toSpringXml();
        PanelDefinition definition = entityFactory.buildPanelDefinition();
        definition.setBeanId(XMLUtil.getRootElementId(xml));
        definition.setOwner(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity());
        definition.setName("test_def");
        definition.setDefinition(XMLUtil.toSpringBeanXmlResource(xml));
        definition.setTags(CoreUtil.tagPanel(parent1));
        definition.setType(CoreUtil.describeClass(getClass()));
        dataWriterService.save(definition);

        SessionContext sessionContext = ContextUtil.getBean(SessionContext.class);
        sessionContext.refresh();

        parent1 = sessionContext.getBean(definition.getBeanId(), LayoutPanel.class);

        Assert.assertEquals(3, parent1.getSubpanels().size());
        for (Panel panel2 : parent1.getSubpanels()) {
            if (panel2 instanceof LayoutPanel) {
                Assert.assertEquals(3, ((LayoutPanel) panel2).getSubpanels().size());
                for (Panel panel3 : parent2.getSubpanels()) {
                    if (panel3 instanceof LayoutPanel) {
                        Assert.assertEquals(2, ((LayoutPanel) panel3).getSubpanels().size());
                    } else {
                        Assert.assertEquals(targetType, ((TargetTypeAware) panel3).getTargetType());
                    }
                }
            } else {
                Assert.assertEquals(targetType, ((TargetTypeAware) panel2).getTargetType());
            }
        }

    }

}
