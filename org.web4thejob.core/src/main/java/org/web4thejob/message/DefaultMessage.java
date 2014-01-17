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

package org.web4thejob.message;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Default implementation of a {@link Message} as a spring bean.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultMessage implements Message {

    final private MessageEnum id;
    final private Object sender;
    final private Map<MessageArgEnum, Object> args = new HashMap<MessageArgEnum, Object>(0);

    protected DefaultMessage(MessageEnum id, Object sender) {
        this.id = id;
        this.sender = sender;
    }

    protected DefaultMessage(MessageEnum id, Object sender, Map<MessageArgEnum, Object> args) {
        this.id = id;
        this.sender = sender;
        this.args.putAll(args);
    }

    protected DefaultMessage(MessageEnum id, Object sender, MessageArgEnum key, Object value) {
        this.id = id;
        this.sender = sender;
        args.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        return obj instanceof Message && hashCode() == obj.hashCode();

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getArg(MessageArgEnum key, Class<T> clazz) {
        return (T) args.get(key);
    }

    @Override
    public String toString() {
        return id.name() + "|" + sender.toString();
    }

    @Override
    public Map<MessageArgEnum, Object> getArgs() {
        return Collections.unmodifiableMap(args);
    }

    @Override
    public MessageEnum getId() {
        return id;
    }

    @Override
    public Object getSender() {
        return sender;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(sender).append(args).toHashCode();
    }
}
