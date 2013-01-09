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

package org.web4thejob.message;

import java.util.Map;

/**
 * <p>The interface of messages exchanged between {@link MessageAware} components. Each message is immutable after
 * creation and consists of an id, a sender and a map of arguments. Arguments map may be empty but never null.</p>
 *
 * @author Veniamin Isaias
 * @see MessageAware
 * @since 1.0.0
 */

public interface Message {

    public <T> T getArg(MessageArgEnum key, Class<T> clazz);

    public Map<MessageArgEnum, Object> getArgs();

    public MessageEnum getId();

    public Object getSender();
}
