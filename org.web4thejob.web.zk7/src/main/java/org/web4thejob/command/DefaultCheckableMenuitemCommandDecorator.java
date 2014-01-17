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
import org.web4thejob.message.MessageEnum;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class DefaultCheckableMenuitemCommandDecorator extends DefaultMenuitemCommandDecorator implements
        CheckableMenuitemCommandDecorator {

    public DefaultCheckableMenuitemCommandDecorator(Command command) {
        super(command);
        ((Menuitem) clickable).setCheckmark(true);
        ((Menuitem) clickable).setAutocheck(true);
    }

    @Override
    public void render() {
        super.render();

        if ((Boolean) command.getValue() && !((Menuitem) clickable).isChecked()) {
            ((Menuitem) clickable).setChecked(true);
        } else if (!(Boolean) command.getValue() && ((Menuitem) clickable).isChecked()) {
            ((Menuitem) clickable).setChecked(false);
        }
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_CLICK.equals(event.getName())) {
            command.setValue(((Menuitem) event.getTarget()).isChecked());
        }
        super.onEvent(event);
    }

    @Override
    public void dispatchMessage(Message message) {
        if (MessageEnum.VALUE_CHANGED == message.getId()) {
            processMessage(message);
        }
        super.dispatchMessage(message);
    }

    @Override
    public void processMessage(Message message) {
        if (message.getSender().equals(command) || message.getSender().equals(this)) {
            switch (message.getId()) {
                case VALUE_CHANGED:
                    render();
                    break;
                default:
                    super.processMessage(message);
            }
        }
    }

}
