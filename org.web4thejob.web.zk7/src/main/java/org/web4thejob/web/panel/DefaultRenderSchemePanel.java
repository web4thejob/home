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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.LookupCommandDecorator;
import org.web4thejob.command.RenderSchemeLookupCommandDecorator;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.PathMetadata;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.RenderSchemeUtil;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.web.panel.base.AbstractBorderLayoutPanel;
import org.web4thejob.web.util.ZkUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultRenderSchemePanel extends AbstractBorderLayoutPanel implements RenderSchemePanel {
    public DefaultRenderSchemePanel() {
        DUMMY_RENDER_SCHEME = ContextUtil.getEntityFactory().buildRenderScheme(RenderElement.class);
    }

    // --------------------- GETTER / SETTER METHODS ---------------------
    private final RenderScheme DUMMY_RENDER_SCHEME;

    public RenderScheme getRenderScheme() {
        if (!hasTargetType() || getListViewPanel() == null) {
            return null;
        }

        RenderScheme renderScheme = (RenderScheme) ZkUtil.getLookupSelectionIfUnique(getCommand(CommandEnum
                .RENDER_SCHEME_LOOKUP), RenderSchemeLookupCommandDecorator.class);
        if (renderScheme == null) {
            renderScheme = ContextUtil.getEntityFactory().buildRenderScheme(getTargetType());
        }
        renderScheme.getElements().clear();

        List<? extends Entity> list = getListViewPanel().getList();
        for (Entity entity : list) {
            renderScheme.addElement((RenderElement) entity);
        }

        return renderScheme;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setRenderScheme(RenderScheme renderScheme) {
        if (!hasTargetType() || !getTargetType().equals(renderScheme.getTargetType()) || getListViewPanel() == null) {
            return;
        }

        renderAfterLookupChange(renderScheme);
        for (LookupCommandDecorator decorator : ZkUtil.getLookupDecorators(getCommand(CommandEnum
                .RENDER_SCHEME_LOOKUP))) {
            decorator.setLookupSelection(renderScheme);
        }
    }

    public boolean hasTargetType() {
        return getModelHierarchyPanel() != null && getModelHierarchyPanel().hasTargetType();
    }

    private ListViewPanel getListViewPanel() {
        return (ListViewPanel) getCenter();
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface CommandAware ---------------------

    public Class<? extends Entity> getTargetType() {
        if (getModelHierarchyPanel() != null) {
            return getModelHierarchyPanel().getTargetType();
        }
        return null;
    }

    // --------------------- Interface LookupCommandOwner ---------------------

    public void setTargetType(Class<? extends Entity> targetType) {
        onSettingValueChanged(SettingEnum.TARGET_TYPE, null, targetType);
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.RENDER_SCHEME_LOOKUP);
        supported.add(CommandEnum.CLEAR);
        supported.add(CommandEnum.REMOVE);
        supported.add(CommandEnum.EDIT);
        supported.add(CommandEnum.MOVE_UP);
        supported.add(CommandEnum.MOVE_DOWN);
        return Collections.unmodifiableSet(supported);
    }

    // --------------------- Interface MessageListener ---------------------

    public void renderAfterLookupChange(RenderScheme renderScheme) {
        if (renderScheme == null || !hasTargetType() || !getTargetType().equals(renderScheme.getTargetType())) {
            return;
        }

        clear();
        for (RenderElement renderElement : renderScheme.getElements()) {
            if (renderElement != null) {
                getListViewPanel().add(renderElement.clone());
            }
        }
    }

    // --------------------- Interface RenderSchemePanel ---------------------

    public void assignLookupDetails(RenderScheme renderScheme) {
        renderScheme.getElements().clear();
        List<? extends Entity> list = getListViewPanel().getList();
        for (Entity entity : list) {
            RenderElement renderElement = renderScheme.addElement((RenderElement) entity);
            if (renderScheme.isNewInstance()) {
                renderElement.setAsNew();
            }
        }
    }

    @Override
    public void processMessage(Message message) {
        switch (message.getId()) {
            case PATH_SELECTED:
                if (hasTargetType() && message.getSender() instanceof TargetTypeAware) {
                    if (((TargetTypeAware) message.getSender()).hasTargetType()) {
                        if (((TargetTypeAware) message.getSender()).getTargetType().equals(getTargetType())) {
                            addElement(message.getArg(MessageArgEnum.ARG_ITEM, PathMetadata.class));
                        }
                    }
                }
                break;
            case ENTITY_SELECTED:
                if (getListViewPanel() != null && getListViewPanel().equals(message.getSender())) {
                    arrangeForState(PanelState.FOCUSED);
                }
                break;
            case ENTITY_DESELECTED:
                if (getListViewPanel() != null && getListViewPanel().equals(message.getSender())) {
                    arrangeForState(PanelState.READY);
                }
                break;
            case VALUE_CHANGED:
                if (message.getSender().equals(getListViewPanel())) {
                    setDirty();
                }
                break;
            default:
                super.processMessage(message);
                break;
        }
    }

    // --------------------- Interface TargetTypeAware ---------------------

    public void addElement(PathMetadata pathMetadata) {
        if (getListViewPanel() != null) {
            RenderElement element = ContextUtil.getEntityFactory().buildRenderElement(DUMMY_RENDER_SCHEME);
            element.setPropertyPath(pathMetadata);
            element.setFriendlyName(pathMetadata.getFriendlyName());
            element.setFormat(pathMetadata.getLastStep().getFormat());
            element.setStyle(pathMetadata.getLastStep().getStyle());
            element.setAlign(pathMetadata.getLastStep().getAlign());
            getListViewPanel().add(element);
            getListViewPanel().setTargetEntity(element);
            setDirty();
        }
    }

    // -------------------------- OTHER METHODS --------------------------

    private void setDirty() {
        if (hasCommand(CommandEnum.RENDER_SCHEME_LOOKUP)) {
            getCommand(CommandEnum.RENDER_SCHEME_LOOKUP).dispatchMessage(ContextUtil.getMessage(MessageEnum
                    .ENTITY_UPDATED, this));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        if (!hasCenter() && getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            setCenter(ContextUtil.getDefaultPanel(ListViewPanel.class));
            setSettingValue(SettingEnum.CENTER_MERGE_COMMANDS, false);
            getListViewPanel().setSettingValue(SettingEnum.SUPRESS_COMMANDS, true);
            getListViewPanel().setInMemoryMode(true);
            getCenter().render();
        } else if (hasCenter() && !getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            subpanels.remove(getCenter());
        }

        if (!hasWest() && getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            setWest(ContextUtil.getDefaultPanel(ModelHierarchyPanel.class));
            getModelHierarchyPanel().allowOneToMany(false);
            setSettingValue(SettingEnum.WEST_MERGE_COMMANDS, true);
            getModelHierarchyPanel().setSettingValue(SettingEnum.SUPRESS_COMMANDS, false);
            getModelHierarchyPanel().supressCommands(true);
            getWest().render();
        } else if (hasWest() && !getSettingValue(SettingEnum.WEST_ENABLED, false)) {
            subpanels.remove(getWest());
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_TYPE.equals(id)) {
            arrangeForNullTargetType();
            if (getListViewPanel() != null) {
                // we need this double assignment in order to force a target
                // type re-arrangemnet
                // since this listview is always of RenderElement type.

                RenderScheme scheme = RenderSchemeUtil.getRenderScheme(getClass().getCanonicalName(),
                        RenderElement.class, SchemeType.LIST_SCHEME);

                if (scheme == null) {
                    scheme = RenderSchemeUtil.createDefaultRenderScheme(RenderElement.class, SchemeType.LIST_SCHEME,
                            CoreUtil.getUserLocale(), new String[]{RenderElement.FLD_ID,
                                    RenderElement.FLD_RENDER_SCHEME});
                    scheme.setName(getClass().getCanonicalName());
                    scheme.setFriendlyName(getClass().getSimpleName());
                    ContextUtil.getDWS().save(scheme);
                }
                getListViewPanel().setSettingValue(SettingEnum.RENDER_SCHEME_FOR_VIEW, scheme.getName());

                getListViewPanel().setSettingValue(id, null);
                getListViewPanel().setSettingValue(id, RenderElement.class);

            }
            if (getModelHierarchyPanel() != null) {
                getModelHierarchyPanel().setSettingValue(id, newValue);
            }

            if (hasTargetType()) {
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.RENDER_SCHEME_LOOKUP, this));
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.CLEAR, this));
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.REMOVE, this));
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.EDIT, this));
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_UP, this));
                registerCommand(ContextUtil.getDefaultCommand(CommandEnum.MOVE_DOWN, this));
                arrangeForState(PanelState.READY);

                if (getModelHierarchyPanel() != null) {
                    if (getSchemeType() == SchemeType.ENTITY_SCHEME) {
                        getModelHierarchyPanel().allowOneToOne(true);
                        getModelHierarchyPanel().allowOneToOneSubset(true);
                        getModelHierarchyPanel().associationsExpanded(false);
                        getModelHierarchyPanel().refresh();
                    }
                }
            }
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    private void arrangeForNullTargetType() {
        if (getCommandRenderer() != null) {
            getCommandRenderer().reset();
        }
        unregisterCommand(CommandEnum.RENDER_SCHEME_LOOKUP);
        unregisterCommand(CommandEnum.CLEAR);
        unregisterCommand(CommandEnum.REMOVE);
        unregisterCommand(CommandEnum.EDIT);
        unregisterCommand(CommandEnum.MOVE_UP);
        unregisterCommand(CommandEnum.MOVE_DOWN);

        arrangeForState(PanelState.UNDEFINED);
    }

    private ModelHierarchyPanel getModelHierarchyPanel() {
        return (ModelHierarchyPanel) getWest();
    }

    public SchemeType getSchemeType() {
        return getSettingValue(SettingEnum.SCHEME_TYPE, null);
    }

    public void setSchemeType(SchemeType schemeType) {
        setSettingValue(SettingEnum.SCHEME_TYPE, schemeType);
    }

    @Override
    protected void arrangeForState(PanelState newState) {
        if (state == newState) {
            return;
        }
        state = newState;
        activateCommands(false);
        activateCommand(CommandEnum.DESIGN, true);
        activateCommand(CommandEnum.LOCALIZE, true);

        switch (state) {
            case READY:
                activateCommand(CommandEnum.CLEAR, true);
                break;
            case FOCUSED:
                activateCommand(CommandEnum.CLEAR, true);
                activateCommand(CommandEnum.REMOVE, true);
                activateCommand(CommandEnum.EDIT, true);
                activateCommand(CommandEnum.MOVE_UP, true);
                activateCommand(CommandEnum.MOVE_DOWN, true);
                break;
        }
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.REMOVE.equals(command.getId())) {
            if (getListViewPanel() != null) {
                if (getListViewPanel().hasTargetEntity()) {
                    getListViewPanel().removeSelected();
                    arrangeForState(PanelState.READY);
                    setDirty();
                }
            }
        } else if (CommandEnum.EDIT.equals(command.getId())) {
            if (getListViewPanel() != null) {
                Command update = getListViewPanel().getCommand(CommandEnum.UPDATE);
                if (update != null) {
                    update.process();
                }
            }
        } else if (CommandEnum.MOVE_UP.equals(command.getId())) {
            if (getListViewPanel() != null) {
                if (getListViewPanel().hasTargetEntity()) {
                    if (getListViewPanel().moveUpSelected()) {
                        setDirty();
                    }
                }
            }
        } else if (CommandEnum.MOVE_DOWN.equals(command.getId())) {
            if (getListViewPanel() != null) {
                if (getListViewPanel().hasTargetEntity()) {
                    if (getListViewPanel().moveDownSelected()) {
                        setDirty();
                    }
                }
            }
        } else if (CommandEnum.CLEAR.equals(command.getId())) {
            clear();
            setDirty();
        } else {
            super.processValidCommand(command);
        }
    }

    private void clear() {
        if (getListViewPanel() != null) {
            getListViewPanel().clear();
        }
        arrangeForState(PanelState.READY);
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();

        registerSetting(SettingEnum.TARGET_TYPE, null);
        registerSetting(SettingEnum.SCHEME_TYPE, null);

        // unregister unwanted BorderLayoutPanlel settings
        unregisterSetting(SettingEnum.NORTH_ENABLED);
        unregisterSetting(SettingEnum.NORTH_OPEN);
        unregisterSetting(SettingEnum.NORTH_COLLAPSIBLE);
        unregisterSetting(SettingEnum.NORTH_SPLITTABLE);
        unregisterSetting(SettingEnum.NORTH_HEIGHT);
        unregisterSetting(SettingEnum.NORTH_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.NORTH_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.NORTH_CHILD_INDEX);
        unregisterSetting(SettingEnum.SOUTH_ENABLED);
        unregisterSetting(SettingEnum.SOUTH_OPEN);
        unregisterSetting(SettingEnum.SOUTH_COLLAPSIBLE);
        unregisterSetting(SettingEnum.SOUTH_SPLITTABLE);
        unregisterSetting(SettingEnum.SOUTH_HEIGHT);
        unregisterSetting(SettingEnum.SOUTH_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.SOUTH_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.SOUTH_CHILD_INDEX);
        unregisterSetting(SettingEnum.EAST_ENABLED);
        unregisterSetting(SettingEnum.EAST_OPEN);
        unregisterSetting(SettingEnum.EAST_COLLAPSIBLE);
        unregisterSetting(SettingEnum.EAST_SPLITTABLE);
        unregisterSetting(SettingEnum.EAST_WIDTH);
        unregisterSetting(SettingEnum.EAST_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.EAST_EXCLUDE_CRUD_COMMANDS);
        unregisterSetting(SettingEnum.EAST_CHILD_INDEX);
    }
}
