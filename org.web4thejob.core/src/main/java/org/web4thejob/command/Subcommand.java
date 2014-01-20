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

/**
 * <p>Interface that defines a {@link Command} as child of another {@link Command}.</p>
 * <p>This functionality is useful for defining group of commands, either
 * {@link org.web4thejob.command.CommandEnum#getSubcommands() statically} or
 * {@link DropdownCommandDecorator#add(Command) dynamically}.</p>
 *
 * @author Veniamin Isaias
 * @see CommandEnum
 * @see DropdownCommandDecorator
 * @since 1.0.0
 */

public interface Subcommand extends Command {

    public Command getParent();

}
