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

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.web.panel.base.AbstractSecuredResourceAuthorizationPanel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultPanelsAuthorizationPanel extends AbstractSecuredResourceAuthorizationPanel<Panel,
        Panel> implements PanelsAuthorizationPanel, ListitemRenderer<Panel> {
// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultPanelsAuthorizationPanel() {
        this(false);
    }

    public DefaultPanelsAuthorizationPanel(boolean readOnly) {
        super(readOnly);
    }

    @Override
    protected String getRootElementName() {
        return PanelsAuthorizationPanel.ROOT_ELEMENT;
    }

    @Override
    protected ListitemRenderer<Panel> getRenderer() {
        return this;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected List<Panel> getSourceList() {
        List<Panel> panels = new ArrayList<Panel>();
        for (Panel panel : ContextUtil.getSessionContext().getPanels(Panel.class)) {
            if (!DesktopLayoutPanel.class.isInstance(panel)) {
                panels.add(panel);
            }
        }
        return panels;
    }

    @Override
    public void render(Listitem item, Panel data, int index) throws Exception {
        item.setImage(data.getImage());
        item.setLabel(data.toString());
        item.setValue(data);
        item.setStyle("white-space:nowrap;");
    }
}
