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

package org.web4thejob.setting;

import java.io.Serializable;
import java.util.Set;

/**
 * <p>Interface for defining the api of an instance aware of the {@link Setting} configurational concept.</p>
 * <p>Implementations should provide a mechanism for holding settings and monitoring the change of their values.</p>
 *
 * @author Veniamin Isaias
 * @see org.web4thejob.web.panel.base.AbstractSettingAwarePanel AbstractSettingAwarePanel
 * @since 1.0.0
 */

public interface SettingAware {

    public Set<Setting<?>> getSettings();

    public <T extends Serializable> T getSettingValue(SettingEnum id, T defaultValue);

    public boolean hasSetting(SettingEnum id);

    public void setSettings(Set<Setting<?>> settings);

    public <T extends Serializable> void setSettingValue(SettingEnum id, T value);

    public void hideSetting(SettingEnum id, boolean hide);

    public boolean hasUnsavedSettings();

    public void setUnsavedSettings(boolean unsavedSettings);
}
