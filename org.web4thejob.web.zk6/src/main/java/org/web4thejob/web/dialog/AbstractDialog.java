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

package org.web4thejob.web.dialog;

import org.web4thejob.ProcessingException;
import org.web4thejob.command.*;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.panel.PanelState;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractDialog implements Dialog, EventListener<Event> {
    public static final L10nString L10N_BUTTON_OK = new L10nString(Dialog.class, "button_ok", "OK");
    public static final L10nString L10N_BUTTON_CANCEL = new L10nString(Dialog.class, "button_cancel", "Cancel");
    public static final L10nString L10N_BUTTON_CLOSE = new L10nString(Dialog.class, "button_close", "Close");


    //protected static final String ON_AFTER_SHOW = "onAfterShow";
    private static final String ON_OK_ECHO = "onOKEcho";
    protected final Window window = new Window();
    protected Panel dialogContent;
    protected Toolbar dialogButtongs;
    protected Button btnOK;
    protected Button btnCancel;
    protected MessageListener listener;
    private CommandRenderer commandRenderer;
    private SortedMap<CommandEnum, Command> commands;
    private boolean designMode = false;
    private boolean l10nMode = false;

    protected AbstractDialog() {
        window.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        window.setAttribute(ATTRIB_DIALOG, true);
        window.addEventListener(Events.ON_CLOSE, this);
        window.addEventListener(Events.ON_OK, this);
        prepareWindow();
        prepareContentLayout();
        prepareBottomToolbar();
        prepareButtons();
    }

    protected boolean isOKReady() {
        return true;
    }

    protected void prepareContent() {
        if (commandRenderer != null) {
            commandRenderer.render();
        }
    }

    protected void doCancel() {
        if (listener != null) {
            listener.processMessage(ContextUtil.getMessage(MessageEnum.NEGATIVE_RESPONSE, this));
        }
        window.detach();
    }

    protected void doClose() {
        window.detach();
    }

    protected Message getOKMessage() {
        return null;
    }

    protected void doOK() {
        if (listener != null) {
            Message message = getOKMessage();
            if (message != null) {
                try {
                    listener.processMessage(message);
                } catch (ProcessingException e) {
                    ZkUtil.displayMessage(e.getMessage(), true, window);
                    return;
                }
            }
        }
        doClose();
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_CANCEL.equals(event.getName())) {
            event.stopPropagation();
            doCancel();
        } else if (Events.ON_CLOSE.equals(event.getName())) {
            event.stopPropagation();
            doCancel();
        } else if (Events.ON_OK.equals(event.getName())) {
            event.stopPropagation();
            if (isOKReady()) {
                Clients.showBusy(window, L10nMessages.L10N_PROCESSING.toString());
                Events.echoEvent(new Event(ON_OK_ECHO, window));
            } else {
                showNotOKMessage();
            }
        } else if (Events.ON_CLICK.equals(event.getName())) {
            if (btnCancel != null && btnCancel.equals(event.getTarget())) {
                doCancel();
            } else if (btnOK != null && btnOK.equals(event.getTarget())) {
                if (isOKReady()) {
                    Clients.showBusy(window, L10nMessages.L10N_PROCESSING.toString());
                    Events.echoEvent(new Event(ON_OK_ECHO, window));
                } else {
                    showNotOKMessage();
                }
            }
        } else if (ON_OK_ECHO.equals(event.getName())) {
            Clients.clearBusy(window);
            doOK();
        }
    }

    protected void showNotOKMessage() {
        //override
    }

    protected void prepareButtons() {
        btnOK = new Button(L10N_BUTTON_OK.toString());
        btnOK.setParent(dialogButtongs);
        btnOK.setMold("trendy");
        btnOK.setWidth("100px");
        //btnOK.setAutodisable("self");
        btnOK.addEventListener(Events.ON_CLICK, this);

        btnCancel = new Button(L10N_BUTTON_CANCEL.toString());
        btnCancel.setParent(dialogButtongs);
        btnCancel.setMold("trendy");
        btnCancel.setWidth("100px");
        //btnCancel.setAutodisable("self");
        btnCancel.addEventListener(Events.ON_CLICK, this);
    }

    protected void prepareContentLayout() {
        dialogContent = new Panel();
        dialogContent.setParent(window);
        new Panelchildren().setParent(dialogContent);
        dialogContent.setWidth("100%");
        dialogContent.setVflex("true");
    }

    protected void prepareBottomToolbar() {
        dialogButtongs = new Toolbar();
        dialogButtongs.setParent(dialogContent);
        dialogButtongs.setMold("panel");
        dialogButtongs.setAlign("end");
    }

    protected String prepareTitle() {
        return toString();
    }

    protected void prepareWindow() {
        window.setBorder("normal");
        ZkUtil.sizeComponent(window, 85, 75);
        //window.setAction("show: slideIn({duration:500})");
        window.setMaximizable(true);
        window.setClosable(true);
        window.setSizable(true);
        window.addEventListener(Events.ON_CANCEL, this);
        window.addEventListener(ON_OK_ECHO, this);
        //window.addEventListener(ON_AFTER_SHOW, this);
    }

    @Override
    public void show(MessageListener listener) {
        this.listener = listener;
        if (window.getPage() != null) {
            prepareContent();
        } else {
            // this means that show is called more than once
            window.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        }
        try {
            onBeforeShow();
            window.setTitle(prepareTitle());
            window.doModal();
            //Events.echoEvent(ON_AFTER_SHOW, window, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void onBeforeShow() {
        // override
    }

    @Override
    public Command getCommand(CommandEnum id) {
        if (commands != null) {
            return commands.get(id);
        }
        return null;
    }

    @Override
    public SortedSet<Command> getCommands() {
        if (commands != null) return Collections.unmodifiableSortedSet(new TreeSet<Command>(commands.values()));
        else return Command.EMPTY_COMMANDS_SET;
    }

    @Override
    public boolean hasCommand(CommandEnum id) {
        return commands != null && commands.containsKey(id);
    }

    @Override
    public void process(Command command) throws CommandProcessingException {
        if (CommandEnum.LOCALIZE.equals(command.getId())) {
            Dialog dialog = ContextUtil.getDialog(DefaultLocalizationDialog.class, this);
            dialog.setInDesignMode(false);
            dialog.setL10nMode(false);
            dialog.show(null);
        }
    }

    @Override
    public void setInDesignMode(boolean designMode) {
        if (this.designMode != designMode) {
            this.designMode = designMode;
        }
    }

    @Override
    public boolean isInDesignMode() {
        return designMode;
    }

    @Override
    public void setL10nMode(boolean l10nMode) {
        if (this.l10nMode != l10nMode) {
            this.l10nMode = l10nMode;
            if (l10nMode) {
                Command command = registerCommand(ContextUtil.getDefaultCommand(CommandEnum.LOCALIZE, this));
                command.setActivated(true);
            } else {
                unregisterCommand(CommandEnum.LOCALIZE);
            }
        }
    }

    @Override
    public boolean getL10nMode() {
        return l10nMode;
    }

    private void initCommandRenderer() {
        if (commandRenderer == null) {
            commandRenderer = ContextUtil.getBean(CommandRenderer.class);
            commandRenderer.addCommandOwner(this);
            commandRenderer.setContainer(window);
        }
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(2);
        supported.add(CommandEnum.LOCALIZE);
        supported.add(CommandEnum.DESIGN);
        return Collections.unmodifiableSet(supported);
    }

    protected Command registerCommand(Command command) {
        if (command != null) {
            if (!Subcommand.class.isInstance(command) && !getSupportedCommands().contains(command.getId())) {
                throw new UnsupportedOperationException("command not supported:" + command.toString());
            }

            if (commands == null) {
                commands = new TreeMap<CommandEnum, Command>();
                initCommandRenderer();
            }

            if (hasCommand(command.getId())) {
                unregisterCommand(command.getId());
            }

            commands.put(command.getId(), command);
            command.setRegistered(true);
            for (CommandEnum id : command.getId().getSubcommands()) {
                Subcommand subcommand = ContextUtil.getSubcommand(id, command);
                if (subcommand != null) {
                    registerCommand(subcommand);
                }
            }

            commandRenderer.reset();
        }

        return command;
    }

    @Override
    public boolean unregisterCommand(CommandEnum id) {
        Command command = getCommand(id);

        if (command != null) {
            for (CommandEnum subid : command.getId().getSubcommands()) {
                unregisterCommand(subid);
            }
            commands.remove(id);
            command.setRegistered(false);
        }

        return false;
    }

    @Override
    public void supressCommands(boolean supress) {
        initCommandRenderer();
        commandRenderer.supress(supress);
    }

    @Override
    public boolean isCommandsSupressed() {
        return commandRenderer != null && commandRenderer.isSupressed();
    }

    @Override
    public String toString() {
        return L10nUtil.getMessage(getClass(), "friendlyBeanName", getClass().getSimpleName());
    }

    @Override
    public PanelState getPanelState() {
        return PanelState.BUSY;
    }
}
