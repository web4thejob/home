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

import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

/**
 * @author Veniamin Isaias
 * @since 3.1.0
 */
public class MediaConverter implements TypeConverter {

    @Override
    public Object coerceToUi(Object val, Component comp) {
        if (val == null) return null;

        String mediaFormat = MediaUtil.getMediaFormat((byte[]) val);

        Media media = new AMedia(MediaUtil.buildName(comp, mediaFormat), mediaFormat.toLowerCase(), null,
                MediaUtil.getMediaBytes((byte[]) val));

        return media;
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        if (val == null) return null;

        return ((Media) val).getByteData();
    }


}
