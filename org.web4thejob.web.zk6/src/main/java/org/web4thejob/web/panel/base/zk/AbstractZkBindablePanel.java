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

package org.web4thejob.web.panel.base.zk;

import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.web4thejob.SystemProtectedEntryException;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.DefaultSettingsDialog;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.dialog.SettingsDialogListener;
import org.web4thejob.web.panel.base.AbstractBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class AbstractZkBindablePanel extends AbstractBindablePanel {
// ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_MSG_DELETE_CONFIRMATION = new L10nString(AbstractZkBindablePanel.class,
            "message_delete_confirmation", "Are you sure you want to delete the selected entry?");
    public static final L10nString L10N_MSG_ENTITY_MODIFIED_BY_OTHERS = new L10nString(AbstractZkBindablePanel.class,
            "message_entity_modified_by_other_user", "It seems that the selected entry has been modified or deleted " +
            "by another session. Please refresh " + "your view.");
    public static final L10nString L10N_MSG_UNIQUE_KEY_VIOLATION = new L10nString(AbstractZkBindablePanel.class,
            "message_unique_key_violation", "The values of field(s) \"{0}\" already exist.");
    public static final L10nString L10N_MSG_DELETION_FAILED = new L10nString(AbstractZkBindablePanel.class,
            "message_delete_failed", "Deletion of entry failed. Most probably the entry is still referenced by other " +
            "" + "entries.");
    private static final String EVENT_BIND_ECHO = "onBindEcho";

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface BindCapable ---------------------

    @Override
    public final void bind(Entity bindEntity) {
        if (bindEntity != null && ((hasMasterType() && getMasterType().isInstance(bindEntity)) || (hasTargetType() &&
                getTargetType().isInstance(bindEntity)))) {
            showBusy();
            Events.echoEvent(EVENT_BIND_ECHO, (Component) base, bindEntity);
        }
    }

// --------------------- Interface Panel ---------------------

    @Override
    public void attach(Object container) {
        if (getParent() != null) throw new IllegalStateException("Cannot attach a child panel.");

        ((Component) base).setParent((Component) container);
    }

    @Override
    public void detach() {
        if (getParent() != null) throw new IllegalStateException("Cannot detach a child panel.");

        ((Component) base).detach();
    }

    @Override
    public Object getAttribute(String name) {
        return ((Component) base).getAttribute(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return ((Component) base).hasAttribute(name);
    }

    @Override
    public boolean isAttached() {
        return ((Component) base).getParent() != null;
    }

    @Override
    public Object removeAttribute(String name) {
        return ((Component) base).removeAttribute(name);
    }

    @Override
    public <T> void setAttribute(String name, T value) {
        ((Component) base).setAttribute(name, value);
    }

    @Override
    public void showBusy() {
        super.showBusy();
        if (base instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) base).setStyle("cursor:wait;");
        }
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected Object initBaseComponent() {
        Panel panel = ZkUtil.initBaseComponent(this);
        panel.addEventListener(EVENT_BIND_ECHO, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                clearBusy();
                if (event.getData() instanceof Entity) {
                    bindEcho((Entity) event.getData());
                }
            }
        });
        return panel;
    }

    @Override
    public void clearBusy() {
        super.clearBusy();
        if (base instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) base).setStyle("cursor:auto;");
        }
    }

    protected void bindEcho(Entity bindEntity) {
        if (canBind(bindEntity)) {
            if (hasMasterType() && getMasterType().isInstance(bindEntity)) {
                setMasterEntity(bindEntity);
            } else if (hasTargetType() && getTargetType().isInstance(bindEntity)) {
                setTargetEntity(bindEntity);
            }
        }
    }


    @Override
    public void hightlightPanel(boolean highlight) {
        ZkUtil.hightlightComponent((HtmlBasedComponent) base, highlight, isPersisted() ? "green" : "yellow");
        if (hasCommand(CommandEnum.HIGHLIGHT_PANEL)) {
            getCommand(CommandEnum.HIGHLIGHT_PANEL).setValue(highlight);
        }
    }


    @Override
    protected void processValidCommand(Command command) {
        final Dialog dialog;
        if (CommandEnum.CONFIGURE_SETTINGS.equals(command.getId())) {
            dialog = ContextUtil.getDialog(DefaultSettingsDialog.class, this);
            dialog.setInDesignMode(isInDesignMode());
            dialog.setL10nMode(getL10nMode());
            dialog.show(new SettingsDialogListener(this));
        } else if (CommandEnum.HIGHLIGHT_PANEL.equals(command.getId())) {
            hightlightPanel((Boolean) command.getValue());
        } else if (CommandEnum.DELETE.equals(command.getId())) {
            if (hasTargetEntity() && !getTargetEntity().isNewInstance()) {
                Messagebox.show(L10N_MSG_DELETE_CONFIRMATION.toString(), L10nMessages.L10N_MSGBOX_TITLE_QUESTION
                        .toString(), new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                        null, Messagebox.QUESTION, Messagebox.Button.CANCEL,
                        new EventListener<Messagebox.ClickEvent>() {
                            @Override
                            public void onEvent(Messagebox.ClickEvent event) throws Exception {
                                if (Messagebox.Button.OK == event.getButton()) {
                                    Entity entity = getTargetEntity();
                                    if (entity != null) {
                                        Message message = ContextUtil.getMessage(MessageEnum.ENTITY_DELETED,
                                                AbstractZkBindablePanel.this,
                                                MessageArgEnum.ARG_ITEM, entity);
                                        try {
                                            ContextUtil.getDWS().delete(entity);
                                            processEntityDeletion(entity);
                                            dispatchMessage(message);
                                        } catch (HibernateOptimisticLockingFailureException e) {
                                            displayMessage(L10N_MSG_ENTITY_MODIFIED_BY_OTHERS.toString(), true);
                                        } catch (SystemProtectedEntryException e) {
                                            displayMessage(e.getMessage(), true);
                                        } catch (Exception e) {
                                            displayMessage(L10N_MSG_DELETION_FAILED.toString(), true);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
            }
        } else {
            super.processValidCommand(command);
        }
    }

    @Override
    protected void displayMessage(String message, boolean error) {
        ZkUtil.displayMessage(message, error, (Component) base);
    }

}
