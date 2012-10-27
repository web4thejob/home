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

package org.web4thejob.web.panel.base;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.web.panel.MessageAwarePanel;
import org.web4thejob.web.panel.ParentCapable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractMessageAwarePanel extends AbstractPanel implements MessageAwarePanel {

    private final Set<MessageAware> listeners = new LinkedHashSet<MessageAware>();
    private boolean bindingSuspended;

    @Override
    public boolean addMessageListener(MessageAware messageAware) {
        return listeners.add(messageAware);
    }

    @Override
    public void dispatchMessage(Message message) {
        for (final MessageAware messageDispatcher : listeners) {
            messageDispatcher.dispatchMessage(message);
        }

        if (getParent() == null) {
            processMessage(message);
        }
    }

    @Override
    public void processMessage(Message message) {
        // override
    }

    @Override
    public void setParent(ParentCapable parent) {
        super.setParent(parent);
        processMessage(ContextUtil.getMessage(MessageEnum.PARENT_CHANGED, this, MessageArgEnum.ARG_ITEM, parent));
    }

    @Override
    public boolean removeMessageListener(MessageAware messageAware) {
        return listeners.remove(messageAware);
    }

    @Override
    public Set<MessageAware> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

    @Override
    public boolean isBindingSuspended() {
        return bindingSuspended;
    }

    @Override
    public void bindingSuspended(boolean suspend) {
        bindingSuspended = suspend;
    }


}
