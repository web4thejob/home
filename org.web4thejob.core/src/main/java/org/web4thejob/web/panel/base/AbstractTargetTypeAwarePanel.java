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

import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.PanelState;
import org.web4thejob.web.panel.TargetTypeAware;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractTargetTypeAwarePanel extends AbstractContentPanel implements TargetTypeAware {
    // --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractTargetTypeAwarePanel() {
        super();
        registerSetting(SettingEnum.TARGET_TYPE, null);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface TargetType ---------------------

    @Override
    public Class<? extends Entity> getTargetType() {
        final Class<? extends Entity> entityType = getSettingValue(SettingEnum.TARGET_TYPE, null);
        if (entityType == null) return null;
        return ContextUtil.getBean(EntityFactory.class).toEntityType(entityType.getName());
    }

    // --------------------- Interface TargetTypeAware ---------------------

    @Override
    public void setTargetType(Class<? extends Entity> targetType) {
        setSettingValue(SettingEnum.TARGET_TYPE, targetType);
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        arrangeForNullTargetType();
        if (hasTargetType()) {
            arrangeForTargetType();
            arrangeForState(PanelState.READY);
        } else {
            arrangeForState(PanelState.UNDEFINED);
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_TYPE.equals(id)) {
            arrangeForNullTargetType();
            if (hasTargetType()) {
                arrangeForTargetType();
                dispatchTitleChange();
            }
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    protected void arrangeForNullTargetType() {
        arrangeForState(PanelState.UNDEFINED);
        if (getCommandRenderer() != null) {
            getCommandRenderer().reset();
        }
        dispatchTitleChange();
    }

    @Override
    public boolean hasTargetType() {
        return getTargetType() != null;
    }

    protected abstract void arrangeForTargetType();

    @Override
    public String toString() {
        String name = getSettingValue(SettingEnum.PANEL_NAME, null);
        if (!StringUtils.hasText(name)) {
            if (hasTargetType()) {
                name = ContextUtil.getMRS().getEntityMetadata(getTargetType()).getFriendlyName();
            } else {
                name = super.toString();
            }
        }

        return name;
    }
}
