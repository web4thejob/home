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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.annotation.Encrypted;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.base.zk.AbstractZkTargetTypeAwarePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultModelHierarchyPanel extends AbstractZkTargetTypeAwarePanel implements ModelHierarchyPanel,
        EventListener<Event> {
    // ------------------------------ FIELDS ------------------------------

    private static final String ATTRIB_PROPERTY_METADATA = PropertyMetadata.class.getName();
    private static final String ON_OPEN_ECHO = Events.ON_OPEN + "Echo";
    private final Tree tree = new Tree();
    private boolean allowOneToMany = true;
    private boolean allowOneToOne = true;
    private boolean allowOneToOneSubset = true;
    private boolean allowBlobs = true;
    private boolean associationsExpanded = true;

    // --------------------------- CONSTRUCTORS ---------------------------

    public DefaultModelHierarchyPanel() {
        ZkUtil.setParentOfChild((Component) base, tree);
        tree.setVflex("true");
        tree.setWidth("100%");
        tree.addEventListener(Events.ON_SELECT, this);
        new Treechildren().setParent(tree);
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface EventListener ---------------------

    @Override
    public void onEvent(Event event) throws Exception {
        if (Events.ON_SELECT.equals(event.getName())) {
            arrangeForState(PanelState.FOCUSED);
        } else if (Events.ON_OPEN.equals(event.getName())) {
            showBusy();
            event.getTarget().removeEventListener(Events.ON_OPEN, this);
            Events.echoEvent(new OpenEvent(ON_OPEN_ECHO, event.getTarget(), ((OpenEvent) event).isOpen()));
        } else if (ON_OPEN_ECHO.equals(event.getName())) {
            clearBusy();
            event.getTarget().removeEventListener(ON_OPEN_ECHO, this);
            renderAssociationType((Treeitem) event.getTarget(), ((PropertyMetadata) event.getTarget().getAttribute
                    (ATTRIB_PROPERTY_METADATA)).getAssociatedEntityMetadata());
        } else if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
            dispatchPropertyPath();
        }

    }

    // --------------------- Interface ModelHierarchyPanel ---------------------

    @Override
    public void allowOneToMany(boolean allow) {
        allowOneToMany = allow;
    }

    @Override
    public boolean isOneToManyAllowed() {
        return allowOneToMany;
    }

    @Override
    public void allowOneToOne(boolean allow) {
        allowOneToOne = allow;
    }

    @Override
    public boolean isOneToOneAllowed() {
        return allowOneToOne;
    }

    @Override
    public void allowOneToOneSubset(boolean allow) {
        allowOneToOneSubset = allow;
    }

    @Override
    public boolean isOneToOneSubsetAllowed() {
        return allowOneToOneSubset;
    }

    // -------------------------- OTHER METHODS --------------------------

    @Override
    protected void arrangeForNullTargetType() {
        unregisterCommand(CommandEnum.REFRESH);
        unregisterCommand(CommandEnum.SELECT);
        super.arrangeForNullTargetType();
        tree.getTreechildren().getChildren().clear();
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.REFRESH);
        supported.add(CommandEnum.SELECT);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    protected void arrangeForTargetType() {
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.REFRESH, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.SELECT, this));
        renderRootItem();
    }

    private void renderRootItem() {
        tree.getTreechildren().getChildren().clear();
        if (hasTargetType()) {
            EntityMetadata entityMetadata = ContextUtil.getMRS().getEntityMetadata(getTargetType());
            Treeitem rootItem = new Treeitem(entityMetadata.getFriendlyName());
            rootItem.setParent(tree.getTreechildren());
            renderAssociationType(rootItem, entityMetadata);
            arrangeForState(PanelState.READY);
        } else {
            arrangeForState(PanelState.UNDEFINED);
        }
    }

    private void renderAssociationType(Treeitem parent, EntityMetadata entityMetadata) {
        if (parent.getTreechildren() == null) {
            new Treechildren().setParent(parent);
        }
        parent.getTreechildren().getChildren().clear();

        for (PropertyMetadata propertyMetadata : entityMetadata.getPropertiesMetadata()) {
            if (propertyMetadata.isAssociationType()) {
                if (propertyMetadata.equals(getBindPropertyMetadata())) {
                    continue;
                } else if (!allowOneToMany && propertyMetadata.isOneToManyType()) {
                    continue;
                } else if (!allowOneToOne && propertyMetadata.isOneToOneType()) {
                    if (propertyMetadata.getAssociatedEntityMetadata().isTableSubset()) {
                        if (!allowOneToOneSubset) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    PropertyMetadata parentPropertyMetadata = (PropertyMetadata) parent.getAttribute
                            (ATTRIB_PROPERTY_METADATA);
                    if (parentPropertyMetadata != null) {
                        if (parentPropertyMetadata.isAssociatedWith(propertyMetadata)) {
                            continue;
                        }
                    }
                }
            } else if (propertyMetadata.isBlobType() && !allowBlobs) {
                continue;
            }

            if (propertyMetadata.isAnnotatedWith(Encrypted.class)) {
                continue;
            }

            Treeitem treeitem = new Treeitem(propertyMetadata.getFriendlyName());
            treeitem.setParent(parent.getTreechildren());
            treeitem.setAttribute(ATTRIB_PROPERTY_METADATA, propertyMetadata);
            treeitem.addEventListener(Events.ON_DOUBLE_CLICK, this);

            if (propertyMetadata.isAssociationType() && associationsExpanded) {
                new Treechildren().setParent(treeitem);
                treeitem.setOpen(false);
                treeitem.addEventListener(Events.ON_OPEN, this);
                treeitem.addEventListener(ON_OPEN_ECHO, this);

                //polymorphic properties
                for (Class<? extends Entity> subtype : propertyMetadata.getAssociatedEntityMetadata().getSubclasses()) {
                    treeitem = new Treeitem();
                    treeitem.setParent(parent.getTreechildren());
                    new Treechildren().setParent(treeitem);
                    treeitem.setOpen(false);
                    treeitem.addEventListener(Events.ON_OPEN, this);
                    treeitem.addEventListener(ON_OPEN_ECHO, this);

                    PropertyMetadata subproperty = propertyMetadata.castForSubclass(subtype);
                    treeitem.setAttribute(ATTRIB_PROPERTY_METADATA, subproperty);
                    treeitem.addEventListener(Events.ON_DOUBLE_CLICK, this);

                    if (treeitem.getTreerow() == null) {
                        new Treerow().setParent(treeitem);
                    }
                    Treecell treecell = new Treecell(subproperty.getFriendlyName());

                    treecell.setParent(treeitem.getTreerow());
                    treecell.setStyle("white-space:nowrap;");
                    treecell.setTooltiptext(treecell.getLabel());

                }
            }
        }
    }

    private PropertyMetadata getBindPropertyMetadata() {
        if (hasTargetType() && getSettingValue(SettingEnum.BIND_PROPERTY, null) != null) {
            return ContextUtil.getMRS().getPropertyMetadata(getTargetType(),
                    getSettingValue(SettingEnum.BIND_PROPERTY, ""));
        }
        return null;
    }

    @Override
    protected void arrangeForState(PanelState newState) {
        super.arrangeForState(newState);
        switch (state) {
            case READY:
                activateCommand(CommandEnum.REFRESH, true);
                break;
            case FOCUSED:
                activateCommand(CommandEnum.REFRESH, true);
                activateCommand(CommandEnum.SELECT, true);
                break;
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (id.equals(SettingEnum.BIND_PROPERTY)) {
            renderRootItem();
        } else {
            super.onSettingValueChanged(id, oldValue, newValue);
        }
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.REFRESH.equals(command.getId())) {
            refresh();
        } else if (CommandEnum.SELECT.equals(command.getId())) {
            dispatchPropertyPath();
        } else {
            super.processValidCommand(command);
        }
    }

    private void dispatchPropertyPath() {
        if (tree.getSelectedItem() == null) return;
        Treeitem treeitem = tree.getSelectedItem();

        PropertyMetadata propertyMetadata;
        propertyMetadata = (PropertyMetadata) treeitem.getAttribute(ATTRIB_PROPERTY_METADATA);
        if (propertyMetadata == null) return;

        Path path = new Path(true);
        while (treeitem != null && treeitem.hasAttribute(ATTRIB_PROPERTY_METADATA)) {
            propertyMetadata = (PropertyMetadata) treeitem.getAttribute(ATTRIB_PROPERTY_METADATA);
            path.append(propertyMetadata);
            treeitem = treeitem.getParentItem();
        }

        Message message = ContextUtil.getMessage(MessageEnum.PATH_SELECTED, this, MessageArgEnum.ARG_ITEM,
                ContextUtil.getMRS().getPropertyPath(getTargetType(), path));
        dispatchMessage(message);
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();
        registerSetting(SettingEnum.BIND_PROPERTY, "");
    }

    @Override
    public boolean isAssociationsExpanded() {
        return associationsExpanded;
    }

    @Override
    public void associationsExpanded(boolean expandAssociations) {
        this.associationsExpanded = expandAssociations;
    }

    @Override
    public void refresh() {
        if (hasTargetType()) {
            renderRootItem();
        }
    }

    @Override
    public boolean isBlobAllowed() {
        return allowBlobs;
    }

    @Override
    public void allowBlobs(boolean allowBlobs) {
        this.allowBlobs = allowBlobs;
    }
}
