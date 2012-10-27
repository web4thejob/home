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

package org.web4thejob.message;

import java.util.Set;

/**
 * <p>Interface that defines the api of an instance aware of the {@link Message} concept.</p>
 * <p>This interface follows the <a href="http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern">Chain of
 * Responsibility</a> design pattern. Thus it facilitates the construction of communication lines between components
 * that are agnostic of the existence of one another.</p>
 * <p>The interface supports the direction of messages both up ({@link MessageAware#dispatchMessage(Message)
 * dispatchMessage()}) and down the chain ({@link MessageListener#processMessage(Message) processMessage()}).</p>
 *
 * @author Veniamin Isaias
 * @see MessageListener
 * @see org.web4thejob.web.panel.base.AbstractMessageAwarePanel AbstractMessageAwarePanel
 * @since 1.0.0
 */

public interface MessageAware extends MessageListener {

    public boolean addMessageListener(MessageAware messageAware);

    public void dispatchMessage(Message message);

    public boolean removeMessageListener(MessageAware messageAware);

    public Set<MessageAware> getListeners();


}
