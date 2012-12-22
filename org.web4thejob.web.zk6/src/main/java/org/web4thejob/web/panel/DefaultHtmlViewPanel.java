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
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Key;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.base.AbstractMutablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Html;
import org.zkoss.zul.Textbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultHtmlViewPanel extends AbstractMutablePanel implements HtmlViewPanel {
    // ------------------------------ FIELDS ------------------------------
    private Component comp;

// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultHtmlViewPanel() {
        this(MutableMode.READONLY);
    }

    public DefaultHtmlViewPanel(MutableMode mutableMode) {
        super(mutableMode);
    }

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

    private void arrangeForMutableMode() {
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
        Component editor = getCKeditor(pathMetadata);
        if (editor == null) {
            editor = new Textbox();
            ZkUtil.setParentOfChild((Component) base, editor);
            ((Textbox) editor).setWidth("100%");
            ((Textbox) editor).setVflex("true");
            ((Textbox) editor).setMultiline(true);
            ZkUtil.addBinding(dataBinder, editor, DEFAULT_BEAN_ID, pathMetadata.getPath());
        }

        return editor;
    }


    private CKeditor getCKeditor(PathMetadata pathMetadata) {
        try {
            CKeditor editor = new CKeditor();
            editor.setWidth(ZkUtil.getDesktopWidthRatio(65));
            editor.setHeight(ZkUtil.getDesktopHeightRatio(45));
            editor.setFilebrowserImageBrowseUrl(CoreUtil.getParameterValue(Category.LOCATION_PARAM,
                    Key.IMAGES_REPOSITORY,
                    String.class, null));
            editor.setWidth("100%");
            editor.setVflex("true");
            ZkUtil.setParentOfChild((Component) base, editor);

            final String[] loadWhen = {Events.ON_CHANGE};
            final String saveWhen = Events.ON_CHANGE;
            final String access = "both";
            dataBinder.addBinding(editor, "value", DEFAULT_BEAN_ID + "." + pathMetadata.getPath(), loadWhen,
                    saveWhen, access,
                    null);

            return editor;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return HtmlViewPanel.class;
    }
}
