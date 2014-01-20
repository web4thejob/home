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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.query.QueryResultMode;
import org.web4thejob.orm.query.Subquery;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.QueryPanel;
import org.zkoss.zk.ui.util.Clients;

import java.util.List;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultQueryDialog extends AbstractDialog implements QueryDialog {
    // ------------------------------ FIELDS ------------------------------
    public static final L10nString L10N_TITLE_QUERY_FOR = new L10nString(DefaultQueryDialog.class, "title_query_for",
            "Query > {0}");
    public static final L10nString L10N_MSG_SELECT_ENTITY_TO_CONTINUE = new L10nString(DefaultQueryDialog.class,
            "msg_select_entity_to_continue",
            "No entity is selected. You need to select an entity in order to continue, or hit Esc to cancel.");


    private final Set<Setting<?>> settings;
    private final QueryResultMode queryResultMode;
    private QueryPanel queryPanel;

    private List<Subquery> subqueryConstraints;
// --------------------------- CONSTRUCTORS ---------------------------

    public DefaultQueryDialog(Set<Setting<?>> settings, QueryResultMode queryResultMode) {
        super();
        this.settings = settings;
        this.queryResultMode = queryResultMode;
    }


    @Override
    protected String prepareTitle() {
        if (queryPanel != null && queryPanel.hasTargetType()) {
            return L10N_TITLE_QUERY_FOR.toString(ContextUtil.getMRS().getEntityMetadata(queryPanel.getTargetType())
                    .getFriendlyName());
        }
        return L10N_TITLE_QUERY_FOR.toString("?");
    }
// -------------------------- OTHER METHODS --------------------------

    @Override
    protected boolean isOKReady() {
        if (QueryResultMode.RETURN_ONE == queryResultMode && queryPanel != null && queryPanel.getListViewPanel() !=
                null) {
            return queryPanel.getListViewPanel().hasTargetEntity();
        } else {
            return queryPanel != null && queryPanel.hasTargetType();
        }
    }


    @Override
    protected void doOK() {
        super.doOK();
        if (queryPanel != null && queryPanel.getListViewPanel() != null) {
            queryPanel.getListViewPanel().clear();
        }
    }

    @Override
    protected void doCancel() {
        super.doCancel();
        if (queryPanel != null && queryPanel.getListViewPanel() != null) {
            queryPanel.getListViewPanel().clear();
        }
    }

    @Override
    protected Message getOKMessage() {
        Message message;
        if (QueryResultMode.RETURN_ONE == queryResultMode) {
            message = ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM,
                    queryPanel.getListViewPanel().getTargetEntity());
        } else {
            message = ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM,
                    queryPanel.getQuery());
        }

        return message;
    }

    @Override
    protected void prepareContent() {
        String dialogName = getSettingValue(SettingEnum.PERSISTED_QUERY_DIALOG, null);
        if (StringUtils.hasText(dialogName)) {
            queryPanel = (QueryPanel) ContextUtil.getPanel(dialogName);
        } else {
            queryPanel = ContextUtil.getDefaultPanel(QueryPanel.class, queryResultMode);
        }

        queryPanel.attach(dialogContent.getPanelchildren());
        queryPanel.setSettings(settings);
        queryPanel.supressCommands(false);
        queryPanel.setUnsavedSettings(false);
        super.prepareContent();
    }

    @Override
    protected void onBeforeShow() {
        queryPanel.setInDesignMode(isInDesignMode());
        queryPanel.setL10nMode(getL10nMode());
        queryPanel.setSubqueryConstraints(subqueryConstraints);
        queryPanel.render();

        if (queryPanel.getListViewPanel() != null) {
            queryPanel.addMessageListener(new MessageAware() {
                @Override
                public boolean addMessageListener(MessageAware messageAware) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void dispatchMessage(Message message) {
                    processMessage(message);
                }

                @Override
                public boolean removeMessageListener(MessageAware messageAware) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Set<MessageAware> getListeners() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void processMessage(Message message) {
                    if (MessageEnum.ENTITY_ACCEPTED == message.getId()) {
                        if (isOKReady()) {
                            doOK();
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings({"unchecked"})
    private <T> T getSettingValue(SettingEnum id, T defaultValue) {
        Setting<?> setting = getSetting(id);
        if (setting != null && setting.getValue() != null) {
            return (T) setting.getValue();
        }
        return defaultValue;
    }

    private Setting<?> getSetting(SettingEnum id) {
        for (Setting<?> setting : settings) {
            if (setting.getId() == id) {
                return setting;
            }
        }
        return null;
    }

    @Override
    public void setSubqueryConstraints(List<Subquery> subquery) {
        this.subqueryConstraints = subquery;
    }

    @Override
    protected void showNotOKMessage() {
        if (QueryResultMode.RETURN_ONE == queryResultMode) {
//            ZkUtil.displayMessage(L10N_MSG_SELECT_ENTITY_TO_CONTINUE.toString(), true, btnOK);
            Clients.showNotification(L10N_MSG_SELECT_ENTITY_TO_CONTINUE.toString(), Clients.NOTIFICATION_TYPE_WARNING,
                    btnOK, "after_center", 3000, true);
        }
    }

    @Override
    public Query getQuery() {
        if (queryPanel != null) {
            return queryPanel.getQuery();
        }
        return null;
    }

}
