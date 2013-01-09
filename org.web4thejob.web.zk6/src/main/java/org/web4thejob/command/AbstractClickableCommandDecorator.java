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

import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractClickableCommandDecorator extends AbstractCommandDecorator implements
        EventListener<Event> {
// ------------------------------ FIELDS ------------------------------

    private static final String ON_CLICK_ECHO = Events.ON_CLICK + "Echo";

    protected final LabelImageElement clickable;

// --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractClickableCommandDecorator(Command command) {
        super(command);
        clickable = getClickable();
        clickable.setTooltiptext(command.getName());
        String image = CoreUtil.getCommandImage(command.getId(), null);
        if (image != null) {
            clickable.setImage(image);
        } else {
            clickable.setLabel(command.getName());
        }
        clickable.setAttribute(CommandDecorator.ATTRIB_DECORATOR, this);
        clickable.addEventListener(Events.ON_CLICK, this);
        clickable.addEventListener(ON_CLICK_ECHO, this);
    }

    protected abstract LabelImageElement getClickable();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CommandDecorator ---------------------

    @Override
    public void attach(Object container) {
        if (!Component.class.isInstance(container))
            throw new IllegalArgumentException("container is not an instance of " + Component.class.getName());
        clickable.setParent((Component) container);
        command.addMessageListener(this);
    }

    @Override
    public void dettach() {
        clickable.detach();
        command.removeMessageListener(this);
    }

    @Override
    public boolean isAttached() {
        return clickable.getPage() != null;
    }

    @Override
    public void render() {
        if (command.isActive() && isDisabled()) {
            setDisabled(false);
        } else if (!command.isActive() && !isDisabled()) {
            setDisabled(true);
        }
    }

    @Override
    public String getName() {
        return clickable.getLabel();
    }

    @Override
    public void setName(String name) {
        clickable.setLabel(name);
    }

// --------------------- Interface EventListener ---------------------


    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_CLICK.equals(event.getName())) {
            Clients.showBusy(null);
            Events.echoEvent(new MouseEvent(ON_CLICK_ECHO, event.getTarget()));
        } else if (ON_CLICK_ECHO.equals(event.getName())) {
            Clients.clearBusy();
            command.process();
        }
    }

// --------------------- Interface MessageListener ---------------------


    @Override
    public void dispatchMessage(Message message) {
        if (MessageEnum.HIGHLIGHT == message.getId() || MessageEnum.MARK_DIRTY == message.getId()) {
            processMessage(message);
            return;
        }
        super.dispatchMessage(message);
    }

    @Override
    public void processMessage(Message message) {
        if (message.getSender().equals(command) || message.getSender().equals(this)) {
            switch (message.getId()) {
                case ACTIVATED:
                    setDisabled(false);
                    break;
                case DEACTIVATED:
                    setDisabled(true);
                    break;
                case HIGHLIGHT:
                    if (message.getArg(MessageArgEnum.ARG_ITEM, Boolean.class)) {
                        clickable.setStyle("border:2px;border-style:solid;border-color:#EB3A05;");
                    } else {
                        clickable.setStyle("border:0px;");
                    }
                    break;
                case MARK_DIRTY:
                    ZkUtil.setCommandDirty(command, message.getArg(MessageArgEnum.ARG_ITEM, Boolean.class), clickable);
                    break;
                default:
                    super.processMessage(message);
            }
        }
    }
}
