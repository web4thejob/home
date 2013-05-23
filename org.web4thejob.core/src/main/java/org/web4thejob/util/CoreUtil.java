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

package org.web4thejob.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.web4thejob.command.CommandEnum;
import org.web4thejob.context.ContextUtil;
import org.web4thejob.message.Message;
import org.web4thejob.message.MessageArgEnum;
import org.web4thejob.message.MessageAware;
import org.web4thejob.message.MessageEnum;
import org.web4thejob.orm.Entity;
import org.web4thejob.orm.ORMUtil;
import org.web4thejob.orm.PanelDefinition;
import org.web4thejob.orm.Path;
import org.web4thejob.orm.parameter.Category;
import org.web4thejob.orm.parameter.Parameter;
import org.web4thejob.orm.query.Condition;
import org.web4thejob.orm.query.Query;
import org.web4thejob.security.Identity;
import org.web4thejob.security.RoleIdentity;
import org.web4thejob.security.RoleMembers;
import org.web4thejob.setting.Setting;
import org.web4thejob.setting.SettingEnum;
import org.web4thejob.web.panel.*;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public abstract class CoreUtil {
    public static final String TAG_MASTER_DETAIL = "MASTER_DETAIL";
    public static final String TAG_ENTITY_VIEW = "ENTITY_VIEW";
    public static final String TAG_LIST_VIEW = "LIST_VIEW";

    private static final Map<Class<? extends Entity>, Set<Serializable>> systemLocks = new HashMap<Class<? extends
            Entity>, Set<Serializable>>();

    public static final String BEAN_ROOT_CONTEXT = "rootApplicationContext";

    public static Set<Setting<?>> cloneSettings(Set<Setting<?>> source) {
        Set<Setting<?>> target = new HashSet<Setting<?>>(source.size());

        for (Setting<?> setting : source) {
            target.add(setting.clone());
        }

        return target;
    }


    public static void addSystemLock(Entity entity) {
        Set<Serializable> ids = systemLocks.get(entity.getEntityType());
        if (ids == null) {
            ids = new HashSet<Serializable>();
            systemLocks.put(entity.getEntityType(), ids);
        }

        ids.add(entity.getIdentifierValue());
    }

    public static boolean isSystemLocked(Entity entity) {
        return systemLocks.containsKey(entity.getEntityType()) && systemLocks.get(entity.getEntityType()).contains
                (entity.getIdentifierValue());
    }

    public static <T> T getParameterValue(Identity owner, Category category, String key, Class<T> clazz) {
        return getParameterValue(owner, category, key, clazz, null);

    }

    public static <T> T getParameterValue(Identity owner, Category category, String key, Class<T> clazz,
                                          T defaultValue) {
        Query query = ContextUtil.getEntityFactory().buildQuery(Parameter.class);
        query.addCriterion(new Path(Parameter.FLD_OWNER), Condition.EQ, owner);
        query.addCriterion(new Path(Parameter.FLD_CATEGORY), Condition.EQ, category);
        query.addCriterion(new Path(Parameter.FLD_KEY), Condition.EQ, key);
        query.setCached(true);

        Parameter parameter = ContextUtil.getDRS().findUniqueByQuery(query);
        if (parameter != null) {
            return ContextUtil.getBean(ConversionService.class).convert(parameter.getValue(), clazz);
        }
        return defaultValue;
    }


    public static <T> T getParameterValue(Category category, String key, Class<T> clazz) {
        return getParameterValue(category, key, clazz, null);
    }

    public static <T> T getParameterValue(Category category, String key, Class<T> clazz, T defaultValue) {
        T result = getParameterValue(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity(),
                category, key, clazz, null);

        if (result == null) {
            Query query = ContextUtil.getEntityFactory().buildQuery(RoleMembers.class);
            query.setCached(true);
            query.addCriterion(new Path(RoleMembers.FLD_USER), Condition.EQ, ContextUtil.getSessionContext()
                    .getSecurityContext().getUserIdentity());
            query.addOrderBy(new Path(RoleMembers.FLD_ROLE).append(RoleIdentity.FLD_INDEX));
            for (Entity members : ContextUtil.getDRS().findByQuery(query)) {
                result = getParameterValue(ContextUtil.getMRS().deproxyEntity(((RoleMembers) members).getRole()),
                        category, key, clazz, null);
                if (result != null) break;
            }
        }

        if (result == null) {
            result = defaultValue;
        }

        return result;
    }


    public static void setParameterValue(Category category, String key, Object value) {
        setParameterValue(ContextUtil.getSessionContext().getSecurityContext().getUserIdentity(), category, key,
                value);
    }

    private static boolean isParamAlreadySetForRole(Identity owner, Category category, String key, Object value) {
        if (value == null) return false;

        Object oldValue = getParameterValue(owner, category, key, value.getClass(), null);
        if (new EqualsBuilder().append(oldValue, value).isEquals()) {
            return true;
        }

        Query query = ContextUtil.getEntityFactory().buildQuery(RoleMembers.class);
        query.setCached(true);
        query.addCriterion(new Path(RoleMembers.FLD_USER), Condition.EQ, owner);
        query.addOrderBy(new Path(RoleMembers.FLD_ROLE).append(RoleIdentity.FLD_INDEX));
        for (Entity members : ContextUtil.getDRS().findByQuery(query)) {
            oldValue = getParameterValue(ContextUtil.getMRS().deproxyEntity(((RoleMembers) members).getRole()),
                    category, key, value.getClass(), null);
            if (new EqualsBuilder().append(oldValue, value).isEquals()) {
                return true;
            }
        }

        return false;
    }

    public static void setParameterValue(Identity owner, Category category, String key, Object value) {

        if (isParamAlreadySetForRole(owner, category, key, value)) {
            //if this parameter is already set in a higher level exit
            return;
        }


        Query query = ContextUtil.getEntityFactory().buildQuery(Parameter.class);
        query.addCriterion(new Path(Parameter.FLD_OWNER), Condition.EQ, owner);
        query.addCriterion(new Path(Parameter.FLD_CATEGORY), Condition.EQ, category);
        query.addCriterion(new Path(Parameter.FLD_KEY), Condition.EQ, key);

        Parameter parameter = ContextUtil.getDRS().findUniqueByQuery(query);
        if (parameter == null) {
            parameter = ContextUtil.getEntityFactory().buildParameter(Parameter.class);
            parameter.setOwner(owner);
            parameter.setCategory(category);
            parameter.setKey(key);
        }

        if (value != null && !new EqualsBuilder().append(parameter.getValue(), value).isEquals()) {
            parameter.setValue(ContextUtil.getBean(ConversionService.class).convert(value, String.class));
            ContextUtil.getDWS().save(parameter);
        } else if (value == null && !parameter.isNewInstance()) {
            ContextUtil.getDWS().delete(parameter);
        }

    }

    public static String describeClass(Class<?> clazz) {
        List<String> classes = new ArrayList<String>();
        classes.add(clazz.getCanonicalName());
        for (Class<?> cls : clazz.getInterfaces()) {
            classes.add(cls.getCanonicalName());
        }
        return StringUtils.collectionToDelimitedString(classes, ", ");
    }

    public static String buildTagValue(String tag, Object value) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(tag);
        sb.append("=");
        if (Boolean.class.isInstance(value)) {
            if ((Boolean) value) {
                sb.append("true");
            } else {
                sb.append("false");
            }
        } else {
            sb.append(value);
        }
        sb.append("]");

        return sb.toString();
    }


    public static String tagPanel(Panel panel) {
        StringBuilder sb = new StringBuilder();

        if (panel instanceof EntityViewPanel) {
            sb.append(buildTagValue(TAG_ENTITY_VIEW, true));
            sb.append(" ");
        }
        if (panel instanceof ListViewPanel) {
            sb.append(buildTagValue(TAG_LIST_VIEW, true));
            sb.append(" ");
        }
        if (panel instanceof TargetTypeAware) {
            sb.append(buildTagValue(SettingEnum.TARGET_TYPE.name(), (((TargetTypeAware) panel).hasTargetType()) ?
                    ((TargetTypeAware) panel).getTargetType().getCanonicalName() : null));
            sb.append(" ");
        }
        if (panel instanceof MasterTypeAware) {
            sb.append(buildTagValue(SettingEnum.MASTER_TYPE.name(), (((MasterTypeAware) panel).hasMasterType()) ?
                    ((MasterTypeAware) panel).getMasterType().getCanonicalName() : null));
            sb.append(" ");
        }
        if (panel instanceof MasterDetailTypeAware) {
            sb.append(buildTagValue(SettingEnum.BIND_PROPERTY.name(), ((MasterDetailTypeAware) panel).getBindProperty
                    ()));
            sb.append(" ");
            sb.append(buildTagValue(TAG_MASTER_DETAIL, ((MasterDetailTypeAware) panel).isMasterDetail()));
            sb.append(" ");
        }

        if (sb.length() == 0 && panel instanceof ParentCapable) {
            for (Panel subpanel : ((ParentCapable) panel).getSubpanels()) {
                //we are interested only in the first child
                sb.append(tagPanel(subpanel));
                if (sb.length() > 0) break;
            }
        }

        return sb.toString().trim();
    }

    public static boolean isSelectionMessage(Message message) {
        return MessageEnum.ENTITY_SELECTED == message.getId() || MessageEnum.ENTITY_DESELECTED == message.getId();
    }

    public static String cleanPanelName(String name) {
        if (name.contains(":")) {
            return name.substring(0, name.indexOf(":"));
        } else {
            return name;
        }
    }

    public static Locale getUserLocale() {
        Locale locale = null;
        try {
            locale = ContextUtil.getSessionContext().getSecurityContext().getUserIdentity().getLocale();
        } catch (Exception ignore) {
        }

        if (locale == null) {
            locale = LocaleContextHolder.getLocale();
        }

        return locale;
    }

    public static Panel getEntityViewPanel(Entity entity) {
        return getEntityViewPanel(entity, null);
    }

    public static String getDefaultEntityViewName(Class<? extends Entity> entityType) {
        return CoreUtil.getParameterValue(Category.ENTITY_TYPE_ENTITY_VIEW_PARAM,
                entityType.getCanonicalName(), String.class);
    }

    public static String getDefaultListViewName(Class<? extends Entity> entityType) {
        return CoreUtil.getParameterValue(Category.ENTITY_TYPE_LIST_VIEW_PARAM,
                entityType.getCanonicalName(), String.class);
    }

    public static Panel getEntityViewPanel(Entity entity, String beainid) {

        if (beainid == null) {
            beainid = getDefaultEntityViewName(entity.getEntityType());
        }

        if (entity != null) {
            org.web4thejob.web.panel.Panel entityPanel;

            if (beainid != null && ContextUtil.getSessionContext().hasPanel(beainid,
                    org.web4thejob.web.panel.Panel.class)) {
                entityPanel = ContextUtil.getPanel(beainid);
                if (!MessageAware.class.isInstance(entityPanel)) return null;
                entityPanel.render();
                ((MessageAware) entityPanel).processMessage(ContextUtil.getMessage(MessageEnum
                        .BIND_DIRECT, entityPanel, MessageArgEnum.ARG_ITEM, entity));

/*              TODO Reconsider. This messes security policy
                CoreUtil.setParameterValue(Category.ENTITY_TYPE_ENTITY_VIEW_PARAM,
                        entity.getEntityType().getCanonicalName(), beainid);
*/
            } else {
                entityPanel = ContextUtil.getDefaultPanel(MutableEntityViewPanel.class);
                ((MutableEntityViewPanel) entityPanel).setTargetType(entity.getEntityType());
                entityPanel.render();
                ((MutableEntityViewPanel) entityPanel).setTargetEntity(entity);
            }


            if (entityPanel instanceof MessageAwarePanel) {
                ((MessageAwarePanel) entityPanel).bindingSuspended(true);
            }

            return entityPanel;
        }

        return null;
    }

    public static String getCommandImage(CommandEnum id, String defaultImage) {
        String sb = "img/CMD_" + id.name() + ".png";
        return ContextUtil.resourceExists(sb.toString()) ? sb.toString() : defaultImage;
    }

    public static String getCommandImage(CommandEnum id, String defaultImage, boolean dirty) {
        if (!dirty) return getCommandImage(id, defaultImage);

        String sb = "img/CMD_" + id.name() + "_Dirty.png";
        return ContextUtil.resourceExists(sb.toString()) ? sb.toString() : defaultImage;
    }

    public static Query getQuery(Class<? extends Entity> targetType, String name) {
        Query lookup = ContextUtil.getEntityFactory().buildQuery(Query.class);
        lookup.addCriterion(new Path(Query.FLD_FLAT_TARGET_TYPE), Condition.EQ, targetType.getCanonicalName());
        lookup.addCriterion(new Path(Query.FLD_NAME), Condition.EQ, name);
        lookup.setCached(true);
        return ContextUtil.getDRS().findUniqueByQuery(lookup);
    }

    public static Query getDefaultQueryForTargetType(Class<? extends Entity> targetType) {
        String id = CoreUtil.getParameterValue(Category.ENTITY_TYPE_QUERY_PARAM,
                targetType.getCanonicalName(), String.class, null);
        if (StringUtils.hasText(id)) {
            return ContextUtil.getDRS().findById(Query.class, Long.valueOf(id));
        }
        return null;
    }

    public static Map<String, String> getRelatedPanelsMap(Class<? extends Entity> entityType,
                                                          Class<? extends ContentPanel> panelType) {

        Map<String, Object> tags = new HashMap<String, Object>();

        if (panelType != null) {
            if (EntityViewPanel.class.isAssignableFrom(panelType)) {
                tags.put(CoreUtil.TAG_ENTITY_VIEW, true);
            } else if (ListViewPanel.class.isAssignableFrom(panelType)) {
                tags.put(CoreUtil.TAG_LIST_VIEW, true);
            } else {
                throw new UnsupportedOperationException("panel type not supported yet:" + panelType.getCanonicalName());
            }
        }

        if (entityType != null) {
            tags.put(SettingEnum.TARGET_TYPE.name(), entityType.getCanonicalName());
        }

        tags.put(CoreUtil.TAG_MASTER_DETAIL, false);

        List<PanelDefinition> panelDefinitions = ORMUtil.getPanelsMatchingTags(tags);
        if (panelDefinitions.isEmpty()) return Collections.emptyMap();

        Map<String, String> map = new LinkedHashMap<String, String>(panelDefinitions.size());
        for (PanelDefinition panelDefinition : ORMUtil.getPanelsMatchingTags(tags)) {
            map.put(panelDefinition.getBeanId(), panelDefinition.getName());
        }

        return map;


    }

    public static boolean isURL(Object obj) {
        try {
            new URL(obj.toString());
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
