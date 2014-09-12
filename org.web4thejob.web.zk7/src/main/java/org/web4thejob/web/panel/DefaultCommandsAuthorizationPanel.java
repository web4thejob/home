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

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.Subcommand;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.security.SecuredResource;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.base.AbstractSecuredResourceAuthorizationPanel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultCommandsAuthorizationPanel extends AbstractSecuredResourceAuthorizationPanel<Command,
        Command> implements CommandsAuthorizationPanel, ListitemRenderer<Command> {
// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultCommandsAuthorizationPanel() {
        this(false);
    }

    public DefaultCommandsAuthorizationPanel(boolean readOnly) {
        super(readOnly);
    }

    @Override
    protected String getRootElementName() {
        return CommandsAuthorizationPanel.ROOT_ELEMENT;
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected List<Command> getSourceList() {
        List<Command> commands = new ArrayList<Command>();
        for (CommandEnum id : CommandEnum.values()) {
            Command command = ContextUtil.getDefaultCommand(id, null);
            if (command != null) {
                commands.add(command);
            }

            for (CommandEnum subid : id.getSubcommands()) {
                Subcommand subcommand = ContextUtil.getSubcommand(subid, command);
                if (subcommand != null) {
                    commands.add(subcommand);
                }
            }
        }

        Collections.sort(commands, new Comparator<Command>() {
            public int compare(Command o1, Command o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });

        return commands;
    }

    @Override
    protected ListitemRenderer<? extends SecuredResource> getRenderer() {
        return this;
    }

    public void render(Listitem item, Command data, int index) throws Exception {
        item.setImage(CoreUtil.getCommandImage(data.getId(), Command.DEFAULT_COMMAND_IMAGE));
        item.setLabel(data.toString());
        item.setValue(data);
        item.setStyle("white-space:nowrap;");

    }
}
