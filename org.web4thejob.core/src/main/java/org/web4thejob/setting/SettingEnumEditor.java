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

package org.web4thejob.setting;

import org.apache.log4j.Logger;

import java.beans.PropertyEditorSupport;

/**
 * <p>Property editor for {@link Setting settings}. It exists so that any error during setting deserialization is
 * ignored, thus the deserialization process does not fail when obsolete or invalid settings are encountered.</p>
 *
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class SettingEnumEditor extends PropertyEditorSupport {

    private static Logger logger = Logger.getLogger(SettingEnumEditor.class);

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(SettingEnum.valueOf(text));
        } catch (IllegalArgumentException e) {
            //so that obsolete or invalid settings will not affect the creation of persistent panels
            setValue(SettingEnum.OBSOLETE_SETTING_PLACEHOLDER);
            logger.error("Setting " + text + " is obsolete or invalid.");
        }
    }
}
