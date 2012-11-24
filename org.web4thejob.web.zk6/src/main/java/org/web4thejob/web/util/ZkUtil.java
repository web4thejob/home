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

package org.web4thejob.web.util;

import org.springframework.util.StringUtils;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandDecorator;
import org.web4thejob.command.LookupCommandDecorator;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.MessageAware;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.annotation.ColorHolder;
import org.web4thejob.orm.annotation.HtmlHolder;
import org.web4thejob.orm.annotation.ImageHolder;
import org.web4thejob.orm.annotation.MediaHolder;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Key;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.panel.SessionInfoPanel;
import org.web4thejob.web.panel.base.AbstractMultiSelectPanel;
import org.web4thejob.web.zbox.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.LabelImageElement;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class ZkUtil {

    private static final String OWNING_PANEL = "owningPanel";
    private static final String DEFAULT_WIDTH = "800px";
    private static final String DEFAULT_HEIGHT = "500px";

    public static void setParentOfChild(Component parent, Component child) {
        if (parent instanceof Panel) {
            child.setParent(((Panel) parent).getPanelchildren());
        } else throw new IllegalArgumentException("cannot handle " + parent.getClass().getName());
    }

    public static org.zkoss.zul.Panel initBaseComponent(org.web4thejob.web.panel.Panel owner) {
        org.zkoss.zul.Panel zkpanel = new org.zkoss.zul.Panel();
        zkpanel.setAttribute(OWNING_PANEL, owner);
        zkpanel.setBorder("none");
        //zkpanel.setHflex("1");
        zkpanel.setVflex("1");
        new Panelchildren().setParent(zkpanel);
        return zkpanel;
    }

    public static Component getEditableComponentWithValue(Object value) {
        Component comp = getEditableComponentForJavaType(value.getClass());
        setEditableComponentToValue(comp, value);
        return comp;
    }

    public static void setEditableComponentToValue(Component component, Object value) {
        if (component instanceof InputElement) {
            ((InputElement) component).setRawValue(value);
        } else if (component instanceof Checkbox) {
            ((Checkbox) component).setChecked((Boolean) value);
        } else {
            throw new IllegalArgumentException("unexpected value encountered: " + value.toString());
        }
    }

    public static Component getEditableComponentForJavaType(Class<?> type) {
        return getEditableComponentForJavaType(type, null);
    }

    public static Component getEditableComponentForRenderElement(RenderElement renderElement) {
        Component component = getEditableComponentForPropertyType(renderElement.getPropertyPath());

        if (StringUtils.hasText(renderElement.getFormat())) {
            if (component instanceof Datebox) {
                ((Datebox) component).setFormat(extractFormatForDatebox(renderElement.getFormat()));
            } else if (component instanceof Timebox) {
                ((Timebox) component).setFormat(extractFormatForDatebox(renderElement.getFormat()));
            }
        }

        return component;
    }

    public static String extractFormatForDatebox(String format) {
        if (format == null) return null;
        if ("date".equals(format)) return null;

        if (format.startsWith("date,")) {
            return format.split(",")[1];
        } else {
            return format;
        }
    }


    public static Component getEditableComponentForPropertyType(PathMetadata pathMetadata) {
        Component comp;
        if (pathMetadata.getLastStep().isAnnotatedWith(HtmlHolder.class)) {
            HtmlEditor htmlEditor = new HtmlEditor(pathMetadata);
            htmlEditor.setHflex("true");
            comp = htmlEditor;
        } else if (pathMetadata.getLastStep().isAnnotatedWith(MediaHolder.class) || pathMetadata.getLastStep()
                .isAnnotatedWith(ImageHolder.class)) {
            comp = new MediaBox(pathMetadata);
        } else if (pathMetadata.getLastStep().isAnnotatedWith(ColorHolder.class)) {
            comp = new ColorDropDownBox(pathMetadata);
        } else if (pathMetadata.getLastStep().isClobType()) {
            Textbox textbox = new Textbox();
            textbox.setHflex("true");
            textbox.setRows(10);
            comp = textbox;
        } else if (pathMetadata.getLastStep().isAssociationType()) {
            if (pathMetadata.getLastStep().getAssociatedEntityMetadata().isCached()) {
                comp = new EntityDropdownBox(pathMetadata);
            } else {
                comp = new EntityBox(pathMetadata);
            }
        } else if (pathMetadata.getLastStep().isTimestampType()) {
            comp = new Datebox();
            ((Datebox) comp).setWidth("200px");
            ((Datebox) comp).setFormat("medium+short");
        } else {
            comp = getEditableComponentForJavaType(pathMetadata.getLastStep().getJavaType());
        }

        if (comp instanceof Textbox && !Combobox.class.isInstance(comp) && pathMetadata.getLastStep().getMaxLength()
                > 0) {
            ((Textbox) comp).setMaxlength(pathMetadata.getLastStep().getMaxLength());
            if (pathMetadata.getLastStep().getMaxLength() <= 40) {
                ((Textbox) comp).setHflex("false");
                ((Textbox) comp).setWidth(15 * pathMetadata.getLastStep().getMaxLength() + "px");
            }
        }

        return comp;
    }

    public static Component getEditableComponentForJavaType(Class<?> type, Class<?> subType) {
        Component comp = null;

        if (Integer.class.isAssignableFrom(type)) {
            comp = new Intbox();
        } else if (Long.class.isAssignableFrom(type)) {
            comp = new Longbox();
        } else if (Double.class.isAssignableFrom(type)) {
            comp = new Doublebox();
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            comp = new Decimalbox();
        } else if (Float.class.isAssignableFrom(type)) {
            comp = new Doublebox();
        } else if (Boolean.class.isAssignableFrom(type)) {
            comp = new Checkbox();
        } else if (Timestamp.class.isAssignableFrom(type)) {
            comp = new Datebox();
            ((Datebox) comp).setLocale(CoreUtil.getUserLocale());
            ((Datebox) comp).setFormat("medium+short");
        } else if (Date.class.isAssignableFrom(type)) {
            comp = new Datebox();
            ((Datebox) comp).setLocale(CoreUtil.getUserLocale());
        } else if (Locale.class.isAssignableFrom(type)) {
            comp = getComboLocaleType();
        } else if (Enum.class.isAssignableFrom(type)) {
            comp = getComboForEnumType(type);
        } else if (Class.class.isAssignableFrom(type)) {
            if (Entity.class.isAssignableFrom(subType)) {
                comp = getComboForEntityTypes();
            }
        } else if (String.class.isAssignableFrom(type)) {
            Textbox textbox = new Textbox();
            textbox.setHflex("true");
            comp = textbox;
        }

        if (comp == null) {
            throw new IllegalArgumentException("unexpected java type encountered: " + type.getName());
        }

        return comp;
    }

    private static Component getComboForEntityTypes() {
        Combobox combobox = new Combobox();
        List<EntityMetadata> entities = new ArrayList<EntityMetadata>(ContextUtil.getMRS().getEntityMetadatas());
        Collections.sort(entities, new Comparator<EntityMetadata>() {
            @Override
            public int compare(EntityMetadata o1, EntityMetadata o2) {
                return (o1.getSchema() + o1.getFriendlyName()).compareTo(o2.getSchema() + o2.getFriendlyName());
            }
        });


        for (EntityMetadata entityMetadata : entities) {
            if (!entityMetadata.isAbstract()) {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                sb.append(entityMetadata.getSchema());
                sb.append(") ");
                sb.append(entityMetadata.getFriendlyName());
                Comboitem comboitem = new Comboitem(sb.toString());
                comboitem.setValue(entityMetadata.getEntityType());
                comboitem.setParent(combobox);
            }
        }

        combobox.setReadonly(true);
        combobox.setHflex("true");
        return combobox;
    }

    private static Combobox getComboLocaleType() {
        Combobox localebox = new Combobox();
        localebox.setReadonly(true);
        localebox.setWidth("65%");

        List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());
        Collections.sort(locales, new Comparator<Locale>() {
            private String getDisplayName(Locale locale) {
                return locale.getDisplayName(CoreUtil.getUserLocale());
            }

            @Override
            public int compare(Locale o1, Locale o2) {
                return getDisplayName(o1).compareTo(getDisplayName(o2));
            }
        });


        Comboitem comboitem = new Comboitem("* User locale (" + CoreUtil.getUserLocale().getDisplayName(CoreUtil
                .getUserLocale()) + ")");   //add the user locale
        comboitem.setValue(CoreUtil.getUserLocale());
        comboitem.setParent(localebox);

        comboitem = new Comboitem("** System locale (" + Locale.getDefault().getDisplayName(CoreUtil
                .getUserLocale()) + ")");   //add the system locale
        comboitem.setValue(Locale.getDefault());
        comboitem.setParent(localebox);

        for (Locale locale : locales) {
            comboitem = new Comboitem(locale.getDisplayName(CoreUtil.getUserLocale()));
            comboitem.setValue(locale);
            comboitem.setParent(localebox);
        }

        comboitem = new Comboitem("");   //add the null locale
        comboitem.setValue(null);
        comboitem.setParent(localebox);


        return localebox;
    }


    private static Combobox getComboForColorType() {
        Combobox colorbox = new Combobox();
        colorbox.setReadonly(true);
        colorbox.setWidth("200px");

        Comboitem item;

        item = new Comboitem("White");
        item.setValue("white");
        item.setStyle("background-color:white;");
        item.setParent(colorbox);

        item = new Comboitem("Red");
        item.setValue("red");
        item.setStyle("background-color:red;");
        item.setParent(colorbox);

        item = new Comboitem("Green");
        item.setValue("green");
        item.setStyle("background-color:green;");
        item.setParent(colorbox);

        item = new Comboitem("Yellow");
        item.setValue("yellow");
        item.setStyle("background-color:yellow;");
        item.setParent(colorbox);

        item = new Comboitem("Blue");
        item.setValue("blue");
        item.setStyle("background-color:blue;color:white;");
        item.setParent(colorbox);

        return colorbox;
    }

    private static Combobox getComboForEnumType(Class<?> clazz) {
        Combobox enumbox = new Combobox();
        enumbox.setReadonly(true);
        enumbox.setWidth("65%");

        Map<String, L10nString> map = new HashMap<String, L10nString>();
        for (L10nString lstring : L10nUtil.getLocalizableResources(clazz)) {
            map.put(lstring.getKey(), lstring);
        }

        for (Object constant : clazz.getEnumConstants()) {
            Comboitem comboitem = new Comboitem();
            comboitem.setParent(enumbox);
            comboitem.setValue(constant);

            String label = null;
            if (map.containsKey(constant.toString())) {
                label = map.get(constant.toString()).toString();
            }

            if (label == null) {
                label = constant.toString();
            }

            comboitem.setLabel(label);
        }

        return enumbox;
    }

    public static void addBinding(DataBinder dataBinder, Component component, String beanId, String propertyPath,
                                  Class<? extends TypeConverter> typeConverter) {

        String converterName = null;
        if (typeConverter != null) {
            converterName = typeConverter.getCanonicalName();
        }

        propertyPath = beanId + (StringUtils.hasText(propertyPath) ? "." + propertyPath : "");

        if (component instanceof PropertyBox) {
            dataBinder.addBinding(component, "entity", beanId, (String[]) null, (String[]) null, "load", converterName);
        } else if (component instanceof Html) {
            dataBinder.addBinding(component, "content", propertyPath, (String[]) null, (String[]) null, null,
                    converterName);
        } else if (component instanceof Iframe || component instanceof Image) {
            dataBinder.addBinding(component, "content", propertyPath, (String[]) null, (String[]) null, null,
                    converterName);
        } else if (component instanceof Checkbox) {
            dataBinder.addBinding(component, "checked", propertyPath, (String[]) null, (String[]) null, null,
                    converterName);
        } else if (component instanceof Combobox) {
            final String[] loadWhen = {Events.ON_CHANGE, Events.ON_SELECT};
            final String saveWhen = Events.ON_CHANGE;
            final String access = "both";
            dataBinder.addBinding(component, "selectedItem", propertyPath, loadWhen, saveWhen, access, converterName);
        } else if (component instanceof RawValueBox) {
            final String[] loadWhen = {Events.ON_CHANGE};
            final String saveWhen = Events.ON_CHANGE;
            final String access = "both";
            dataBinder.addBinding(component, "rawValue", propertyPath, loadWhen, saveWhen, access, converterName);
        } else if (component instanceof InputElement) {
            dataBinder.addBinding(component, "value", propertyPath, (String[]) null, (String[]) null, null,
                    converterName);
        } else if (component instanceof Label) {
            dataBinder.addBinding(component, "value", propertyPath, (String[]) null, (String[]) null, null,
                    converterName);
        } else {
            throw new IllegalArgumentException("cannot bind " + component.toString());
        }

        // special case for password encyption
        if (component instanceof PasswordEditor) {
            dataBinder.addBinding(component, "userIdentity", beanId, (String[]) null, (String[]) null, null,
                    converterName);
        }

    }

    public static void addBinding(DataBinder dataBinder, Component component, String beanId, String propertyPath) {

        if (component instanceof Combobox) {
            addBinding(dataBinder, component, beanId, propertyPath, ComboItemConverter.class);
        } else if (component instanceof Iframe) {
            addBinding(dataBinder, component, beanId, propertyPath, MediaConverter.class);
        } else if (component instanceof Image) {
            addBinding(dataBinder, component, beanId, propertyPath, ImageConverter.class);
        } else {
            addBinding(dataBinder, component, beanId, propertyPath, null);
        }
    }

    public static String getDesktopWidthRatio(Integer desktopWidthRatio) {
        final ClientInfoEvent info = ContextUtil.getSessionContext().getAttribute(SessionInfoPanel.ATTRIB_CLIENT_INFO);
        if (info != null) {
            if (desktopWidthRatio != null) {
                return String.valueOf(info.getDesktopWidth() * desktopWidthRatio / 100) + "px";
            }
        }

        return DEFAULT_WIDTH;
    }

    public static String getDesktopHeightRatio(Integer desktopHeightRatio) {
        final ClientInfoEvent info = ContextUtil.getSessionContext().getAttribute(SessionInfoPanel.ATTRIB_CLIENT_INFO);
        if (info != null) {
            if (desktopHeightRatio != null) {
                return String.valueOf(info.getDesktopHeight() * desktopHeightRatio / 100) + "px";
            }
        }

        return DEFAULT_HEIGHT;
    }

    public static void sizeComponent(HtmlBasedComponent comp, Integer desktopWidthRatio, Integer desktopHeightRatio) {
        comp.setWidth(getDesktopWidthRatio(desktopWidthRatio));
        comp.setHeight(getDesktopHeightRatio(desktopHeightRatio));
    }

    public static void hightlightComponent(HtmlBasedComponent component, boolean highlight, String color) {

        if (highlight) {
            component.setStyle("border-style:solid;border-width:2px;border-color:" + color + ";");
        } else {
            component.setStyle("");
        }
    }

    public static Borderlayout buildMultiSelectLayout(Borderlayout outerBorderlayout, boolean readOnly,
                                                      boolean excludeUpDown, EventListener<Event> listener) {
        outerBorderlayout.getChildren().clear();
        outerBorderlayout.setWidth("100%");
        outerBorderlayout.setVflex("true");
        new Center().setParent(outerBorderlayout);
        outerBorderlayout.getCenter().setBorder("none");

        Vbox vbox;
        Toolbarbutton btn;
        if (!excludeUpDown) {
            new East().setParent(outerBorderlayout);
            outerBorderlayout.getEast().setBorder("none");
            outerBorderlayout.getEast().setWidth("110px");
            //outerBorderlayout.getEast().setFlex(false);

            vbox = new Vbox();
            vbox.setParent(outerBorderlayout.getEast());
            vbox.setHflex("true");
            vbox.setVflex("true");
            vbox.setAlign("center");
            vbox.setPack("center");
            vbox.setSpacing("10px");

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/ROUND_UP.png")) {
                btn.setImage("img/ROUND_UP.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_UP.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_UP.toString());
            btn.setParent(vbox);
            btn.setAttribute("up", true);
            btn.addEventListener(Events.ON_CLICK, listener);

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/ROUND_DOWN.png")) {
                btn.setImage("img/ROUND_DOWN.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_DOWN.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_DOWN.toString());
            btn.setParent(vbox);
            btn.setAttribute("down", true);
            btn.addEventListener(Events.ON_CLICK, listener);
        }

        final Borderlayout innerBorderlayout = new Borderlayout();
        innerBorderlayout.setHflex("true");
        innerBorderlayout.setVflex("true");
        innerBorderlayout.setParent(outerBorderlayout.getCenter());

        if (!readOnly) {
            new West().setParent(innerBorderlayout);
            innerBorderlayout.getWest().setWidth("45%");
            //innerBorderlayout.getWest().setFlex(false);
            innerBorderlayout.getWest().setBorder("none");
            new Center().setParent(innerBorderlayout);
            innerBorderlayout.getCenter().setBorder("none");

            vbox = new Vbox();
            vbox.setParent(innerBorderlayout.getCenter());
            vbox.setHflex("true");
            vbox.setVflex("true");
            vbox.setAlign("center");
            vbox.setPack("center");
            vbox.setSpacing("10px");

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/REDO.png")) {
                btn.setImage("img/REDO.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_ADD_ALL.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_ADD_ALL.toString());
            btn.setParent(vbox);
            btn.setAttribute("addall", true);
            btn.addEventListener(Events.ON_CLICK, listener);

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/ROUND_RIGHT.png")) {
                btn.setImage("img/ROUND_RIGHT.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_ADD.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_ADD.toString());
            btn.setParent(vbox);
            btn.setAttribute("add", true);
            btn.addEventListener(Events.ON_CLICK, listener);

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/ROUND_LEFT.png")) {
                btn.setImage("img/ROUND_LEFT.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_REMOVE.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_REMOVE.toString());
            btn.setParent(vbox);
            btn.setAttribute("remove", true);
            btn.addEventListener(Events.ON_CLICK, listener);

            btn = new Toolbarbutton();
            if (ContextUtil.resourceExists("img/UNDO.png")) {
                btn.setImage("img/UNDO.png");
            } else {
                btn.setLabel(AbstractMultiSelectPanel.L10N_BUTTON_REMOVE_ALL.toString());
            }
            btn.setTooltiptext(AbstractMultiSelectPanel.L10N_BUTTON_REMOVE_ALL.toString());
            btn.setParent(vbox);
            btn.setAttribute("removeall", true);
            btn.addEventListener(Events.ON_CLICK, listener);
        }

        new East().setParent(innerBorderlayout);
        if (!readOnly) {
            innerBorderlayout.getEast().setWidth("45%");
        } else {
            innerBorderlayout.getEast().setWidth("100%");
        }
        //innerBorderlayout.getEast().setFlex(readOnly);
        innerBorderlayout.getEast().setBorder("none");

        return innerBorderlayout;
    }

    @SuppressWarnings("rawtypes")
    public static Entity getLookupSelectionIfUnique(Command command, Class<? extends LookupCommandDecorator>
            listenerType) {
        if (command == null) return null;

        Entity entity = null;
        boolean found = false;
        for (MessageAware listener : command.getListeners()) {
            if (listenerType.isInstance(listener)) {
                if (!found) {
                    entity = ((LookupCommandDecorator) listener).getLookupSelection();
                    found = true;
                } else {
                    throw new IllegalStateException("more than one command decorators were found for command " +
                            command.getId());
                }
            }
        }

        return entity;
    }

    @SuppressWarnings("rawtypes")
    public static List<LookupCommandDecorator> getLookupDecorators(Command command) {
        if (command == null) return Collections.emptyList();

        List<LookupCommandDecorator> decorators = new ArrayList<LookupCommandDecorator>();
        for (MessageAware listener : command.getListeners()) {
            if (LookupCommandDecorator.class.isInstance(listener)) {
                decorators.add((LookupCommandDecorator) listener);
            }
        }

        return decorators;
    }

    public static void displayMessage(String message, boolean error, Component owner) {
        Clients.scrollIntoView(owner);
        Clients.showNotification(message, error ? Clients.NOTIFICATION_TYPE_ERROR : Clients.NOTIFICATION_TYPE_INFO,
                owner, "before_center", 5000, true);


/*
        Clients.scrollIntoView(owner);
        Popup popup = new Popup();
        popup.setPage(owner.getPage());
        popup.setWidth("400px");
        popup.setHeight("70px");
        Vbox vbox = new Vbox();
        vbox.setParent(popup);
        vbox.setVflex("true");
        vbox.setHflex("true");
        vbox.setPack("center");
        vbox.setAlign("center");
        Label label = new Label(message);
        label.setParent(vbox);

        if (error) {
            popup.setStyle("background-color:#FFAEAE;");
            vbox.setStyle("background-color:#FFAEAE;");
            label.setStyle("background-color:#FFAEAE;");
        } else {
            popup.setStyle("background-color:#B0E57C;");
            vbox.setStyle("background-color:#B0E57C;");
            label.setStyle("background-color:#B0E57C;");
        }

        popup.open(owner, "overlap");
*/

    }

    public static org.web4thejob.web.panel.Panel getOwningPanelOfComponent(Component component) {

        Component parent = component;
        while (parent != null) {
            if (parent.getAttribute(OWNING_PANEL) instanceof org.web4thejob.web.panel.Panel) {
                return (org.web4thejob.web.panel.Panel) parent.getAttribute(OWNING_PANEL);
            }
            parent = parent.getParent();
        }

        return null;
    }

    public static boolean isDialogContained(Component component) {

        while (component != null) {
            if (component.hasAttribute(Dialog.ATTRIB_DIALOG)) {
                return true;
            }
            component = component.getParent();
        }

        return false;
    }

    public static void setInactive(HtmlBasedComponent component, boolean inactive) {
        String style = component.getStyle();
        if (!inactive) {
            style = ZkUtil.replaceStyleElement(style, "color", "default");
            style = ZkUtil.replaceStyleElement(style, "text-decoration", "none");
        } else {
            style = ZkUtil.replaceStyleElement(style, "color", "red");
            style = ZkUtil.replaceStyleElement(style, "text-decoration", "line-through");
        }
        component.setStyle(style);
    }

    public static String replaceStyleElement(String style, String key, String value) {
        final StringBuilder sb = new StringBuilder();

        if (style == null) {
            style = "";
        } else {
            style = style.trim();
        }

        if (!style.trim().endsWith(";")) {
            style += ";";
        }

        boolean keyFound = false;
        final StringTokenizer st = new StringTokenizer(style, ";");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();

            final String[] pair = token.split(":");
            final String[] newPair = new String[]{pair[0], ""};

            if (pair.length > 1) {
                newPair[1] = pair[1];
            }

            if (newPair[0].trim().equals(key)) {
                newPair[0] = key;
                newPair[1] = value;
                keyFound = true;
            }

            sb.append(newPair[0].trim()).append(":").append(newPair[1].trim()).append(";");
        }

        if (!keyFound) {
            sb.append(key).append(":").append(value).append(";");
        }

        return sb.toString();
    }

    public static void downloadCsv(File file) {
        if (file != null) {
            try {
                Filedownload.save(new InputStreamReader(new FileInputStream(file),
                        CoreUtil.getParameterValue(Category.PRINTER, Key.CHARSET, String.class, "UTF-8")) {
                }, "text/csv", "export.csv");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("printing failed");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("printing failed");
            }
        }
    }

    public static void setCommandDirty(Command command, boolean dirty, LabelImageElement element) {
        if (command == null) return;

        String image = CoreUtil.getCommandImage(command.getId(), "", dirty);
        if (!image.equals(element.getImage())) {
            if (ContextUtil.resourceExists(image)) {
                element.setImage(image);
            } else {
                element.setImage(null);
                element.setLabel(command.getName());
            }

            if (dirty) {
                boolean showNotification = !command.hasArg(CommandDecorator.ATTRIB_DIRTY_NOTIFIED);

                if (showNotification) {
                    command.setArg(CommandDecorator.ATTRIB_DIRTY_NOTIFIED, true);
                    Clients.showNotification(L10nMessages.L10N_UNSAVED_CHANGES.toString(),
                            Clients.NOTIFICATION_TYPE_WARNING, element,
                            "after_center", 3000, true);

                    element.setTooltiptext(command.getName() + ": " + L10nMessages.L10N_UNSAVED_CHANGES.toString());
                }

            } else {
                command.removeArg(CommandDecorator.ATTRIB_DIRTY_NOTIFIED);
                element.setTooltiptext(command.getName());
            }
        }

    }
}
