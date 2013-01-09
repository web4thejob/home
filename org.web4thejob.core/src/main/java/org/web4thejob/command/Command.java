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

import org.springframework.beans.factory.InitializingBean;
import org.web4thejob.message.MessageAware;
import org.web4thejob.security.SecuredResource;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>The interface follows the <a href="http://en.wikipedia.org/wiki/Command_pattern">Command design pattern</a>.</p>
 * <p>Commands have always an {@link CommandEnum id} and an {@link CommandAware owner} which are immutable and never
 * null. Command owners are responsible of processing the command when requested.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */


public interface Command extends InitializingBean, SecuredResource, Comparable<Command>, MessageAware {
    public static final SortedSet<Command> EMPTY_COMMANDS_SET = Collections.unmodifiableSortedSet(new
            TreeSet<Command>());
    public static final String DEFAULT_COMMAND_IMAGE = "img/CMD_Placeholder.png";

    public String getName();

    public CommandEnum getId();

    public CommandAware getOwner();

    public boolean isActive();

    public void process() throws CommandProcessingException;

    public void setActivated(boolean active);

    public boolean isRegistered();

    public void setRegistered(boolean registered);

    public Object getValue();

    public void setHighlighted(boolean highlighted);

    public void setValue(Object value);

    public <T> void setArg(String key, T value);

    public <T> T getArg(String key, Class<T> clazz);

    public boolean hasArg(String key);

    public <T> T removeArg(String key);

    public void removeArgs();

}
