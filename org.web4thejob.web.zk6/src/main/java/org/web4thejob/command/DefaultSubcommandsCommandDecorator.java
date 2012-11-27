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

import org.web4thejob.context.ContextUtil;
import org.web4thejob.setting.SettingAware;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DefaultSubcommandsCommandDecorator extends AbstractCommandDecorator implements
        SubcommandsCommandDecorator, EventListener<Event> {
    protected final Combobutton combobutton = new Combobutton();

    public DefaultSubcommandsCommandDecorator(Command command) {
        super(command);

        combobutton.setAttribute(CommandDecorator.ATTRIB_DECORATOR, this);
        combobutton.addEventListener(Events.ON_CLICK, this);
        combobutton.setMold("toolbar");

        String image = CoreUtil.getCommandImage(command.getId(), null);
        combobutton.setTooltiptext(command.getName());
        if (image != null) {
            combobutton.setImage(image);
        } else {
            combobutton.setLabel(command.getName());
        }

        if (command.getId() == CommandEnum.USER_DROPDOWN) {
            String username = ContextUtil.getSessionContext().getSecurityContext().getUserIdentity().getCode();
            combobutton.setTooltiptext(command.getName() + ": " + username);
            if (ContextUtil.resourceExists(image)) {
                combobutton.setLabel(username);
            } else {
                combobutton.setLabel(command.getName() + ": " + username);
            }
        } else if (command.getId() == CommandEnum.DESIGN && command.getOwner() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(command.getName());
            sb.append(": ");
            sb.append(command.getOwner().toString());
            sb.append(" (");
            sb.append(command.getOwner().getClass().getCanonicalName());
            sb.append("), ");
            sb.append("state: ");
            sb.append(command.getOwner().getPanelState().toString());
            combobutton.setTooltiptext(sb.toString());
        }
    }

    @Override
    public void attach(Object container) {
        combobutton.setParent((Component) container);
        command.addMessageListener(this);
    }

    @Override
    public void dettach() {
        combobutton.detach();
        command.removeMessageListener(this);
    }

    @Override
    public boolean isAttached() {
        return combobutton.getPage() != null;
    }


    protected void renderSubCommands(Command parent, Menupopup container) {
        for (CommandEnum id : parent.getId().getSubcommands()) {
            Command subcommand = parent.getOwner().getCommand(id);
            if (subcommand != null) {

                if (id.isRequiresStartSeparator() && subcommand.getId() != parent.getId().getSubcommands().first() &&
                        !Menuseparator.class.isInstance(container.getLastChild())) {
                    addSeparator(container);
                }

                CommandDecorator commandDecorator = null;
                if (!subcommand.getId().getSubcommands().isEmpty()) {
                    Menu menu = new Menu(subcommand.getName(), CoreUtil.getCommandImage(subcommand.getId(), null));
                    menu.setParent(container);
                    Menupopup subpopup = new Menupopup();
                    subpopup.setParent(menu);
                    renderSubCommands(subcommand, subpopup);
                } else {
                    if (Boolean.class.isInstance(subcommand.getValue())) {
                        commandDecorator = new DefaultCheckableMenuitemCommandDecorator(subcommand);
                    } else {
                        commandDecorator = new DefaultMenuitemCommandDecorator(subcommand);
                    }
                }

                if (commandDecorator != null) {
                    commandDecorator.attach(container);
                    commandDecorator.addMessageListener(this);
                    commandDecorator.render();
                }

                if (id.isRequiresEndSeparator() && subcommand.getId() != parent.getId().getSubcommands().last() &&
                        !Menuseparator.class.isInstance(container.getLastChild())) {
                    addSeparator(container);
                }
            }
        }
    }

    @Override
    public void render() {
        Menupopup menupopup = new Menupopup();
        if (combobutton.getDropdown() != null) {
            combobutton.getDropdown().detach();
        }
        menupopup.setParent(combobutton);

        renderSubCommands(command, menupopup);
        setDisabled(!command.isActive());
        arrangeDesignIcon();
    }

    private void arrangeDesignIcon() {
        if (command.getId().equals(CommandEnum.DESIGN) && command.getOwner() instanceof SettingAware) {
            boolean dirty = ((SettingAware) command.getOwner()).hasUnsavedSettings();

            ZkUtil.setCommandDirty(command, dirty, combobutton);

            if (command.getOwner().hasCommand(CommandEnum.SAVE_PANEL)) {
                command.getOwner().getCommand(CommandEnum.SAVE_PANEL).setHighlighted(dirty);
            }

        }
    }

    private void addSeparator(Menupopup menupopup) {
        Menuseparator separator = new Menuseparator();
        separator.setParent(menupopup);
    }

    @Override
    public boolean isDisabled() {
        return combobutton.isDisabled();
    }

    @Override
    public void setDisabled(boolean disabled) {
        combobutton.setDisabled(disabled);
    }

    @Override
    public String getName() {
        return combobutton.getLabel();
    }

    @Override
    public void setName(String name) {
        combobutton.setLabel(name);
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_CLICK.equals(event.getName())) {
            if (combobutton.getDropdown() != null) {
                combobutton.setOpen(true);
            }
        }
    }

}
