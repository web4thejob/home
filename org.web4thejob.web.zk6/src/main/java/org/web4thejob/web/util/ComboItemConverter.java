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

package org.web4thejob.web.util;

import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class ComboItemConverter implements TypeConverter {
    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface TypeConverter ---------------------

    @Override
    public Object coerceToUi(Object val, Component comp) {
        Combobox combobox = (Combobox) comp;
        boolean found = false;

        if (val == null) {
            combobox.setSelectedIndex(-1);
            return IGNORE;
        }

        if (combobox.getModel() != null) {
            throw new IllegalArgumentException("cannot work with a model");
        }

        for (Object item : combobox.getItems()) {
            if (val.equals(((Comboitem) item).getValue())) {
                combobox.setSelectedItem((Comboitem) item);
                found = true;
                break;
            }
        }

        if (!found) {
            Comboitem comboitem = new Comboitem();
            comboitem.setValue(val);
            comboitem.setLabel(val.toString());
            comboitem.setParent(combobox);
            combobox.setSelectedItem(comboitem);
        }

        if (StringUtils.hasText(combobox.getSelectedItem().getStyle())) {
            combobox.setStyle(combobox.getSelectedItem().getStyle());
        }


        return IGNORE;
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        Combobox combobox = (Combobox) comp;
        if (combobox.getModel() != null) {
            throw new IllegalArgumentException("cannot work with a model");
        }

        if (val != null) {
            return ((Comboitem) val).getValue();
        }

        return null;

    }
}
