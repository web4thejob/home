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
package org.web4thejob.web.zbox.ckeb;

import org.web4thejob.context.ContextUtil;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;

/**
 * @author Veniamin Isaias
 * @since 3.5.2
 */
public class CKeditorBox extends AbstractComponent {
    private static final String DEFAULT_CUSTOM_CONFIG = "/js/ckeditor_config.js";
    private static final String ON_FLUSH = "onFlush";
    private String value = "";
    private String configurationPath;
    private boolean flushed;
    private boolean focused;

    static {
        addClientEvent(CKeditorBox.class, ON_FLUSH, 0);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null)
            value = "";
        if (!value.equals(this.value)) {
            this.value = value;
            smartUpdate("value", value);
        }
    }

    public void flush() {
        flushed = !flushed;
        smartUpdate("flush", flushed);
    }

    public void focus() {
        focused = !focused;
        smartUpdate("focus", focused);
    }

    public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
        final String cmd = request.getCommand();

        if (cmd.equals(ON_FLUSH)) {
            value = request.getData().get("value").toString();
            Events.postEvent(Events.ON_CHANGE, this, value);
        } else
            super.service(request, everError);
    }

    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
            throws java.io.IOException {
        super.renderProperties(renderer);

        render(renderer, "value", value);
        render(renderer, "flush", flushed);
        render(renderer, "focus", focused);

        if (!Strings.isBlank(configurationPath))
            render(renderer, "configPath", getEncodedURL(configurationPath));

    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
        if (!Objects.equals(this.configurationPath, configurationPath)) {
            this.configurationPath = configurationPath;
            smartUpdate("configPath", getEncodedURL(this.configurationPath));
        }
    }

    private String getEncodedURL(String path) {
        final Desktop dt = getDesktop();
        return dt != null ? dt.getExecution().encodeURL(path) : "";
    }

    public static CKeditorBox newInstance(Component parent, String value) {
        CKeditorBox editor = new CKeditorBox();
        editor.setParent(parent);
        editor.setValue(value);
        if (ContextUtil.resourceExists(DEFAULT_CUSTOM_CONFIG)) {
            editor.setConfigurationPath(DEFAULT_CUSTOM_CONFIG);
        }
        return editor;
    }
}
