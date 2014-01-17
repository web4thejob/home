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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.Subcommand;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.security.SecuredResource;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.base.AbstractSecuredResourceAuthorizationPanel;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultPanelCommandsAuthorizationPanel extends
        AbstractSecuredResourceAuthorizationPanel<Command, Command> implements
        PanelCommandsAuthorizationPanel, ListitemRenderer<Command> {

    public DefaultPanelCommandsAuthorizationPanel() {
        this(false);
    }

    protected DefaultPanelCommandsAuthorizationPanel(boolean readOnly) {
        super(readOnly);
    }

    @Override
    protected String getRootElementName() {
        return PanelCommandsAuthorizationPanel.ROOT_ELEMENT;
    }

    @Override
    protected List<Command> getSourceList() {
        List<Command> resources = new ArrayList<Command>();
        for (Panel panel : ContextUtil.getSessionContext().getPanels(Panel.class)) {
            if (!DesktopLayoutPanel.class.isInstance(panel) && panel instanceof CommandAware) {
                for (CommandEnum id : ((CommandAware) panel).getSupportedCommands()) {
                    Command command = ContextUtil.getDefaultCommand(id, (CommandAware) panel);
                    appendCommand(command, resources);
                }
            }
        }
        return resources;
    }

    private void appendCommand(Command command, List<Command> resources) {
        resources.add(command);

        for (CommandEnum subid : command.getId().getSubcommands()) {
            Subcommand subcommand = ContextUtil.getSubcommand(subid, command);
            if (subcommand != null) {
                appendCommand(subcommand, resources);
            }
        }
    }

    @Override
    protected ListitemRenderer<? extends SecuredResource> getRenderer() {
        return this;
    }

    @Override
    public void render(Listitem item, Command data, int index) throws Exception {
        Listcell listcell = new Listcell();
        listcell.setParent(item);

        StringBuilder sb = new StringBuilder();
        Command ref = data;
        while (ref instanceof Command) {

            sb.insert(0, ref.getName());

            String image = CoreUtil.getCommandImage(ref.getId(), null);
            if (image != null) {
                StringBuilder sbHelp = new StringBuilder();
                sbHelp.append("<img src=\"");
                sbHelp.append(image);
                sbHelp.append("\" style=\"padding-left:5px;padding-right:5px;\" />");
                sb.insert(0, sbHelp.toString());
            }
            sb.insert(0, " -> ");

            if (ref instanceof Subcommand) {
                ref = ((Subcommand) ref).getParent();
            } else {
                ref = null;
            }
        }

        sb.insert(0, data.getOwner().toString());
        StringBuilder sbHelp = new StringBuilder();
        sbHelp.append("<img src=\"");
        if (data.getOwner() instanceof Panel) {
            sbHelp.append(((Panel) data.getOwner()).getImage());
        } else {
            sbHelp.append(Command.DEFAULT_COMMAND_IMAGE);
        }
        sbHelp.append("\" style=\"padding-right:5px;\" />");
        sb.insert(0, sbHelp.toString());


        Html html = new Html(sb.toString().trim());
        html.setParent(listcell);

        item.setValue(data);
        item.setStyle("white-space:nowrap;");
    }

}
