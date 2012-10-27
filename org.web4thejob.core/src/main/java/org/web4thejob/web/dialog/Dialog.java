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

package org.web4thejob.web.dialog;

import org.web4thejob.command.CommandAware;
import org.web4thejob.message.MessageListener;
import org.web4thejob.web.panel.DesignModeAware;
import org.web4thejob.web.panel.I18nAware;

/**
 * <p>The basic interface of application dialogs. Dialogs are modal panels that are used in order to collect
 * some information from the user.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface Dialog extends CommandAware, I18nAware, DesignModeAware {
    public static final String ATTRIB_DIALOG = "dialog";

    public void show(MessageListener listener);
}
