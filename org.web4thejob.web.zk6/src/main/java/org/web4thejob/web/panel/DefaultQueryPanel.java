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

package org.web4thejob.web.panel;

import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.command.QueryLookupCommandDecorator;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.EntityFactory;
import org.web4thejob.orm.query.*;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.L10nString;
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
public class DefaultQueryPanel extends AbstractBorderLayoutPanel implements QueryPanel {
    // ------------------------------ FIELDS ------------------------------
    public static final L10nString L10N_TAB_TITLE_CRITERIA = new L10nString(DefaultQueryPanel.class,
            "tab_title_criteria", "Criteria");
    public static final L10nString L10N_TAB_TITLE_ORDERING = new L10nString(DefaultQueryPanel.class,
            "tab_title_ordering", "Ordering");

    private final QueryResultMode queryResultMode;
    private ModelCriteriaPanel modelCriteriaPanel;
    private ModelOrderingsPanel modelOrderByPanel;
    private ListViewPanel listViewPanel;
    private List<Subquery> subqueryConstraints;

    // --------------------------- CONSTRUCTORS ---------------------------

    public DefaultQueryPanel() {
        this(QueryResultMode.RETURN_ONE);
    }

    public DefaultQueryPanel(QueryResultMode queryResultMode) {
        this.queryResultMode = queryResultMode;
    }

    // --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public ListViewPanel getListViewPanel() {
        return listViewPanel;
    }

    @Override
    public Query getQuery() {
        if (!hasTargetType() || modelCriteriaPanel == null) {
            return null;
        }

        Query query = (Query) ZkUtil.getLookupSelectionIfUnique(getCommand(CommandEnum.QUERY_LOOKUP),
                QueryLookupCommandDecorator.class);
        if (query == null) {
            query = ContextUtil.getEntityFactory().buildQuery(getTargetType());
        }
        query.getCriteria().clear();
        query.getOrderings().clear();

        if (modelCriteriaPanel.getCriterionPanel() != null) {
            for (Criterion criterion : modelCriteriaPanel.getCriterionPanel().getCriteria()) {
                query.addCriterion(criterion);
            }
        }


        if (modelOrderByPanel.getOrderByPanel() != null) {
            for (OrderBy orderBy : modelOrderByPanel.getOrderByPanel().getOrderings()) {
                query.addOrderBy(orderBy);
            }
        }

        query.setSubqueries(subqueryConstraints);

        return query;
    }

    @Override
    public boolean hasTargetType() {
        return getTargetType() != null;
    }

    @Override
    public Class<? extends Entity> getTargetType() {
        final Class<? extends Entity> entityType = getSettingValue(SettingEnum.TARGET_TYPE, null);
        if (entityType == null) return null;
        return ContextUtil.getBean(EntityFactory.class).toEntityType(entityType.getName());
    }

    @Override
    public QueryResultMode getQueryResultMode() {
        return queryResultMode;
    }

    // ------------------------ INTERFACE METHODS ------------------------

    // --------------------- Interface CommandAware ---------------------

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.QUERY);
        supported.add(CommandEnum.QUERY_LOOKUP);
        return Collections.unmodifiableSet(supported);
    }

    // --------------------- Interface LookupCommandOwner ---------------------

    @Override
    public void assignLookupDetails(Query query) {
        query.getCriteria().clear();
        query.getOrderings().clear();

        if (modelCriteriaPanel.getCriterionPanel() != null) {
            for (Criterion criterion : modelCriteriaPanel.getCriterionPanel().getCriteria()) {
                Criterion crit = query.addCriterion(criterion);
                if (query.isNewInstance()) {
                    crit.setAsNew();
                }
            }
        }

        if (modelOrderByPanel.getOrderByPanel() != null) {
            for (OrderBy orderBy : modelOrderByPanel.getOrderByPanel().getOrderings()) {
                OrderBy ord = query.addOrderBy(orderBy);
                if (query.isNewInstance()) {
                    ord.setAsNew();
                }
            }
        }

    }

    // --------------------- Interface TargetTypeAware ---------------------

    @Override
    public void setTargetType(Class<? extends Entity> targetType) {
        setSettingValue(SettingEnum.TARGET_TYPE, targetType);
    }

    // -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        if (getQueryResultMode() == QueryResultMode.RETURN_ONE) {
            setSettingValue(SettingEnum.NORTH_ENABLED, true);
            setSettingValue(SettingEnum.CENTER_ENABLED, true);
        } else {
            setSettingValue(SettingEnum.NORTH_ENABLED, true);
            unregisterSetting(SettingEnum.CENTER_ENABLED);
        }

        TabbedLayoutPanel tabbedLayoutPanel;
        if (!hasNorth() && getSettingValue(SettingEnum.NORTH_ENABLED, false)) {
            tabbedLayoutPanel = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
            tabbedLayoutPanel.setSettingValue(SettingEnum.CLOSEABLE_TABS, false);
            tabbedLayoutPanel.setSettingValue(SettingEnum.SHOW_STARTUP_TAB, false);
            setNorth(tabbedLayoutPanel);
            modelCriteriaPanel = ContextUtil.getDefaultPanel(ModelCriteriaPanel.class);
            modelCriteriaPanel.setSettingValue(SettingEnum.PANEL_NAME, L10N_TAB_TITLE_CRITERIA.toString());
            modelCriteriaPanel.setParent(tabbedLayoutPanel);
            modelOrderByPanel = ContextUtil.getDefaultPanel(ModelOrderingsPanel.class);
            modelOrderByPanel.setSettingValue(SettingEnum.PANEL_NAME, L10N_TAB_TITLE_ORDERING.toString());
            modelOrderByPanel.setParent(tabbedLayoutPanel);
            tabbedLayoutPanel.setSelectedIndex(0);
            tabbedLayoutPanel.render();
        } else if (hasNorth() && !getSettingValue(SettingEnum.NORTH_ENABLED, false)) {
            subpanels.remove(getNorth());
        } else {
            tabbedLayoutPanel = (TabbedLayoutPanel) getNorth();
            for (Panel subpanel : tabbedLayoutPanel.getSubpanels()) {
                if (subpanel instanceof ModelCriteriaPanel) {
                    modelCriteriaPanel = (ModelCriteriaPanel) subpanel;
                    modelCriteriaPanel.setSettingValue(SettingEnum.PANEL_NAME, L10N_TAB_TITLE_CRITERIA.toString());
                } else if (subpanel instanceof ModelOrderingsPanel) {
                    modelOrderByPanel = (ModelOrderingsPanel) subpanel;
                    modelOrderByPanel.setSettingValue(SettingEnum.PANEL_NAME, L10N_TAB_TITLE_ORDERING.toString());
                }
            }
        }

        if (!hasCenter() && getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            listViewPanel = ContextUtil.getDefaultPanel(ListViewPanel.class);
            setCenter(listViewPanel);
            listViewPanel.setSettingValue(SettingEnum.DISPATCH_DOUBLE_CLICK, true);
            listViewPanel.render();
        } else if (hasCenter() && !getSettingValue(SettingEnum.CENTER_ENABLED, false)) {
            subpanels.remove(getCenter());
        } else {
            listViewPanel = (ListViewPanel) getCenter();
            if (listViewPanel != null) {
                listViewPanel.unregisterCommand(CommandEnum.QUERY);
            }
        }

        if (!hasCenter() && hasNorth()) {
            setSettingValue(SettingEnum.NORTH_HEIGHT, "100%");
        }

    }


    @Override
    protected void afterSettingsSet() {
        super.afterSettingsSet();

        if (hasTargetType()) {
            String queryName = getSettingValue(SettingEnum.PERSISTED_QUERY_NAME, null);
            if (StringUtils.hasText(queryName)) {
                Query query = CoreUtil.getQuery(getTargetType(), queryName);
                if (query != null) {
                    renderAfterLookupChange(query);
                    Command command = getCommand(CommandEnum.QUERY_LOOKUP);
                    if (command != null) {
                        command.setValue(query);
                    }
                }
            }
        }
    }

    @Override
    public void renderAfterLookupChange(Query query) {
        if (query == null || !hasTargetType() || !getTargetType().equals(query.getTargetType())) {
            return;
        }

        if (modelCriteriaPanel != null) {
            modelCriteriaPanel.getCriterionPanel().clearAll();
            for (Criterion criterion : query.getCriteria()) {
                modelCriteriaPanel.getCriterionPanel().addCriterion(criterion);
            }
            modelCriteriaPanel.hideHierarchy(!query.getCriteria().isEmpty());
        }
        if (modelOrderByPanel != null) {
            modelOrderByPanel.getOrderByPanel().clearAll();
            for (OrderBy orderBy : query.getOrderings()) {
                modelOrderByPanel.getOrderByPanel().addOrderBy(orderBy);
            }
            modelOrderByPanel.hideHierarchy(!query.getCriteria().isEmpty());
        }
        if (listViewPanel != null) {
            listViewPanel.clear();
        }
    }

    @Override
    protected <T extends Serializable> void onSettingValueChanged(SettingEnum id, T oldValue, T newValue) {
        if (SettingEnum.TARGET_TYPE.equals(id)) {
            arrangeForNullTargetType();
            if (hasTargetType()) {
                arrangeForTargetType();
            }


            String beanid = CoreUtil.getDefaultListViewName((Class<? extends Entity>) newValue);
            if (beanid != null) {
                if (listViewPanel != null) {
                    subpanels.remove(listViewPanel);
                }

                listViewPanel = (ListViewPanel) ContextUtil.getPanel(beanid);
                listViewPanel.unregisterCommand(CommandEnum.QUERY);
                listViewPanel.unregisterCommand(CommandEnum.RELATED_PANELS);
                listViewPanel.setSettingValue(SettingEnum.DISPATCH_DOUBLE_CLICK, true);
                setCenter(listViewPanel);
                listViewPanel.render();


            } else if (listViewPanel != null) {
                listViewPanel.setSettingValue(id, newValue);
                listViewPanel.unregisterCommand(CommandEnum.QUERY);
            }

            if (modelCriteriaPanel != null) {
                modelCriteriaPanel.setSettingValue(id, newValue);
            }
            if (modelOrderByPanel != null) {
                modelOrderByPanel.setSettingValue(id, newValue);
            }
        } else if (SettingEnum.BIND_PROPERTY.equals(id)) {
            if (modelCriteriaPanel != null) {
                if (modelCriteriaPanel.getModelHierarchyPanel() != null) {
                    modelCriteriaPanel.getModelHierarchyPanel().setSettingValue(id, newValue);
                }
            }
            if (modelOrderByPanel != null) {
                if (modelOrderByPanel.getModelHierarchyPanel() != null) {
                    modelOrderByPanel.getModelHierarchyPanel().setSettingValue(id, newValue);
                }
            }
        }
        super.onSettingValueChanged(id, oldValue, newValue);
    }

    private void arrangeForNullTargetType() {
        if (getCommandRenderer() != null) {
            getCommandRenderer().reset();
        }

        arrangeForState(PanelState.UNDEFINED);
    }

    private void arrangeForTargetType() {
        if (QueryResultMode.RETURN_ONE == queryResultMode) {
            registerCommand(ContextUtil.getDefaultCommand(CommandEnum.QUERY, this));
        }
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.QUERY_LOOKUP, this));

        arrangeForState(PanelState.READY);
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.QUERY.equals(command.getId())) {
            Query query = getQuery();
            if (query != null) {
                if (QueryResultMode.RETURN_ONE == getQueryResultMode() && listViewPanel != null) {
                    listViewPanel.processMessage(ContextUtil.getMessage(MessageEnum.QUERY, this,
                            MessageArgEnum.ARG_ITEM, query));
                } else if (QueryResultMode.RETURN_QUERY == getQueryResultMode()) {
                    dispatchMessage(ContextUtil.getMessage(MessageEnum.QUERY, this, MessageArgEnum.ARG_ITEM,
                            query));
                }
            }
        } else {
            super.processValidCommand(command);
        }
    }

    @Override
    protected void registerSettings() {
        super.registerSettings();

        registerSetting(SettingEnum.TARGET_TYPE, null);
        registerSetting(SettingEnum.BIND_PROPERTY, null);
        registerSetting(SettingEnum.NORTH_HEIGHT, "45%");
        registerSetting(SettingEnum.PERSISTED_QUERY_NAME, null);
        registerSetting(SettingEnum.RUN_QUERY_ON_STARTUP, false);

        // unregister unwanted settings
        unregisterSetting(SettingEnum.SOUTH_ENABLED);
        unregisterSetting(SettingEnum.SOUTH_OPEN);
        unregisterSetting(SettingEnum.SOUTH_COLLAPSIBLE);
        unregisterSetting(SettingEnum.SOUTH_SPLITTABLE);
        unregisterSetting(SettingEnum.SOUTH_HEIGHT);
        unregisterSetting(SettingEnum.SOUTH_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.SOUTH_CHILD_INDEX);
        unregisterSetting(SettingEnum.WEST_ENABLED);
        unregisterSetting(SettingEnum.WEST_OPEN);
        unregisterSetting(SettingEnum.WEST_COLLAPSIBLE);
        unregisterSetting(SettingEnum.WEST_SPLITTABLE);
        unregisterSetting(SettingEnum.WEST_WIDTH);
        unregisterSetting(SettingEnum.WEST_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.WEST_CHILD_INDEX);
        unregisterSetting(SettingEnum.EAST_ENABLED);
        unregisterSetting(SettingEnum.EAST_OPEN);
        unregisterSetting(SettingEnum.EAST_COLLAPSIBLE);
        unregisterSetting(SettingEnum.EAST_SPLITTABLE);
        unregisterSetting(SettingEnum.EAST_WIDTH);
        unregisterSetting(SettingEnum.EAST_MERGE_COMMANDS);
        unregisterSetting(SettingEnum.EAST_CHILD_INDEX);
    }

    @Override
    public void setSubqueryConstraints(List<Subquery> subquery) {
        this.subqueryConstraints = subquery;
    }

    private void setDirty() {
        if (hasCommand(CommandEnum.QUERY_LOOKUP)) {
            getCommand(CommandEnum.QUERY_LOOKUP).dispatchMessage(ContextUtil.getMessage(MessageEnum
                    .ENTITY_UPDATED, this));
        }
    }

    @Override
    public void dispatchMessage(Message message) {
        if (message.getId() == MessageEnum.VALUE_CHANGED) {
            setDirty();
        } else {
            super.dispatchMessage(message);
        }
    }
}
