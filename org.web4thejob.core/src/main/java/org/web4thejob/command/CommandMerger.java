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

package org.web4thejob.command;

import java.util.Set;
import java.util.SortedSet;

/**
 * <p>The interface is used by container components that want to integrate the {@link Command} of their children in
 * order to produce a more coherent visual effect to the user.</p>
 *
 * @author Veniamin Isaias
 * @see org.web4thejob.web.panel.BorderedLayoutPanel
 * @since 1.0.0
 */

public interface CommandMerger {
    public static final String ATTRIB_COMMAND_MERGER = "commandMerger";

    public Set<CommandAware> getMergedOwners();

    public SortedSet<Command> getMergedCommands();
}
