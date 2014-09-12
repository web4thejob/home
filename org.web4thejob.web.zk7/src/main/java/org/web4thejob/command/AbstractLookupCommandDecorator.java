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

package org.web4thejob.command;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.web4thejob.ProcessingException;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.UniqueKeyConstraint;
import org.web4thejob.orm.query.Query;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nString;
import org.web4thejob.web.dialog.DefaultEntityPersisterDialog;
import org.web4thejob.web.dialog.EntityPersisterDialog;
import org.web4thejob.web.panel.MutableMode;
import org.web4thejob.web.panel.PanelState;
import org.web4thejob.web.panel.base.zk.AbstractZkBindablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class AbstractLookupCommandDecorator<E extends Entity> extends AbstractCommandDecorator implements
        LookupCommandDecorator<E>, EventListener<Event> {
    // ------------------------------ FIELDS ------------------------------

    protected AbstractLookupCommandDecorator(Command command) {
        super(command);

        div.setSclass("z-toolbarbutton");
        div.setAttribute(CommandDecorator.ATTRIB_DECORATOR, this);
        combobox.setParent(div);
        combobox.setWidth("350px");
        combobox.addEventListener(Events.ON_SELECT, this);
        combobox.addEventListener(Events.ON_OPEN, this);
        combobox.setReadonly(true);

        combobutton.setMold("toolbar");
        combobutton.setParent(div);
        combobutton.addEventListener(Events.ON_CLICK, this);
        combobutton.setTooltiptext(command.getName());
        String image = CoreUtil.getCommandImage(command.getId(), null);
        if (image != null) {
            combobutton.setImage(image);
        } else {
            combobutton.setLabel(L10N_COMBOBUTTON_LOOKUP.toString());
        }


        Menupopup menupopup = new Menupopup();
        menupopup.setParent(combobutton);

        subcommandsOwner = new SubcommandsOwner(command);
        for (Command subcommand : subcommandsOwner.getCommands()) {
            CommandDecorator decorator = new DefaultMenuitemCommandDecorator(subcommand);
            decorator.attach(menupopup);
            decorator.render();
        }
        arrangeForState(PanelState.READY);
    }
    public static final L10nString L10N_COMBOBUTTON_LOOKUP = new L10nString(AbstractLookupCommandDecorator.class,
            "combobutton_lookup", "Lookup");
    private final Div div = new Div();
    private final Combobox combobox = new Combobox();
    private final Combobutton combobutton = new Combobutton();
    private final SubcommandsOwner subcommandsOwner;
    // --------------------------- CONSTRUCTORS ---------------------------
    private boolean modified;

    @Override
    public void dispatchMessage(Message message) {
        if (MessageEnum.ENTITY_UPDATED == message.getId() && subcommandsOwner.hasCommand(CommandEnum.UPDATE)) {
            setModified(true);
            return;
        }
        super.dispatchMessage(message);
    }

    protected void arrangeForState(PanelState newState) {
        switch (newState) {
            case READY:
                if (subcommandsOwner.hasCommand(CommandEnum.REFRESH)) {
                    subcommandsOwner.getCommand(CommandEnum.REFRESH).setActivated(true);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.ADDNEW)) {
                    subcommandsOwner.getCommand(CommandEnum.ADDNEW).setActivated(true);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.UPDATE)) {
                    subcommandsOwner.getCommand(CommandEnum.UPDATE).setActivated(false);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.DELETE)) {
                    subcommandsOwner.getCommand(CommandEnum.DELETE).setActivated(false);
                }
                break;
            case FOCUSED:
                if (subcommandsOwner.hasCommand(CommandEnum.REFRESH)) {
                    subcommandsOwner.getCommand(CommandEnum.REFRESH).setActivated(true);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.ADDNEW)) {
                    subcommandsOwner.getCommand(CommandEnum.ADDNEW).setActivated(true);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.UPDATE)) {
                    subcommandsOwner.getCommand(CommandEnum.UPDATE).setActivated(true);
                }
                if (subcommandsOwner.hasCommand(CommandEnum.DELETE)) {
                    subcommandsOwner.getCommand(CommandEnum.DELETE).setActivated(true);
                }
                break;
        }
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface CommandDecorator ---------------------

    @Override
    public void attach(Object container) {
        if (!Component.class.isInstance(container))
            throw new IllegalArgumentException("container is not an instance of " + Component.class.getName());
        div.setParent((Component) container);
        command.addMessageListener(this);
    }

    @Override
    public void dettach() {
        div.detach();
        command.removeMessageListener(this);
    }

    @Override
    public boolean isAttached() {
        return div.getPage() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render() {
        if (combobox.getModel() == null) {
            refresh();
        }
        setLookupSelection((E) command.getValue());
    }

    @Override
    public boolean isDisabled() {
        return combobox.isDisabled();
    }

    @Override
    public void setDisabled(boolean disabled) {
        combobox.setDisabled(disabled);
        combobutton.setDisabled(disabled);
    }

    @Override
    public String getName() {
        return command.getName();
    }

    @Override
    public void setName(String name) {
        // do nothing
    }

    // --------------------- Interface EventListener ---------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            if (getLookupSelection() != null) {
                ((LookupCommandOwner) command.getOwner()).renderAfterLookupChange(getLookupSelection());
                arrangeForState(PanelState.FOCUSED);
                setModified(false);
            }
        } else if (Events.ON_OPEN.equals(event.getName())) {
            if (((OpenEvent) event).isOpen() && isModified()) {
                Clients.showNotification(L10nMessages.L10N_DISCARD_CHANGES_WARNING.toString(),
                        Clients.NOTIFICATION_TYPE_WARNING, combobutton,
                        "after_center", -1, true);
            }
        } else if (Events.ON_CLICK.equals(event.getName())) {
            if (combobutton.getDropdown() != null) {
                combobutton.setOpen(true);
            }
        }
    }

    // --------------------- Interface MessageListener ---------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void processMessage(Message message) {
        if (MessageEnum.AFFIRMATIVE_RESPONSE == message.getId() && EntityPersisterDialog.class.isInstance(message
                .getSender())) {
            E entity = (E) message.getArg(MessageArgEnum.ARG_ITEM, Entity.class);
            if (((EntityPersisterDialog) message.getSender()).getMutableMode() == MutableMode.UPDATE) {
                getLookupSelection().merge(entity);
                entity = getLookupSelection();
                //just to force refresh of combo item
                ((ListModelList) combobox.getModel()).set(combobox.getSelectedIndex(), entity);
            }

            ((LookupCommandOwner) command.getOwner()).assignLookupDetails(entity);
            try {
                ContextUtil.getDWS().save(entity);
            } catch (HibernateOptimisticLockingFailureException e) {
                throw new ProcessingException(AbstractZkBindablePanel.L10N_MSG_ENTITY_MODIFIED_BY_OTHERS.toString());
            } catch (DataIntegrityViolationException e) {
                for (UniqueKeyConstraint constraint : ContextUtil.getMRS().getEntityMetadata(entity.getEntityType())
                        .getUniqueConstraints()) {
                    if (constraint.isViolated(entity)) {
                        throw new ProcessingException(AbstractZkBindablePanel.L10N_MSG_UNIQUE_KEY_VIOLATION.toString
                                (constraint.getFriendlyName()));

                    }
                }
                throw new ProcessingException(DefaultEntityPersisterDialog.L10N_MESSAGE_DATA_NTEGRITY_ERRORS.toString
                        ());
            }

            if (((EntityPersisterDialog) message.getSender()).getMutableMode() == MutableMode.INSERT) {
                ((ListModelList) combobox.getModel()).add(entity);
                setLookupSelection(entity);
            }

            ((LookupCommandOwner) command.getOwner()).renderAfterLookupChange(getLookupSelection());

        } else {
            super.processMessage(message);
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    protected void addNew() {
        E entity = newInstance();
        Set<Setting<?>> settings = new HashSet<Setting<?>>(1);
        settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, entity.getEntityType()));
        EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class, entity, settings,
                MutableMode.INSERT, true);
        dialog.show(this);
    }

    protected abstract E newInstance();

    @SuppressWarnings("unchecked")
    @Override
    public boolean setLookupSelection(E entity) {
        if (combobox.getModel() == null) {
            refresh();
        }

        if (entity != null && !entity.isNewInstance() && !entity.equals(getLookupSelection())) {
            ListModelList<E> entities = (ListModelList) combobox.getModel();
            for (E item : entities) {
                if (item.equals(entity)) {
                    entities.addToSelection(item);
                    setModified(entity.hasAttribute(ATTRIB_MODIFIED) ? (Boolean) entity.getAttribute(ATTRIB_MODIFIED)
                            : false);

                    if (!entities.getSelection().iterator().next().equals(entity)) {
                        throw new IllegalArgumentException();
                    }

                    arrangeForState(PanelState.FOCUSED);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        Entity selection = getLookupSelection();
        if (selection != null && this.modified != modified) {
            this.modified = modified;

            if (modified) {
                selection.setAttribute(ATTRIB_MODIFIED, modified);
            } else {
                selection.removeAttribute(ATTRIB_MODIFIED);
            }

            ZkUtil.setCommandDirty(command, modified, combobutton);

            if (subcommandsOwner.hasCommand(CommandEnum.UPDATE)) {
                subcommandsOwner.getCommand(CommandEnum.UPDATE).setHighlighted(modified);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void delete() {
        if (getLookupSelection() != null) {
            E entity = getLookupSelection();
            ContextUtil.getDWS().delete(entity);
            ((ListModelList) combobox.getModel()).remove(entity);
            arrangeForState(PanelState.READY);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void refresh() {
        E selected = getLookupSelection();
        combobox.setModel(new ListModelList(ContextUtil.getDRS().findByQuery(getRefreshQuery())));
        if (selected != null) {
            if (setLookupSelection(selected)) {
                ((LookupCommandOwner) command.getOwner()).renderAfterLookupChange(getLookupSelection());
                arrangeForState(PanelState.FOCUSED);
                return;
            }
        }
        arrangeForState(PanelState.READY);
    }

    protected abstract Query getRefreshQuery();

    @SuppressWarnings("unchecked")
    protected void update() {
        E entity = getLookupSelection();
        if (entity == null) return;

        entity = (E) entity.clone();
        Set<Setting<?>> settings = new HashSet<Setting<?>>(1);
        settings.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, entity.getEntityType()));
        EntityPersisterDialog dialog = ContextUtil.getDefaultDialog(EntityPersisterDialog.class, entity, settings,
                MutableMode.UPDATE, true);
        dialog.setDirty(true);
        dialog.show(this);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public E getLookupSelection() {
        E selection = null;
        if (combobox.getModel() != null && !((ListModelList) combobox.getModel()).getSelection().isEmpty()) {
            selection = (E) ((ListModelList) combobox.getModel()).getSelection().iterator().next();
            command.setValue(selection);
        }
        return selection;
    }

    // -------------------------- INNER CLASSES --------------------------

    protected class SubcommandsOwner implements CommandAware {
        public SubcommandsOwner(Command parent) {
            for (CommandEnum id : command.getId().getSubcommands()) {
                registerCommand(ContextUtil.getSubcommand(id, parent, this));
            }
        }

        private SortedMap<CommandEnum, Command> commands = new TreeMap<CommandEnum, Command>();

        private void registerCommand(Command command) {
            if (command != null) {
                commands.put(command.getId(), command);
                command.setRegistered(true);
            }
        }

        @Override
        public Set<CommandEnum> getSupportedCommands() {
            return Collections.unmodifiableSet(commands.keySet());
        }

        @Override
        public Command getCommand(CommandEnum id) {
            return commands.get(id);
        }

        @Override
        public SortedSet<Command> getCommands() {
            return Collections.unmodifiableSortedSet(new TreeSet<Command>(commands.values()));
        }

        @Override
        public boolean hasCommand(CommandEnum id) {
            return commands.containsKey(id);
        }

        @Override
        public void process(Command command) throws CommandProcessingException {
            if (CommandEnum.REFRESH.equals(command.getId())) {
                refresh();
                setModified(false);
            } else if (CommandEnum.ADDNEW.equals(command.getId())) {
                addNew();
                setModified(false);
            } else if (CommandEnum.UPDATE.equals(command.getId())) {
                update();
                setModified(false);
            } else if (CommandEnum.DELETE.equals(command.getId())) {
                Messagebox.show(AbstractZkBindablePanel.L10N_MSG_DELETE_CONFIRMATION.toString(),
                        L10nMessages.L10N_MSGBOX_TITLE_QUESTION.toString(), new Messagebox.Button[]{Messagebox
                        .Button.OK, Messagebox.Button.CANCEL}, null, Messagebox.QUESTION,
                        Messagebox.Button.CANCEL, new EventListener<Messagebox.ClickEvent>() {
                    @Override
                    public void onEvent(Messagebox.ClickEvent event) throws Exception {
                        if (Messagebox.Button.OK == event.getButton()) {
                            delete();
                        }
                    }
                });
                setModified(false);
            } else {
                throw new UnsupportedOperationException("command " + command.getId().name() + " was not expected"
                        + ".");
            }
        }

        @Override
        public void supressCommands(boolean supress) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCommandsSupressed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean unregisterCommand(CommandEnum id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PanelState getPanelState() {
            throw new UnsupportedOperationException();
        }


    }
}
