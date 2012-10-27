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

package org.web4thejob.command;

import org.web4thejob.web.panel.PanelState;

import java.util.Set;
import java.util.SortedSet;

/**
 * <p>Interface for defining the api of an instance aware of the {@link Command} concept.</p>
 * <p>Along with the {@link Command} interface it supplements the integration of the <a href="http://en.wikipedia
 * .org/wiki/Command_pattern">Command design pattern</a> in the framework.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface CommandAware extends CommandListener {

    public Set<CommandEnum> getSupportedCommands();

    public Command getCommand(CommandEnum id);

    public SortedSet<Command> getCommands();

    public boolean hasCommand(CommandEnum id);

    public void supressCommands(boolean supress);

    public boolean isCommandsSupressed();

    public boolean unregisterCommand(CommandEnum id);

    public PanelState getPanelState();
}
