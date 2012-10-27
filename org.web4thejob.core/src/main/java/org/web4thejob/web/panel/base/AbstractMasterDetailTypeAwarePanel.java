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

import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.MasterDetailTypeAware;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractMasterDetailTypeAwarePanel extends AbstractMasterTypeAwarePanel implements
        MasterDetailTypeAware {

    protected AbstractMasterDetailTypeAwarePanel() {
        super();
        registerSetting(SettingEnum.BIND_PROPERTY, "");
    }

    @Override
    public String getBindProperty() {
        return getSettingValue(SettingEnum.BIND_PROPERTY, null);
    }

    @Override
    public void setBindProperty(String propertyName) {
        setSettingValue(SettingEnum.BIND_PROPERTY, propertyName);
    }

    @Override
    public boolean hasBindProperty() {
        return getBindProperty() != null;
    }

    @Override
    public boolean isMasterDetail() {
        return hasMasterType() && hasBindProperty();
    }

}
