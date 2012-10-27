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

import org.web4thejob.orm.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>A class that serves as a caching mechanism of bind/unbind related messages ({@link MessageEnum#ENTITY_SELECTED
 * ENTITY_SELECTED}, {@link MessageEnum#ENTITY_DELETED ENTITY_DELETED} and {@link MessageEnum#ENTITY_DESELECTED
 * ENTITY_DESELECTED}). Intended for use by {@link org.web4thejob.web.panel.LayoutPanel layout panels} that have hidden
 * children
 * and therefore want to defer processing of bind/unbind messages for performance reasons.
 * </p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class MessageCache {
    private final Map<MessageListener, List<Message>> cache = new HashMap<MessageListener, List<Message>>(1);

    public void put(MessageListener listener, Message message) {
        switch (message.getId()) {
            case ENTITY_DELETED:
                cancelSelectionMessageIfNecessary(listener, message);
                break;
            case ENTITY_DESELECTED:
                cancelSelectionMessageIfNecessary(listener, message);
                break;
            case ENTITY_SELECTED:
                if (!cache.containsKey(listener)) {
                    cache.put(listener, new ArrayList<Message>());
                }
                cache.get(listener).add(message);
                break;
            default:
                throw new IllegalArgumentException("cannot cache message " + message.getId().toString());
        }
    }

    public void flush(MessageListener listener) {
        if (cache.containsKey(listener)) {
            for (Message message : cache.get(listener)) {
                listener.processMessage(message);
            }
            cache.remove(listener);
        }
    }

    public void remove(MessageListener listener) {
        cache.remove(listener);
    }

    private void cancelSelectionMessageIfNecessary(MessageListener listener, Message message) {
        if (cache.containsKey(listener)) {

            for (Message cachedMessage : new ArrayList<Message>(cache.get(listener))) {
                Entity cachedEntity = cachedMessage.getArg(MessageArgEnum.ARG_ITEM, Entity.class);
                Entity cancelledEntity = message.getArg(MessageArgEnum.ARG_ITEM, Entity.class);
                if (cachedEntity.equals(cancelledEntity)) {
                    cache.get(listener).remove(cachedMessage);
                }
            }
        }

    }
}
