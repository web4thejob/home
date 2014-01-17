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

package org.web4thejob.web.zbox;

import org.springframework.util.StringUtils;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.annotation.ImageHolder;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.web.util.MediaUtil;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Fileupload;

/**
 * @author Veniamin Isaias
 * @since 3.1.0
 */

public class MediaBox extends AbstractBox<byte[]> {
    private static final long serialVersionUID = 1L;
    private final PathMetadata pathMetadata;
    private final boolean imageHolder;
    private byte[] content;
    private String name;

    public MediaBox(PathMetadata pathMetadata) {
        super.marshallEmptyValue();
        this.pathMetadata = pathMetadata;
        this.imageHolder = pathMetadata.getLastStep().isAnnotatedWith(ImageHolder.class);
        addEventListener(Events.ON_UPLOAD, this);
    }

    @Override
    protected PropertyBox getValueBox() {
        PropertyBox propertyBox = new PropertyBox(pathMetadata);
        propertyBox.setParent(this);
        return propertyBox;
    }

    @Override
    protected void onEdit() {
        Executions.getCurrent().getDesktop().setAttribute("org.zkoss.zul.Fileupload.target", this);
        Fileupload.get(pathMetadata.getLastStep().getMaxLength(), false);
    }

    @Override
    protected void marshallEmptyValue() {
        super.marshallEmptyValue();
        content = null;
    }

    @Override
    protected void marshallToString(byte[] value) {
        content = value;
        super.marshallToString(value);
    }

    @Override
    protected byte[] unmarshallToRawValue() {
        return content;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof UploadEvent) {
            Media media = ((UploadEvent) event).getMedia();
            if (media == null) {
                ZkUtil.displayMessage(L10nMessages.L10N_NOTHING_SELECTED_ERROR.toString(), true, this);
                return;
            }

            name = media.getName();
            if (imageHolder && !MediaUtil.isImage(StringUtils.getFilenameExtension(name))) {
                ZkUtil.displayMessage(L10nMessages.L10N_INVALID_IMAGE_FORMAT_ERROR.toString(), true, this);
            } else {
                if (media.isBinary()) {
                    setRawValue(MediaUtil.signMedia(name, media.getByteData()));
                } else {
                    setRawValue(MediaUtil.signMedia(name, media.getStringData().getBytes()));
                }

            }

            return;
        }
        super.onEvent(event);
    }
}
