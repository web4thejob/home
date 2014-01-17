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
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.AuthorizationPolicyPanel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultAuthorizationPolicyDialog extends AbstractDialog implements AuthorizationPolicyDialog {
    public static final L10nString L10N_TITLE_AUTHORIZATION_MANAGEMENT = new L10nString
            (DefaultAuthorizationPolicyDialog.class, "title_authorization_management", "Authorization Management");

    private AuthorizationPolicyPanel authorizationPolicyPanel;
    private String authorizationMenu;

    @Override
    protected String prepareTitle() {
        return L10N_TITLE_AUTHORIZATION_MANAGEMENT.toString();
    }

    @Override
    protected void prepareContent() {
        super.prepareContent();

        authorizationPolicyPanel = ContextUtil.getDefaultPanel(AuthorizationPolicyPanel.class, false);
        authorizationPolicyPanel.setDefinition(authorizationMenu);
        authorizationPolicyPanel.attach(dialogContent.getPanelchildren());
        authorizationPolicyPanel.setL10nMode(getL10nMode());
        authorizationPolicyPanel.render();
    }

    @Override
    public void setAuthorizationMenu(String xml) {
        authorizationMenu = xml;
    }

    @Override
    protected Message getOKMessage() {
        return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM,
                authorizationPolicyPanel.getDefinition());
    }
}
