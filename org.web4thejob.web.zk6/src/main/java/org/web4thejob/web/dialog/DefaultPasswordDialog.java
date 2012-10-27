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

package org.web4thejob.web.dialog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.security.UserIdentity;
import org.web4thejob.util.L10nString;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultPasswordDialog extends AbstractDialog implements PasswordDialog {
    // ------------------------------ FIELDS ------------------------------
    private static final int MINIMUM_PASSWORD_LENGTH = 4;

    public static final L10nString L10N_DIALOG_TITLE = new L10nString(DefaultPasswordDialog.class, "dialog_title",
            "Password change");
    public static final L10nString L10N_OLD_PASSWORD = new L10nString(DefaultPasswordDialog.class,
            "label_old_password", "Old password");
    public static final L10nString L10N_NEW_PASSWORD = new L10nString(DefaultPasswordDialog.class,
            "label_new_password", "New password");
    public static final L10nString L10N_CONF_PASSWORD = new L10nString(DefaultPasswordDialog.class,
            "label_conf_password", "Confirm password");
    public static final L10nString L10N_OLD_PASSWORD_FAILURE = new L10nString(DefaultPasswordDialog.class,
            "msg_old_password_failure", "The old password is wrong.");
    public static final L10nString L10N_NEW_PASSWORD_FAILURE = new L10nString(DefaultPasswordDialog.class,
            "msg_new_password_failure", "The new passwords do not match.");
    public static final L10nString L10N_NEW_PASSWORD_LENGTH_FAILURE = new L10nString(DefaultPasswordDialog.class,
            "msg_new_password_length_failure", "The new password should be at least {0} characters long.");
    public static final L10nString L10N_PASSWORD_MATCH_USERNAME_FAILURE = new L10nString(DefaultPasswordDialog.class,
            "msg_password_match_username_failure", "The password can not match the user name.");

    private final boolean validateOld;
    private final Textbox oldPassword;
    private final Textbox newPassword = new Textbox();
    private final Textbox confPassword = new Textbox();
    private UserIdentity userIdentity;

    // --------------------------- CONSTRUCTORS ---------------------------

    public DefaultPasswordDialog(UserIdentity userIdentity) {
        this(userIdentity, true);
    }

    public DefaultPasswordDialog(UserIdentity userIdentity, boolean validateOld) {
        super();
        this.userIdentity = userIdentity;
        this.validateOld = validateOld;
        if (validateOld) {
            oldPassword = new Textbox();
            oldPassword.setType("password");
            oldPassword.setHflex("true");
        } else {
            oldPassword = null;
        }

        newPassword.setType("password");
        newPassword.setHflex("true");
        confPassword.setType("password");
        confPassword.setHflex("true");
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected Message getOKMessage() {
        Map<MessageArgEnum, Object> args = new HashMap<MessageArgEnum, Object>(2);
        args.put(MessageArgEnum.ARG_ITEM, newPassword.getValue());
        if (validateOld) {
            args.put(MessageArgEnum.ARG_OLD_ITEM, oldPassword.getValue());
        }
        return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, args);
    }

    @Override
    protected boolean isOKReady() {
        if (!validated()) {
            Clients.wrongValue(oldPassword, L10N_OLD_PASSWORD_FAILURE.toString());
            return false;
        }
        if (!confirmed()) {
            Clients.wrongValue(newPassword, L10N_NEW_PASSWORD_FAILURE.toString());
            Clients.wrongValue(confPassword, L10N_NEW_PASSWORD_FAILURE.toString());
            return false;
        }
        if (newPassword.getValue().length() < MINIMUM_PASSWORD_LENGTH) {
            Clients.wrongValue(newPassword, L10N_NEW_PASSWORD_LENGTH_FAILURE.toString(MINIMUM_PASSWORD_LENGTH));
            return false;
        }
        if (newPassword.getValue().equals(userIdentity.getUserName())) {
            Clients.wrongValue(newPassword, L10N_PASSWORD_MATCH_USERNAME_FAILURE.toString());
            return false;
        }
        return true;
    }

    private boolean validated() {
        return !validateOld || ContextUtil.getSessionContext().getSecurityContext().isPasswordValid(oldPassword
                .getValue());
    }

    private boolean confirmed() {
        return newPassword.getValue().equals(confPassword.getValue());
    }

    @Override
    protected void prepareContent() {
        super.prepareContent();

        window.setWidth("500px");
        window.setHeight("250px");
        window.setMaximizable(false);

        Grid grid = new Grid();
        grid.setParent(dialogContent.getPanelchildren());
        grid.setVflex(true);

        new Columns().setParent(grid);
        new Column().setParent(grid.getColumns());
        new Column().setParent(grid.getColumns());
        new Rows().setParent(grid);

        Row row;
        if (validateOld) {
            row = new Row();
            row.setParent(grid.getRows());
            new Label(L10N_OLD_PASSWORD.toString()).setParent(row);
            oldPassword.setParent(row);
        }

        row = new Row();
        row.setParent(grid.getRows());
        new Label(L10N_NEW_PASSWORD.toString()).setParent(row);
        newPassword.setParent(row);

        row = new Row();
        row.setParent(grid.getRows());
        new Label(L10N_CONF_PASSWORD.toString()).setParent(row);
        confPassword.setParent(row);
    }

    @Override
    protected String prepareTitle() {
        return L10N_DIALOG_TITLE.toString();
    }

}
