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

import org.zkoss.zul.Menupopup;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class DefaultDropdownCommandDecorator extends DefaultSubcommandsCommandDecorator implements
        DropdownCommandDecorator {
    private final Set<Command> commands = new LinkedHashSet<Command>(3);

    public DefaultDropdownCommandDecorator(Command command) {
        super(command);
        commands.add(command);
    }

    @Override
    public void add(Command command) {
        commands.add(command);
    }

    @Override
    protected void renderSubCommands(Command parent, Menupopup container) {
        for (Command command : commands) {
            CommandDecorator commandDecorator;
            if (Boolean.class.isInstance(command.getValue())) {
                commandDecorator = new DefaultCheckableMenuitemCommandDecorator(command);
            } else {
                commandDecorator = new DefaultMenuitemCommandDecorator(command);
            }
            commandDecorator.attach(container);
            commandDecorator.setName(commandDecorator.getCommand().getName() + " (" + commandDecorator.getCommand()
                    .getOwner().toString() + ")");

            commandDecorator.addMessageListener(this);
            commandDecorator.render();
        }
    }

}
