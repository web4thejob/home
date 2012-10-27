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

import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandMerger;
import org.web4thejob.setting.SettingAware;

/**
 * <p>Defines the api of a layout panel able to host five subpanels (North,South,West,East and Center) as below:</p>
 * <code>
 * ---------------------<br>
 * |.........N.........|<br>
 * |--------------------<br>
 * |...|...........|...|<br>
 * |.W.|.....C.....|.E.|<br>
 * |...|...........|...|<br>
 * |--------------------<br>
 * |.........S.........|<br>
 * ---------------------<br>
 * </code>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface BorderedLayoutPanel extends LayoutPanel, SettingAware, CommandAware, TargetType, CommandMerger {
// -------------------------- OTHER METHODS --------------------------

    public Panel getCenter();

    public Panel getEast();

    public Panel getNorth();

    public Panel getSouth();

    public Panel getWest();

    public boolean hasCenter();

    public boolean hasEast();

    public boolean hasNorth();

    public boolean hasSouth();

    public boolean hasWest();

    public boolean setCenter(Panel panel);

    public boolean setEast(Panel panel);

    public boolean setNorth(Panel panel);

    public boolean setSouth(Panel panel);

    public boolean setWest(Panel panel);
}
