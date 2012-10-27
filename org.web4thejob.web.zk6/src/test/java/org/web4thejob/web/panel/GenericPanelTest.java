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

import org.junit.Assert;
import org.junit.Test;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.test.AbstractWebApplicationContextTest;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class GenericPanelTest extends AbstractWebApplicationContextTest {

    @Test
    public void comparatorTest() {
        final LayoutPanel layout = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(layout);

        for (int i = 1; i <= 5; i++) {
            final Panel content = ContextUtil.getDefaultPanel(EntityViewPanel.class);
            content.setParent(layout);
            content.setIndex(i);
            Assert.assertEquals(layout, content.getParent());
        }
        Assert.assertEquals(5, layout.getSubpanels().size());

        final Panel[] array = layout.getSubpanels().toArray(new Panel[]{});
        for (int i = 0; i <= 4; i++) {
            array[i].setIndex(4 - i);
        }
        layout.getSubpanels().sort();

        int i = 4;
        for (final Panel panel : layout.getSubpanels()) {
            Assert.assertEquals(panel, array[i]);
            i--;
        }
    }

    @Test
    public void creationTest() {
        final Panel content = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        Assert.assertNotNull(content);

        final Panel layout = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(layout);

    }

    @Test
    public void layoutParentTest() {
        final LayoutPanel layout = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(layout);

        for (int i = 1; i <= 5; i++) {
            final Panel content = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
            Assert.assertNotNull(content);
            layout.getSubpanels().add(content);
            Assert.assertEquals(layout, content.getParent());
        }
        Assert.assertEquals(5, layout.getSubpanels().size());

        for (final Panel content : layout.getSubpanels().toArray(new Panel[]{})) {
            content.setParent(null);
        }
        Assert.assertEquals(0, layout.getSubpanels().size());

        for (int i = 1; i <= 5; i++) {
            final Panel content = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
            Assert.assertNotNull(content);
            layout.getSubpanels().add(content);
            Assert.assertEquals(layout, content.getParent());
        }
        Assert.assertEquals(5, layout.getSubpanels().size());
        final Panel[] array = layout.getSubpanels().toArray(new Panel[]{});
        layout.getSubpanels().clear();
        for (final Panel panel : array) {
            Assert.assertNull(panel.getParent());
        }
    }

    @Test
    public void myTest() {
        final PlaceholderPanel panel1 = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        Assert.assertNotNull(panel1);

        final PlaceholderPanel panel2 = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        Assert.assertNotNull(panel2);

        final PlaceholderPanel panel3 = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        Assert.assertNotNull(panel3);

        final TabbedLayoutPanel tabbed1 = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(tabbed1);

        final TabbedLayoutPanel tabbed2 = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(tabbed2);

        tabbed1.getSubpanels().add(panel1);
        tabbed1.getSubpanels().add(panel2);
        tabbed1.getSubpanels().add(panel3);
        Assert.assertEquals(3, tabbed1.getSubpanels().size());

        panel1.setParent(null);
        panel2.setParent(null);
        Assert.assertEquals(1, tabbed1.getSubpanels().size());

        tabbed1.getSubpanels().replace(panel3, panel1);
        Assert.assertEquals(1, tabbed1.getSubpanels().size());

        panel1.setParent(tabbed2);
        Assert.assertEquals(1, tabbed2.getSubpanels().size());
        Assert.assertEquals(0, tabbed1.getSubpanels().size());
    }

    @Test
    public void replaceTest() {
        final Panel content1 = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        final Panel content2 = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        final LayoutPanel layout1 = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);

        Assert.assertFalse(layout1.getSubpanels().replace(content1, content2));

        content1.setParent(layout1);
        content2.setParent(layout1);
        Assert.assertFalse(layout1.getSubpanels().replace(content1, content2));

        content2.setParent(null);
        Assert.assertTrue(layout1.getSubpanels().replace(content1, content2));

        Assert.assertNull(content1.getParent());
        Assert.assertNotNull(content2.getParent());
        Assert.assertEquals(content2, layout1.getSubpanels().first());

    }

    @Test
    public void setParentTest() {
        final LayoutPanel layout1 = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(layout1);

        final LayoutPanel layout2 = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        Assert.assertNotNull(layout2);

        final Panel content = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        content.setParent(layout1);
        Assert.assertNotNull(content.getParent());
        Assert.assertEquals(layout1.getSubpanels().first(), content);

        content.setParent(layout2);
        Assert.assertNotNull(content.getParent());
        Assert.assertEquals(layout2.getSubpanels().first(), content);
        Assert.assertEquals(0, layout1.getSubpanels().size());

        content.setParent(null);
        Assert.assertNull(content.getParent());
        Assert.assertEquals(0, layout1.getSubpanels().size());
        Assert.assertEquals(0, layout2.getSubpanels().size());
    }

}
