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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.util.StringUtils;
import org.web4thejob.command.Command;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.PropertyMetadata;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.RenderSchemeUtil;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.print.Printer;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.dialog.RenderSchemeDialog;
import org.web4thejob.web.panel.base.AbstractMutablePanel;
import org.web4thejob.web.util.ZkUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.*;

import java.io.File;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.2.0
 */

@org.springframework.stereotype.Component
@Scope("prototype")
public class DefaultTabbedEntityViewPanel extends AbstractMutablePanel implements TabbedEntityViewPanel {
    private static final String BEAN_ID = "beanid";
    private Tabbox tabbox = new Tabbox();
    private List<RenderElement> oneToOneElements = new ArrayList<RenderElement>();
    private List<RenderScheme> renderSchemes = new ArrayList<RenderScheme>(2);

    protected DefaultTabbedEntityViewPanel() {
        this(MutableMode.READONLY);
    }

    protected DefaultTabbedEntityViewPanel(MutableMode mutableMode) {
        super(mutableMode);
        ZkUtil.setParentOfChild((Component) base, tabbox);
        tabbox.setWidth("100%");
        tabbox.setVflex("true");
        tabbox.setMold("accordion-lite");
        new Tabs().setParent(tabbox);
        new Tabpanels().setParent(tabbox);
    }

    @Override
    protected void registerSettings() {
        registerSetting(SettingEnum.RENDER_SCHEME_FOR_VIEW, null);
        registerSetting(SettingEnum.RENDER_SCHEME_FOR_UPDATE, null);
        registerSetting(SettingEnum.RENDER_SCHEME_FOR_INSERT, null);
        super.registerSettings();
    }

    @Override
    public Set<CommandEnum> getSupportedCommands() {
        Set<CommandEnum> supported = new HashSet<CommandEnum>(super.getSupportedCommands());
        supported.add(CommandEnum.CONFIGURE_HEADERS);
        return Collections.unmodifiableSet(supported);
    }

    @Override
    protected void afterSettingsSet() {
        super.afterSettingsSet();
        arrangeForRenderSchemes();
    }

    private int getIndexOfType(Class<? extends Entity> entityType) {
        if (entityType.equals(getTargetType())) {
            return 0;
        } else {
            int i = 1;
            for (RenderElement renderElement : oneToOneElements) {
                if (renderElement.getRenderScheme().getTargetType().equals(entityType)) {
                    return i;
                }
                i++;
            }
        }

        return -1;
    }


    private String getSettingPart(SettingEnum id, int index, String defaultValue) {
        String value = getSettingValue(id, defaultValue);
        if (value != null && StringUtils.hasText(value)) {
            String[] parts = StringUtils.tokenizeToStringArray(value, ",", true, false);
            if (parts.length > index) {
                return parts[index];
            }
        }

        return defaultValue;
    }

    private RenderScheme getRenderScheme(Class<? extends Entity> entityType) {
        int indexOfType = getIndexOfType(entityType);
        if (indexOfType >= 0 && renderSchemes.size() > indexOfType && renderSchemes.get(indexOfType) != null) {
            return renderSchemes.get(indexOfType);
        }

        RenderScheme renderScheme = null;
        if (getMutableMode() == MutableMode.READONLY && StringUtils.hasText(getSettingPart(SettingEnum
                .RENDER_SCHEME_FOR_VIEW, indexOfType, ""))) {
            renderScheme = RenderSchemeUtil.getRenderScheme(getSettingPart(SettingEnum.RENDER_SCHEME_FOR_VIEW,
                    indexOfType, ""), entityType, SchemeType.ENTITY_SCHEME);
        } else if (getMutableMode() == MutableMode.UPDATE && StringUtils.hasText(getSettingPart(SettingEnum
                .RENDER_SCHEME_FOR_UPDATE, indexOfType, ""))) {
            renderScheme = RenderSchemeUtil.getRenderScheme(getSettingPart(SettingEnum.RENDER_SCHEME_FOR_UPDATE,
                    indexOfType, ""), entityType, SchemeType.ENTITY_SCHEME);
        } else if (getMutableMode() == MutableMode.INSERT && StringUtils.hasText(getSettingPart(SettingEnum
                .RENDER_SCHEME_FOR_INSERT, indexOfType, ""))) {
            renderScheme = RenderSchemeUtil.getRenderScheme(getSettingPart(SettingEnum.RENDER_SCHEME_FOR_INSERT,
                    indexOfType, ""), entityType, SchemeType.ENTITY_SCHEME);
        }

        if (renderScheme == null) {
            renderScheme = RenderSchemeUtil.getDefaultRenderScheme(entityType, SchemeType.ENTITY_SCHEME);
        }

        return renderScheme;
    }

    private Tabpanel buildTabpanel(String name) {
        Tab tab = new Tab(name, "img/TAG.png");
        tab.setParent(tabbox.getTabs());
        tab.setClosable(false);
        Tabpanel tabpanel = new Tabpanel();
        tabpanel.setParent(tabbox.getTabpanels());
        return tabpanel;
    }

    private void arrangeForRenderSchemes() {
        if (!hasTargetType()) {
            return;
        }

        tabbox.getTabs().getChildren().clear();
        tabbox.getTabpanels().getChildren().clear();

        dataBinder = new DataBinder();
        for (RenderScheme renderScheme : renderSchemes) {
            Grid grid = buildGrid();
            grid.setParent(buildTabpanel(ContextUtil.getMRS().getEntityMetadata(renderScheme.getTargetType())
                    .getFriendlyName()));
            super.arrangeForRenderScheme(grid, renderScheme, (String) renderScheme.getAttribute(BEAN_ID));
        }
    }

    private String buildBeanIdFromRenderScheme(RenderScheme renderScheme) {
        return StringUtils.uncapitalize(renderScheme.getTargetType().getSimpleName());
    }


    private List<Entity> getTargetEntities() {
        List<Entity> entities = new ArrayList<Entity>(renderSchemes.size());

        for (int i = 0; i < renderSchemes.size(); i++) {
            if (i == 0) {
                entities.add(getTargetEntityDirect());
            } else {
                RenderElement renderElement = oneToOneElements.get(i - 1);
                Entity subsetBean = null;
                if (getTargetEntityDirect() != null) {
                    subsetBean = renderElement.getPropertyPath().getValue(getTargetEntityDirect());
                    if (subsetBean == null && getMutableMode() != MutableMode.READONLY) {

                        PropertyMetadata associationProperty = renderElement.getPropertyPath().getLastStep();
                        subsetBean = ContextUtil.getMRS().newInstance(associationProperty.getAssociatedEntityMetadata()
                                .getEntityType());

                        for (PropertyMetadata propertyMetadata : associationProperty.getAssociatedEntityMetadata()
                                .getPropertiesMetadata()) {
                            if (propertyMetadata.isAssociatedWith(associationProperty)) {
                                propertyMetadata.setValue(subsetBean, getTargetEntityDirect());
                                break;
                            }
                        }

                        associationProperty.setValue(getTargetEntityDirect(), subsetBean);
                    }
                }
                entities.add(subsetBean);
            }
        }

        return entities;
    }

    @Override
    protected void setDataBinderBeans() {
        List<Entity> entities = getTargetEntities();
        int i = 0;
        for (RenderScheme renderScheme : renderSchemes) {
            dataBinder.bindBean((String) renderScheme.getAttribute(BEAN_ID), entities.get(i));
            i++;
        }
    }

    @Override
    protected void persistLocal() throws Exception {
        ContextUtil.getTransactionWrapper().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final List<Entity> entities = new ArrayList<Entity>(renderSchemes.size());
                ContextUtil.getDWS().save(getTargetEntity());

                for (RenderElement renderElement : oneToOneElements) {
                    PropertyMetadata oneToOneProperty = renderElement.getPropertyPath().getLastStep();
                    Entity oneToOneEntity = (Entity) oneToOneProperty.getValue(getTargetEntityDirect());
                    if (oneToOneProperty.getAssociatedEntityMetadata().isTableSubset()) {
                        PropertyMetadata oneToOneId = oneToOneProperty.getAssociatedEntityMetadata().getPropertyMetadata
                                (oneToOneProperty.getAssociatedEntityMetadata().getIdentifierName());
                        oneToOneId.setValue(oneToOneEntity, getTargetEntityDirect().getIdentifierValue());
                    }

                    entities.add(oneToOneEntity);
                }
                ContextUtil.getDWS().save(entities);

            }
        });
    }

    private Collection<RenderElement> getSubsetElementsFromScheme(RenderScheme renderScheme) {
        Collection<RenderElement> elements = new ArrayList<RenderElement>();
        for (RenderElement renderElement : renderScheme.getElements()) {
            if (renderElement.getPropertyPath().getLastStep().isOneToOneType()) {
                elements.add(renderElement);
            }
        }
        return elements;
    }

    public void arrangeForNullTargetType() {
        super.arrangeForNullTargetType();
        oneToOneElements.clear();
        renderSchemes.clear();
        tabbox.getTabs().getChildren().clear();
        tabbox.getTabpanels().getChildren().clear();
    }


    private void setBaseRenderScheme(RenderScheme baseRenderScheme) {
        oneToOneElements.clear();
        renderSchemes.clear();
        tabbox.getTabs().getChildren().clear();
        tabbox.getTabpanels().getChildren().clear();

        baseRenderScheme.setAttribute(BEAN_ID, buildBeanIdFromRenderScheme(baseRenderScheme));
        oneToOneElements.addAll(getSubsetElementsFromScheme(baseRenderScheme));
        renderSchemes.add(baseRenderScheme);
        for (RenderElement renderElement : oneToOneElements) {
            RenderScheme renderScheme = getRenderScheme(renderElement.getPropertyPath().getLastStep()
                    .getAssociatedEntityMetadata().getEntityType());
            renderScheme.setAttribute(BEAN_ID, buildBeanIdFromRenderScheme(renderScheme));
            renderSchemes.add(renderScheme);
        }
    }

    @Override
    protected void arrangeForTargetType() {
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.CONFIGURE_HEADERS, this));
        registerCommand(ContextUtil.getDefaultCommand(CommandEnum.PRINT, this));
        super.arrangeForTargetType();

        setBaseRenderScheme(getRenderScheme(getTargetType()));
        arrangeForRenderSchemes();
    }

    @Override
    protected void processValidCommand(Command command) {
        if (CommandEnum.PRINT.equals(command.getId())) {
            if (hasTargetEntity()) {
                String title = getSettingValue(SettingEnum.PANEL_NAME, null);
                Query query = null;
                if (isMasterDetail() && hasMasterEntity()) {
                    query = ContextUtil.getEntityFactory().buildQuery(getTargetType());
                    query.addCriterion(new Path(getBindProperty()), Condition.EQ, getMasterEntity(), true, true);
                }

                File file = ContextUtil.getBean(Printer.class).print(title, renderSchemes, query,
                        getTargetEntities());
                ZkUtil.downloadCsv(file);
            }
        } else if (CommandEnum.CONFIGURE_HEADERS.equals(command.getId())) {
            if (hasTargetType()) {

                Set<Setting<?>> settingSet = new TreeSet<Setting<?>>();
                RenderScheme renderScheme = renderSchemes.get(tabbox.getSelectedIndex());
                settingSet.add(ContextUtil.getSetting(SettingEnum.TARGET_TYPE, renderScheme.getTargetType()));

                RenderSchemeDialog dialog = ContextUtil.getDefaultDialog(RenderSchemeDialog.class, settingSet,
                        SchemeType.ENTITY_SCHEME, renderScheme);
                dialog.setL10nMode(getL10nMode());
                dialog.show(new RenderSchemeDialogListener());
            }
        } else {
            super.processValidCommand(command);
        }
    }

    private class RenderSchemeDialogListener implements MessageListener {
        @Override
        public void processMessage(Message message) {
            switch (message.getId()) {
                case AFFIRMATIVE_RESPONSE:
                    if (RenderSchemeDialog.class.isInstance(message.getSender())) {
                        int i = tabbox.getSelectedIndex();
                        RenderScheme newScheme = message.getArg(MessageArgEnum.ARG_ITEM,
                                RenderScheme.class);

                        if (i == 0) {
                            setBaseRenderScheme(newScheme);
                        } else {
                            RenderScheme oldScheme = renderSchemes.get(i);
                            newScheme.setAttribute(BEAN_ID, oldScheme.getAttribute(BEAN_ID));
                            renderSchemes.set(i, newScheme);
                        }


                        arrangeForRenderSchemes();
                        setTargetEntity(getTargetEntityDirect());

                        if (i < tabbox.getTabs().getChildren().size()) {
                            tabbox.setSelectedIndex(i);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected Class<? extends MutablePanel> getMutableType() {
        return TabbedEntityViewPanel.class;
    }
}
