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

import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.EntityMetadata;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Criterion;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.AuthorizationPolicy;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.EntityViewPanel;
import org.web4thejob.web.panel.ListViewPanel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.List;

/**
 * @author Veniamin Isaias
 * @since 2.1.0
 */
public class SafeModeWindow extends GenericForwardComposer<Window> {
    private static final long serialVersionUID = 1L;
    private Textbox models;


    @Override
    public void doAfterCompose(Window comp) throws Exception {
        if (!ContextUtil.getSessionContext().getSecurityContext().isAdministrator()) {
            Executions.getCurrent().sendRedirect("/");
        }

        super.doAfterCompose(comp);
    }

    public void onClick$btnGo1(MouseEvent event) throws Exception {

        List<PanelDefinition> panels = ContextUtil.getDRS().getAll(PanelDefinition.class);
        for (PanelDefinition panelDefinition : panels) {
            panelDefinition.setBeanId(repalce(panelDefinition.getBeanId()));
            panelDefinition.setDefinition(repalce(panelDefinition.getDefinition()));
            panelDefinition.setType(repalce(panelDefinition.getType()));
            panelDefinition.setTags(repalce(panelDefinition.getTags()));

            ContextUtil.getDWS().save(panelDefinition);
        }

        for (AuthorizationPolicy policy : ContextUtil.getDRS().getAll(AuthorizationPolicy.class)) {
            policy.setDefinition(repalce(policy.getDefinition()));
            ContextUtil.getDWS().save(policy);
        }

        ContextUtil.getSessionContext().refresh();
        ContextUtil.getDRS().evictCache();
        Executions.sendRedirect("../j_spring_security_logout");
    }


    public void onClick$btnGo2(MouseEvent event) throws Exception {
        Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
        query.addCriterion(new Path(PanelDefinition.FLD_TAGS), Condition.CN, "[AUTO=true]");

        Criterion targetType = query.addCriterion(new Path(PanelDefinition.FLD_TAGS), Condition.CN);
        Criterion panelType = query.addCriterion(new Path(PanelDefinition.FLD_TYPE), Condition.CN);


        for (EntityMetadata entityMetadata : ContextUtil.getMRS().getEntityMetadatas()) {
            if (models.getText().equals(entityMetadata.getSchema())) {

                StringBuilder sb = new StringBuilder().append("[").append(SettingEnum.TARGET_TYPE.name()).append("=")
                        .append(entityMetadata.getName()).append("]");
                targetType.setValue(sb.toString());

                panelType.setValue(EntityViewPanel.class.getCanonicalName());
                PanelDefinition panelDefinition = ContextUtil.getDRS().findFirstByQuery(query);
                if (panelDefinition == null) {
                    EntityViewPanel panel = ContextUtil.getDefaultPanel(EntityViewPanel.class);
                    panel.setTargetType(entityMetadata.getEntityType());
                    panel.setSettingValue(SettingEnum.PANEL_NAME, entityMetadata.getFriendlyName());
                    ORMUtil.persistPanel(panel, "Entity view of " + entityMetadata.getName(), "[AUTO=true]");
                }

                panelType.setValue(ListViewPanel.class.getCanonicalName());
                panelDefinition = ContextUtil.getDRS().findFirstByQuery(query);
                if (panelDefinition == null) {
                    ListViewPanel panel = ContextUtil.getDefaultPanel(ListViewPanel.class);
                    panel.setTargetType(entityMetadata.getEntityType());
                    panel.setSettingValue(SettingEnum.PANEL_NAME, inPlural(entityMetadata.getFriendlyName()));
                    ORMUtil.persistPanel(panel, "List view of " + entityMetadata.getName(), "[AUTO=true]");
                }
            }
        }

        ContextUtil.getSessionContext().refresh();
        ContextUtil.getDRS().evictCache();
        Executions.sendRedirect("..");
    }


    private String inPlural(String name) {
        if (name.toLowerCase().endsWith("s")) {
            name += "e";
        }
        return name + "s";
    }

    private static String repalce(String in) {
        String out = in;
        out = StringUtils.replace(out, "org.w4tj.panel.", "org.web4thejob.web.panel.");
        out = StringUtils.replace(out, "org.w4tj.dialog.", "org.web4thejob.web.dialog.");
        out = StringUtils.replace(out, "org.w4tj.composer.", "org.web4thejob.web.composer.");
        return out;
    }

}
