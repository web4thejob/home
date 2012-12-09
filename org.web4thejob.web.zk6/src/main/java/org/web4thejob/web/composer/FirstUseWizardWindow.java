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

package org.web4thejob.web.composer;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.message.MessageListener;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.parameter.*;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.OrderBy;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.orm.scheme.RenderScheme;
import org.web4thejob.orm.scheme.RenderSchemeUtil;
import org.web4thejob.orm.scheme.SchemeType;
import org.web4thejob.security.*;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.util.L10nMessages;
import org.web4thejob.util.L10nUtil;
import org.web4thejob.web.dialog.PasswordDialog;
import org.web4thejob.web.panel.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.1.1
 */

public class FirstUseWizardWindow extends GenericForwardComposer<Window> {
    private static final long serialVersionUID = 1L;
    private Button btnPrev;
    private Button btnNext;
    private Component stepContainer;
    private Label stepTitle;
    private List<Step> steps = new ArrayList<Step>(3);

    private Step step;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        steps.add(new Step1());
        steps.add(new Step2());
        steps.add(new Step3());
        renderStep(0);
    }

    private void renderStep(int index) {
        step = steps.get(index);
        btnPrev.setDisabled(index == 0);
        btnNext.setDisabled(index == steps.size() - 1);
        step.render();
    }

    public void onClick$btnPrev(MouseEvent event) throws Exception {
        stepContainer.getChildren().clear();
        renderStep(steps.indexOf(step) - 1);
    }

    public void onClick$btnNext(MouseEvent event) throws Exception {
        stepContainer.getChildren().clear();
        renderStep(steps.indexOf(step) + 1);
    }


    private abstract class Step {

        public abstract void render();
    }

    private class Step1 extends Step {
        Html body;

        @Override
        public void render() {
            stepTitle.setValue(L10nUtil.getMessage(getClass(), "title", "Welcome!"));
            getBody(stepContainer, L10nUtil.getMessage(getClass(), "body", ""));
        }
    }

    private Html getBody(Component parent, String content) {
        Html body = new Html();
        body.setSclass("contentText");
        body.setHflex("true");
        body.setVflex("true");
        body.setParent(parent);
        body.setZclass("z-label");
        body.setContent(content);
        return body;
    }

    private class Step2 extends Step {

        @Override
        public void render() {
            FirstUseWizardWindow.this.btnNext.setDisabled(true);
            stepTitle.setValue(L10nUtil.getMessage(getClass(), "title", "License Agreement"));

            Vbox vlayout = new Vbox();
            vlayout.setHflex("true");
            vlayout.setVflex("true");
            vlayout.setParent(stepContainer);

            getBody(vlayout, L10nUtil.getMessage(getClass(), "body", "")).setVflex("false");

            Iframe iframe = new Iframe();
            iframe.setParent(vlayout);
            iframe.setHflex("true");
            iframe.setHeight("2800px");
            iframe.setSrc("/license/en/lgpl.txt");

            Checkbox checkbox = new Checkbox(L10nUtil.getMessage(getClass(), "accept_license",
                    "I accept the license agreement terms."));
            checkbox.setParent(vlayout);
            checkbox.setSclass("contentText");
            checkbox.setZclass("z-label");
            checkbox.addEventListener(Events.ON_CHECK, new EventListener<CheckEvent>() {
                @Override
                public void onEvent(CheckEvent event) throws Exception {
                    FirstUseWizardWindow.this.btnNext.setDisabled(!event.isChecked());
                }
            });

        }
    }

    private class Step3 extends Step {

        @Override
        public void render() {
            stepTitle.setValue(L10nUtil.getMessage(getClass(), "title", "Administrative Account setup"));

            Vbox vlayout = new Vbox();
            vlayout.setHflex("true");
            vlayout.setVflex("true");
            vlayout.setParent(stepContainer);

            getBody(vlayout, L10nUtil.getMessage(getClass(), "body", "")).setVflex("false");

            Space space = new Space();
            space.setSpacing("20px");
            space.setParent(vlayout);


            A a = new A(L10nUtil.getMessage(getClass(), "set_password_link", "Click here to set password"));
            a.setParent(vlayout);
            a.setSclass("contentLink");
            a.setImage("../img/KEY_32.png");
            a.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {
                @Override
                public void onEvent(MouseEvent event) throws Exception {
                    final UserIdentity admin = ContextUtil.getSecurityService().getAdministratorIdentity();
                    PasswordDialog dialog = ContextUtil.getDefaultDialog(PasswordDialog.class, admin, false);
                    dialog.show(new FirstUseConfiguration(admin));
                }
            });
        }
    }

    private class FirstUseConfiguration implements MessageListener,
            EventListener<Event>, TransactionCallback<Boolean> {
        private UserIdentity userIdentity;
        private String passwd;

        public FirstUseConfiguration(UserIdentity userIdentity) {
            this.userIdentity = userIdentity;
            stepContainer.addEventListener(Events.ON_USER, this);
        }

        @Override
        public void onEvent(Event event) throws Exception {
            Clients.clearBusy();
            if (ContextUtil.getTransactionWrapper().execute(this)) {
                ContextUtil.getSessionContext().refresh();
                Executions.sendRedirect("/");
            }

        }

        @Override
        public void processMessage(Message message) {
            if (message.getId() == MessageEnum.AFFIRMATIVE_RESPONSE) {
                Clients.showBusy(L10nMessages.L10N_MSG_PREPARE_FIRST_USE.toString());
                passwd = message.getArg(MessageArgEnum.ARG_ITEM, String.class);
                Events.echoEvent(Events.ON_USER, stepContainer, null);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Boolean doInTransaction(TransactionStatus status) {
            userIdentity.setPassword(ContextUtil.getBean(SecurityService.class).encodePassword(userIdentity,
                    passwd));
            ContextUtil.getDWS().save(userIdentity);

            if (ContextUtil.getSecurityService().authenticate(userIdentity.getCode(), passwd, true) == null) {
                throw new RuntimeException();
            }

            Map<org.web4thejob.web.panel.Panel, String> beanIds = new HashMap<org.web4thejob.web.panel.Panel, String>();

            ListViewPanel panels = ContextUtil.getDefaultPanel(ListViewPanel.class);
            panels.setTargetType(PanelDefinition.class);
            panels.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(PanelDefinition
                    .class).getFriendlyName());
            beanIds.put(panels, ORMUtil.persistPanel(panels));

            ListViewPanel roles = ContextUtil.getDefaultPanel(ListViewPanel.class);
            roles.setTargetType(RoleIdentity.class);
            roles.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(RoleIdentity
                    .class).getFriendlyName());
            beanIds.put(roles, ORMUtil.persistPanel(roles));

            ListViewPanel users = ContextUtil.getDefaultPanel(ListViewPanel.class);
            users.setTargetType(UserIdentity.class);
            users.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(UserIdentity
                    .class).getFriendlyName());
            beanIds.put(users, ORMUtil.persistPanel(users));

            ListViewPanel policies = ContextUtil.getDefaultPanel(ListViewPanel.class);
            policies.setTargetType(AuthorizationPolicy.class);
            policies.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(AuthorizationPolicy
                    .class).getFriendlyName());
            beanIds.put(policies, ORMUtil.persistPanel(policies));

            //----------------------------------------------------------------------------------------------------------
            //Parameters
            //----------------------------------------------------------------------------------------------------------
            ListViewPanel entityViewParameters = ContextUtil.getDefaultPanel(ListViewPanel.class);
            entityViewParameters.setTargetType(EntityTypeEntityViewParameter.class);
            entityViewParameters.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata
                    (EntityTypeEntityViewParameter.class).getFriendlyName());
            beanIds.put(entityViewParameters, ORMUtil.persistPanel(entityViewParameters));

            ListViewPanel listViewParameters = ContextUtil.getDefaultPanel(ListViewPanel.class);
            listViewParameters.setTargetType(EntityTypeListViewParameter.class);
            listViewParameters.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata
                    (EntityTypeListViewParameter.class).getFriendlyName());
            beanIds.put(listViewParameters, ORMUtil.persistPanel(listViewParameters));

            ListViewPanel queryParameters = ContextUtil.getDefaultPanel(ListViewPanel.class);
            queryParameters.setTargetType(EntityTypeQueryParameter.class);
            queryParameters.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata
                    (EntityTypeQueryParameter.class).getFriendlyName());
            beanIds.put(queryParameters, ORMUtil.persistPanel(queryParameters));

            ListViewPanel charsetParameters = ContextUtil.getDefaultPanel(ListViewPanel.class);
            charsetParameters.setTargetType(PrinterCharsetParameter.class);
            charsetParameters.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata
                    (PrinterCharsetParameter.class).getFriendlyName());
            beanIds.put(charsetParameters, ORMUtil.persistPanel(charsetParameters));

            ListViewPanel imageRepoParameters = ContextUtil.getDefaultPanel(ListViewPanel.class);
            imageRepoParameters.setTargetType(LocationImagesRepoParameter.class);
            imageRepoParameters.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata
                    (LocationImagesRepoParameter.class).getFriendlyName());
            beanIds.put(imageRepoParameters, ORMUtil.persistPanel(imageRepoParameters));
            //----------------------------------------------------------------------------------------------------------

            ListViewPanel renderSchemes = ContextUtil.getDefaultPanel(ListViewPanel.class);
            renderSchemes.setTargetType(RenderScheme.class);
            renderSchemes.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(RenderScheme
                    .class).getFriendlyName());
            beanIds.put(renderSchemes, ORMUtil.persistPanel(renderSchemes));

            ListViewPanel queries = ContextUtil.getDefaultPanel(ListViewPanel.class);
            queries.setTargetType(Query.class);
            queries.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(Query
                    .class).getFriendlyName());
            beanIds.put(queries, ORMUtil.persistPanel(queries));

            ModuleInfoPanel moduleInfoPanel = ContextUtil.getDefaultPanel(ModuleInfoPanel.class);
            moduleInfoPanel.setSettingValue(SettingEnum.PANEL_NAME, moduleInfoPanel.toString());
            beanIds.put(moduleInfoPanel, ORMUtil.persistPanel(moduleInfoPanel));

            ListViewPanel members = ContextUtil.getDefaultPanel(ListViewPanel.class);
            members.setTargetType(RoleMembers.class);
            members.setMasterType(UserIdentity.class);
            members.setBindProperty(RoleMembers.FLD_USER);
            members.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(RoleMembers
                    .class).getFriendlyName());
            RenderScheme scheme = RenderSchemeUtil.createDefaultRenderScheme(RoleMembers.class,
                    SchemeType.LIST_SCHEME);
            scheme.setName(scheme.getName() + "\\user bound");
            scheme.getElements().clear();
            RenderElement renderElement = scheme.addElement(ContextUtil.getMRS().getPropertyPath(RoleMembers
                    .class, new Path(RoleMembers.FLD_ROLE)));
            renderElement.setFriendlyName(ContextUtil.getMRS().getEntityMetadata(RoleIdentity.class).getFriendlyName());
            renderElement = scheme.addElement(ContextUtil.getMRS().getPropertyPath(RoleMembers.class,
                    new String[]{RoleMembers.FLD_ROLE, RoleIdentity.FLD_AUTHORIZATION_POLICY}));
            renderElement.setFriendlyName(ContextUtil.getMRS().getEntityMetadata(AuthorizationPolicy.class)
                    .getFriendlyName());
            ContextUtil.getDWS().save(scheme);
            members.setSettingValue(SettingEnum.RENDER_SCHEME_FOR_VIEW, scheme.getName());


            ListViewPanel elements = ContextUtil.getDefaultPanel(ListViewPanel.class);
            elements.setTargetType(RenderElement.class);
            elements.setMasterType(RenderScheme.class);
            elements.setBindProperty(RenderElement.FLD_RENDER_SCHEME);
            elements.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(RenderElement
                    .class).getFriendlyName());
            scheme = RenderSchemeUtil.createDefaultRenderScheme(RenderElement.class,
                    SchemeType.LIST_SCHEME, new String[]{RenderElement.FLD_RENDER_SCHEME});
            scheme.setName(scheme.getName() + "\\scheme bound");
            ContextUtil.getDWS().save(scheme);
            elements.setSettingValue(SettingEnum.RENDER_SCHEME_FOR_VIEW, scheme.getName());

            ListViewPanel criteria = ContextUtil.getDefaultPanel(ListViewPanel.class);
            criteria.setTargetType(Criterion.class);
            criteria.setMasterType(Query.class);
            criteria.setBindProperty(Criterion.FLD_QUERY);
            criteria.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(Criterion
                    .class).getFriendlyName());
            scheme = RenderSchemeUtil.createDefaultRenderScheme(Criterion.class,
                    SchemeType.LIST_SCHEME, new String[]{Criterion.FLD_QUERY});
            scheme.setName(scheme.getName() + "\\query bound");
            ContextUtil.getDWS().save(scheme);
            criteria.setSettingValue(SettingEnum.RENDER_SCHEME_FOR_VIEW, scheme.getName());

            ListViewPanel orderings = ContextUtil.getDefaultPanel(ListViewPanel.class);
            orderings.setTargetType(OrderBy.class);
            orderings.setMasterType(Query.class);
            orderings.setBindProperty(OrderBy.FLD_QUERY);
            orderings.setSettingValue(SettingEnum.PANEL_NAME, ContextUtil.getMRS().getEntityMetadata(OrderBy
                    .class).getFriendlyName());
            scheme = RenderSchemeUtil.createDefaultRenderScheme(OrderBy.class,
                    SchemeType.LIST_SCHEME, new String[]{OrderBy.FLD_QUERY});
            scheme.setName(scheme.getName() + "\\query bound");
            ContextUtil.getDWS().save(scheme);
            orderings.setSettingValue(SettingEnum.RENDER_SCHEME_FOR_VIEW, scheme.getName());

            ContextUtil.getSessionContext().refresh();

            BorderedLayoutPanel userRoles = ContextUtil.getDefaultPanel(BorderedLayoutPanel.class);
            userRoles.setCenter(ContextUtil.getPanel(beanIds.get(users)));
            userRoles.setSouth(members);
            userRoles.setSettingValue(SettingEnum.NORTH_ENABLED, false);
            userRoles.setSettingValue(SettingEnum.EAST_ENABLED, false);
            userRoles.setSettingValue(SettingEnum.WEST_ENABLED, false);
            userRoles.setSettingValue(SettingEnum.CENTER_ENABLED, true);
            userRoles.setSettingValue(SettingEnum.SOUTH_ENABLED, true);
            userRoles.setSettingValue(SettingEnum.SOUTH_COLLAPSIBLE, true);
            userRoles.setSettingValue(SettingEnum.SOUTH_SPLITTABLE, true);
            userRoles.setSettingValue(SettingEnum.SOUTH_MERGE_COMMANDS, false);
            userRoles.setSettingValue(SettingEnum.SOUTH_HEIGHT, "50%");
            userRoles.setSettingValue(SettingEnum.PANEL_NAME, users.toString() + " & " + members.toString());
            userRoles.render();
            beanIds.put(userRoles, ORMUtil.persistPanel(userRoles));

            BorderedLayoutPanel schemeElements = ContextUtil.getDefaultPanel(BorderedLayoutPanel.class);
            schemeElements.setCenter(ContextUtil.getPanel(beanIds.get(renderSchemes)));
            schemeElements.setSouth(elements);
            schemeElements.setSettingValue(SettingEnum.NORTH_ENABLED, false);
            schemeElements.setSettingValue(SettingEnum.EAST_ENABLED, false);
            schemeElements.setSettingValue(SettingEnum.WEST_ENABLED, false);
            schemeElements.setSettingValue(SettingEnum.CENTER_ENABLED, true);
            schemeElements.setSettingValue(SettingEnum.SOUTH_ENABLED, true);
            schemeElements.setSettingValue(SettingEnum.SOUTH_COLLAPSIBLE, true);
            schemeElements.setSettingValue(SettingEnum.SOUTH_SPLITTABLE, true);
            schemeElements.setSettingValue(SettingEnum.SOUTH_MERGE_COMMANDS, false);
            schemeElements.setSettingValue(SettingEnum.SOUTH_HEIGHT, "50%");
            schemeElements.setSettingValue(SettingEnum.PANEL_NAME, renderSchemes.toString());
            schemeElements.render();
            beanIds.put(schemeElements, ORMUtil.persistPanel(schemeElements));

            BorderedLayoutPanel queriesElements = ContextUtil.getDefaultPanel(BorderedLayoutPanel.class);
            queriesElements.setCenter(ContextUtil.getPanel(beanIds.get(queries)));
            TabbedLayoutPanel tabbedLayoutPanel = ContextUtil.getDefaultPanel(TabbedLayoutPanel.class);
            tabbedLayoutPanel.setSettingValue(SettingEnum.DISABLE_DYNAMIC_TAB_TITLE, true);
            tabbedLayoutPanel.getSubpanels().add(criteria);
            tabbedLayoutPanel.getSubpanels().add(orderings);
            tabbedLayoutPanel.setSelectedIndex(0);
            queriesElements.setSouth(tabbedLayoutPanel);
            queriesElements.setSettingValue(SettingEnum.NORTH_ENABLED, false);
            queriesElements.setSettingValue(SettingEnum.EAST_ENABLED, false);
            queriesElements.setSettingValue(SettingEnum.WEST_ENABLED, false);
            queriesElements.setSettingValue(SettingEnum.CENTER_ENABLED, true);
            queriesElements.setSettingValue(SettingEnum.SOUTH_ENABLED, true);
            queriesElements.setSettingValue(SettingEnum.SOUTH_COLLAPSIBLE, true);
            queriesElements.setSettingValue(SettingEnum.SOUTH_SPLITTABLE, true);
            queriesElements.setSettingValue(SettingEnum.SOUTH_MERGE_COMMANDS, false);
            queriesElements.setSettingValue(SettingEnum.SOUTH_HEIGHT, "50%");
            queriesElements.setSettingValue(SettingEnum.PANEL_NAME, queries.toString());
            queriesElements.render();
            beanIds.put(queriesElements, ORMUtil.persistPanel(queriesElements));

            ContextUtil.getSessionContext().refresh();

            AuthorizationPolicyPanel policyPanel = ContextUtil.getDefaultPanel(AuthorizationPolicyPanel.class);
            policyPanel.render();
            MenuAuthorizationPanel<Treeitem> menuAuthorizationPanel = (MenuAuthorizationPanel<Treeitem>) policyPanel
                    .getMenuAuthorizationPanel();
            Treeitem rootItem = menuAuthorizationPanel.getRootItem();
            Treeitem securityMenu = menuAuthorizationPanel.renderAddedMenu(rootItem,
                    L10nMessages.L10N_NAME_DEFAULT_SECURITY_MENU.toString());
            menuAuthorizationPanel.renderAddedPanel(securityMenu, ContextUtil.getPanel(beanIds.get(userRoles)));
            menuAuthorizationPanel.renderAddedPanel(securityMenu, ContextUtil.getPanel(beanIds.get(policies)));
            menuAuthorizationPanel.renderAddedPanel(securityMenu, ContextUtil.getPanel(beanIds.get(roles)));

            Treeitem panelsMenu = menuAuthorizationPanel.renderAddedMenu(rootItem,
                    L10nMessages.L10N_NAME_DEFAULT_PANELS_MENU.toString());
            menuAuthorizationPanel.renderAddedPanel(panelsMenu, ContextUtil.getPanel(beanIds.get(panels)));
            menuAuthorizationPanel.renderAddedPanel(panelsMenu, ContextUtil.getPanel(beanIds.get(schemeElements)));
            menuAuthorizationPanel.renderAddedPanel(panelsMenu, ContextUtil.getPanel(beanIds.get(queriesElements)));
            menuAuthorizationPanel.renderAddedPanel(panelsMenu, ContextUtil.getPanel(beanIds.get(moduleInfoPanel)));

            Treeitem paramsMenu = menuAuthorizationPanel.renderAddedMenu(rootItem,
                    L10nMessages.L10N_NAME_DEFAULT_PARAMETERS_MENU.toString());
            menuAuthorizationPanel.renderAddedPanel(paramsMenu, ContextUtil.getPanel(beanIds.get
                    (entityViewParameters)));
            menuAuthorizationPanel.renderAddedPanel(paramsMenu, ContextUtil.getPanel(beanIds.get(listViewParameters)));
            menuAuthorizationPanel.renderAddedPanel(paramsMenu, ContextUtil.getPanel(beanIds.get(queryParameters)));
            menuAuthorizationPanel.renderAddedPanel(paramsMenu, ContextUtil.getPanel(beanIds.get(charsetParameters)));
            menuAuthorizationPanel.renderAddedPanel(paramsMenu, ContextUtil.getPanel(beanIds.get(imageRepoParameters)));


            AuthorizationPolicy authorizationPolicy = ContextUtil.getEntityFactory().buildAuthorizationPolicy();
            authorizationPolicy.setName(L10nMessages.L10N_NAME_DEFAULT_SECURITY_MENU.toString());
            authorizationPolicy.setDefinition(policyPanel.getDefinition());
            ContextUtil.getDWS().save(authorizationPolicy);

            RoleIdentity role = userIdentity.getRoles().iterator().next().getRole();
            role.setAuthorizationPolicy(authorizationPolicy);
            ContextUtil.getDWS().save(role);

            //--------------------------------------------------------------------------------------------------------
            // ANYONE role and default query for sorting identities
            //--------------------------------------------------------------------------------------------------------
            role = ContextUtil.getEntityFactory().buildRoleIdentity();
            role.setCode("ANYONE");
            role.setIndex(1000);
            ContextUtil.getDWS().save(role);

            RoleMembers roleMembers = ContextUtil.getEntityFactory().buildRoleMembers();
            roleMembers.setRole(role);
            roleMembers.setUser(userIdentity);
            ContextUtil.getDWS().save(roleMembers);

            Query query = ContextUtil.getEntityFactory().buildQuery(Identity.class);
            query.setName("by code");
            query.setCached(true);
            query.addOrderBy(new Path(Identity.FLD_CODE));
            ContextUtil.getDWS().save(query);

            EntityTypeQueryParameter entityTypeQueryParameter = ContextUtil.getEntityFactory().buildParameter
                    (EntityTypeQueryParameter.class);
            entityTypeQueryParameter.setOwner(role);
            entityTypeQueryParameter.setKey(Identity.class.getCanonicalName());
            entityTypeQueryParameter.setValue(Long.valueOf(query.getId()).toString());
            ContextUtil.getDWS().save(entityTypeQueryParameter);

            return true;

        }

    }

}
