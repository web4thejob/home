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
import org.web4thejob.message.MessageAware;
import org.web4thejob.setting.SettingAware;

/**
 * <p>Defines the api of a content panel for displaying the properties and associations of an entity type in a
 * hierarchical form.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public interface ModelHierarchyPanel extends ContentPanel, MessageAware, SettingAware, CommandAware, TargetTypeAware {

    public void allowOneToMany(boolean allow);

    public boolean isOneToManyAllowed();

    public void allowOneToOne(boolean allow);

    public boolean isOneToOneAllowed();

    public void allowOneToOneSubset(boolean allow);

    public boolean isOneToOneSubsetAllowed();

    public boolean isAssociationsExpanded();

    public void associationsExpanded(boolean expandOneToOneSubset);

    public void refresh();

    public boolean isBlobAllowed();

    public void allowBlobs(boolean allowBlobs);
}
