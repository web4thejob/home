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

import org.web4thejob.command.CommandAware;
import org.web4thejob.message.MessageAware;
import org.web4thejob.orm.EntityHierarchyItem;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
public interface EntityHierarchyPanel extends MessageAware, ContentPanel, CommandAware, DesignModeAware,
        TargetTypeAware {

    public EntityHierarchyItem getSelectedItem();

    public int getItemCount();

    public boolean isReadOnly();

}
