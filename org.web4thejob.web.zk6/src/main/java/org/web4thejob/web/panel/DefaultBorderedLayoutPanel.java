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
import org.web4thejob.orm.Entity;
import org.web4thejob.web.panel.base.AbstractBorderLayoutPanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultBorderedLayoutPanel extends AbstractBorderLayoutPanel implements BorderedLayoutPanel {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface BorderedLayoutPanel ---------------------


    public boolean setSouth(Panel panel) {
        return super.setSouth(panel);
    }

    public boolean hasCenter() {
        return super.hasCenter();
    }

    public Panel getEast() {
        return super.getEast();
    }

    public Panel getWest() {
        return super.getWest();
    }

    public boolean hasEast() {
        return super.hasEast();
    }

    public boolean hasNorth() {
        return super.hasNorth();
    }

    public boolean setCenter(Panel panel) {
        return super.setCenter(panel);
    }

    public boolean setNorth(Panel panel) {
        return super.setNorth(panel);
    }

    public Panel getCenter() {
        return super.getCenter();
    }

    public Panel getNorth() {
        return super.getNorth();
    }

    public Panel getSouth() {
        return super.getSouth();
    }

    public boolean hasSouth() {
        return super.hasSouth();
    }

    public boolean hasWest() {
        return super.hasWest();
    }

    public boolean setEast(Panel panel) {
        return super.setEast(panel);
    }

    public boolean setWest(Panel panel) {
        return super.setWest(panel);
    }

// --------------------- Interface TargetType ---------------------


    @Override
    public Class<? extends Entity> getTargetType() {
        for (Panel panel : subpanels) {
            if (panel instanceof TargetType && ((TargetType) panel).hasTargetType()) {
                return ((TargetType) panel).getTargetType();
            }
        }
        return null;
    }

    @Override
    public boolean hasTargetType() {
        return getTargetType() != null;
    }
}
