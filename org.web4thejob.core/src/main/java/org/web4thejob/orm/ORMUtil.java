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

package org.web4thejob.orm;

import org.springframework.util.StringUtils;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.orm.query.Subquery;
import org.web4thejob.orm.scheme.RenderElement;
import org.web4thejob.util.CoreUtil;
import org.web4thejob.util.XMLUtil;
import org.web4thejob.web.panel.BindCapable;
import org.web4thejob.web.panel.DesktopLayoutPanel;
import org.web4thejob.web.panel.MasterDetailTypeAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

public class ORMUtil {

    public static PanelDefinition getUserDesktop() {
        Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
        query.addCriterion(PanelDefinition.FLD_TYPE, Condition.CN, DesktopLayoutPanel.class.getSimpleName());
        query.addCriterion(PanelDefinition.FLD_OWNER, Condition.EQ, ContextUtil.getSessionContext()
                .getSecurityContext().getUserIdentity());
        return ContextUtil.getDRS().findUniqueByQuery(query);
    }

    public static PanelDefinition getPanelDefinition(String beanid) {
        Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
        query.addCriterion(PanelDefinition.FLD_BEANID, Condition.EQ, beanid);
        query.setCached(true);
        return ContextUtil.getDRS().findUniqueByQuery(query);
    }

    public static List<Subquery> buildUniqueKeyCriteria(org.web4thejob.web.panel.Panel panel,
                                                        RenderElement renderElement) {
        if (panel instanceof BindCapable && panel instanceof MasterDetailTypeAware) {

            if (((MasterDetailTypeAware) panel).hasBindProperty() && ((BindCapable) panel).hasMasterEntity()) {
                if (renderElement.getPropertyPath().getLastStep().isUniqueKeyWith(((MasterDetailTypeAware) panel)
                        .getBindProperty())) {

                    EntityMetadata target = renderElement.getPropertyPath().getLastStep().getEntityMetadata();
                    Subquery subquery = new Subquery(Subquery.SubqueryType.TYPE_NOT_EXISTS, target);
                    subquery.addCriterion(ContextUtil.getMRS().getPropertyPath(target.getEntityType(),
                            ((MasterDetailTypeAware) panel).getBindProperty()), Condition.EQ,
                            ((BindCapable) panel).getMasterEntity());
                    subquery.addCriterion(renderElement.getPropertyPath(), Condition.EQ,
                            Subquery.MASTER_ID_PLACEHOLDER);

                    List<Subquery> subqueries = new ArrayList<Subquery>();
                    subqueries.add(subquery);
                    return subqueries;
                }
            }
        }

        return null;
    }

    public static String persistPanel(org.web4thejob.web.panel.Panel panel, String description, String extraTags) {
        String xml = panel.toSpringXml();
        String beanid = XMLUtil.getRootElementId(xml);
        PanelDefinition panelDefinition = ContextUtil.getEntityFactory().buildPanelDefinition();
        panelDefinition.setBeanId(beanid);
        panelDefinition.setName(CoreUtil.cleanPanelName(panel.toString()));
        panelDefinition.setType(CoreUtil.describeClass(panel.getClass()));
        panelDefinition.setDescription(description);

        String tags = CoreUtil.tagPanel(panel);
        if (!StringUtils.hasText(extraTags)) {
            panelDefinition.setTags(tags);
        } else {
            panelDefinition.setTags(tags + extraTags);
        }

        panelDefinition.setDefinition(XMLUtil.toSpringBeanXmlResource(xml));
        ContextUtil.getDWS().save(panelDefinition);
        return beanid;
    }

    public static String persistPanel(org.web4thejob.web.panel.Panel panel) {
        return persistPanel(panel, null, null);
    }

    public static List<PanelDefinition> getPanelsMatchingTags(Map<String, Object> tags) {
        Query query = ContextUtil.getEntityFactory().buildQuery(PanelDefinition.class);
        for (String tag : tags.keySet()) {
            query.addCriterion(PanelDefinition.FLD_TAGS, Condition.CN, CoreUtil.buildTagValue(tag, tags.get(tag)));
        }
        query.addOrderBy(PanelDefinition.FLD_NAME);
        query.setCached(true);

        List<PanelDefinition> panels = new ArrayList<PanelDefinition>();
        for (Entity item : ContextUtil.getDRS().findByQuery(query)) {
            if (ContextUtil.getSessionContext().hasPanel(((PanelDefinition) item).getBeanId(),
                    org.web4thejob.web.panel.Panel.class)) {
                panels.add((PanelDefinition) item);
            }
        }

        if (panels.isEmpty()) {
            Collections.emptyList();
        }

        return panels;
    }

}
