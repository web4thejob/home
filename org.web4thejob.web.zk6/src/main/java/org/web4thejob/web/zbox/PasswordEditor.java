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

import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.security.SecurityService;
import org.web4thejob.security.UserIdentity;
import org.web4thejob.web.dialog.PasswordDialog;
import org.web4thejob.web.panel.I18nAware;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class PasswordEditor extends AbstractBox<String> implements MessageListener, I18nAware {
    private static final long serialVersionUID = 1L;
    private UserIdentity userIdentity;
    private String password;
    private boolean l10nMode;

    public PasswordEditor() {
        marshallEmptyValue();
    }

    @Override
    protected void onEdit() {
        PasswordDialog dialog = ContextUtil.getDefaultDialog(PasswordDialog.class, userIdentity, false);
        dialog.setL10nMode(l10nMode);
        dialog.show(this);
    }

    @Override
    protected void marshallEmptyValue() {
        password = null;
        super.marshallEmptyValue();
    }

    @Override
    protected void marshallToString(String value) {
        password = value;
        super.marshallToString("********************");
    }

    @Override
    protected String unmarshallToRawValue() {
        return password;
    }

    @Override
    public void processMessage(Message message) {
        if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
            String encPassword = ContextUtil.getBean(SecurityService.class).encodePassword(userIdentity,
                    message.getArg(MessageArgEnum.ARG_ITEM, String.class));
            setRawValue(encPassword);
        }
    }

    public void setUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    public UserIdentity getUserIdentity() {
        return this.userIdentity;
    }

    @Override
    public void setL10nMode(boolean l10nMode) {
        this.l10nMode = l10nMode;
    }

    @Override
    public boolean getL10nMode() {
        return l10nMode;
    }

    protected boolean isEmpty() {
        return password == null;
    }

}
