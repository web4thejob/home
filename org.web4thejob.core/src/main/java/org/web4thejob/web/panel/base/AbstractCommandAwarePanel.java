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

package org.web4thejob.web.panel.base;

import org.springframework.core.NestedRuntimeException;
import org.web4thejob.command.*;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.UnauthorizedResourceException;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.dialog.Dialog;
import org.web4thejob.web.dialog.EntityPersisterDialog;
import org.web4thejob.web.dialog.LocalizationDialog;
import org.web4thejob.web.panel.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public abstract class AbstractCommandAwarePanel extends AbstractSettingAwarePanel implements CommandAware,
        DesignModeAware, I18nAware {
// ------------------------------ FIELDS ------------------------------

    public static final L10nString L10N_UNAUTHORIZED_ACCESS = new L10nString(AbstractCommandAwarePanel.class,
            "message_unauthorized_access", "Unauthorized access error");
    public static final L10nString L10N_UNEXPECTED_ERROR = new L10nString(AbstractCommandAwarePanel.class,
            "message_unexpected_error", "Unexpected error occured");

    protected static final CommandsSorter COMMANDS_SORTER = new CommandsSorter();
    protected PanelState state = PanelState.UNDEFINED;
    protected PanelState prevState;
    private CommandRenderer commandRenderer;
    private Map<CommandEnum, Command> commands;
    private boolean l10nMode = false;
    private boolean designMode = false;
    private boolean supressCommands = false;

// --------------------------- CONSTRUCTORS ---------------------------

    protected AbstractCommandAwarePanel() {
        registerCommands();
    }

    protected void registerCommands() {
        //override
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    protected final CommandRenderer getCommandRenderer() {
        return commandRenderer;
    }

    @Override
    public boolean getL10nMode() {
        return l10nMode;
    }

// ------------------------ CANONICAL METHODS ------------------------

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CommandAware ---------------------

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(2);
        supported.add(CommandEnum.LOCALIZE);
        supported.add(CommandEnum.DESIGN);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    public boolean isCommandsSupressed() {
        return supressCommands;
    }

// --------------------- Interface CommandListener ---------------------

    @Override
    public final void process(Command command) throws CommandProcessingException {
        if (canProcess(command)) {
            try {
                processValidCommand(command);
            } catch (Exception e) {
                if (e instanceof NestedRuntimeException && ((NestedRuntimeException) e).contains
                        (UnauthorizedResourceException.class)) {
                    displayMessage(L10N_UNAUTHORIZED_ACCESS.toString(), true);
                } else {
                    displayMessage(L10N_UNEXPECTED_ERROR.toString(), true);
                }
                e.printStackTrace();
            }
        }
    }

// --------------------- Interface DesignModeAware ---------------------

    @Override
    public boolean isInDesignMode() {
        return designMode;
    }

// --------------------- Interface I18nAware ---------------------

    @Override
    public void setL10nMode(boolean l10nMode) {
        if (this.l10nMode != l10nMode) {
            this.l10nMode = l10nMode;
            if (l10nMode && !L10nUtil.getLocalizableResources(this.getClass()).isEmpty()) {
                Command command = registerCommand(ContextUtil.getDefaultCommand(CommandEnum.LOCALIZE, this));
                command.setActivated(true);
            } else {
                unregisterCommand(CommandEnum.LOCALIZE);
            }
        }
    }

// --------------------- Interface InitializingBean ---------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        arrangeForState(PanelState.READY);
    }

// -------------------------- OTHER METHODS --------------------------


    @Override
    public void showBusy() {
        prevState = state;
        arrangeForState(PanelState.BUSY);
    }

    @Override
    public void clearBusy() {
        //if this is not the case don't do anything because
        //the stae has been changed to somethinbg else
        if (state == PanelState.BUSY) {
            arrangeForState(prevState);
        }
    }

    protected void arrangeForState(PanelState newState) {
        if (state == newState) {
            return;
        }
        state = newState;
        activateCommands(false);

        if (state == PanelState.BUSY) {
            return;
        }

        activateCommand(CommandEnum.DESIGN, true, true);
        activateCommand(CommandEnum.LOCALIZE, true);
        activateCommand(CommandEnum.CONFIGURE_HEADERS, true);
        activateCommand(CommandEnum.DESIGN_PANEL, true, true);
        activateCommand(CommandEnum.VALIDATE, true);


        switch (state) {
            case READY:
                activateCommand(CommandEnum.QUERY, true);
                activateCommand(CommandEnum.ADDNEW, true);
                activateCommand(CommandEnum.CLEAR, true);
                break;
            case BROWSING:
                activateCommand(CommandEnum.QUERY, true);
                activateCommand(CommandEnum.REFRESH, true);
                activateCommand(CommandEnum.ADDNEW, true);
                activateCommand(CommandEnum.CLEAR, true);
                activateCommand(CommandEnum.PRINT, true);
                break;
            case FOCUSED:
                activateCommand(CommandEnum.QUERY, true);
                activateCommand(CommandEnum.REFRESH, true);
                activateCommand(CommandEnum.ADDNEW, true);
                activateCommand(CommandEnum.UPDATE, true);
                activateCommand(CommandEnum.DELETE, true);
                activateCommand(CommandEnum.PRINT, true);

                activateCommand(CommandEnum.SELECT, true);
                activateCommand(CommandEnum.MOVE_DOWN, true);
                activateCommand(CommandEnum.MOVE_UP, true);
                activateCommand(CommandEnum.MOVE_LEFT, true);
                activateCommand(CommandEnum.MOVE_RIGHT, true);
                activateCommand(CommandEnum.RELATED_PANELS, true);

                activateCommand(CommandEnum.CLEAR, true);
                break;
            case INSERTING:
                activateCommand(CommandEnum.SAVE, true);
                activateCommand(CommandEnum.SAVE_ADDNEW, true);
                break;
            case UPDATING:
                activateCommand(CommandEnum.SAVE, true);
                break;
        }

    }

    protected void activateCommands(boolean activate) {
        for (Command command : getCommands()) {
            if (command.isActive() != activate) {
                command.setActivated(activate);
            }
        }
    }

    @Override
    public SortedSet<Command> getCommands() {
        if (commands != null) {
            SortedSet<Command> sortedSet = new TreeSet<Command>(COMMANDS_SORTER);
            sortedSet.addAll(commands.values());
            return Collections.unmodifiableSortedSet(sortedSet);
        } else {
            return Command.EMPTY_COMMANDS_SET;
        }
    }

    protected void activateCommand(CommandEnum id, boolean activate) {
        activateCommand(id, activate, false);
    }

    protected void activateCommand(CommandEnum id, boolean activate, boolean includeSubCommands) {
        Command command = getCommand(id);
        if (command != null && command.isActive() != activate) {
            command.setActivated(activate);
        }

        if (includeSubCommands && !id.getSubcommands().isEmpty()) {
            for (CommandEnum subid : id.getSubcommands()) {
                activateCommand(subid, activate, includeSubCommands);
            }
        }
    }


    @Override
    public Command getCommand(CommandEnum id) {
        if (commands != null) {
            return commands.get(id);
        }
        return null;
    }

    protected boolean canProcess(Command command) {
        return command.isActive() && hasCommand(command.getId()) && equals(command.getOwner());
    }

    @Override
    public boolean hasCommand(CommandEnum id) {
        return commands != null && commands.containsKey(id);
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.SUPRESS_COMMANDS.equals(id)) {
            supressCommands((Boolean) newValue);
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    @Override
    public void supressCommands(boolean supress) {
        if (supressCommands != supress) {
            supressCommands = supress;
            initCommandRenderer();
        }
    }

    protected void processValidCommand(Command command) {
        if (CommandEnum.LOCALIZE.equals(command.getId())) {
            final Dialog dialog = ContextUtil.getDefaultDialog(LocalizationDialog.class, this);
            dialog.show(null);
        } else if (CommandEnum.HIDE_SETTINGS.equals(command.getId())) {
            setInDesignMode(false);
            render();
        } else if (CommandEnum.RENDER_SETTINGS.equals(command.getId())) {
            render();
        } else if (CommandEnum.DESTROY_PANEL.equals(command.getId())) {
            discardPanel();
        } else if (CommandEnum.CUT.equals(command.getId())) {
            dispatchMessage(ContextUtil.getMessage(MessageEnum.PANEL_COPY_START, this));
            discardPanel();
            ContextUtil.getSessionContext().setAttribute(Attributes.ATTRIB_CUT_PASTE_PANEL, this);
        } else if (CommandEnum.SAVE_PANEL.equals(command.getId())) {
            savePanel(false);
        } else if (CommandEnum.SAVE_PANEL_AS.equals(command.getId())) {
            savePanel(true);
        }
    }

    private void discardPanel() {
        if (getParent() != null) {
            org.web4thejob.web.panel.ParentCapable ref = getParent();
            ref.getSubpanels().replace(this, ContextUtil.getDefaultPanel(PlaceholderPanel.class));
            if (ref instanceof Panel) {
                ((Panel) ref).render();
            }
        } else if (isAttached()) {
            detach();
        }
    }

    @Override
    public void setInDesignMode(boolean designMode) {
        if (this.designMode != designMode) {
            this.designMode = designMode;

            if (designMode) {
                Command command = registerCommand(ContextUtil.getDefaultCommand(CommandEnum.DESIGN, this));
                unregisterCommand(CommandEnum.PASTE);
                activateCommand(command.getId(), true, true);
            } else {
                unregisterCommand(CommandEnum.DESIGN);
                hightlightPanel(false);
            }
        }
    }

    @Override
    public void render() {
        super.render();
        if (commandRenderer != null) {
            commandRenderer.render();
        }
    }

    private void savePanel(boolean asNew) {
        if (asNew) {
            //force new name generation
            updateBeanName("<temp>");
        }

        PanelDefinition panelDefinition = null;
        if (!asNew && isPersisted()) {
            Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
            query.addCriterion(PanelDefinition.FLD_BEANID, Condition.EQ, getBeanName());
            panelDefinition = ContextUtil.getDRS().findUniqueByQuery(query);
        }

        String xml = toSpringXml();
        if (panelDefinition == null) {
            panelDefinition = ContextUtil.getEntityFactory().buildPanelDefinition();
            panelDefinition.setBeanId(XMLUtil.getRootElementId(xml));
            panelDefinition.setOwner(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity());
        }

        panelDefinition.setName(CoreUtil.cleanPanelName(toString()));
        panelDefinition.setDefinition(XMLUtil.toSpringBeanXmlResource(xml));
        panelDefinition.setTags(CoreUtil.tagPanel(this));
        panelDefinition.setType(CoreUtil.describeClass(getClass()));

        Set<Setting<?>> settings = new HashSet<Setting<?>>();
        settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, PanelDefinition.class));
        EntityPersisterDialog persisterDialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class,
                panelDefinition, settings, (panelDefinition.isNewInstance() ? MutableMode.INSERT : MutableMode
                .UPDATE), false, false, false);
        persisterDialog.setDirty(true);
        persisterDialog.show(new PanelDefinitionListener());
    }

    protected Command registerCommand(Command command) {
        if (command != null) {
            if (!Subcommand.class.isInstance(command) && !getSupportedCommands().contains(command.getId())) {
                throw new UnsupportedOperationException("command not supported");
            }

            if (commands == null) {
                commands = new HashMap<CommandEnum, Command>();
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

    protected void initCommandRenderer() {
        if (commandRenderer == null) {
            commandRenderer = ContextUtil.getBean(CommandRenderer.class);
            commandRenderer.addCommandOwner(this);
            commandRenderer.setContainer(base);
        }
        commandRenderer.supress(supressCommands);
    }

    public boolean unregisterCommand(CommandEnum id) {
        Command command = getCommand(id);

        if (command != null) {
            for (CommandEnum subid : command.getId().getSubcommands()) {
                unregisterCommand(subid);
            }
            commands.remove(id);
            command.setRegistered(false);

            if (commandRenderer != null) {
                commandRenderer.reset();
            }
        }

        return true;
    }

    @Override
    public PanelState getPanelState() {
        return state;
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.SUPRESS_COMMANDS, false);
    }

// -------------------------- INNER CLASSES --------------------------

    private class PanelDefinitionListener implements MessageListener {
        @Override
        public void processMessage(Message message) {
            if (message.getId() == MessageEnum.ENTITY_INSERTED || message.getId() == MessageEnum.ENTITY_UPDATED) {
                PanelDefinition panelDefinition = message.getArg(MessageArgEnum.ARG_ITEM, PanelDefinition.class);
                updateBeanName(panelDefinition.getBeanId());
                ContextUtil.getSessionContext().refresh();
                AbstractCommandAwarePanel.this.setUnsavedSettings(false);
            } else if (message.getId() == MessageEnum.NEGATIVE_RESPONSE) {
                //do nothing
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public void setUnsavedSettings(boolean unsavedSettings) {
        if (unsavedSettings != hasUnsavedSettings()) {
            super.setUnsavedSettings(unsavedSettings);
            Command command = getCommand(CommandEnum.DESIGN);
            if (command != null) {
                command.dispatchMessage(ContextUtil.getMessage(MessageEnum.RENDER, this));
            }
        }
    }

}
