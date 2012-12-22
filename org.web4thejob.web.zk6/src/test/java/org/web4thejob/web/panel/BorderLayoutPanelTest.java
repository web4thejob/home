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

package org.web4thejob.web.panel;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.test.AbstractWebApplicationContextTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class BorderLayoutPanelTest extends AbstractWebApplicationContextTest {

    private final List<Panel> list = new ArrayList<Panel>();

    @Test
    public void childrenTest() {
        final BorderedLayoutPanel layoutPanel = ContextUtil.getDefaultPanel(BorderedLayoutPanel.class);
        Assert.assertFalse(layoutPanel.isPersisted());

        layoutPanel.getSubpanels().addAll(list);
        Assert.assertEquals(5, layoutPanel.getSubpanels().size());

        Assert.assertEquals(list.get(0), layoutPanel.getCenter());
        Assert.assertEquals(list.get(1), layoutPanel.getNorth());
        Assert.assertEquals(list.get(2), layoutPanel.getSouth());
        Assert.assertEquals(list.get(3), layoutPanel.getWest());
        Assert.assertEquals(list.get(4), layoutPanel.getEast());

        layoutPanel.getSubpanels().remove(list.get(3));
        Assert.assertEquals(list.get(0), layoutPanel.getCenter());
        Assert.assertEquals(list.get(1), layoutPanel.getNorth());
        Assert.assertEquals(list.get(2), layoutPanel.getSouth());
        Assert.assertNull(layoutPanel.getWest());
        Assert.assertEquals(list.get(4), layoutPanel.getEast());
    }

    @Test
    public void constructorTest() {
        final BorderedLayoutPanel layoutPanel = new DefaultBorderedLayoutPanel();
        layoutPanel.setChildren(list);
        Assert.assertEquals(5, layoutPanel.getSubpanels().size());

        Assert.assertEquals(list.get(0), layoutPanel.getCenter());
        Assert.assertEquals(list.get(1), layoutPanel.getNorth());
        Assert.assertEquals(list.get(2), layoutPanel.getSouth());
        Assert.assertEquals(list.get(3), layoutPanel.getWest());
        Assert.assertEquals(list.get(4), layoutPanel.getEast());

        layoutPanel.getSubpanels().remove(list.get(1));
        layoutPanel.getSubpanels().remove(list.get(3));
        Assert.assertEquals(list.get(0), layoutPanel.getCenter());
        Assert.assertNull(layoutPanel.getNorth());
        Assert.assertEquals(list.get(2), layoutPanel.getSouth());
        Assert.assertNull(layoutPanel.getWest());
        Assert.assertEquals(list.get(4), layoutPanel.getEast());
    }

    @Before
    public void prepare() {
        list.add(ContextUtil.getDefaultPanel(MutableEntityViewPanel.class));// center
        list.add(ContextUtil.getDefaultPanel(MutableEntityViewPanel.class));// north
        list.add(ContextUtil.getDefaultPanel(MutableEntityViewPanel.class));// south
        list.add(ContextUtil.getDefaultPanel(MutableEntityViewPanel.class));// west
        list.add(ContextUtil.getDefaultPanel(MutableEntityViewPanel.class));// east
    }

}
