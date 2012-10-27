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

package org.web4thejob.web.panel;

import org.web4thejob.command.CommandAware;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.setting.SettingAware;

/**
 * <p>Layout panels control the positioning of child panels within their given space. In other words they
 * are containers of child panels and don't have a content of their own.</p>
 * <p>Layout panels can hold either content panels or other layout panels, thus enabling the construction of complex
 * panel hierarchies.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public interface LayoutPanel extends Panel, ParentCapable, DesignModeAware, I18nAware, CommandAware, SettingAware {

    public boolean unregisterCommand(CommandEnum id, boolean recursive);
}
