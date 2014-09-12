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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.HtmlHolder;
import org.web4thejob.orm.annotation.ImageHolder;
import org.web4thejob.orm.annotation.MediaHolder;
import org.web4thejob.orm.annotation.UrlHolder;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingAware;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.util.ComboItemConverter;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.*;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultSettingsDialog extends AbstractDialog implements SettingsDialog {
    // ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected DefaultSettingsDialog(SettingAware settingsAware) {
        super();
        ownerClassName = settingsAware.toString() + " (" + settingsAware.getClass().getCanonicalName() + ")";
        dialogContent.getPanelchildren().setStyle("overflow:auto;");
        for (Setting<?> setting : CoreUtil.cloneSettings(settingsAware.getSettings())) {
            settings.put(setting.getId(), setting);
        }
        L10nString prevCategory = null;
        for (SettingEnum id : settings.keySet()) {
            Setting<?> setting = settings.get(id);
            if (setting.isHidden()) {
                continue;
            }

            if (!setting.getId().getCategory().equals(prevCategory)) {
                prevCategory = setting.getId().getCategory();

                Groupbox groupbox = new Groupbox();
                groupbox.setOpen(listboxes.isEmpty());
                groupbox.setMold("3d");
                groupbox.setWidth("");
                groupbox.setParent(dialogContent.getPanelchildren());
                Caption caption = new Caption(setting.getId().getCategory().toString());
                caption.setHflex("true");
                caption.setParent(groupbox);

                Listbox listbox = new Listbox();
                listbox.setParent(groupbox);
                new Listhead().setParent(listbox);
                listbox.setHflex("true");
                listbox.setVflex("true");
                listbox.setSpan(true);
                listbox.getListhead().setSizable(true);
                listbox.setMold("paging");
                listbox.setPageSize(Integer.MAX_VALUE);

                Listheader header;
                header = new Listheader(L10N_LIST_HEADER_SETTING.toString());
                header.setWidth("40%");
                header.setParent(listbox.getListhead());
                header = new Listheader(L10N_LIST_HEADER_VALUE.toString());
                header.setParent(listbox.getListhead());

                listbox.setItemRenderer(new ListboxRenderer(listbox));
                listbox.setModel(new ListModelSet<Setting<?>>());
                listboxes.add(listbox);
            }

            Listbox listbox = listboxes.get(listboxes.size() - 1);
            ((ListModelSet) listbox.getModel()).add(setting);
        }

        // now sort each category per setting id
        for (Listbox listbox : listboxes) {
            listbox.setModel(new ListModelSet(new TreeSet<Setting<?>>((Set) listbox.getModel()), true));
        }
    }

    public static final L10nString L10N_LIST_HEADER_SETTING = new L10nString(DefaultSettingsDialog.class,
            "list_header_setting", "Setting");
    public static final L10nString L10N_LIST_HEADER_VALUE = new L10nString(DefaultSettingsDialog.class,
            "list_header_value", "Value");
    private final String ATTRIB_DATABINDER = DataBinder.class.getName();
    private final List<Listbox> listboxes = new ArrayList<Listbox>();
    private final SortedMap<SettingEnum, Setting<?>> settings = new TreeMap<SettingEnum,
            Setting<?>>(new SettingComparator());
    private final String ownerClassName;
    private Combobox bindProperty;
    private Combobox htmlProperty;
    private Combobox urlProperty;
    private Combobox mediaProperty;
    // --------------------------- CONSTRUCTORS ---------------------------
    private Textbox panelName;

    // -------------------------- OTHER METHODS --------------------------

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Message getOKMessage() {
        Set<Setting<?>> settings = new HashSet<Setting<?>>();
        for (Listbox listbox : listboxes) {
            ((DataBinder) listbox.getAttribute(ATTRIB_DATABINDER)).saveAll();
            settings.addAll((Set) listbox.getModel());
        }

        return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM, settings);
    }

    @Override
    protected String prepareTitle() {
        return L10nUtil.getMessage(CommandEnum.CONFIGURE_SETTINGS.getClass(), CommandEnum.CONFIGURE_SETTINGS.name(),
                CommandEnum.CONFIGURE_SETTINGS.name()) + ": " + ownerClassName;

    }

    // -------------------------- INNER CLASSES --------------------------

    @SuppressWarnings("unchecked")
    private void loadBindProperties() throws Exception {
        if (bindProperty == null) return;

        bindProperty.getItems().clear();
        bindProperty.setText("");
        if (settings.get(SettingEnum.TARGET_TYPE) != null &&
                settings.get(SettingEnum.TARGET_TYPE).getValue() != null &&
                settings.get(SettingEnum.MASTER_TYPE) != null &&
                settings.get(SettingEnum.MASTER_TYPE).getValue() != null) {

            for (PropertyMetadata propertyMetadata : ContextUtil.getMRS().getEntityMetadata((Class<? extends
                    Entity>) settings.get(SettingEnum.TARGET_TYPE).getValue()).getPropertiesMetadata()) {
                if (propertyMetadata.isAssociationType() && !propertyMetadata.isOneToManyType() && propertyMetadata
                        .getAssociatedEntityMetadata().getEntityType().isAssignableFrom((Class<?>) settings.get
                                (SettingEnum.MASTER_TYPE)
                                .getValue())) {
                    Comboitem comboitem = new Comboitem(propertyMetadata.getFriendlyName());
                    comboitem.setValue(propertyMetadata.getName());
                    comboitem.setParent(bindProperty);
                }
            }
        }
    }

    private void loadAnnotatedProperties(Combobox combobox, Class<? extends Annotation> annotationType) throws
            Exception {
        loadAnnotatedProperties(combobox, annotationType, false);
    }

    @SuppressWarnings("unchecked")
    private void loadAnnotatedProperties(Combobox combobox, Class<? extends Annotation> annotationType,
                                         boolean append) throws
            Exception {
        if (combobox == null) return;

        if (!append) {
            combobox.getItems().clear();
        }

        combobox.setText("");
        if (settings.get(SettingEnum.TARGET_TYPE) != null && settings.get(SettingEnum.TARGET_TYPE).getValue() != null) {

            for (PropertyMetadata propertyMetadata : ContextUtil.getMRS().getEntityMetadata((Class<? extends
                    Entity>) settings.get(SettingEnum.TARGET_TYPE).getValue()).getPropertiesMetadata()) {
                if (propertyMetadata.isAnnotatedWith(annotationType)) {
                    Comboitem comboitem = new Comboitem(propertyMetadata.getFriendlyName());
                    comboitem.setValue(propertyMetadata.getName());
                    comboitem.setParent(combobox);
                }
            }
        }
    }

    private class SettingComparator implements Comparator<SettingEnum> {
        public int compare(SettingEnum o1, SettingEnum o2) {
            String s1 = o1.getCategory().getKey() + "-" + MessageFormat.format("{0,number,0000}", o1.ordinal());
            String s2 = o2.getCategory().getKey() + "-" + MessageFormat.format("{0,number,0000}", o2.ordinal());
            return s1.compareTo(s2);
        }
    }

    private class ListboxRenderer implements ListitemRenderer<Setting<?>>, RendererCtrl {
        private ListboxRenderer(Listbox listbox) {
            this.listbox = listbox;
        }

        private final Listbox listbox;

        @SuppressWarnings("unchecked")
        public void render(Listitem item, Setting<?> setting, int index) throws Exception {
            final String beanid = "setting_" + setting.getId().name();
            new Listcell(L10nUtil.getMessage(setting.getId().getClass(), setting.getId().name(),
                    setting.getId().name())).setParent(item);

            DataBinder dataBinder = (DataBinder) item.getListbox().getAttribute(ATTRIB_DATABINDER);
            dataBinder.bindBean(beanid, setting);

            Listcell listcell = new Listcell();
            listcell.setParent(item);
            Component comp;
            if (setting.getId() == SettingEnum.BIND_PROPERTY) {
                bindProperty = new Combobox();
                bindProperty.setWidth("60%");
                bindProperty.setReadonly(true);
                loadBindProperties();
                comp = bindProperty;
                ZkUtil.addBinding(dataBinder, comp, beanid, "value", ComboItemConverter.class);
            } else if (setting.getId() == SettingEnum.HTML_PROPERTY) {
                htmlProperty = new Combobox();
                htmlProperty.setWidth("60%");
                htmlProperty.setReadonly(true);
                loadAnnotatedProperties(htmlProperty, HtmlHolder.class);
                comp = htmlProperty;
                ZkUtil.addBinding(dataBinder, comp, beanid, "value", ComboItemConverter.class);
            } else if (setting.getId() == SettingEnum.URL_PROPERTY) {
                urlProperty = new Combobox();
                urlProperty.setWidth("60%");
                urlProperty.setReadonly(true);
                loadAnnotatedProperties(urlProperty, UrlHolder.class);
                comp = urlProperty;
                ZkUtil.addBinding(dataBinder, comp, beanid, "value", ComboItemConverter.class);
            } else if (setting.getId() == SettingEnum.MEDIA_PROPERTY) {
                mediaProperty = new Combobox();
                mediaProperty.setWidth("60%");
                mediaProperty.setReadonly(true);
                loadAnnotatedProperties(mediaProperty, MediaHolder.class);
                loadAnnotatedProperties(mediaProperty, ImageHolder.class, true);
                comp = mediaProperty;
                ZkUtil.addBinding(dataBinder, comp, beanid, "value", ComboItemConverter.class);
            } else {
                comp = ZkUtil.getEditableComponentForJavaType(setting.getType(), setting.getSubType());
                ZkUtil.addBinding(dataBinder, comp, beanid, "value");
            }


            if (setting.getId() == SettingEnum.PANEL_NAME) {
                panelName = (Textbox) comp;
            } else if (setting.getId() == SettingEnum.TARGET_TYPE) {
                comp.addEventListener(Events.ON_CHANGE, new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        if (panelName != null) {
                            panelName.setText(((Combobox) event.getTarget()).getText());
                        }
                        loadBindProperties();
                        loadAnnotatedProperties(htmlProperty, HtmlHolder.class);
                        loadAnnotatedProperties(urlProperty, UrlHolder.class);
                        loadAnnotatedProperties(mediaProperty, MediaHolder.class);
                        loadAnnotatedProperties(mediaProperty, ImageHolder.class, true);
                    }
                });
            } else if (setting.getId() == SettingEnum.MASTER_TYPE) {
                comp.addEventListener(Events.ON_CHANGE, new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        loadBindProperties();
                    }
                });
            }

            comp.setParent(listcell);

        }

        public void doTry() {
            listbox.setAttribute(ATTRIB_DATABINDER, new DataBinder());
        }

        public void doCatch(Throwable ex) throws Throwable {
            // nothing to do
        }

        public void doFinally() {
            ((DataBinder) listbox.getAttribute(ATTRIB_DATABINDER)).loadAll();
        }
    }

}

