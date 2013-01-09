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

/**
 * <p>The eligible message ids of the framework.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public enum MessageEnum {

    AFFIRMATIVE_RESPONSE,
    NEGATIVE_RESPONSE,
    BEFORE_ADD,
    AFTER_ADD,
    BEFORE_REMOVE,
    AFTER_REMOVE,
    BEFORE_REPLACE,
    AFTER_REPLACE,
    SETTING_CHANGED,
    ADOPT_ME,
    PATH_SELECTED,
    ACTIVATED,
    DEACTIVATED,
    QUERY,
    VALUE_CHANGED,
    TITLE_CHANGED,
    PARENT_CHANGED,
    RENDER,
    HIGHLIGHT,
    MARK_DIRTY,

    ENTITY_SELECTED,
    ENTITY_DESELECTED,
    ENTITY_INSERTED,
    ENTITY_UPDATED,
    ENTITY_DELETED,
    ENTITY_ACCEPTED,
    BIND_DIRECT,

    PANEL_COPY_START,
    PANEL_COPY_END,
    EXECUTE_SECURED_RESOURCE
}
