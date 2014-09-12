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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.AbstractMutablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.web4thejob.web.zbox.ckeb.CKeditorBox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Html;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultHtmlViewPanel extends AbstractMutablePanel implements HtmlViewPanel {
    public DefaultHtmlViewPanel() {
        this(MutableMode.READONLY);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultHtmlViewPanel(MutableMode mutableMode) {
        super(mutableMode);
    }

    // ------------------------------ FIELDS ------------------------------
    private Component comp;

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.HTML_PROPERTY, null);
        registerSetting(SettingEnum.PANEL_STYLE, null);
        registerSetting(SettingEnum.PERSISTED_QUERY_DIALOG, null);
        registerSetting(SettingEnum.PERSISTED_QUERY_NAME, null);
        registerSetting(SettingEnum.RUN_QUERY_ON_STARTUP, false);
    }

    @Override
    protected void afterSettingsSet() {
        super.afterSettingsSet();
        arrangeForMutableMode();
    }

    protected void arrangeForMutableMode() {
        if (comp != null) {
            comp.detach();
            comp = null;
        }

        if (StringUtils.hasText(getSettingValue(SettingEnum.HTML_PROPERTY, ""))) {
            PathMetadata pathMetadata = ContextUtil.getMRS().getPropertyPath(getTargetType(),
                    new Path(getSettingValue(SettingEnum.HTML_PROPERTY, "")));
            dataBinder = new DataBinder();

            if (getMutableMode() == MutableMode.READONLY) {
                Html html = new Html();
                ZkUtil.setParentOfChild((Component) base, html);
                ((org.zkoss.zul.Panel) base).setBorder(true);
                ((org.zkoss.zul.Panel) base).getPanelchildren().setStyle("overflow: auto;");
                html.setZclass("z-label");
                html.setWidth("100%");
                html.setVflex("true");
                comp = html;
                ZkUtil.addBinding(dataBinder, comp, DEFAULT_BEAN_ID, pathMetadata.getPath());
            } else {
                comp = getHtmlEditor(pathMetadata);
            }

            if (comp instanceof HtmlBasedComponent && getSettingValue(SettingEnum.PANEL_STYLE, null) != null) {
                //e.g white-space:pre-wrap;
                ((HtmlBasedComponent) comp).setStyle(getSettingValue(SettingEnum.PANEL_STYLE, ""));
            }
            comp.setAttribute(ATTRIB_PATH_META, pathMetadata);
        }
    }

    @Override
    protected void arrangeForTargetType() {
        super.arrangeForTargetType();
        arrangeForMutableMode();
    }

    private Component getHtmlEditor(PathMetadata pathMetadata) {
        final CKeditorBox editor = getCKeditor(pathMetadata);
        editor.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                editor.focus();
            }
        });
        return editor;
    }


    private CKeditorBox getCKeditor(PathMetadata pathMetadata) {
        CKeditorBox editor = CKeditorBox.newInstance(((org.zkoss.zul.Panel) base).getPanelchildren(), "");
        final String[] loadWhen = {Events.ON_CHANGE};
        final String saveWhen = Events.ON_CHANGE;
        final String access = "both";
        dataBinder.addBinding(editor, "value", DEFAULT_BEAN_ID + "." + pathMetadata.getPath(), loadWhen,
                saveWhen, access, null);

        return editor;
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return HtmlViewPanel.class;
    }

    @Override
    public void beforePersist() {
        super.beforePersist();
        ((CKeditorBox) comp).flush();
    }
}
