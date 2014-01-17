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

package org.web4thejob.orm.serial;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class DateFormulaConverter implements Converter {

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String value = reader.getValue();
        //supports @date OR @date+#days OR @date-#days OR @date00
        if (value.startsWith("@date00")) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.AM_PM, Calendar.AM);

            if (value.contains("+")) {
                final int days = Integer.parseInt(value.split("\\+")[1]);
                cal.add(Calendar.DAY_OF_MONTH, days);
            } else if (value.contains("-")) {
                final int days = Integer.parseInt(value.split("\\-")[1]);
                cal.add(Calendar.DAY_OF_MONTH, -days);
            }

            return cal.getTime();
        } else if (value.startsWith("@date")) {
            Date date = Calendar.getInstance().getTime();
            if (value.contains("+")) {
                final int days = Integer.parseInt(value.split("\\+")[1]);
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, days);
                date = cal.getTime();
            } else if (value.contains("-")) {
                final int days = Integer.parseInt(value.split("\\-")[1]);
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -days);
                date = cal.getTime();
            }
            return date;
        }
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return DateFormula.class.isAssignableFrom(type);
    }
}
