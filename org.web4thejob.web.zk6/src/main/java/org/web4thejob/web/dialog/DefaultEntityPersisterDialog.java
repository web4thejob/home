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

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.CommandProcessingException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.panel.*;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@Component
@Scope("prototype")
public class DefaultEntityPersisterDialog extends AbstractDialog implements EntityPersisterDialog, DirtyListener {
    public static final L10nString L10N_MESSAGE_UNEXPECTED_ERRORS = new L10nString(DefaultEntityPersisterDialog
            .class, "message_unexpected_errors", "Save failed due to unexpected error.");
    public static final L10nString L10N_MESSAGE_DATA_NTEGRITY_ERRORS = new L10nString(DefaultEntityPersisterDialog
            .class, "message_data_integrity_error", "Save failed due to data integrity error.");
    public static final L10nString L10N_MESSAGE_IGNORE_CHANGES = new L10nString(DefaultEntityPersisterDialog
            .class, "message_ignore_changes", "Ignore changes?");

    private Entity entity;
    private final Set<Setting<?>> settings;
    private final boolean inMemoryEditing;
    private final boolean skipValidation;
    private final boolean allowMultipleNew;
    private final MutableMode mutableMode;
    private MutablePanel mutablePanel;
    private Class<? extends MutablePanel> mutableType = MutableEntityViewPanel.class;
    private boolean dirty;

    protected DefaultEntityPersisterDialog(Entity entity, Set<Setting<?>> settings, MutableMode mutableMode) {
        this(entity, settings, mutableMode, false, false);
    }

    protected DefaultEntityPersisterDialog(Entity entity, Set<Setting<?>> settings, MutableMode mutableMode,
                                           boolean inMemoryEditing) {
        this(entity, settings, mutableMode, inMemoryEditing, false);
    }

    protected DefaultEntityPersisterDialog(Entity entity, Set<Setting<?>> settings, MutableMode mutableMode,
                                           boolean inMemoryEditing, boolean skipValidation) {
        this(entity, settings, mutableMode, inMemoryEditing, false, true);
    }

    protected DefaultEntityPersisterDialog(Entity entity, Set<Setting<?>> settings, MutableMode mutableMode,
                                           boolean inMemoryEditing, boolean skipValidation, boolean allowMultipleNew) {
        super();
        this.entity = entity;
        this.settings = settings;
        this.mutableMode = mutableMode;
        this.inMemoryEditing = inMemoryEditing;
        this.skipValidation = skipValidation;
        this.allowMultipleNew = allowMultipleNew;
    }

    @Override
    public MutableMode getMutableMode() {
        return mutableMode;
    }

    @Override
    public void setMutableType(Class<? extends MutablePanel> mutableType) {
        this.mutableType = mutableType;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.VALIDATE);
        supported.add(CommandEnum.SAVE);
        supported.add(CommandEnum.SAVE_AS);
        supported.add(CommandEnum.SAVE_ADDNEW);
        supported.add(CommandEnum.CLEAR);
        return Collections.unmodifiableSet(supported);
    }

    protected Set<Setting<?>> getFilteredSettings() {
        Set<Setting<?>> filtered = new HashSet<Setting<?>>(5);
        for (Setting<?> setting : settings) {
            if (setting.getId() == SettingEnum.TARGET_TYPE ||
                    setting.getId() == SettingEnum.MASTER_TYPE ||
                    setting.getId() == SettingEnum.BIND_PROPERTY ||
                    setting.getId() == SettingEnum.HTML_PROPERTY ||
                    setting.getId() == SettingEnum.MEDIA_PROPERTY ||
                    setting.getId() == SettingEnum.RENDER_SCHEME_FOR_INSERT ||
                    setting.getId() == SettingEnum.RENDER_SCHEME_FOR_UPDATE) {
                filtered.add(setting);
            }
        }

        return filtered;
    }

    @Override
    protected void prepareContent() {
        mutablePanel = ContextUtil.getDefaultPanel(mutableType, mutableMode);
        mutablePanel.attach(dialogContent.getPanelchildren());
        mutablePanel.setSettings(getFilteredSettings());
        mutablePanel.supressCommands(true);
        mutablePanel.addDirtyListener(this);
        mutablePanel.setL10nMode(getL10nMode());
        mutablePanel.render();

        if (mutablePanel instanceof MasterDetailTypeAware && ((MasterDetailTypeAware) mutablePanel).isMasterDetail()) {
            MasterDetailTypeAware masterDetailTypeAware = (MasterDetailTypeAware) mutablePanel;
            Entity masterEntity = ContextUtil.getMRS().getPropertyMetadata(masterDetailTypeAware.getTargetType(),
                    masterDetailTypeAware.getBindProperty()).getValue(entity);
            mutablePanel.setMasterEntity(masterEntity);
        }

        if (entity.isNewInstance()) {
            mutablePanel.setTargetEntity(entity.clone());
        } else {
            mutablePanel.setTargetEntity(entity);
        }
        mutablePanel.setDirty(dirty);


        boolean denyAddNew = ContextUtil.getMRS().getEntityMetadata(mutablePanel.getTargetType()).isDenyAddNew();
        if (!skipValidation) {
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.VALIDATE, this));
        }
        if (!inMemoryEditing) {
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.SAVE, this));
            if (mutableMode == MutableMode.INSERT) {
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.CLEAR, this));
                if (allowMultipleNew && !denyAddNew) {
                    registerCommand(ContextUtil.getDefaultCommand(CommandEnum.SAVE_ADDNEW, this));
                }
            } else if (mutableMode == MutableMode.UPDATE && !denyAddNew) {
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.SAVE_AS, this));
            }
        }

        super.prepareContent();
    }

    @Override
    protected boolean isOKReady() {
        if (!skipValidation) {
            if (mutablePanel.isDirty() && hasCommand(CommandEnum.SAVE)) {
                return persist();
            } else {
                return mutablePanel.validate().isEmpty();
            }
        } else {
            return true;
        }
    }

    @Override
    protected Message getOKMessage() {
        if (inMemoryEditing) {
            return ContextUtil.getMessage(MessageEnum.AFFIRMATIVE_RESPONSE, this, MessageArgEnum.ARG_ITEM,
                    mutablePanel.getTargetEntity());
        } else {
            return null;
        }
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getName().equals("on" + CommandEnum.SAVE.name() + "_Echo")) {
            persist();
        } else if (event.getName().equals("on" + CommandEnum.SAVE_AS.name() + "_Echo")) {
            mutablePanel.getTargetEntity().setAsNew();
            persist();
        } else if (event.getName().equals("on" + CommandEnum.SAVE_ADDNEW.name() + "_Echo")) {
            if (persist()) {
                mutablePanel.setTargetEntity(entity.clone());
            }
        } else {
            super.onEvent(event);
        }
    }

    @Override
    protected void prepareWindow() {
        super.prepareWindow();
        window.addEventListener("on" + CommandEnum.SAVE.name() + "_Echo", this);
        window.addEventListener("on" + CommandEnum.SAVE_AS.name() + "_Echo", this);
        window.addEventListener("on" + CommandEnum.SAVE_ADDNEW.name() + "_Echo", this);
    }

    @Override
    public void process(Command command) throws CommandProcessingException {
        if (CommandEnum.SAVE.equals(command.getId())) {
            mutablePanel.beforePersist();
            Events.echoEvent(new Event("on" + command.getId().name() + "_Echo", super.window));
        } else if (CommandEnum.SAVE_AS.equals(command.getId())) {
            mutablePanel.beforePersist();
            Events.echoEvent(new Event("on" + command.getId().name() + "_Echo", super.window));
        } else if (CommandEnum.SAVE_ADDNEW.equals(command.getId())) {
            mutablePanel.beforePersist();
            Events.echoEvent(new Event("on" + command.getId().name() + "_Echo", super.window));
        } else if (CommandEnum.VALIDATE.equals(command.getId())) {
            mutablePanel.validate();
        } else if (CommandEnum.CLEAR.equals(command.getId())) {
            PropertyMetadata masterProperty = null;
            Entity masterEntity = null;
            if (mutablePanel instanceof MasterDetailTypeAware && ((MasterDetailTypeAware) mutablePanel)
                    .isMasterDetail()) {
                MasterDetailTypeAware masterDetailTypeAware = (MasterDetailTypeAware) mutablePanel;
                masterProperty = ContextUtil.getMRS().getPropertyMetadata(masterDetailTypeAware.getTargetType(),
                        masterDetailTypeAware.getBindProperty());
                masterEntity = masterProperty.getValue(entity);
            }
            entity = ContextUtil.getMRS().newInstance(entity.getEntityType());
            if (masterEntity != null) {
                masterProperty.setValue(entity, masterEntity);
            }
            mutablePanel.setTargetEntity(entity.clone());
        } else {
            super.process(command);
        }

    }

    private boolean persist() {
        try {
            boolean isSaveAs = mutablePanel.getTargetEntity().isNewInstance();

            mutablePanel.persist();

            if (!inMemoryEditing) {
                if (mutableMode == MutableMode.INSERT) {
                    listener.processMessage(ContextUtil.getMessage(MessageEnum.ENTITY_INSERTED, this,
                            MessageArgEnum.ARG_ITEM, mutablePanel.getTargetEntity()));
                } else if (mutableMode == MutableMode.UPDATE || isSaveAs) {
                    listener.processMessage(ContextUtil.getMessage(isSaveAs ? MessageEnum.ENTITY_INSERTED :
                            MessageEnum.ENTITY_UPDATED, this,
                            MessageArgEnum.ARG_ITEM, mutablePanel.getTargetEntity()));
                }
            }

            return true;
        } catch (HibernateOptimisticLockingFailureException e) {
            ZkUtil.displayMessage(AbstractZkBindablePanel.L10N_MSG_ENTITY_MODIFIED_BY_OTHERS.toString(), true, window);
            return true;    //we return true so that dialog closes; nothing was saved.
        } catch (DataIntegrityViolationException e) {
            ZkUtil.displayMessage(L10N_MESSAGE_DATA_NTEGRITY_ERRORS.toString(), true, window);
        } catch (ConstraintViolationException e) {
            return false; //validation messages should have already appeared.
        } catch (Exception e) {
            e.printStackTrace();
            ZkUtil.displayMessage(L10N_MESSAGE_UNEXPECTED_ERRORS.toString(), true, window);
        }

        return false;
    }

    @Override
    public void onDirty(boolean dirty) {
        this.dirty = dirty;
        btnOK.setDisabled(!dirty);
        for (Command command : getCommands()) {
            if (CommandEnum.SAVE == command.getId() || CommandEnum.SAVE_ADDNEW == command.getId()) {
                command.setActivated(dirty);
            } else {
                command.setActivated(true);
            }
        }
    }

    @Override
    protected void prepareButtons() {
        super.prepareButtons();
        btnCancel.setLabel(L10N_BUTTON_CLOSE.toString());
    }

    @Override
    protected void doCancel() {
        if (mutablePanel.isDirty()) {
            Messagebox.show(L10N_MESSAGE_IGNORE_CHANGES.toString(), L10nMessages.L10N_MSGBOX_TITLE_QUESTION.toString
                    (), new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL}, null,
                    Messagebox.QUESTION, Messagebox.Button.CANCEL, new EventListener<Messagebox.ClickEvent>() {
                @Override
                public void onEvent(Messagebox.ClickEvent event) throws Exception {
                    if (Messagebox.Button.OK == event.getButton()) {
                        DefaultEntityPersisterDialog.super.doCancel();
                    }
                }
            });
        } else {
            super.doCancel();
        }
    }

    @Override
    protected void prepareForOK() {
        mutablePanel.beforePersist();
    }
}
