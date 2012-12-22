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

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Iframe;

import java.io.Serializable;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultFramePanel extends AbstractZkBindablePanel implements FramePanel {
    private final Iframe iframe = new Iframe();
    private Entity targetEntity;

    public DefaultFramePanel() {
        ZkUtil.setParentOfChild((Component) base, iframe);
        iframe.setVflex("true");
        iframe.setWidth("100%");
        iframe.setStyle("background-color:transparent");
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.URL_PROPERTY, null);
        registerSetting(SettingEnum.TARGET_URL, null);
        registerSetting(SettingEnum.ASSUME_DETAIL_BEHAVIOR, false);
    }

    private boolean isBound() {
        return hasTargetType() && StringUtils.hasText(getSettingValue(SettingEnum.URL_PROPERTY, ""));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (!isBound()) {
            setUrl((String) getSettingValue(SettingEnum.TARGET_URL, null));
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_URL.equals(id) && !isBound()) {
            setUrl((String) newValue);
        } else {
            super.onSettingValueChanged(id, oldValue, newValue);
        }
    }

    @Override
    protected void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        iframe.setSrc(null);
    }

    @Override
    protected void arrangeForNullMasterType() {
        super.arrangeForNullMasterType();
        iframe.setSrc(null);
    }

    @Override
    protected void arrangeForTargetType() {
        iframe.setSrc(null);
    }

    @Override
    public String getUrl() {
        return iframe.getSrc();
    }

    @Override
    public void setUrl(String url) {
        iframe.setSrc(url);
    }

    @Override
    protected void arrangeForMasterEntity() {
        if (getMasterEntity() == null) {
            setTargetEntity(null);
        } else if (getBindProperty() != null) {
            Entity trgEntity = ContextUtil.getDRS().findById(getTargetType(), getMasterEntity().getIdentifierValue());
            setTargetEntity(trgEntity);
        }
    }

    @Override
    protected void arrangeForTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
        if (this.targetEntity == null) {
            iframe.setSrc(null);
        } else if (isBound()) {
            iframe.setSrc((String) ContextUtil.getMRS().getPropertyMetadata(getTargetType(),
                    getSettingValue(SettingEnum.URL_PROPERTY, "")).getValue(this.targetEntity));
        }
    }

    @Override
    protected boolean processEntityDeselection(Entity entity) {
        return (isMasterDetail() || getSettingValue(SettingEnum.ASSUME_DETAIL_BEHAVIOR,
                false)) && processEntityDeletion(entity);
    }

    @Override
    protected boolean processEntityInsertion(Entity entity) {
        if (canBind(entity)) {
            bindEcho(entity);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processEntityUpdate(Entity entity) {
        if (hasTargetEntity() && getTargetEntity().equals(entity)) {
            setTargetEntity(entity);
            return true;
        }
        return false;
    }

    @Override
    protected boolean processEntityDeletion(Entity entity) {
        if (hasMasterEntity() && getMasterEntity().equals(entity)) {
            setMasterEntity(null);
            return true;
        } else if (hasTargetEntity() && getTargetEntity().equals(entity)) {
            setTargetEntity(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTargetEntity() {
        return targetEntity != null;
    }

    @Override
    public Entity getTargetEntity() {
        return targetEntity;
    }
}
