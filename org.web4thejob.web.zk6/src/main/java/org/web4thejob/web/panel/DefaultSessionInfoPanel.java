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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.base.zk.AbstractZkContentPanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zul.*;

import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultSessionInfoPanel extends AbstractZkContentPanel implements SessionInfoPanel {
// ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_COLUMN_ATTRIBUTE = new L10nString(DefaultSessionInfoPanel
            .class, "column_attribute", "Attribute");
    public static final L10nString L10N_COLUMN_VALUE = new L10nString(DefaultSessionInfoPanel.class, "column_value",
            "Value");
    public static final L10nString L10N_LABEL_USER_LOCALE = new L10nString(DefaultSessionInfoPanel.class,
            "label_user_locale", "User locale");
    public static final L10nString L10N_LABEL_SERVER_LOCALE = new L10nString(DefaultSessionInfoPanel.class,
            "label_server_locale", "Server locale");
    public static final L10nString L10N_LABEL_SERVER_CHARSET = new L10nString(DefaultSessionInfoPanel.class,
            "label_server_charset", "Server charset");
    public static final L10nString L10N_LABEL_REMOTE_ADDRESS = new L10nString(DefaultSessionInfoPanel.class,
            "label_remote_address", "Remote Address");
    public static final L10nString L10N_LABEL_LOCAL_ADDRESS = new L10nString(DefaultSessionInfoPanel.class,
            "label_local_address", "Local Address");
    public static final L10nString L10N_LABEL_CLIENT_TYPE = new L10nString(DefaultSessionInfoPanel.class,
            "label_client_type", "Client Type");
    public static final L10nString L10N_LABEL_DEVICE_TYPE = new L10nString(DefaultSessionInfoPanel.class,
            "label_device_type", "Device Type");
    public static final L10nString L10N_LABEL_SESSION_TIMEOUT = new L10nString(DefaultSessionInfoPanel.class,
            "label_session_timeout", "Session Timeout");
    public static final L10nString L10N_LABEL_SCREEN_RESOLUTION = new L10nString(DefaultSessionInfoPanel.class,
            "label_screen_resolution", "Screen Resolution");
    public static final L10nString L10N_LABEL_COLOR_DEPTH = new L10nString(DefaultSessionInfoPanel.class,
            "label_color_depth", "Color Depth");
    public static final L10nString L10N_LABEL_SESSION_CREATE_TIME = new L10nString(DefaultSessionInfoPanel.class,
            "label_session_create_time", "Session create time");
    public static final L10nString L10N_LABEL_SESSION_ACCESSED_TIME = new L10nString(DefaultSessionInfoPanel.class,
            "label_session_accessed_time", "Session accessed time");

    private final Grid grid = new Grid();

// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultSessionInfoPanel() {
        ZkUtil.setParentOfChild((Component) base, grid);
//        grid.setWidth("100%");
        grid.setVflex("true");
        grid.setSpan(true);
        new Columns().setParent(grid);
        new Rows().setParent(grid);

        final Column col1 = new Column(L10N_COLUMN_ATTRIBUTE.toString());
        col1.setParent(grid.getColumns());
        col1.setWidth("30%");

        final Column col2 = new Column(L10N_COLUMN_VALUE.toString());
        col2.setParent(grid.getColumns());

        prepareContent();
    }

    private void prepareContent() {
        grid.getRows().getChildren().clear();

        Row row = new Row();
        row.setParent(grid.getRows());
        Label label = new Label(L10N_LABEL_USER_LOCALE.toString());
        label.setParent(row);
        label = new Label(CoreUtil.getUserLocale().toString());
        label.setParent(row);

        if (ContextUtil.getSessionContext().getSecurityContext().isAdministrator()) {
            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SERVER_LOCALE.toString());
            label.setParent(row);
            label = new Label(Locale.getDefault().toString());
            label.setParent(row);

            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SERVER_CHARSET.toString());
            label.setParent(row);
            label = new Label(Charset.defaultCharset().toString());
            label.setParent(row);
        }

        row = new Row();
        row.setParent(grid.getRows());
        label = new Label(L10N_LABEL_REMOTE_ADDRESS.toString());
        label.setParent(row);
        label = new Label(Executions.getCurrent().getServerName() + ":" + Executions.getCurrent().getServerPort());
        label.setParent(row);

        row = new Row();
        row.setParent(grid.getRows());
        label = new Label(L10N_LABEL_LOCAL_ADDRESS.toString());
        label.setParent(row);
        label = new Label(Executions.getCurrent().getLocalAddr() + ":" + Executions.getCurrent().getLocalPort());
        label.setParent(row);

        row = new Row();
        row.setParent(grid.getRows());
        label = new Label(L10N_LABEL_CLIENT_TYPE.toString());
        label.setParent(row);
        label = new Label(Executions.getCurrent().getUserAgent());
        label.setParent(row);

        row = new Row();
        row.setParent(grid.getRows());
        label = new Label(L10N_LABEL_DEVICE_TYPE.toString());
        label.setParent(row);
        label = new Label(Executions.getCurrent().getSession().getDeviceType());
        label.setParent(row);

        final ClientInfoEvent info = ContextUtil.getSessionContext().getAttribute(ATTRIB_CLIENT_INFO);
        if (info != null) {
            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SCREEN_RESOLUTION.toString());
            label.setParent(row);
            label = new Label(info.getScreenWidth() + "x" + info.getScreenHeight() + " px");
            label.setParent(row);

            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_COLOR_DEPTH.toString());
            label.setParent(row);
            label = new Label(String.valueOf(info.getColorDepth()) + "-bit");
            label.setParent(row);
        }

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,
                CoreUtil.getUserLocale());

        if (Executions.getCurrent().getSession().getNativeSession() instanceof HttpSession) {
            HttpSession session = (HttpSession) Executions.getCurrent().getSession().getNativeSession();

            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SESSION_TIMEOUT.toString());
            label.setParent(row);
            label = new Label(String.valueOf(session.getMaxInactiveInterval() / 60) + "'");
            label.setParent(row);

            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SESSION_CREATE_TIME.toString());
            label.setParent(row);
            label = new Label(formatter.format(new Date(session.getCreationTime())));
            label.setParent(row);

            row = new Row();
            row.setParent(grid.getRows());
            label = new Label(L10N_LABEL_SESSION_ACCESSED_TIME.toString());
            label.setParent(row);
            label = new Label(formatter.format(new Date(session.getLastAccessedTime())));
            label.setParent(row);
        }

    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return L10nUtil.getMessage(CommandEnum.class, CommandEnum.SESSION_INFO.name(), "Session Information");
    }
}
