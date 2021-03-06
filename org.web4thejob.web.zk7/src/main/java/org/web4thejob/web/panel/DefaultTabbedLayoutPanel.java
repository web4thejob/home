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

import org.springframework.context.annotation.Scope;
import org.web4thejob.web.panel.base.AbstractTabbedLayoutPanel;
import org.zkoss.zul.Tabpanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultTabbedLayoutPanel extends AbstractTabbedLayoutPanel implements TabbedLayoutPanel {

    @Override
    public int getSelectedIndex() {
        return super.getSelectedIndex();
    }

    @Override
    public void setSelectedIndex(int index) {
        super.setSelectedIndex(index);
    }

    @Override
    protected boolean isActive(Panel panel) {
        Tabpanel tabpanel = findHostingTabpanel(panel);
        return (tabpanel != null && tabpanel.getIndex() == getSelectedIndex());
    }
}
