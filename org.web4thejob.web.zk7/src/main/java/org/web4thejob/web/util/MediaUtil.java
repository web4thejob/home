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

package org.web4thejob.web.util;

import org.apache.commons.lang.ArrayUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.web.panel.BindCapable;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.panel.TargetType;
import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.zk.ui.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;

import static org.imgscalr.Scalr.*;

/**
 * @author Veniamin Isaias
 * @since 3.1.0
 */

public class MediaUtil {
    public static final int MEDIA_TYPE_SIGNATURE = 10;
    public static final int THUMBNAIL_HEIGHT = 32;


    public static byte[] signMedia(String name, byte[] media) {
//        not working in java 5
//        byte[] mediaFormat = Arrays.copyOf(StringUtils.getFilenameExtension(name).toLowerCase().getBytes(),
//                MEDIA_TYPE_SIGNATURE);

        byte[] mediaFormat = ArrayUtils.clone(ArrayUtils.subarray(media, 0, MEDIA_TYPE_SIGNATURE));

        return ArrayUtils.addAll(mediaFormat, media);
    }

    public static String getMediaFormat(byte[] media) {
        int index = 0;
        for (byte b : ArrayUtils.subarray(media, 0, MEDIA_TYPE_SIGNATURE)) {
            if (b == 0) break;
            index += 1;
        }

        if (index > 0) {
            return new String(ArrayUtils.subarray(media, 0, index)).toLowerCase();
        }

        return "";
    }

    public static byte[] getMediaBytes(byte[] media) {
        return ArrayUtils.subarray(media, MEDIA_TYPE_SIGNATURE, media.length);
    }

    public static String getMediaDescription(byte[] media) {
        String mediaFormat = getMediaFormat(media);

        if (isImage(mediaFormat)) {
            Image image = getImage(media);
            mediaFormat = new StringBuilder().append(mediaFormat).append(" ").append(image.getWidth()).append("x")
                    .append
                            (image.getHeight()).toString();
        }

        return new StringBuilder().append("media | ").append(mediaFormat).append(" (").append(MessageFormat.format
                ("{0," +
                        "number,#,##0}", media.length - MEDIA_TYPE_SIGNATURE)).append(" bytes)")
                .toString();
    }

    public static Image getImage(byte[] bytes) {
        try {
            return new AImage(getMediaFormat(bytes), getMediaBytes(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage createThumbnail(byte[] bytes) {
        Image image = getImage(bytes);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image.toImageIcon().getImage(), 0, 0, null);
        g.dispose();
        return createThumbnail(bufferedImage);
    }

    public static BufferedImage createThumbnail(BufferedImage img) {
        if (img.getHeight() <= THUMBNAIL_HEIGHT) return img;

        BufferedImage resizedImage = resize(img, Method.AUTOMATIC, Mode.FIT_TO_HEIGHT, THUMBNAIL_HEIGHT);
        img.flush();

        return resizedImage;
    }

    public static boolean isImage(String mediaFormat) {
        mediaFormat = mediaFormat.toLowerCase();
        return "png".equals(mediaFormat) || "jpg".equals(mediaFormat) || "gif".equals(mediaFormat);
    }

    public static String buildName(Component comp, String mediaFormat) {
        return MediaUtil.buildName(ZkUtil.getOwningPanelOfComponent(comp), mediaFormat);
    }

    public static String buildName(Panel panel, String mediaFormat) {
        StringBuilder sb = new StringBuilder();
        if (panel instanceof TargetType) {
            EntityMetadata entityMetadata = ContextUtil.getMRS().getEntityMetadata(((TargetType) panel).getTargetType
                    ());
            sb.append(entityMetadata.getFriendlyName());
            if (panel instanceof BindCapable) {
                Entity entity = ((BindCapable) panel).getTargetEntity();
                if (entity != null && !entity.isNewInstance()) {
                    sb.append("-");
                    sb.append(entity.getIdentifierValue());
                }
            }
        } else {
            sb.append("Media");
        }

        sb.append("-");
        sb.append(Calendar.getInstance().getTimeInMillis());
        sb.append(".");
        sb.append(mediaFormat);

        return sb.toString();
    }

}