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

package org.web4thejob.web.panel.base;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.MasterTypeAware;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractMasterTypeAwarePanel extends AbstractTargetTypeAwarePanel implements MasterTypeAware {
    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractMasterTypeAwarePanel() {
        registerSetting(SettingEnum.MASTER_TYPE, null);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface MasterType ---------------------

    @Override
    public Class<? extends Entity> getMasterType() {
        final Class<? extends Entity> entityType = getSettingValue(SettingEnum.MASTER_TYPE, null);
        if (entityType == null) return null;
        return ContextUtil.getBean(EntityFactory.class).toEntityType(entityType.getName());
    }

    // --------------------- Interface MasterTypeAware ---------------------

    @Override
    public void setMasterType(Class<? extends Entity> masterType) {
        setSettingValue(SettingEnum.MASTER_TYPE, masterType);
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        arrangeForNullMasterType();
        if (hasMasterType()) {
            arrangeForMasterType();
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.MASTER_TYPE.equals(id)) {
            arrangeForNullMasterType();
            if (hasMasterType()) {
                arrangeForMasterType();
            }
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    protected abstract void arrangeForNullMasterType();

    @Override
    public boolean hasMasterType() {
        return getMasterType() != null;
    }

    protected void arrangeForMasterType() {
        // override
    }
}
