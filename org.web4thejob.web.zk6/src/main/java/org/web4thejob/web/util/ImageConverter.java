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

import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.image.Images;
import org.zkoss.zk.ui.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 3.1.0
 */
public class ImageConverter extends MediaConverter {

    @Override
    public Object coerceToUi(Object val, Component comp) {
        if (val == null) {
            try {
                return Images.encode("blank.png", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
            } catch (IOException e) {
                e.printStackTrace();
                return IGNORE;
            }
        }

        String mediaFormat = MediaUtil.getMediaFormat((byte[]) val);
        Image media;
        try {
            media = new AImage(MediaUtil.buildName(comp, mediaFormat), MediaUtil.getMediaBytes((byte[]) val));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return media;
    }

    @Override
    public Object coerceToBean(Object val, Component comp) {
        if (val == null) return null;

        return ((Image) val).getByteData();
    }

}
