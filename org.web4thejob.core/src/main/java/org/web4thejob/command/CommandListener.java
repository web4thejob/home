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

/**
 * <p>The interface follows the <a href="http://en.wikipedia.org/wiki/Observer_pattern">Observer design pattern</a>.
 * It is extended from command aware objects in order to establish a method of invocation when the command is
 * triggered.</p>
 * <p>When a command is triggered, the usual flow of events is as follows:
 * <ul>
 * <li>The user clicks on a button</li>
 * <li>The event is captured by the {@link CommandDecorator} and dispatched to the {@link Command} by calling {@link
 * org.web4thejob.command.Command#process()}</li>
 * <li>The {@link Command} notifies its {@link CommandAware} owner by calling the
 * {@link CommandListener#process(Command)} method.</li>
 * <li>Finally, the command is processed and control is returned to the user.</li>
 * </ul>
 *
 * @author Veniamin Isaias
 * @see Command
 * @see CommandAware
 * @see CommandDecorator
 * @since 1.0.0
 */

public interface CommandListener {

    public void process(Command command) throws CommandProcessingException;
}
