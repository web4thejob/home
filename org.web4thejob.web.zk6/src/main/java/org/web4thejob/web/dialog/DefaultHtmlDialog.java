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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Key;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.util.ZkUtil;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.2.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultHtmlDialog extends AbstractDialog implements HtmlDialog {
    public static final L10nString L10N_DIALOG_TITLE = new L10nString(DefaultHtmlDialog.class, "dialog_title",
            "HTML Editor");

    private Component editor;

    protected DefaultHtmlDialog() {
        editor = getHtmlEditor(null);
    }

    protected DefaultHtmlDialog(String content) {
        editor = getHtmlEditor(content);
    }

    private org.zkoss.zk.ui.Component getHtmlEditor(String content) {
        org.zkoss.zk.ui.Component editor = getCKeditor(content);
        if (editor == null) {
            editor = new Textbox();
            editor.setParent(dialogContent.getPanelchildren());
            ((Textbox) editor).setHflex("true");
            ((Textbox) editor).setVflex("true");
            ((Textbox) editor).setMultiline(true);
            ((Textbox) editor).setValue(content);
        }

        dialogContent.getPanelchildren().setStyle("overflow: auto");
        return editor;
    }


    private CKeditor getCKeditor(String content) {
        try {
            CKeditor editor = new CKeditor();
            editor.setParent(dialogContent.getPanelchildren());
            editor.setWidth(ZkUtil.getDesktopWidthRatio(65));
            editor.setHeight(ZkUtil.getDesktopHeightRatio(45));
            editor.setValue(content);
            editor.setFilebrowserImageBrowseUrl(CoreUtil.getParameterValue(Category.LOCATION, Key.IMAGES_REPOSITORY,
                    String.class, null));
            editor.setHflex("true");
            editor.setVflex("true");

            return editor;
        } catch (Exception e) {
            return null;
        }
    }

    private String getValue() {
        if (editor instanceof Textbox) {
            return ((Textbox) editor).getValue();
        } else if (editor instanceof CKeditor) {
            return ((CKeditor) editor).getValue();
        }
        return null;
    }


    @Override
    protected boolean isOKReady() {
        return StringUtils.hasText(getValue());
    }

    @Override
    protected Message getOKMessage() {
        Map<MessageArgEnum, Object> args = new HashMap<MessageArgEnum, Object>(1);
        args.put(MessageArgEnum.ARG_ITEM, getValue());
        return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, args);
    }

    @Override
    protected String prepareTitle() {
        return L10N_DIALOG_TITLE.toString();
    }
}
