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

package org.web4thejob.web.panel;

import junit.framework.Assert;
import org.junit.Test;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.test.AbstractWebApplicationContextTest;
import org.web4thejob.util.FieldLocator;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tabbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class TabbedLayoutTest extends AbstractWebApplicationContextTest {

    @SuppressWarnings("unchecked")
    @Test
    public void checkAddRemoveTabsBehavior() throws Exception {
        final TabbedLayoutPanel tabbed = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
        final PlaceholderPanel panel = ContextUtil.getDefaultPanel(PlaceholderPanel.class);

        Panel[] panels = new Panel[3];
        panels[0] = panel;
        panel.setParent(tabbed);

        panels[1] = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        panels[1].setParent(tabbed);

        panels[2] = ContextUtil.getDefaultPanel(PlaceholderPanel.class);
        panels[2].setParent(tabbed);

        Assert.assertEquals(3, tabbed.getSubpanels().size());

        final Tabbox tabbox = (Tabbox) FieldLocator.getFieldValue(tabbed, "tabbox");
        Assert.assertNotNull(tabbox);
        Assert.assertEquals(3, tabbox.getTabs().getChildren().size());
        Assert.assertEquals(3, tabbox.getTabpanels().getChildren().size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(panels[i], tabbox.getTabpanels().getChildren().get(i).getAttribute(Attributes
                    .ATTRIB_PANEL));
        }

        tabbed.getSubpanels().first().setParent(null);
        Assert.assertEquals(2, tabbed.getSubpanels().size());
        Assert.assertEquals(2, tabbox.getTabs().getChildren().size());
        Assert.assertEquals(2, tabbox.getTabpanels().getChildren().size());
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(panels[i + 1], tabbox.getTabpanels().getChildren().get(i).getAttribute(Attributes
                    .ATTRIB_PANEL));
        }

        final Event event = new Event(Events.ON_CLOSE, tabbox.getTabs().getLastChild());
        ((EventListener<Event>) tabbed).onEvent(event);
        Assert.assertEquals(1, tabbed.getSubpanels().size());
        Assert.assertEquals(1, tabbox.getTabs().getChildren().size());
        Assert.assertEquals(1, tabbox.getTabpanels().getChildren().size());
        Assert.assertEquals(panels[1], tabbox.getTabpanels().getChildren().get(0).getAttribute(Attributes
                .ATTRIB_PANEL));

    }
}
