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

import org.web4thejob.message.Message;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nUtil;
import org.zkoss.zul.Menuitem;

/**
 * @author Veniamin Isaias
 * @since 3.5.0
 */

public class DefaultHyperlinkMenuitemCommandDecorator extends DefaultMenuitemCommandDecorator implements
        HyperlinkMenuitemCommandDecorator {

    public DefaultHyperlinkMenuitemCommandDecorator(Command command) {
        super(command);
    }

    @Override
    public void render() {
        super.render();

        if (CoreUtil.isURL(command.getValue())) {
            String url = L10nUtil.getMessage(command.getId().getClass(), command.getId().name() + ".url",
                    command.getValue().toString());
            ((Menuitem) clickable).setHref(url);
            ((Menuitem) clickable).setTarget("_blank");
        }
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
