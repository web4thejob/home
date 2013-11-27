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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.zbox.ckeb.CKeditorBox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;

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

    private CKeditorBox editor;

    protected DefaultHtmlDialog() {
        this(null);
    }

    protected DefaultHtmlDialog(String content) {
        editor = getCKeditor(content);
        editor.addEventListener(Events.ON_CHANGE, this);
        dialogContent.getPanelchildren().setStyle("overflow: auto");
    }

    private CKeditorBox getCKeditor(String content) {
        try {
            CKeditorBox editor = new CKeditorBox();
            editor.setParent(dialogContent.getPanelchildren());
            editor.setValue(content);
/*
            editor.setFilebrowserImageBrowseUrl(CoreUtil.getParameterValue(Category.LOCATION_PARAM,
                    Key.IMAGES_REPOSITORY,
                    String.class, null));
            editor.setHflex("true");
            editor.setVflex("true");
            editor.setCustomConfigurationsPath("/js/ckeditor_config.js");
*/

            return editor;
        } catch (Exception e) {
            return null;
        }
    }

    private String getValue() {
        return editor.getValue();
    }


    @Override
    protected boolean isOKReady() {
        return StringUtils.hasText(getValue());
    }

    @Override
    protected void prepareForOK() {
        editor.flush();
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

    @Override
    protected void doCancel() {
        if (!btnOK.isDisabled()) {
            Messagebox.show(DefaultEntityPersisterDialog.L10N_MESSAGE_IGNORE_CHANGES.toString(),
                    L10nMessages.L10N_MSGBOX_TITLE_QUESTION.toString
                            (), new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL}, null,
                    Messagebox.QUESTION, Messagebox.Button.CANCEL,
                    new EventListener<Messagebox.ClickEvent>() {
                        @Override
                        public void onEvent(Messagebox.ClickEvent event) throws Exception {
                            if (Messagebox.Button.OK == event.getButton()) {
                                DefaultHtmlDialog.super.doCancel();
                            }
                        }
                    });
        } else {
            super.doCancel();
        }
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals(Events.ON_CHANGE) && event.getTarget().equals(editor)) {
            btnOK.setDisabled(false);
            editor.focus();
        } else {
            super.onEvent(event);
        }
    }

    @Override
    protected void onBeforeShow() {
        btnOK.setDisabled(true);
        super.onBeforeShow();
    }
}
