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
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractCommandDecorator implements CommandDecorator {
    protected AbstractCommandDecorator(Command command) {
        this.command = command;
    }

    private final Set<MessageAware> listeners = new LinkedHashSet<MessageAware>();
    protected final Command command;

    public boolean addMessageListener(MessageAware messageAware) {
        return listeners.add(messageAware);
    }

    public void dispatchMessage(Message message) {
        if (MessageEnum.ACTIVATED == message.getId() || MessageEnum.DEACTIVATED == message.getId() || MessageEnum
                .RENDER == message.getId()) {
            processMessage(message);
            return;
        }

        if (MessageEnum.AFTER_REMOVE == message.getId()) {
            processMessage(message);
        }
        for (final MessageAware messageDispatcher : new LinkedHashSet<MessageAware>(listeners)) {
            messageDispatcher.dispatchMessage(message);
        }
    }


    public boolean removeMessageListener(MessageAware messageAware) {
        return listeners.remove(messageAware);
    }

    public Command getCommand() {
        return command;
    }

    public void processMessage(Message message) {
        switch (message.getId()) {
            case AFTER_REMOVE:
                if (isAttached()) {
                    dettach();
                }
                break;
            case ACTIVATED:
                setDisabled(false);
                break;
            case DEACTIVATED:
                setDisabled(true);
                break;
            case RENDER:
                render();
                break;
        }

    }

    public Set<MessageAware> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

}
