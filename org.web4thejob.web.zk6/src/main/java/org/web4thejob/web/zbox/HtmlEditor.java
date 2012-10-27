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

package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.web.dialog.HtmlDialog;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * @author Veniamin Isaias
 * @since 1.2.0
 */

public class HtmlEditor extends AbstractBox<String> implements MessageListener {
    private static final long serialVersionUID = 1L;
    private final PathMetadata pathMetadata;
    private String htmlContent;


    public HtmlEditor(PathMetadata pathMetadata) {
        this.pathMetadata = pathMetadata;
        marshallEmptyValue();
    }

    public HtmlEditor() {
        this(null);
    }

    @Override
    protected void onEdit() {
        HtmlDialog dialog = ContextUtil.getDefaultDialog(HtmlDialog.class, getRawValue());
        dialog.show(this);
    }

    @Override
    protected void marshallEmptyValue() {
        htmlContent = "";
        super.marshallEmptyValue();
    }

    @Override
    protected void marshallToString(String value) {
        htmlContent = value;
        super.marshallToString(value);

        if (this.pathMetadata != null && _valueBox instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) _valueBox).setStyle(pathMetadata.getLastStep().getStyle());
        }

    }

    @Override
    protected String unmarshallToRawValue() {
        return htmlContent;
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.AFFIRMATIVE_RESPONSE) {
            setRawValue(message.getArg(MessageArgEnum.ARG_ITEM, String.class));
        }
    }
}
