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

import org.web4thejob.message.MessageAware;

import java.util.Set;

/**
 * <p>The interface responsible for rendering to the UI the list of {@link Command} instances of a {@link
 * CommandAware} owner.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface CommandRenderer extends MessageAware {
    public static final String ATTRIB_SUPPRESS_CRUD_COMMANDS = "suppress_crud_commands";

    public String getAlign();

    public void render();

    public void reset();

    public void setAlign(String align);

    public void setContainer(Object container);

    public void supress(boolean supress);

    public boolean isSupressed();

    public void addCommandOwner(CommandAware commandAware);

    public void removeCommandOwner(CommandAware commandAware);

    public Set<CommandAware> getCommandOwners();

    public CommandAware getPrimaryOwner();

}
