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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingAware;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.L10nUtil;

import java.io.Serializable;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractSettingAwarePanel extends AbstractMessageAwarePanel implements SettingAware,
        InitializingBean {
// ------------------------------ FIELDS ------------------------------

    protected AbstractSettingAwarePanel() {
        registerSettings();
    }

    private final Map<SettingEnum, Setting<?>> settings = new HashMap<SettingEnum, Setting<?>>();
    private int index;

    // --------------------------- CONSTRUCTORS ---------------------------
    private boolean unsavedSettings;

    protected void registerSettings() {
        registerSetting(SettingEnum.PANEL_NAME, null);
        registerSetting(SettingEnum.SCLASS, null);
    }

    protected <T extends Serializable> void registerSetting(SettingEnum id, T value) {
        settings.put(id, ContextUtil.getSetting(id, value));
    }

    public void hideSetting(SettingEnum id, boolean hide) {
        Setting<?> setting = settings.get(id);
        if (setting != null) {
            setting.setHidden(hide);
        }
    }

    public boolean hasUnsavedSettings() {
        return unsavedSettings;
    }

    public void setUnsavedSettings(boolean unsavedSettings) {
        this.unsavedSettings = unsavedSettings;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public Set<Setting<?>> getSettings() {
        return Collections.unmodifiableSet(new HashSet<Setting<?>>(settings.values()));
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Panel ---------------------

    public void setSettings(Set<Setting<?>> settings) {
        for (final Setting<?> setting : new TreeSet<Setting<?>>(settings)) {
            if (this.settings.containsKey(setting.getId())) {
                setSettingValue(setting.getId(), setting.getValue());
            }
        }
        afterSettingsSet();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isPersisted() {
        return getBeanName() != null && getBeanName().startsWith(getClass().getCanonicalName()) && getBeanName()
                .contains("-panel_");
    }

// --------------------- Interface SettingAware ---------------------

    @Override
    public String toSpringXml() {
        final Builder parser = new Builder(false);
        try {
            final Document dom = parser.build(super.toSpringXml(), null);
            final Element bean = dom.getRootElement();

            final Element prop = new Element("property", BEANS_NAMESPACE);
            prop.addAttribute(new Attribute("name", "settings"));
            bean.appendChild(prop);

            final Element set = new Element("set", BEANS_NAMESPACE);
            prop.appendChild(set);

            for (final Setting<?> setting : settings.values()) {
                String targetClassName;
                if (setting instanceof Advised) {
                    targetClassName = ((Advised) setting).getTargetSource().getTargetClass().getName();
                } else {
                    targetClassName = setting.getClass().getName();
                }

                final Element item = new Element("bean", BEANS_NAMESPACE);
                set.appendChild(item);
                item.addAttribute(new Attribute("class", targetClassName));
                item.addAttribute(new Attribute("scope", "prototype"));

                final Element arg1 = new Element("constructor-arg", BEANS_NAMESPACE);
                item.appendChild(arg1);
                arg1.addAttribute(new Attribute("value", setting.getId().name()));

                final Element arg2 = new Element("constructor-arg", BEANS_NAMESPACE);
                item.appendChild(arg2);
                String value = setting.coerceToString();
                arg2.addAttribute(new Attribute("value", (value == null ? "" : value)));
            }

            return bean.toXML();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected Setting<?> getSetting(SettingEnum id) {
        return settings.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getSettingValue(SettingEnum id, T defaultValue) {
        final Setting<T> setting = (Setting<T>) settings.get(id);
        if (setting != null) {
            if (setting.getValue() != null && StringUtils.hasText(setting.getValue().toString()))
                return setting.getValue();
        }

        return defaultValue;
    }

    public boolean hasSetting(SettingEnum id) {
        return settings.containsKey(id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        setUnsavedSettings(false);
    }

    protected void afterSettingsSet() {
        //override
    }


    @SuppressWarnings("unchecked")
    public <T extends Serializable> void setSettingValue(SettingEnum id, T value) {
        if (settings.containsKey(id)) {
            final Setting<T> setting = (Setting<T>) settings.get(id);
            final T oldValue = setting.getValue();

            T newValue;
            if (!setting.getType().isInstance(value) && setting.getType().equals(String.class)) {
                final ConversionService conversionService = ContextUtil.getBean(ConversionService.class);
                newValue = conversionService.convert(value, setting.getType());
            } else {
                newValue = value;
            }

            if ((oldValue != null && !oldValue.equals(newValue)) || (newValue != null && !newValue.equals(oldValue))) {
                setting.setValue(newValue);
                onSettingValueChanged(id, oldValue, newValue);
            }
        }
    }

// -------------------------- OTHER METHODS --------------------------

    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        setUnsavedSettings(true);
        if (SettingEnum.PANEL_NAME == id) {
            dispatchTitleChange();
        } else if (SettingEnum.SCLASS == id) {
            setSclass((String) newValue);
        }
    }

    protected void dispatchTitleChange() {
        dispatchMessage(ContextUtil.getMessage(MessageEnum.TITLE_CHANGED, this));
    }

    protected Setting<?> unregisterSetting(SettingEnum id) {
        return settings.remove(id);
    }

    public String toString() {
        String name = getSettingValue(SettingEnum.PANEL_NAME, null);
        if (!StringUtils.hasText(name)) {
            name = L10nUtil.getMessage(getClass(), "friendlyBeanName", null);
            if (!StringUtils.hasText(name)) {
                name = getBeanName();
            }
        }
        return name;
    }
}
