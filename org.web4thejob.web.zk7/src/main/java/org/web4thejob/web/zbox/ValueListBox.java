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

package org.web4thejob.web.zbox;

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.web.dialog.ValueListDialog;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class ValueListBox extends AbstractBox<List<?>> implements MessageListener {
    private static final long serialVersionUID = 1L;
    private final PathMetadata pathMetadata;
    private List<?> values;

    public ValueListBox(PathMetadata pathMetadata) {
        this.pathMetadata = pathMetadata;
        marshallEmptyValue();
    }


    @Override
    protected void marshallEmptyValue() {
        values = null;
        super.marshallEmptyValue();
        setTooltiptext(null);
    }

    @Override
    protected void marshallToString(List<?> value) {
        values = value;
        super.marshallToString(values);
    }

    @Override
    protected List<?> unmarshallToRawValue() {
        return values;
    }

    @Override
    protected void onEdit() {
        ValueListDialog dialog = ContextUtil.getDefaultDialog(ValueListDialog.class, pathMetadata, values);
        dialog.show(this);
    }

    @Override
    public void processMessage(Message message) {
        if (message.getId() == MessageEnum.AFFIRMATIVE_RESPONSE) {
            setRawValue(message.getArg(MessageArgEnum.ARG_ITEM, List.class));
        }
    }

    protected boolean isEmpty() {
        return values == null;
    }

}
