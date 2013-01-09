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

package org.web4thejob.command;

import org.web4thejob.message.MessageAware;

/**
 * <p>This interface follows the <a href="http://en.wikipedia.org/wiki/Decorator_pattern">Decorator design
 * pattern</a>. In other words it acts as a visual wrapper of the {@link Command} instance,
 * decoupling this way commands from their visual representation.</p>
 *
 * @author Veniamin Isaias
 * @see Command
 * @since 1.0.0
 */

public interface CommandDecorator extends MessageAware {
    public static final String ATTRIB_DECORATOR = CommandDecorator.class.getCanonicalName();
    public static final String ATTRIB_DIRTY_NOTIFIED = "dirtyNotification";
    public static final String ATTRIB_MODIFIED = "modified";

    public Command getCommand();

    public void attach(Object container);

    public void dettach();

    public boolean isAttached();

    public void render();

    public boolean isDisabled();

    public void setDisabled(boolean disabled);

    public String getName();

    public void setName(String name);

}
