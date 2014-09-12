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

package org.web4thejob.web.zbox;

import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.dialog.AuthorizationPolicyDialog;
import org.web4thejob.web.panel.I18nAware;
import org.web4thejob.web.panel.Panel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class AuthorizationPolicyEditorBox extends AuthorizationPolicyViewerBox implements EventListener<Event> {
    // --------------------------- CONSTRUCTORS ---------------------------

    public AuthorizationPolicyEditorBox() {
        super();

        Vbox vbox = new Vbox();
        vbox.setParent(this);
        vbox.setSpacing("5px");

        Toolbarbutton edit = new Toolbarbutton();
        edit.setParent(vbox);
        edit.setAttribute("edit", true);
        edit.setTooltiptext(AbstractBox.L10N_BUTTON_EDIT.toString());
        edit.addEventListener(Events.ON_CLICK, this);
        edit.addEventListener(ON_CLICK_ECHO, this);
        String image = CoreUtil.getCommandImage(CommandEnum.UPDATE, null);
        if (image != null) {
            edit.setImage(image);
        } else {
            edit.setLabel(AbstractBox.L10N_BUTTON_EDIT.toString());
        }

/*
        Clear is not needed, the user can reset the authorization plicy, or delete it and start clean.

        Toolbarbutton clear = new Toolbarbutton();
        clear.setParent(vbox);
        clear.setAttribute("clear", true);
        clear.setTooltiptext(AbstractBox.L10N_BUTTON_CLEAR.toString());
        clear.addEventListener(Events.ON_CLICK, this);
        clear.addEventListener(ON_CLICK_ECHO, this);
        image = CoreUtil.getCommandImage(CommandEnum.CLEAR, null);
        if (image != null) {
            clear.setImage(image);
        } else {
            clear.setLabel(AbstractBox.L10N_BUTTON_CLEAR.toString());
        }
*/
    }

    private static final long serialVersionUID = 1L;
    private static final String ON_CLICK_ECHO = Events.ON_CLICK + "Echo";

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface EventListener ---------------------

    public void onEvent(Event event) throws Exception {
        if (Events.ON_CLICK.equals(event.getName())) {
            Clients.showBusy(null);
            Events.echoEvent(ON_CLICK_ECHO, event.getTarget(), null);
        } else if (ON_CLICK_ECHO.equals(event.getName())) {
            Clients.clearBusy();
            if (event.getTarget().hasAttribute("edit")) {
                edit();
            } else if (event.getTarget().hasAttribute("clear")) {
                clear();
            }
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    private void clear() {
        setRawValue(null);
        Events.postEvent(Events.ON_CHANGE, this, null);
    }

    private void edit() {
        AuthorizationPolicyDialog dialog = ContextUtil.getDefaultDialog(AuthorizationPolicyDialog.class);
        dialog.setAuthorizationMenu(authorizationPolicyPanel.getDefinition());
        Panel panel = ZkUtil.getOwningPanelOfComponent(this);
        if (panel instanceof I18nAware) {
            dialog.setL10nMode(((I18nAware) panel).getL10nMode());
        }
        dialog.show(new MessageListener() {
            public void processMessage(Message message) {
                if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId()) {
                    setRawValue(message.getArg(MessageArgEnum.ARG_ITEM, String.class));
                    Events.postEvent(Events.ON_CHANGE, AuthorizationPolicyEditorBox.this, null);
                }
            }
        });
    }
}
