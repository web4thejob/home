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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.AbstractBorderLayoutPanel;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultModelOrderingsPanel extends AbstractBorderLayoutPanel implements ModelOrderingsPanel {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TargetType ---------------------

    @Override
    public Class<? extends Entity> getTargetType() {
        if (getOrderByPanel() != null) {
            return getOrderByPanel().getTargetType();
        }
        if (getModelHierarchyPanel() != null) {
            return getModelHierarchyPanel().getTargetType();
        }
        return null;
    }

    @Override
    public boolean hasTargetType() {
        if (getOrderByPanel() != null) {
            return getOrderByPanel().hasTargetType();
        }
        return getModelHierarchyPanel() != null && getModelHierarchyPanel().hasTargetType();
    }

// --------------------- Interface TargetTypeAware ---------------------

    @Override
    public void setTargetType(Class<? extends Entity> targetType) {
        if (getOrderByPanel() != null) {
            getOrderByPanel().setTargetType(targetType);
        }
        if (getModelHierarchyPanel() != null) {
            getModelHierarchyPanel().setTargetType(targetType);
            getModelHierarchyPanel().refresh();
        }
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (!hasCenter() && getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            setCenter(ContextUtil.getDefaultPanel(OrderByPanel.class));
            getCenter().render();
        } else if (hasCenter() && !getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            subpanels.remove(getCenter());
        }


        if (!hasWest() && getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            setWest(ContextUtil.getDefaultPanel(ModelHierarchyPanel.class));
            getWest().render();
        } else if (hasWest() && !getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            subpanels.remove(getWest());
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_TYPE.equals(id)) {
            if (getCommandRenderer() != null) {
                getCommandRenderer().reset();
            }

            if (getOrderByPanel() != null) {
                getOrderByPanel().setSettingValue(id, newValue);
            }
            if (getModelHierarchyPanel() != null) {
                getModelHierarchyPanel().setSettingValue(id, newValue);
                getModelHierarchyPanel().allowBlobs(false);
                getModelHierarchyPanel().refresh();
            }
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    @Override
    public OrderByPanel getOrderByPanel() {
        return (OrderByPanel) getCenter();
    }

    @Override
    public ModelHierarchyPanel getModelHierarchyPanel() {
        return (ModelHierarchyPanel) getWest();
    }

    @Override
    public void hideHierarchy(boolean hide) {
        if (getModelHierarchyPanel() != null) {
            getRegionByPanel(getWest()).setOpen(!hide);
        }
    }

    @Override
    public void dispatchMessage(Message message) {
        if (message.getId() == MessageEnum.PATH_SELECTED) {
            if (getOrderByPanel() != null) {
                getOrderByPanel().processMessage(message);
            }
        } else {
            super.dispatchMessage(message);
        }
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();

        registerSetting(SettingEnum.TARGET_TYPE, null);

        // unregister unwanted BorderLayoutPanlel settings
        unregisterSetting(SettingEnum.NORTH_ENABLED);
        unregisterSetting(SettingEnum.NORTH_OPEN);
        unregisterSetting(SettingEnum.NORTH_COLLAPSIBLE);
        unregisterSetting(SettingEnum.NORTH_SPLITTABLE);
        unregisterSetting(SettingEnum.NORTH_HEIGHT);
        unregisterSetting(SettingEnum.NORTH_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.NORTH_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.NORTH_CHILD_INDEX);
        unregisterSetting(SettingEnum.SOUTH_ENABLED);
        unregisterSetting(SettingEnum.SOUTH_OPEN);
        unregisterSetting(SettingEnum.SOUTH_COLLAPSIBLE);
        unregisterSetting(SettingEnum.SOUTH_SPLITTABLE);
        unregisterSetting(SettingEnum.SOUTH_HEIGHT);
        unregisterSetting(SettingEnum.SOUTH_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.SOUTH_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.SOUTH_CHILD_INDEX);
        unregisterSetting(SettingEnum.EAST_ENABLED);
        unregisterSetting(SettingEnum.EAST_OPEN);
        unregisterSetting(SettingEnum.EAST_COLLAPSIBLE);
        unregisterSetting(SettingEnum.EAST_SPLITTABLE);
        unregisterSetting(SettingEnum.EAST_WIDTH);
        unregisterSetting(SettingEnum.EAST_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.EAST_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.EAST_CHILD_INDEX);
    }
}
